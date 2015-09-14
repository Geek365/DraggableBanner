package com.yo.draggablebanner.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yo.draggablebanner.R;
import com.yo.draggablebanner.bean.BannerItem;
import com.yo.draggablebanner.listener.ItemTouchHelperAdapter;
import com.yo.draggablebanner.listener.ItemTouchHelperViewHolder;
import com.yo.draggablebanner.listener.OnStartDragListener;
import com.yo.draggablebanner.listener.SimpleItemTouchHelperCallback;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kaming on 2015/9/7.
 */
public class Banner extends RelativeLayout implements OnStartDragListener, ViewPager.OnPageChangeListener {
    /**
     * Banner轮播默认定时时间
     */
    private static final int DEFAULT_DELAY_TIME = 2000;
    /**
     * Banner轮播默认延迟时间
     */
    private static final int DEFAULT_PERIOD_TIME = 2000;
    /**
     * /**
     * Banner默认字体大小
     */
    private static final int DEFAULT_TEXT_SIZE = 20;
    /**
     * Banner默认字体颜色
     */
    private static final int DEFAULT_TEXT_COLOR = 0xffffff;
    /**
     * 默认Banner宽度
     */
    private static final int DEFAULT_BANNER_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * 默认Banner高度
     */
    private static final int DEFAULT_BANNER_HEIGHT = 200;
    /**
     * 默认缩略图背景颜色
     */
    private static final int DEFAULT_BANNER_THUM_BACKGROUND = Color.LTGRAY;
    /**
     * 默认缩略图大小
     */
    private static final int DEFAULT_BANNER_THUM_SIZE = 70;
    /**
     * 缩略图背景颜色
     */
    private int mThumBgColor;
    /**
     * 缩略图大小
     */
    private int mThumSize;
    /**
     * 延时时间
     */
    private int mDelayTime;
    /**
     * 定时时间
     */
    private int mPeriodTime;
    /**
     * 字体大小
     */
    private int mTextSize = -1;
    /**
     * 字体颜色
     */
    private int mTextColor = -1;
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
     * 是否播放Banner
     */
    private boolean mIsPlay;
    /**
     * Indicator 位置(泛型)
     */
    private CircleIndicator.IndicatorGravity mIndicatorGravity;
    /**
     * Indicator 未选中背景
     */
    private int mIndicatorUnselectedBackgroundResId = R.drawable.white_radius;
    /**
     * Banner 数据集合
     */
    private List<BannerItem> mBanners;
    /**
     * Indicator
     */
    private CircleIndicator mCircleIndicator;

    private RecyclerView mDraggableGrid;
    /**
     * 定时器
     */
    private Timer mTimer = new Timer();
    /**
     * 是否用户触摸
     */
    private boolean isUserTouched = false;
    private ViewPager mViewPager;
    private Context context;
    /**
     * Banner宽
     */
    private int mBannerWidth;
    /**
     * Banner 高
     */
    private int mBannerHeight;
    /**
     * ViewPager index
     */
    private int mPosition;

    private BannerPagerAdapter mBannerPagerAdapter;

    private ItemTouchHelper itemTouchHelper;

    private TimerTask mPlayTimerTask;

    private int mFromPosition;

    private int mToPosition;

    private RecyclerListAdapter mRecyclerListAdapter;

    private float startX;

    private float endX;


