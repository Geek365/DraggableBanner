**Draggable Banner**
====================

A banner can be dragged 

SAMPLE:
-------

![SAMPLE](https://github.com/ng2Kaming/DraggableBanner/blob/master/img/draggable_banner_sample.gif)


Gradle
------
In your project:
> compile project(':draggablebanner')

USAGE:
-------


    <com.yo.draggablebanner.ui.Banner
            android:id="@+id/banner"
            app:banner_play_enable="true"
            app:banner_delay_time="2000"
            app:banner_period_time="2000"
            app:banner_thum_background_color="#cbccca"
            app:indicator_drawable="@drawable/test_grey_radius"
            app:indicator_drawable_unselected="@drawable/test_white_radius"
            android:layout_width="match_parent"
            android:layout_height="300dp" />

  
  

> Banner mBanner = (Banner) findViewById(R.id.banner);
> mBanner.setBannerData(mItems);
> 
> //get changed data...
> List<BannerItem> banners = mBanner.obtainChangedBanners();

More: app

## THANKS: ##

 - CircleIndicator
 - Android-ItemTouchHelper-Demo

