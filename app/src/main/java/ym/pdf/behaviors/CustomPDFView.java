package ym.pdf.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import ym.pdf.R;

public class CustomPDFView extends PDFView {

    float lastX;
    float lastY;
    private  AppBarLayout appBarLayout;

    /**
     * Construct the initial view
     *
     * @param context
     * @param set
     */
    public CustomPDFView(Context context, AttributeSet set) {
        super(context, set);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("TopViewBehavior", "dispatchTouchEvent getTop: " + getTop() +" ,getTranslationY: " + getTranslationY() + ev.getAction()
        );

        requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("TopViewBehavior", "onInterceptTouchEvent getTop: " + getTop() +" ,getTranslationY: " + getTranslationY() + ev.getAction()
        );
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TopViewBehavior", "onTouchEvent getTop: " + getTop() +" ,getTranslationY: " + getTranslationY() + event.getAction()
        );
        return super.onTouchEvent(event);
    }
}
