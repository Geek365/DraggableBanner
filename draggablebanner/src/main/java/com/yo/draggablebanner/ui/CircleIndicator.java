package com.yo.draggablebanner.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.yo.draggablebanner.R;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * Indicator Widget
 * Created by Kaming on 2015/9/7.
 */
public class CircleIndicator extends LinearLayout implements OnPageChangeListener {

    /**
     * 默认Indicator大小
     */
    private static final int DEFAULT_INDICATOR_WIDTH = 5;
    /**
     * Indicator 居左
     */
    private static final int INDICATOR_GRAVITY_LEFT = 0;
    /**
     * Indicator 居中
     */
    private static final int INDICATOR_GRAVITY_CENTER = 1;
    /**
     * Indicator 居右
     */
    private static final int INDICATOR_GRAVITY_RIGHT = 2;
    private ViewPager mViewPager;
    /**
     * Indicator的外边框
     */
    private int mIndicatorMargin = -1;
    /**
     * Indicator 宽
     */
    private int mIndicatorWidth = -1;
    /**
     * Indicator 高
     */
    private int mIndicatorHeight = -1;
    /**
     * Indicator 动画
     */
    private int mAnimatorResId = R.animator.scale_with_alpha;
    /**
     * Indicator 反转动画
     */
    private int mAnimatorReverseResId = 0;
    /**
     * Indicator 背景
     */
    private int mIndicatorBackgroundResId = R.drawable.white_radius;
    /**
     * Indicator 位置
     */
    private int mGravity = -1;
    /**
     * Indicator 未选中背景
     */
    private int mIndicatorUnselectedBackgroundResId = R.drawable.white_radius;
    private int mCurrentPosition = 0;
    private int mIndicatorSize = 0;
    private int mPlayPosition = 0;
    private Animator mAnimatorOut;
    private Animator mAnimatorIn;

    public enum IndicatorGravity {
        LEFT(0), CENTER(1), RIGHT(2);
        int gravity;

        IndicatorGravity(int gravity) {
            this.gravity = gravity;
        }

        public int getGravity() {
            return gravity;
        }
    }

