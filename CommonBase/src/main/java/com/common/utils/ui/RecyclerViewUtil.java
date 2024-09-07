package com.common.utils.ui;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.View;

import com.common.utils.thread.UIHandler;
import com.common.utils.log.LogUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView工具类
 *
 * @author LiuFeng
 * @data 2021/11/12 17:04
 */
public class RecyclerViewUtil {

    /**
     * 判断RecyclerView是否显示最后一条消息
     *
     * @param recyclerView
     * @return
     */
    public static boolean isLastMessageVisible(RecyclerView recyclerView) {
        if (null == recyclerView || null == recyclerView.getAdapter()) {
            return false;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int itemCount = layoutManager.getItemCount();
        return lastVisiblePosition >= itemCount - 2;
    }

    /**
     * 位置上的item是否可见
     *
     * @param recyclerView
     * @param position
     * @return
     */
    public static boolean isVisible(RecyclerView recyclerView, int position) {
        if (null == recyclerView || null == recyclerView.getAdapter()) {
            return false;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int start = layoutManager.findFirstVisibleItemPosition();
        int end = layoutManager.findLastVisibleItemPosition();
        return position <= end && position >= start;
    }

    /**
     * 判断RecyclerView是否开启滚动到最后一条消息
     *
     * @param recyclerView
     * @return
     */
    public static boolean isScrollToBottom(RecyclerView recyclerView) {
        if (null == recyclerView || null == recyclerView.getAdapter()) {
            return false;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();
        if (itemCount - lastVisiblePosition < 5) {
            scrollToBottom(recyclerView);
            return true;
        }
        return false;
    }

    /**
     * 判断RecyclerView滚动到底部
     *
     * @param recyclerView
     * @return
     */
    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }

    /**
     * 设置RecyclerView滚动到底部
     *
     * @param recyclerView
     */
    public static void scrollToBottom(RecyclerView recyclerView) {
        if (null == recyclerView || null == recyclerView.getAdapter()) {
            return;
        }

        int lastPosition = recyclerView.getAdapter().getItemCount() - 1;
        scrollToPosition(recyclerView, lastPosition);
    }

    /**
     * 设置RecyclerView滚动到指定的位置
     *
     * @param position
     */
    public static void scrollToPosition(final RecyclerView recyclerView, final int position) {
        if (null == recyclerView || null == recyclerView.getAdapter() || position < 0) {
            return;
        }

        UIHandler.run(() -> {
            recyclerView.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (mLayoutManager != null) {
                mLayoutManager.scrollToPositionWithOffset(position, 0);
            }
        });
    }

    /**
     * 设置RecyclerView平滑滚动到指定的位置
     *
     * @param position
     */
    public static void smoothScrollToPosition(final RecyclerView recyclerView, final int position) {
        if (null == recyclerView || null == recyclerView.getAdapter() || position < 0) {
            return;
        }

        UIHandler.run(() -> recyclerView.smoothScrollToPosition(position));
    }

    /**
     * 设置RecyclerView滚动到指定的位置并背景2秒消失
     *
     * @param position
     */
    public static void scrollToPositionForBackground(final RecyclerView recyclerView, final int position) {
        if (null == recyclerView || null == recyclerView.getAdapter() || position < 0) {
            return;
        }

        // 先滚动到指定位置
        recyclerView.scrollToPosition(position);

        // 再设置位置item背景色
        recyclerView.postDelayed(() -> {
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (mLayoutManager != null) {
                int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                View childView = mLayoutManager.getChildAt(position - firstPosition);
                if (childView == null) {
                    LogUtil.e("无法定位到上下文，请稍后重试");
                } else {

                    childView.setBackgroundColor(Color.parseColor("#ffdae0e7"));

                    // 最后背景色1s后，重新设置成白色
                    recyclerView.postDelayed(() -> childView.setBackgroundColor(Color.parseColor("#00000000")), 1000);
                }
            }
        }, 100);
    }

    /**
     * 启动高亮透明动画
     *
     * @param recyclerView
     * @param position
     */
    public static void startHighLightAlphaAnimate(RecyclerView recyclerView, int position) {
        if (null == recyclerView || null == recyclerView.getAdapter() || position < 0) {
            return;
        }

        LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (mLayoutManager != null) {
            View view = mLayoutManager.findViewByPosition(position);
            if (view == null) return;

            ObjectAnimator mHighLightAnimator = ObjectAnimator.ofFloat(view, View.ALPHA.getName(), 1.0f, 0.3f, 1.0f);
            mHighLightAnimator.setDuration(1000);
            mHighLightAnimator.setRepeatCount(1);
            mHighLightAnimator.start();
        }
    }

    /**
     * 关闭动画
     *
     * @param recyclerView
     */
    public static void closeRecyclerViewAnimator(@NonNull RecyclerView recyclerView) {
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setAddDuration(0);
            itemAnimator.setChangeDuration(0);
            itemAnimator.setMoveDuration(0);
            itemAnimator.setRemoveDuration(0);
        }
    }
}
