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

public class PDFViewBehavior222 extends CoordinatorLayout.Behavior<PDFView> {

    private float touchDownX;
    private float touchDownY;
    private float lastMoveX;
    private float lastMoveY;

    private boolean moved;

    private enum Direction {UP, DOWN}

    private static final float MIN_MOVE = 10;

    public PDFViewBehavior222() {
    }

    public PDFViewBehavior222(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 依赖条件，true表示绑定关系成立
     *
     * @param parent
     * @param child      指应用Behavior的View
     * @param dependency 担任触发behavior的角色，并于child进行互动
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, PDFView child, View dependency) {
        return dependency.getId() == R.id.topview;
    }

    /**
     * 属性依赖逻辑，返回true表示要执行
     *
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
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
        StringBuilder sb = new StringBuilder("action = " + ev.getAction());
        Log.d("TopViewBehavior", "onInterceptTouchEvent action" + ev.hashCode() + " = " + ev.getAction() + " moved = " + moved);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            moved = false;

            touchDownX = ev.getX();
            touchDownY = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = ev.getX() - touchDownX;
            float dy = ev.getY() - touchDownY;

            moved = isMoved(dx, dy);
            if (moved) {
                lastMoveX = touchDownX;
                lastMoveY = touchDownY;
                Log.d("TopViewBehavior", "onInterceptTouchEvent end action = " + ev.getAction() + " moved = " + needIntercept(parent, child, dx, dy));
                return needIntercept(parent, child, dx, dy);
            }
        }
        Log.d("TopViewBehavior", "onInterceptTouchEvent end action = " + ev.getAction() + " moved = " + moved);
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull PDFView child, @NonNull MotionEvent ev) {
        View topView = parent.findViewById(R.id.topview);
        Log.d("TopViewBehavior", "onTouchEvent action = " + ev.getAction() + " tranlationY: " + topView.getTranslationY());
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {


            float dy = ev.getY() - lastMoveY;
            float translationY;
            if (topView.getTranslationY() > 0) {
                translationY = 0;
                topView.setTranslationY(translationY);
            } else if (topView.getTranslationY() < -topView.getHeight()) {
                translationY = -topView.getTranslationY();
                topView.setTranslationY(translationY);
            } else if (topView.getTranslationY() == topView.getHeight()) {
                MotionEvent newEv = MotionEvent.obtain(ev);
                newEv.setAction(MotionEvent.ACTION_DOWN);
                parent.dispatchTouchEvent(newEv);
                Log.i("TopViewBehavior", "转发" + newEv.hashCode());
            } else {
                translationY = Math.min(0, Math.max(topView.getTranslationY() + dy, -topView.getHeight()));
                if (translationY == 0) {
                    translationY = 0;
                    topView.setTranslationY(translationY);
                } else if (translationY == -topView.getHeight()) {
                    translationY = -topView.getHeight();
                    topView.setTranslationY(translationY);
                    MotionEvent newEv = MotionEvent.obtain(ev);
                    newEv.setAction(MotionEvent.ACTION_DOWN);
                    parent.dispatchTouchEvent(newEv);
                    Log.i("TopViewBehavior", "转发" + newEv.hashCode());
                }
                topView.setTranslationY(translationY);
            }

            lastMoveX = ev.getX();
            lastMoveY = ev.getY();
        }
        return true;
    }

    /**
     * 是否需要拦截事件
     *
     * @param parent
     * @param child
     * @param dx
     * @param dy
     * @return true 自己消费 false 传递给子类
     */
    private boolean needIntercept(@NonNull CoordinatorLayout parent, @NonNull PDFView child, float dx, float dy) {
        View topView = parent.findViewById(R.id.topview);
        Log.d("TopViewBehavior", "  getPositionOffset = " + child.getPositionOffset());
        if (topView != null && child.getPositionOffset() == 0) {
            Direction direction = calDirection(dx, dy);
            switch (direction) {
                case DOWN:
                    Log.d("TopViewBehavior", " Down getTranslationY = " + topView.getTranslationY());
                    return topView.getTranslationY() < 0;
                case UP:
                    Log.d("TopViewBehavior", " UP getTranslationY = " + topView.getTranslationY());
                    return topView.getTranslationY() > -topView.getHeight();
                default:
                    return false;

            }
//            return (topView.getTranslationY() > -topView.getHeight() && direction == Direction.UP) ||
//                    (topView.getTranslationY() < 0 && direction == Direction.DOWN);
        }
        return false;
    }

    /**
     * 根据距离差判断是否移动
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoved(float dx, float dy) {
        boolean b = Math.abs(dx) >= MIN_MOVE || Math.abs(dy) >= MIN_MOVE;
        Log.d("TopViewBehavior", "isMoved:" + b);
        return b;
    }

    /**
     * 判断手势方向
     *
     * @param dx
     * @param dy
     * @return
     */
    private Direction calDirection(float dx, float dy) {

        return dy > 0 ? Direction.DOWN : Direction.UP;
    }
}