    public CircleIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        handleTypedArray(context, attrs);
        checkIndicatorConfig(context);
        layoutIndicator();
    }

    /**
     * 定位indicator位置
     */
    private void layoutIndicator() {
        switch (mGravity) {
            case INDICATOR_GRAVITY_LEFT:
                setGravity(Gravity.LEFT);
                break;
            case INDICATOR_GRAVITY_CENTER:
                setGravity(Gravity.CENTER);
                break;
            case INDICATOR_GRAVITY_RIGHT:
                setGravity(Gravity.RIGHT);
                break;
        }
    }

    /**
     * 检查属性值
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void checkIndicatorConfig(Context context) {
        mIndicatorMargin = (mIndicatorMargin < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorMargin;
        mIndicatorWidth = (mIndicatorWidth < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight = (mIndicatorHeight < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorHeight;
        mAnimatorResId = (mAnimatorResId == 0) ? R.animator.scale_with_alpha : mAnimatorResId;
        mAnimatorOut = AnimatorInflater.loadAnimator(context, mAnimatorResId);
        if (mAnimatorReverseResId == 0) {
            mAnimatorIn = AnimatorInflater.loadAnimator(context, mAnimatorResId);
            mAnimatorIn.setInterpolator(new ReverseInterpolator());
        } else {
            mAnimatorIn = AnimatorInflater.loadAnimator(context, mAnimatorResId);
        }
        mIndicatorBackgroundResId = (mIndicatorBackgroundResId == 0) ? R.drawable.white_radius : mIndicatorBackgroundResId;
        mIndicatorUnselectedBackgroundResId = (mIndicatorUnselectedBackgroundResId == 0) ? mIndicatorBackgroundResId : mIndicatorUnselectedBackgroundResId;
        mGravity = (mGravity == -1) ? IndicatorGravity.CENTER.getGravity() : mGravity;
    }

    /**
     * 通过代码创建 indicator
     */
    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin, int indicatorSize) {
        configureIndicator(indicatorWidth, indicatorHeight, indicatorMargin, indicatorSize, IndicatorGravity.CENTER,
                R.animator.scale_with_alpha, 0, R.drawable.white_radius, R.drawable.white_radius);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin, int indicatorSize, IndicatorGravity indicatorGravity,
                                   @AnimatorRes int animatorId, @AnimatorRes int animatorReverseId,
                                   @DrawableRes int indicatorBackgroundId,
                                   @DrawableRes int indicatorUnselectedBackgroundId) {

        mGravity = indicatorGravity.getGravity();
        mIndicatorWidth = indicatorWidth;
        mIndicatorHeight = indicatorHeight;
        mIndicatorMargin = indicatorMargin;
        mIndicatorSize = indicatorSize;
        mAnimatorResId = animatorId;
        mAnimatorReverseResId = animatorReverseId;
        mIndicatorBackgroundResId = indicatorBackgroundId;
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId;

        checkIndicatorConfig(getContext());
        layoutIndicator();
    }

    public void setViewPager(ViewPager viewPager,Banner banner){
        setViewPager(viewPager);
        banner.onPageSelected(viewPager.getCurrentItem());
    }

    /**
     * 设置indicator的ViewPager
     *
     * @param viewPager 自己定义的ViewPager
     */
    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mCurrentPosition = mViewPager.getCurrentItem();
        createIndicators(viewPager);
        mViewPager.removeOnPageChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        onPageSelected(mCurrentPosition);
    }

    /**
     * 创建indicator
     */
    private void createIndicators(ViewPager viewPager) {
        removeAllViews();
        if (viewPager.getAdapter() == null) {
            return;
        }
        int count = (mIndicatorSize == 0) ? viewPager.getAdapter().getCount() : mIndicatorSize;
        if (count <= 0) {
            return;
        }
        addIndicator(mIndicatorBackgroundResId, mAnimatorOut);
        for (int i = 1; i < count; i++) {
            addIndicator(mIndicatorUnselectedBackgroundResId, mAnimatorIn);
        }
    }

    /**
     * 添加indicator
     *
     * @param indicatorResId indicator背景资源
     * @param animator       indicator动画
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addIndicator(@DrawableRes int indicatorResId, Animator animator) {
        if (animator.isRunning()) {
            animator.end();
        }
        View indicator = new View(getContext());
        indicator.setBackgroundResource(indicatorResId);
        addView(indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) indicator.getLayoutParams();
        lp.leftMargin = mIndicatorMargin;
        lp.rightMargin = mIndicatorMargin;
        indicator.setLayoutParams(lp);
        animator.setTarget(indicator);
        animator.start();
    }


    /**
     * 处理所有自定义属性
     */
    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, -1);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_width, -1);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_height, -1);
        mAnimatorResId = typedArray.getResourceId(R.styleable.CircleIndicator_ci_animator, R.animator.scale_with_alpha);
        mAnimatorReverseResId = typedArray.getResourceId(R.styleable.CircleIndicator_ci_animator_reverse, 0);
        mIndicatorBackgroundResId = typedArray.getResourceId(R.styleable.CircleIndicator_ci_drawable, R.drawable.white_radius);
        mIndicatorUnselectedBackgroundResId = typedArray.getResourceId(R.styleable.CircleIndicator_ci_drawable_unselected, mIndicatorBackgroundResId);
        mGravity = typedArray.getInteger(R.styleable.CircleIndicator_ci_gravity, 1);
        typedArray.recycle();
    }

    /**
     * 设置 ViewPager 监听
     *
     * @param onPageChangeListener ViewPager监听
     */
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (mViewPager == null) {
            throw new NullPointerException("can not find ViewPager,setViewPager first!!!");
        }
        mViewPager.removeOnPageChangeListener(onPageChangeListener);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public int getPlayPosition() {
        return mPlayPosition;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onPageSelected(int position) {
        mPlayPosition = position;
        position = (mIndicatorSize <= 0) ? position : position % mIndicatorSize;
        if (mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() <= 0) {
            return;
        }
        if (mAnimatorIn.isRunning()) {
            mAnimatorIn.end();
        }
        if (mAnimatorOut.isRunning()) {
            mAnimatorOut.end();
        }
        View currentIndicator = getChildAt(mCurrentPosition);
        currentIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
        mAnimatorIn.setTarget(currentIndicator);
        mAnimatorIn.start();
        View selectedIndicator = getChildAt(position);
        selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId);
        mAnimatorOut.setTarget(selectedIndicator);
        mAnimatorOut.start();
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * dp --> px
     *
     * @param dpValue dp值
     * @return 转换后的px值
     */
    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class ReverseInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return Math.abs(1.0f - input);
        }

    }


}
