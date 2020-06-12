package jp.ac.chibafjb.asaka.myplaceapp.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.app.Fragment;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import jp.ac.chibafjb.asaka.myplaceapp.R;

public class CameraLegacyFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    // カメラオブジェクト
    private Camera mCamera;
    // プレビューを表示するテクスチャービュー
    private TextureView mTextureView;
    // 写真撮影後、ファイルに保存したり、DBに保存するためのスレッド
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.ShutterButton).setOnClickListener(this);
        mTextureView = (TextureView)view.findViewById(R.id.PreviewTexture);
    }

    private void startCamera() {
        if (mTextureView.isAvailable()) {
            // TextureViewはすでに使用可能なので、カメラを開く
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
            // TextureViewと関連付けられているSurfaceTextureが使用可能になった
            // カメラデバイスへの接続を開始する
            openCamera(surface, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // SurfaceTextureが破棄されたので、カメラを開放する
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
            FragmentCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return;
        }
        // Cameraへのアクセスを取得する
        mCamera = Camera.open();

        try {
            // カメラのプレビュー表示用のTextureを設定する
            mCamera.setPreviewTexture(surface);
            // プレビューの表示を開始する
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // スレッドを開始する
        startThread();
        // カメラを開く
        startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        //        // スレッドを停止する
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ShutterButton) {
            if (mCamera != null) {
                mCamera.takePicture(
                        mShutterCallback,   // Camera.ShutterCallbackを指定可能
                        null,               // raw画像用のPictureCallback
                        mPictureCallback    // JPEG用のPictureCallback
                );
            }
        }
    }

    // シャッターボタンを押したときのコールバック
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
            // dataはJPEG画像のバイトデータ
            mBackgroundHandler.post(new PictureSaver(getActivity(), data));
            // プレビュー再開
            mCamera.startPreview();
        }
    };
}
