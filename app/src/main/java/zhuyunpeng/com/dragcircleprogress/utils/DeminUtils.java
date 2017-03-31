package zhuyunpeng.com.dragcircleprogress.utils;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 宽度工具
 * Created by 玉光 on 2016-11-10.
 */

public class DeminUtils {
    /**
     * 获取文本的宽度
     * @param paint
     * @param str
     * @return
     */
    public static int getTextWidth(Paint paint,String str){
        Rect rect=new Rect();
        paint.getTextBounds(str,0,str.length(),rect);
        return  rect.width();

    }

    /**
     * 获取文本的高度
     * @param paint
     * @param str
     * @return
     */
    public static int getTextHeight(Paint paint,String str){
        Rect rect=new Rect();
        paint.getTextBounds(str,0,str.length(),rect);
        return  rect.height();

    }
}
