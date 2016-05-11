package priv.liuxy.scanfilesdemo;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Liuxy
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int WHAT_SACN_RESULT = 0;

    private AppCompatButton btnStart, btnSuspend, btnCancel, btnResume;

    private TextView mResultView;
    //单线程池
    private ExecutorService mSinglePool;
    //搜索根目录
    private String mRoot;
    //遍历任务
    private ScanTask mScanTask;
    //
    private Future future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResultView = (TextView) findViewById(R.id.result);

        btnStart = (AppCompatButton) findViewById(R.id.start);
        btnSuspend = (AppCompatButton) findViewById(R.id.suspend);
        btnCancel = (AppCompatButton) findViewById(R.id.cancel);
        btnResume = (AppCompatButton) findViewById(R.id.resume);

        btnStart.setOnClickListener(this);
        btnSuspend.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnResume.setOnClickListener(this);

        mSinglePool = Executors.newSingleThreadExecutor();
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        mScanTask = new ScanTask(mHandler, mRoot);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start:
                if (mScanTask != null) {
                    if (future == null || future.isDone()) {
                        future = mSinglePool.submit(mScanTask);
                    } else {
                        Toast.makeText(this, "task is running!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.suspend:
                mScanTask.requestSuspend();
                break;
            case R.id.resume:
                mScanTask.requestResume();
                break;
            case R.id.cancel:
                if (future != null && !future.isDone()) {
                    Toast.makeText(this,
                            future.cancel(true) ? "cancel success!" : "cancel fail!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SACN_RESULT:
                    String result = (String) msg.obj;
                    if (!TextUtils.isEmpty(result)) {
                        mResultView.setText(result);
                        mScanTask.releaseSemaphore();
                    }
                    break;
            }
        }
    };
}
