package zhuyunpeng.com.dragcircleprogress.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * 静态土司工具
 * Created by 玉光 on 2016-11-7.
 */

public class ToastUtils {
    public static Toast toast;

    /**
     * 展示短时间信息
     * @param context
     * @param msg
     */
    public static void showMsg(Context context,String msg){
        if (toast==null){
            toast=Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }

    /**
     * 展示自定义控件土司
     * @param context
     * @param view
     */
    public static void showViewMsg(Context context,View view){
        if (toast==null){
            toast=Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
        toast.setView(view);
        toast.show();
    }
}
