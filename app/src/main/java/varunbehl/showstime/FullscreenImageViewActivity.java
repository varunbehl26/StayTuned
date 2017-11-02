package varunbehl.showstime;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import varunbehl.showstime.util.ImageUtil;

public class FullscreenImageViewActivity extends AppCompatActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image_view);
        final ImageView fullScreenImage = findViewById(R.id.fullscreen_content);
        Intent intent = getIntent();
        url = intent.getStringExtra("Image_Path");
        ImageUtil.loadImageWithFullScreen(this, fullScreenImage, url);

        if (intent.getIntExtra("orientartion", 0) == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        fullScreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new WallpapperLoadThread().start();

            }
        });

    }

    private class WallpapperLoadThread extends Thread {
        @Override
        public void run() {
            super.run();
            Bitmap result = null;
            try {
                result = Picasso.with(FullscreenImageViewActivity.this)
                        .load("http://image.tmdb.org/t/p/w780" + url)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullscreenImageViewActivity.this);
            try {
                wallpaperManager.setBitmap(result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
