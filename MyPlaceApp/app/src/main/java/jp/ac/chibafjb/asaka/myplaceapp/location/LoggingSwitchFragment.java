package jp.ac.chibafjb.asaka.myplaceapp.location;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import jp.ac.chibafjb.asaka.myplaceapp.R;

class LoggingSwitchFragment extends Fragment
    implements CompoundButton.OnCheckedChangeListener {

    // インストールまたはアップデートによってGoogle Play servicesを利用可能にするリクエスト
    private static final int REQUEST_INSTALL_OR_UPDATE = 1;
    // ユーザーに問題を解決してもらえる場合のリクエスト
    private static final int REQUEST_RESOLVE_PROBLEMS = 2;
    // 位置情報の設定変更リクエスト
    private static final int REQUEST_CHANGE_SETTINGS = 3;
    // 位置情報取得のパーミッション要求
    private static final int REQUEST_PERMISSION = 4;

    // 位置情報記録サービスのPendingIntentのID
    private static final int PENDING_INTENT_LOCATION = 1;

    private static final long MINUTE = 60 * 1000;
    // 位置情報取得の間隔(ms)
    private static final long INTERVAL_MS = 10 * MINUTE;
    // 位置情報取得の最短間隔(ms)
    private static final long FASTEST_INTERVAL_MS = 5 * MINUTE;
    // 位置情報の更新を待つ最長間隔(ms)
    private static final long MAX_WAIT_TIME_MS = 60 * MINUTE;

    // Google APIを使用するためのクライアント
    private GoogleApiClient mGoogleApiClient;
    // 位置情報のリクエスト
    private LocationRequest mLocationRequest;
    // ON・OFFスイッチ
    private Switch mSwitch;
    // Viewを生成する
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logging_switch,
                container, false);
    }

    // スイッチにON/OFF切り替え時のコールバックを設定する
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSwitch = (Switch)view.findViewById(R.id.Switch);
        mSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            // スイッチをONにした場合
            // 位置情報取得のシーケンスを開始
            checkServiceAvailability();
        } else if (mGoogleApiClient != null
            && mGoogleApiClient.isConnected()) {
            // すでにGoogleApiClientに接続済みで、スイッチをOFFにした場合
            // 位置情報のリクエストをキャンセル
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient,
                    getLocationPendingIntent(
                            PendingIntent.FLAG_UPDATE_CURRENT));
            // Google Api Clientとの接続を切る
            mGoogleApiClient.disconnect();
        }
    }

    // Google Play servicesが利用可能か確認する
    private void checkServiceAvailability() {
        if (mGoogleApiClient != null
            && mGoogleApiClient.isConnected()) {
            // 接続済みなら何もしない
            return;
        }

        // Google Play servicesの利用可否をチェックするクラス
        GoogleApiAvailability checker =
                GoogleApiAvailability.getInstance();

        // Google Play servicesの利用可否をチェック
        int result = checker.isGooglePlayServicesAvailable(getActivity());

        if (result == ConnectionResult.SUCCESS) {
            // 使用可能
            onGooglePlayServicesAvailable();
        } else {
            // 使用不可能な場合は、エラーダイアログを出す
            checker.showErrorDialogFragment(getActivity(),
                    result, REQUEST_INSTALL_OR_UPDATE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_INSTALL_OR_UPDATE
            || requestCode == REQUEST_RESOLVE_PROBLEMS) {
            if (resultCode == Activity.RESULT_OK) {
                // インストールやアップデートによって、
                // Google Play開発者サービスが利用可能になった場合
                onGooglePlayServicesAvailable();
            } else {
                // 利用可能にならなかったら、終了
                handleError();
            }
        }
    }

    // Google Play開発者サービスが利用可能だった場合
    private void onGooglePlayServicesAvailable() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                // 位置情報のAPI
                .addApi(LocationServices.API)
                // 接続・切断のイベントを受け取るコールバックを指定
                .addConnectionCallbacks(mGoogleApiCallback)
                // 接続失敗イベントを受け取るコールバックを指定
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();
        // 接続する
        mGoogleApiClient.connect();
    }

    // Google Play servicesへの接続・切断イベントを受け取るコールバック
    private GoogleApiClient.ConnectionCallbacks mGoogleApiCallback
            = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            // 位置情報を取得できるかチェックする
            checkUserLocationAvailability();
        }

        @Override
        public void onConnectionSuspended(int i) {
            // 接続が停止された
        }
    };

    // Google Play servicesへの接続失敗イベントを受け取るコールバック
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener
            = new GoogleApiClient.OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(@Nullable ConnectionResult connectionResult) {
            if (connectionResult.hasResolution()) {
                // 何らかの方法で解決可能な場合
                try {
                    // 解決するためのアクティビティを起動する
                    connectionResult.startResolutionForResult(getActivity(),
                            REQUEST_RESOLVE_PROBLEMS);
                    return;
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }

            // どうにもならない場合
            handleError();
        }
    };

    // ユーザーの位置情報が利用可能かを確認する
    private void checkUserLocationAvailability() {
        // 位置情報のリクエスト用オブジェクトを生成する
        mLocationRequest = new LocationRequest()
                .setInterval(INTERVAL_MS)       // 取得間隔
                .setFastestInterval(FASTEST_INTERVAL_MS)    // 最短の取得間隔
                .setMaxWaitTime(MAX_WAIT_TIME_MS)   // 最長の待ち時間
                .setPriority( // 省電力性と精度のバランス
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);  // ほどほど

        // この位置情報のリクエストは可能化を確認するためのオブジェクトを生成する
        LocationSettingsRequest checkRequest =
                new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)   // 確認対象のリクエスト
                .build();

        // ユーザーの設定をチェックする
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient, checkRequest);

        // チェック結果を受け取るコールバック
        result.setResultCallback(mResultCallback);
    }

    // 位置情報取得設定の確認結果
    private ResultCallback<LocationSettingsResult> mResultCallback
            = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
            // 設定チェック結果のステータスによって処理を分ける
            final Status status = locationSettingsResult.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // 特に問題なく利用できる
                    onUserLocationAvailable();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // ユーザーが設定を変更してくれれば利用できる場合
                    try {
                        // 解決するための画面を表示する
                        status.startResolutionForResult(getActivity(),
                                REQUEST_CHANGE_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        handleError();
                    }
                    break;

                default:
                    // その他、利用できない場合
                    handleError();
            }
        }
    };

    // 位置情報をリクエストできる
    private void onUserLocationAvailable() {
        // ここまでで接続が切れていないかチェックする
        if (mGoogleApiClient.isConnected()) {
            // パーミッションをチェックする
            if (ContextCompat.checkSelfPermission(getActivity(),
                // 高精度位置情報パーミッション
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                // 低精度位置情報パーミッション
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                // パーミッションを求めるダイアログを表示する
                FragmentCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_PERMISSION);
                return;
            }

            // 位置情報のリクエストを行う
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    getLocationPendingIntent(
                            PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    // 位置情報を受け取るサービスのPendingIntent
    private PendingIntent getLocationPendingIntent(int flag) {
        Intent intent = new Intent(getActivity(), PlaceStoreService.class);
        return PendingIntent.getService(getActivity(),
                PENDING_INTENT_LOCATION, intent, flag);
    }

    // パーミッション要求の結果を受け取る
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CHANGE_SETTINGS) {
            // 許可されたパーミッションがあるかを確認する
            boolean isSomethingGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    isSomethingGranted = true;
                    break;
                }
            }

            if (isSomethingGranted) {
                // 設定を変更してもらえた場合、処理を継続する
                onUserLocationAvailable();
            } else {
                // 設定を変更してもらえなかった場合、終了
                handleError();
            }
        }
    }

    // 位置情報を取れそうもないときの処理
    private void handleError() {
        mSwitch.setChecked(false);
        mSwitch.setEnabled(false);
    }

}
