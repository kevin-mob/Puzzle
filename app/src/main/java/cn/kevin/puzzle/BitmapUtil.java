package cn.kevin.puzzle;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 创建日期：2017/10/23.
 *
 * @author kevin
 */

public class BitmapUtil {
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }
}