    public Banner(Context context) {
        super(context);
        init(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        handleTypedArray(context, attrs);
        this.context = context;
    }

    /**
     * 处理自定义参数
     */
    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.Banner_banner_text_size, dip2px(DEFAULT_TEXT_SIZE));
        mTextColor = typedArray.getColor(R.styleable.Banner_banner_text_color, DEFAULT_TEXT_COLOR);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_margin, -1);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_width, -1);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_height, -1);
        mAnimatorResId = typedArray.getResourceId(R.styleable.Banner_indicator_animator, R.animator.scale_with_alpha);
        mAnimatorReverseResId = typedArray.getResourceId(R.styleable.Banner_indicator_animator_reverse, 0);
        mIndicatorBackgroundResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable, R.drawable.white_radius);
        mIndicatorUnselectedBackgroundResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable_unselected, mIndicatorBackgroundResId);
        mBannerWidth = typedArray.getDimensionPixelSize(R.styleable.Banner_banner_width, DEFAULT_BANNER_WIDTH);
        mBannerHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_banner_height, dip2px(DEFAULT_BANNER_HEIGHT));
        mThumSize = typedArray.getDimensionPixelSize(R.styleable.Banner_banner_thum_size, dip2px(DEFAULT_BANNER_THUM_SIZE));
        mThumBgColor = typedArray.getColor(R.styleable.Banner_banner_thum_background_color, DEFAULT_BANNER_THUM_BACKGROUND);
        mPeriodTime = typedArray.getInt(R.styleable.Banner_banner_period_time, DEFAULT_PERIOD_TIME);
        mDelayTime = typedArray.getInt(R.styleable.Banner_banner_delay_time, DEFAULT_DELAY_TIME);
        mGravity = typedArray.getInt(R.styleable.Banner_indicator_gravity, 1);
        mIsPlay = typedArray.getBoolean(R.styleable.Banner_banner_play_enable, false);
        switch (mGravity) {
            case 0:
                mIndicatorGravity = CircleIndicator.IndicatorGravity.LEFT;
                break;
            case 1:
                mIndicatorGravity = CircleIndicator.IndicatorGravity.CENTER;
                break;
            case 2:
                mIndicatorGravity = CircleIndicator.IndicatorGravity.RIGHT;
                break;
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBannerHeight = MeasureSpec.getSize(widthMeasureSpec);
        mBannerWidth = MeasureSpec.getSize(heightMeasureSpec);
    }

    /**
     * 获取转换后的图像列表
     */
    public List<BannerItem> obtainChangedBanners() {
        return this.mBanners;
    }

    /**
     * 开启Banner轮播
     */
    public void startPlayBanner() {
        if (mBanners == null || mBanners.size() <= 0) {
            throw new NullPointerException("banner list must be not null,please setBannerData() firstly.");
        }
        if (mIsPlay) {
            startPlayBanner(mDelayTime, mPeriodTime);
        }
    }

    /**
     * 开启Banner轮播
     *
     * @param delayTime  推迟时间
     * @param periodTime 定时时间
     */
    public void startPlayBanner(int delayTime, int periodTime) {
        mPlayTimerTask = new PlayTimerTask();
        mTimer.schedule(mPlayTimerTask, delayTime, periodTime);
    }

    /**
     * 停止Banner轮播
     */
    public void stopPlayBanner() {
        if (mPlayTimerTask != null) {
            mPlayTimerTask.cancel();
            mPlayTimerTask = null;
        }
    }

    /**
     * 设置Banner数据
     *
     * @param banners Banner数据
     */
    public void setBannerData(List<BannerItem> banners) throws Exception {
        if (banners == null && banners.size() == 0) {
            throw new NullPointerException("The banners must be not null,and size > 0");
        }
        if (banners.size() > 5) {
            throw new Exception("The banners must be size <= 5");
        } else {
            this.mBanners = banners;
            removeAllViews();
            initView();
        }
    }


    /**
     * 初始化控件布局
     */
    private void initView() {
        RelativeLayout bannerFrame = new RelativeLayout(context);
        bannerFrame.setId(R.id.banner);
        LayoutParams bannerLP = new LayoutParams(mBannerWidth, mBannerHeight);
        bannerFrame.setLayoutParams(bannerLP);

        mViewPager = new ViewPager(context);
        bannerFrame.addView(mViewPager, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mCircleIndicator = new CircleIndicator(context);
        mCircleIndicator.configureIndicator(mIndicatorWidth, mIndicatorHeight, mIndicatorMargin,
                mBanners.size(), mIndicatorGravity, mAnimatorResId, mAnimatorReverseResId, mIndicatorBackgroundResId,
                mIndicatorUnselectedBackgroundResId);
        //底部透明
//        LinearLayout bottomLinear = new LinearLayout(context);
//        bottomLinear.setBackgroundColor(0xffffff);
//
//        LayoutParams bottomLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBannerHeight / 3);
//        bottomLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        LayoutParams indicatorLP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(20));
        indicatorLP.addRule(ALIGN_PARENT_BOTTOM);
        indicatorLP.bottomMargin = dip2px(2);

//        bottomLinear.addView(mCircleIndicator, bottomLp);
//        addView(bottomLinear, lp);

        bannerFrame.addView(mCircleIndicator, indicatorLP);

        addView(bannerFrame);

        mDraggableGrid = new RecyclerView(context);
        mDraggableGrid.setHasFixedSize(true);

        RelativeLayout.LayoutParams recyclerLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerLP.topMargin = dip2px(5);
        recyclerLP.addRule(BELOW, R.id.banner);

        addView(mDraggableGrid, recyclerLP);
        initRecyclerTouch(mDraggableGrid);
        initViewPager(mViewPager);
        mCircleIndicator.setViewPager(mViewPager);

        startPlayBanner();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        onPageSelected(mViewPager.getCurrentItem());
                    }
                });
            }
        }, 500);
    }

    private void initViewPager(ViewPager viewPager) {
        mBannerPagerAdapter = new BannerPagerAdapter();
        viewPager.setAdapter(mBannerPagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN
                        || action == MotionEvent.ACTION_MOVE) {
                    isUserTouched = true;
                } else if (action == MotionEvent.ACTION_UP) {
                    isUserTouched = false;
                }

                return false;
            }
        });
        viewPager.addOnPageChangeListener(this);
    }

    private void initRecyclerTouch(RecyclerView recyclerView) {
        mRecyclerListAdapter = new RecyclerListAdapter(context, this);
        recyclerView.setAdapter(mRecyclerListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, mBanners.size());
        recyclerView.setLayoutManager(gridLayoutManager);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mRecyclerListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * dip -> px
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RelativeLayout fromView = (RelativeLayout) mDraggableGrid.getChildAt(mFromPosition);
        if (fromView != null) {
            fromView.setBackgroundColor(0);
        }

        RelativeLayout toView = (RelativeLayout) mDraggableGrid.getChildAt(position);
        if (toView != null) {
            toView.setBackgroundColor(mThumBgColor);
        }
        mFromPosition = position;
        mRecyclerListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class PlayTimerTask extends TimerTask {

        @Override
        public void run() {
            if (!isUserTouched) {
                final int indicatorPos = mCircleIndicator.getPlayPosition();
                mPosition = (indicatorPos + 1) % mBanners.size();
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mPosition == mBanners.size() - 1) {
                            mViewPager.setCurrentItem(mBanners.size() - 1, false);
                        } else {
                            mViewPager.setCurrentItem(mPosition);
                        }
                    }
                });
            }
        }
    }

    private class BannerPagerAdapter extends PagerAdapter {

        /**
         * ImageView 数组
         */
        private ImageView[] mImages;

        private BannerPagerAdapter() {
            mImages = new ImageView[mBanners.size()];
            for (int i = 0; i < mImages.length; i++) {
                ImageView bannerImg = new ImageView(context);
                Bitmap bitImg = mBanners.get(i).getImage();
                bannerImg.setImageBitmap(bitImg);
                bannerImg.setScaleType(ImageView.ScaleType.FIT_XY);
                mImages[i] = bannerImg;
            }
        }

        @Override
        public int getCount() {
            return mBanners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(mImages[position], 0);
            } catch (Exception e) {
                //TODO
            }
            return mImages[position];
        }
    }


    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
            implements ItemTouchHelperAdapter {


        private final OnStartDragListener mDragStartListener;

        public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener) {
            mDragStartListener = dragStartListener;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            holder.handleView.setImageBitmap(mBanners.get(position).getImage());

            // Start a drag whenever the handle view it touched
            holder.handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mToPosition = position;
                        mDragStartListener.onStartDrag(holder);
                    }

                    return false;
                }
            });
        }

        @Override
        public void onItemDismiss(int position) {
            mBanners.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mBanners, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            mFromPosition = fromPosition;
            mToPosition = toPosition;
            return true;
        }

        @Override
        public int getItemCount() {
            return mBanners.size();
        }

        /**
         * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
         * "handle" view that initiates a drag event when touched.
         */
        public class ItemViewHolder extends RecyclerView.ViewHolder implements
                ItemTouchHelperViewHolder {

            public final ImageView handleView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                handleView = (ImageView) itemView.findViewById(R.id.img);
                handleView.setMaxWidth(mThumSize);
                handleView.setMaxHeight(mThumSize);
            }

            @Override
            public void onItemSelected() {
                stopPlayBanner();
                RelativeLayout fromRelative = (RelativeLayout) mDraggableGrid.getChildAt(mFromPosition);
                fromRelative.setBackgroundColor(0);
                itemView.setBackgroundColor(mThumBgColor);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
                mBannerPagerAdapter = new BannerPagerAdapter();
                mViewPager.setAdapter(mBannerPagerAdapter);
                if (mToPosition == 0) {
                    onPageSelected(0);
                    mCircleIndicator.onPageSelected(0);
                }
                mViewPager.setCurrentItem(mToPosition);
                startPlayBanner();
            }

        }
    }

}
