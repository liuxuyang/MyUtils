package priv.liuxy.collagedemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import priv.liuxy.collagedemo.templates.CollageStyle1;
import priv.liuxy.collagedemo.templates.CollageStyle2;
import priv.liuxy.collagedemo.templates.CollageStyle3;
import priv.liuxy.collagedemo.templates.CollageStyle4;

public class MainActivity extends AppCompatActivity
        implements CollageLayout.CollageListener,
        FloatingLayout.onFloatingListener {

    private static final String TAG = "MainActivity";

    private CollageController mCollageLayout;
    private int[] bitmaps;
    private int[] icons;

    private TextView mStateTv;
    private TextView mErrorTv;
    private ImageView mResultView;
    private FloatingLayout mFloatingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCollageLayout = (CollageLayout) findViewById(R.id.collage_layout);
        mCollageLayout.setCollageListener(this);

        mFloatingMenu = (FloatingLayout) findViewById(R.id.floating_menu);

        mResultView = (ImageView) findViewById(R.id.result);
        mResultView.setVisibility(View.GONE);
        mStateTv = (TextView) findViewById(R.id.collage_state);
        mErrorTv = (TextView) findViewById(R.id.collage_error);
        bitmaps = new int[]{R.mipmap.img_1, R.mipmap.img_2, R.mipmap.img_3, R.mipmap.img_4};
        icons = new int[]{R.drawable.ic_collage_four,
                R.drawable.ic_collage_three_horizontal,
                R.drawable.ic_collage_two_horizontal,
                R.drawable.ic_collage_three_vertical};
        mCollageLayout.setStyle(CollageStyle1.STYLE_ID);
        mFloatingMenu.setItemDrawableIds(icons);
        mFloatingMenu.setFloatingListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollageLayout.reset();
    }

    public void merge(View view) {
        mCollageLayout.startMerge();
    }

    public void reset(View view) {
        mCollageLayout.reset();
        mResultView.setImageBitmap(null);
        mResultView.setVisibility(View.GONE);
    }

    @Override
    public void onMergeEnd(Bitmap bitmap) {
        if (mResultView.getVisibility() != View.VISIBLE) {
            mResultView.setVisibility(View.VISIBLE);
        }
        mResultView.setImageBitmap(bitmap);
    }

    @Override
    public void onError(String msg) {
        mErrorTv.setText("error : " + msg);
    }

    @Override
    public void onStateChange(int state) {
        String stateStr = "";
        switch (state) {
            case CollageController.STATE_IDLE:
                stateStr = "STATE_IDLE";
                break;
            case CollageController.STATE_CAPTURE:
                stateStr = "STATE_CAPTURE";
                break;
            case CollageController.STATE_CAPTURE_COMPLETE:
                stateStr = "STATE_CAPTURE_COMPLETE";
                break;
            case CollageController.STATE_EDIT:
                stateStr = "STATE_EDIT";
                break;
            case CollageController.STATE_DRAG:
                stateStr = "STATE_DRAG";
                break;
            /*case CollageController.STATE_ZOOM:
                stateStr = "STATE_ZOOM";
                break;
            case CollageController.STATE_ZOOM_DRAG:
                stateStr = "STATE_ZOOM_DRAG";
                break;*/
            case CollageController.STATE_MERGE:
                stateStr = "STATE_MERGE";
                break;
        }
        mStateTv.setText("state : " + stateStr);
    }

    public void add(View view) {
        int index = mCollageLayout.getCurCaptureIndex();
        if (index != CollageLayout.INVALID_INDEX && index < bitmaps.length) {
            mCollageLayout.addImage(bitmaps[index]);
        }
    }

    public void remove(View view) {
        mCollageLayout.removeImage();
    }

    @Override
    public void onFloatOpen() {
        Log.d(TAG, "onFloatOpen");
    }

    @Override
    public void onFloatClose() {
        Log.d(TAG, "onFloatClose");
    }

    @Override
    public void onItemClick(int index) {
        mCollageLayout.reset();
        Log.d(TAG, "onItemClick : " + index);
        switch (index) {
            case 0:
                mCollageLayout.setStyle(CollageStyle1.STYLE_ID);
                break;
            case 1:
                mCollageLayout.setStyle(CollageStyle2.STYLE_ID);
                break;
            case 2:
                mCollageLayout.setStyle(CollageStyle3.STYLE_ID);
                break;
            case 3:
                mCollageLayout.setStyle(CollageStyle4.STYLE_ID);
                break;
        }
    }
}
