package com.example.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (RotateImageView) findViewById(R.id.view);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    private RotateImageView mView;

    private int mRotate = 0;
    @Override
    public void onClick(View v) {
        mRotate += 90;
        if(mRotate >= 360) {
            mRotate = 0;
        }
        mView.setOrientation(mRotate,true);
    }
}
