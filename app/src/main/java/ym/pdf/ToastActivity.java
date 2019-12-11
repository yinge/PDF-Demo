package ym.pdf;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ToastActivity extends Activity {
    String TAG = "Gavin Toast";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
    }

    public void fun1(View view) {
        ToastUtils.showCustomLongToast("按钮一显示");
        Log.d(TAG, "fun1: ");
    }

    public void fun2(View view) {
        ToastUtils.showCustomLongToast("按钮二显示");
        Log.d(TAG, "fun2: ");
    }

    public void fun3(View view) {
        ToastUtils.showCustomLongToast("按钮三显示");
        Log.d(TAG, "fun3: ");
    }
}
