package yo.adbanner.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yo.draggablebanner.bean.BannerItem;
import com.yo.draggablebanner.ui.Banner;

import java.util.ArrayList;
import java.util.List;

import yo.adbanner.R;

/**
 * Created by Kaming on 2015/9/9.
 */
public class TouchActivity extends AppCompatActivity {

    private List<BannerItem> mItems = new ArrayList<>();
    private Banner mBanner;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
        initData();
        mBanner = (Banner) findViewById(R.id.banner);
        mText = (TextView) findViewById(R.id.textView);
        try {
            mBanner.setBannerData(mItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void obtain(View view) {
        mText.setText("");
        List<BannerItem> banners = mBanner.obtainChangedBanners();
        for (BannerItem banner : banners) {
            mText.append(banner.getTitle() + " ");
        }
    }

    private void initData() {
        Bitmap zero = createBitmapFromRes(R.drawable.zero);
        Bitmap one = createBitmapFromRes(R.drawable.one);
        Bitmap two = createBitmapFromRes(R.drawable.two);
        Bitmap three = createBitmapFromRes(R.drawable.three);
        Bitmap four = createBitmapFromRes(R.drawable.four);
        Bitmap[] bitArr = new Bitmap[]{zero, one, two, three, four};
        for (int i = 0; i < bitArr.length; i++) {
            mItems.add(new BannerItem("" + i, bitArr[i]));
        }
    }

    private Bitmap createBitmapFromRes(int resId) {
        return getSmallBitmap(resId);
    }

    public Bitmap getSmallBitmap(int bit) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(options, 1024, 800);
        return BitmapFactory.decodeResource(getResources(), bit, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 3;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


}
