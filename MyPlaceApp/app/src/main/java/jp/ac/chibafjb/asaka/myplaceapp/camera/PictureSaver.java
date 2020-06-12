package jp.ac.chibafjb.asaka.myplaceapp.camera;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

class PictureSaver implements Runnable {
    // ファイル名のテンプレート
    private static final String FILE_NAME_TEMPLATE
            = "image-%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
    // 出力先ディレクトリ
    private File mOutputDir;
    // 画像データ
    private byte[] mData;

    public PictureSaver(Context context, byte[] data) {
        mOutputDir = context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        mData = data;
    }

    @Override
    public void run() {
        // ファイル名を作成
        String fileName = String.format(FILE_NAME_TEMPLATE, Calendar.getInstance());
        // 出力先ファイル
        File outputFile = new File(mOutputDir, fileName);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(outputFile);
            output.write(mData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
