package com.uselessapps.recentsdock;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.hoko.blur.drawable.BlurDrawable;

public class MainActivity extends AppCompatActivity {

//    private ValueAnimator mAnimator;
    private long backPressTime;
    private Toast backToast;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //ALERT WINDOW PERMISSION
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }

        //STORAGE PERMISSION
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        } else {
            //You already have the permission, just go ahead.
            ImageView wallpaperView = findViewById(R.id.user_wallpaper);

            final WallpaperManager userWallpaper = WallpaperManager.getInstance(this);
            final Drawable wallpaperDrawable = userWallpaper.getDrawable();

            wallpaperView.setBackground(wallpaperDrawable);

            BlurDrawable blurDrawable = new BlurDrawable();
            blurDrawable.radius(10);
            blurDrawable.sampleFactor(4.0f);
//            View view = findViewById(R.id.test_view);
//            view.setBackground(blurDrawable);
//            view.setRadius(40);



//            GradientDrawable gradientDrawable = new GradientDrawable();
//            gradientDrawable.setColor(getResources().getColor(R.color.transparent_black));
//            gradientDrawable.setCornerRadius(128);
//            LayerDrawable finalDrawable = new LayerDrawable(new Drawable[]{blurDrawable, gradientDrawable});
//            view.setBackground(finalDrawable);
        }
    }
    /**
     * Set and initialize the view elements.
     */
    private void initializeView() {
        findViewById(R.id.start_service_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, FloatingDockService.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if(backPressTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "str_press_back_again", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressTime = System.currentTimeMillis();
    }
}