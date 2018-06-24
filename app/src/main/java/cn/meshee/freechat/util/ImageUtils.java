package cn.meshee.freechat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.SystemClock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    private static final String thumbImgDirPath = UIUtils.getContext().getCacheDir().getAbsolutePath();

    private static File thumbImgDir;

    public static File genThumbImgFile(String srcImgPath) {
        if (thumbImgDir == null)
            thumbImgDir = new File(thumbImgDirPath);
        if (!thumbImgDir.exists())
            thumbImgDir.mkdirs();
        String thumbImgName = SystemClock.currentThreadTimeMillis() + FileUtils.getFileNameFromPath(srcImgPath);
        File imageFileThumb = null;
        try {
            InputStream is = new FileInputStream(srcImgPath);
            Bitmap bmpSource = BitmapFactory.decodeStream(is);
            File imageFileSource = new File(srcImgPath);
            imageFileSource.createNewFile();
            FileOutputStream fosSource = new FileOutputStream(imageFileSource);
            bmpSource.compress(Bitmap.CompressFormat.JPEG, 100, fosSource);
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, bmpSource.getWidth(), bmpSource.getHeight()), new RectF(0, 0, 160, 160), Matrix.ScaleToFit.CENTER);
            Bitmap bmpThumb = Bitmap.createBitmap(bmpSource, 0, 0, bmpSource.getWidth(), bmpSource.getHeight(), m, true);
            imageFileThumb = new File(ImageUtils.thumbImgDirPath, thumbImgName);
            imageFileThumb.createNewFile();
            FileOutputStream fosThumb = new FileOutputStream(imageFileThumb);
            bmpThumb.compress(Bitmap.CompressFormat.JPEG, 60, fosThumb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFileThumb;
    }
}
