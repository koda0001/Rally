package com.example.rally;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.utils.DatabaseUtils;

public class BaseActivity extends AppCompatActivity {
    protected DatabaseUtils databaseUtils;
    private LoadingView loadingView;
    private FrameLayout rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = new FrameLayout(BaseActivity.this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        loadingView = new LoadingView(BaseActivity.this);
        databaseUtils = new DatabaseUtils(BaseActivity.this, loadingView);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(LayoutInflater.from(this).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        rootView.removeAllViews();
        rootView.addView(view, 0);
        rootView.addView(loadingView);
        super.setContentView(rootView);
    }
}
