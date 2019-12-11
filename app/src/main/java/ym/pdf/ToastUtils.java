package ym.pdf;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Description : The class includes some static methods of toast.
 * Author : Gavin.GaoTJ  29.10.2018
 */
public class ToastUtils {

    private static Toast mToast;

    private static Toast mCustomToast;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Show short custom toast in a safe way.
     *
     * @param msgRes The text res that needs to be displayed.
     */
    public static void showCustomShortToast(@NonNull final int msgRes) {
        showCustomShortToast(App.getApplication().getString(msgRes));
    }

    /**
     * Show short custom toast in a safe way.
     *
     * @param msg The text that needs to be displayed.
     */
    public static void showCustomShortToast(@NonNull final String msg) {
        mHandler.post(() -> showToast(msg, Toast.LENGTH_SHORT));
    }

    /**
     * Show long custom toast in a safe way.
     *
     * @param msgRes The text res that needs to be displayed.
     */
    public static void showCustomLongToast(final int msgRes) {
        showCustomLongToast(App.getApplication().getString(msgRes));
    }

    /**
     * Show long custom toast in a safe way.
     *
     * @param msg The text that needs to be displayed.
     */
    public static void showCustomLongToast(final String msg) {
        mHandler.post(() -> showToast(msg, Toast.LENGTH_LONG));
    }

    /**
     * Show short system toast in a safe way.
     *
     * @param msg The text that needs to be displayed.
     */
    public static void showShortToast(@NonNull final String msg) {
        mHandler.post(() -> showSystemToast(msg, Toast.LENGTH_SHORT));
    }


    /**
     * Show long system toast in a safe way.
     *
     * @param msg The text that needs to be displayed.
     */
    public static void showLongToast(final String msg) {
        mHandler.post(() -> showSystemToast(msg, Toast.LENGTH_LONG));
    }

    /**
     * The custom toast is shown.
     *
     * @param msg      The text that needs to be displayed.
     * @param duration duration time.
     */
    private static void showToast(String msg, int duration) {
        if (mCustomToast == null) {
            mCustomToast = new Toast(App.getApplication().getApplicationContext());
        } else {
            mCustomToast.cancel();
        }
        View layout = LayoutInflater.from(App.getApplication().getApplicationContext()).inflate(R.layout.view_custom_toast, null);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast_text);
        text.setText(msg);
        mCustomToast.setGravity(Gravity.CENTER, 0, 0);
        mCustomToast.setDuration(duration);
        mCustomToast.setView(layout);
        mCustomToast.show();
    }

    /**
     * The system toast is shown.
     *
     * @param text     The text that needs to be displayed.
     * @param duration duration time.
     */
    private static void showSystemToast(CharSequence text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(App.getApplication().getApplicationContext(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}
