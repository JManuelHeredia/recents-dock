package com.uselessapps.recentsdock;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hoko.blur.drawable.BlurDrawable;

public class FloatingDockService extends Service {
    private WindowManager mWindowManager;
    private View mDockView;

    public FloatingDockService(){

    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        mDockView = LayoutInflater.from(this).inflate(R.layout.floating_dock, null);

//        BlurDrawable blurDrawableDock = new BlurDrawable();
//        blurDrawableDock.mixColor(getResources().getColor(R.color.transparent_black));
//        blurDrawableDock.mixPercent(1.0f);
//        blurDrawableDock.enableBlur();
//        View dockView = mDockView.findViewById(R.id.root_container);
//
//        dockView.setBackground(blurDrawableDock);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.y = 160;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mDockView, params);

        //========================================================================================

        final View collapsedDockView = mDockView.findViewById(R.id.collapse_view);

        final View expandedDockView = mDockView.findViewById(R.id.expanded_dock_container);

        //Close-Collapse Button

        ImageView collapseDockButton = (ImageView) mDockView.findViewById(R.id.dock_close_buton);
        collapseDockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedDockView.setVisibility(View.VISIBLE);
                expandedDockView.setVisibility(View.GONE);
                params.gravity = Gravity.TOP | Gravity.RIGHT;
                mWindowManager.updateViewLayout(mDockView, params);
            }
        });

        ImageView collapsedDock = (ImageView) mDockView.findViewById(R.id.collapsed_dock_view);
        collapsedDock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedDockView.setVisibility(View.GONE);
                expandedDockView.setVisibility(View.VISIBLE);
                params.gravity = Gravity.TOP | Gravity.CENTER;
                mWindowManager.updateViewLayout(mDockView, params);
            }
        });
    }

    private boolean isViewCollapsed() {
        return mDockView == null || mDockView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mDockView != null) mWindowManager.removeView(mDockView);
    }
}
