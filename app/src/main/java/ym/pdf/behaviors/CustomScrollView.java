package ym.pdf.behaviors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import ym.pdf.R;

public class CustomScrollView extends NestedScrollView {
    float lastX;
    float lastY;
    private PDFView pdfView;
    private ViewGroup parent;
    private AppBarLayout appBarLayout;
    private int minCollapseHeight;
    private Direction direction;

    private enum Direction {
        UP, DOWN, OTHER
    }

    public CustomScrollView(@NonNull Context context) {
        super(context);
    }

    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (parent == null) {
            parent = (ViewGroup) getParent();
        } else {
            ViewGroup parent = (ViewGroup) getParent();
            if (this.parent != parent && parent != null) {
                this.parent = parent;
            }
        }
        if (appBarLayout == null && parent != null) {
            appBarLayout = (AppBarLayout) parent.findViewById(R.id.app_bar);
        } else if (appBarLayout != null && parent != null) {
            AppBarLayout layout = parent.findViewById(R.id.app_bar);
            if (layout != null && layout != appBarLayout) {
                appBarLayout = layout;
            }
        }
        if (pdfView == null) {
            pdfView = findViewById(R.id.pdfview);
        }
        if (appBarLayout != null) {
            int marginTop = 0;
            if (getLayoutParams() != null && getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
                marginTop = layoutParams.topMargin;
            }
            minCollapseHeight = appBarLayout.getHeight() - appBarLayout.getTotalScrollRange() + marginTop;
        }
    }

    /**
     * requestDisallowInterceptTouchEvent(true); 下面动
     * requestDisallowInterceptTouchEvent(false);上面动
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d("TopViewBehavior", "dispatchTouchEvent appBarLayout: getTotalScrollRange" + appBarLayout.getTotalScrollRange() + " appBarLayout height:" + appBarLayout.getHeight() + " appBarLayout " + appBarLayout.getMinimumHeightForVisibleOverlappingContent());
        StringBuilder stringBuilder = new StringBuilder("dispatchTouchEvent : offset" + pdfView.getPositionOffset() + " getTop: " + getTop());
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = ev.getX();
            lastY = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float x = ev.getX();
            float y = ev.getY();
            float dy = y - lastY;
            direction = calDirection(dy);
            // 移动过程中 到顶部下拉的逻辑
            if (direction == Direction.DOWN) {
                stringBuilder.append("移动过程中 下滑");
                if (pdfView.getPositionOffset() == 0) {
                    requestDisallowInterceptTouchEvent(false);
                    stringBuilder.append(" false ");
                } else {
                    requestDisallowInterceptTouchEvent(true);
                    stringBuilder.append(" true ");
                }

            } else if (direction == Direction.UP) {
                stringBuilder.append("移动过程中 上拉 ");
                if (getTop() == minCollapseHeight) {
                    stringBuilder.append("true");
                    requestDisallowInterceptTouchEvent(true);
                } else {
                    requestDisallowInterceptTouchEvent(false);
                    stringBuilder.append("false");
                }
            } else {
                stringBuilder.append("移动过程中 其他状态 ");
            }
            lastX = ev.getX();
            lastY = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            lastX = ev.getX();
            lastY = ev.getY();
        }

        Log.d("TopViewBehavior", stringBuilder.toString());

        return super.dispatchTouchEvent(ev);
    }

    /**
     * Get current direction.
     * @param dy
     * @return
     */
    private Direction calDirection(float dy) {
        return dy > 0 ? Direction.DOWN : Direction.UP;
    }
}
