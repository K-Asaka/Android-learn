package jp.ac.chibafjb.asaka.myplaceapp.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class CameraChooser {
    private Context context;

    private int width;
    private int height;

    public CameraChooser(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraInfo chooseCamera() {
        CameraManager cameraManager = (CameraManager)context.getSystemService(
                Context.CAMERA_SERVICE);

        try {
            String[] cameraIds = cameraManager.getCameraIdList();

            for (String cameraId : cameraIds) {
                // カメラデバイスの特徴を取得する
                CameraCharacteristics characteristics
                        = cameraManager.getCameraCharacteristics(cameraId);

                if (!isBackFacing(characteristics)) continue;

                // 設定可能な画像サイズやプレビューサイズを取得する
                StreamConfigurationMap map;
                if ((map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP))
                        == null) {
                    // ストリーム情報を取得できないなら採用しない
                    continue;
                }

                Size pictureSize;
                if ((pictureSize = chooseImageSize(map)) == null) {
                    // 適切なサイズがないなら、採用しない
                    continue;
                }

                Size previewSize;
                if ((previewSize = choosePreviewSize(map)) == null) {
                    // 適切なサイズがないなら、採用しない
                    continue;
                }

                // カメラセンサーのついている向き
                // 通常は90度だが、Nexus 5Xなど一部の端末は270度になっている
                Integer sensorOrientation;
                if ((sensorOrientation = characteristics.get(
                        CameraCharacteristics.SENSOR_ORIENTATION)) == null) {
                    // 取得できないなら、採用しない
                    continue;
                }

                // ここまで合格なら、それを返す
                CameraInfo cameraInfo = new CameraInfo();
                cameraInfo.setCameraId(cameraId);
                cameraInfo.setPictureSize(pictureSize);
                cameraInfo.setPreviewSize(previewSize);
                cameraInfo.setSensorOrientation(sensorOrientation);

                return cameraInfo;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Size choosePreviewSize(StreamConfigurationMap map) {
        // 設定可能なプレビューサイズのリスト
        Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);

        // プレビュー用のTextureViewの半分より大きいなかで最小のサイズを選択する
        return getMinimalSize(width / 2, height / 2, previewSizes);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Size chooseImageSize(StreamConfigurationMap map) {
        // 設定可能な写真サイズのリスト
        Size[] pictureSizes = map.getOutputSizes(ImageFormat.JPEG);

        // プレビュー用のTextureViewより大きい中で最小のサイズを選択する
        return getMinimalSize(width, height, pictureSizes);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isBackFacing(CameraCharacteristics characteristics) {
        Integer facing = characteristics.get(
                CameraCharacteristics.LENS_FACING);

        // カメラの向きが取得できない、または前面カメラの場合は採用しない
        return (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK);
    }

    // 必要最小限のサイズを選択する
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Size getMinimalSize(int minWidth, int minHeight, Size[] sizes) {
        List<Size> sizeList = Arrays.asList(sizes);

        // 面積の小さい順に並べる
        Collections.sort(sizeList, new Comparator<Size>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public int compare(Size lhs, Size rhs) {
                return lhs.getHeight() * lhs.getWidth()
                        - rhs.getHeight() * rhs.getWidth();
            }
        });

        for (Size size : sizeList) {
            if ((size.getWidth() >= minWidth && size.getHeight() >= minHeight)
                || (size.getWidth() >= minHeight
                && size.getHeight() >= minWidth)) {
                return size;
            }
        }

        return null;
    }
}
