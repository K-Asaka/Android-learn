package jp.ac.chibafjb.asaka.myplaceapp.camera;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import jp.ac.chibafjb.asaka.myplaceapp.R;

import java.io.IOException;

/**
 * 古いAPIレベル用のカメラフラグメント
 */
public class CameraLegacyFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    // カメラオブジェクト
    private Camera mCamera;
    // カメラプレビューを表示するためのTextureView
    private TextureView mTextureView;

    // 写真撮影後、ファイルに保存したり、DBに保存するためのスレッド
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.ShutterButton).setOnClickListener(this);
        mTextureView = (TextureView)view.findViewById(R.id.PreviewTexture);
    }

    @Override
    public void onResume() {
        super.onResume();
        // カメラの使用手続きを開始する
        startCamera();
        startThread();
    }

    @Override
    public void onPause() {
        super.onPause();
        // スレッドを停止する
        stopThread();
    }

    private void startThread() {
        // 画像処理を行うためのスレッドを立てる
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopThread() {
        // スレッドを止める
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startCamera() {
        if (mTextureView.isAvailable()) {
            // TextureViewは既に使用可能なので、カメラを開く
            openCamera(mTextureView.getSurfaceTexture(),
                    mTextureView.getWidth(),
                    mTextureView.getHeight());

        } else {
            mTextureView.setSurfaceTextureListener(mTextureListener);
        }
    }

    private final TextureView.SurfaceTextureListener mTextureListener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // TextureViewと関連付けられているSurfaceTextureが使用可能になったら、
            // カメラデバイスへの接続を開始する
            openCamera(surface, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // SurfaceTextureが破棄されたので、カメラを解放する
            stopCamera();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void stopCamera() {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void openCamera(SurfaceTexture surface, int width, int height) {

        // パーミッションチェック
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // パーミッションを求めるダイアログを表示する
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);

            return;
        }

        // Cameraへのアクセスを取得する
        mCamera = Camera.open();

        try {
            // カメラのプレビューの角度を調整する
            setDisplayOrientation();
            // カメラのプレビュー表示用のTextureを設定する
            mCamera.setPreviewTexture(surface);
            // プレビューの表示を開始する
            mCamera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ShutterButton) {
            if (mCamera != null) {
                mCamera.takePicture(
                        mShutterCallback, // Camera.ShutterCallbackを指定可能
                        null,  // raw画像用のPictureCallback
                        mPictureCallback  // jpeg用のPictureCallback
                );
            }
        }
    }

    // シャッターボタンを押した時のコールバック。
    // 設定しておくと、強制的にシャッター音が鳴らされる
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // 特に何もしない
        }
    };

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // dataはjpeg画像のバイトデータ
            mBackgroundHandler.post(new PictureSaver(getActivity(), data));
            // プレビュー再開
            mCamera.startPreview();
        }
    };

    //　端末の向きに合わせてカメラの角度を調整する
    public void setDisplayOrientation() {
        // カメラ情報を取得
        // カメラ情報は、値を格納するためのオブジェクトを先に作成し、
        // Camera#getCameraInfo()でオブジェクトに値を設定してもらう
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        // 端末の方向を取得する
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        // 端末の方向に合わせて、調整する値を決定する
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        // 最終的なカメラの角度を計算する。
        // 0〜360度に収まるように、360を足した上で、360で割った余りを計算する
        int result = (info.orientation - degrees + 360) % 360;
        mCamera.setDisplayOrientation(result);
    }
}
