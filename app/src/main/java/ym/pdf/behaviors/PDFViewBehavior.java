package ym.pdf.behaviors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;

import ym.pdf.R;

public class PDFViewBehavior extends CoordinatorLayout.Behavior<PDFView> {

    private float touchDownX;
    private float touchDownY;
    private float lastMoveX;
    private float lastMoveY;

    private boolean moved;
    private boolean intercepting;

    private enum Direction {UP, DOWN, OTHER}

    private static final float MIN_MOVE = 10;

    public PDFViewBehavior() {
    }

    public PDFViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, PDFView child, View dependency) {
        return dependency.getId() == R.id.topview;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, PDFView child, View dependency) {
        float y = dependency.getHeight() + dependency.getTranslationY();
        if (y < 0) {
            y = 0;
        }
        child.setY(y);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull PDFView child, @NonNull MotionEvent ev) {
        Log.d("TopViewBehavior", "onInterceptTouchEvent action = " + ev.getAction() + " moved = " + moved + " intercepting = " + intercepting);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            intercepting = false;
            moved = false;

            touchDownX = ev.getX();
            touchDownY = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (!moved) {
                float dx = ev.getX() - touchDownX;
                float dy = ev.getY() - touchDownY;

                moved = isMoved(dx, dy);
                if (moved) {
                    lastMoveX = touchDownX;
                    lastMoveY = touchDownY;
                    intercepting = needIntercept(parent, child, dx, dy);
                }
            }
        }
        return intercepting;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull PDFView child, @NonNull MotionEvent ev) {
//        Log.d("TopViewBehavior", "onTouchEvent action = " + ev.getAction());
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (intercepting) {
                View topView = parent.findViewById(R.id.topview);

                float dy = ev.getY() - lastMoveY;
                float translationY = topView.getTranslationY() + dy;
                if (translationY > 0) {
                    translationY = 0;
                } else if (translationY < -topView.getHeight()) {
                    translationY = -topView.getHeight();

                    intercepting = false;
                    MotionEvent newEv = MotionEvent.obtain(ev);
                    newEv.setAction(MotionEvent.ACTION_DOWN);
                    parent.dispatchTouchEvent(newEv);
                }
                topView.setTranslationY(translationY);

                lastMoveX = ev.getX();
                lastMoveY = ev.getY();
            }
        }
        return intercepting;
    }

    private boolean needIntercept(@NonNull CoordinatorLayout parent, @NonNull PDFView child, float dx, float dy) {
        View topView = parent.findViewById(R.id.topview);
        if (child.getPositionOffset() == 0) {
            Direction direction = calDirection(dx, dy);
            return (topView.getTranslationY() > -topView.getHeight() && direction == Direction.UP) ||
                    (topView.getTranslationY() < 0 && direction == Direction.DOWN);
        }
        return false;
    }

    private boolean isMoved(float dx, float dy) {
        return Math.abs(dx) >= MIN_MOVE || Math.abs(dy) >= MIN_MOVE;
    }

    private Direction calDirection(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            return Direction.OTHER;
        }

        return dy > 0 ? Direction.DOWN : Direction.UP;
    }
}
