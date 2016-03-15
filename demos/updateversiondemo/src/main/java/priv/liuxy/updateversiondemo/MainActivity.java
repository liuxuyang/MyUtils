package priv.liuxy.updateversiondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import priv.liuxy.utils.LogUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

    UpdateManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (R.id.start == v.getId()) {
                    if (manager == null) {
                        manager = new UpdateManager();
                    }
                    manager.checkUpdate();
                    manager.startUpdate();
                }
            }
        });
    }
}
