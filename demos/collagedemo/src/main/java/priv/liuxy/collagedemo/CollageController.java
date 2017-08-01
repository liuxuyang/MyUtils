package priv.liuxy.collagedemo;

import android.graphics.Bitmap;

/**
 * Created by xuyang.liu on 17-7-5.
 */

public interface CollageController {

    int STATE_IDLE = 0;
    int STATE_CAPTURE = STATE_IDLE + 1;
    int STATE_CAPTURE_COMPLETE = STATE_CAPTURE + 1;
    int STATE_EDIT = STATE_CAPTURE_COMPLETE + 1;
    int STATE_DRAG = STATE_EDIT + 1;
    int STATE_MERGE = STATE_DRAG + 1;

    void setState(int state);

    int getState();

    void setCollageListener(CollageListener listener);

    String getStyle();

    void setStyle(String style);

    int getCurCaptureIndex();

    boolean removeImage();

    boolean addImage(int id);

    boolean addImage(Bitmap bitmap);

    void startMerge();

    void reset();

    interface CollageListener {
        void onMergeEnd(Bitmap bitmap);

        void onError(String msg);

        void onStateChange(int state);
    }
}
