package com.tatuas.android.cameraview.sample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tatuas.android.cameraview.AfterShutterListener;
import com.tatuas.android.cameraview.CameraLayout;
import com.tatuas.android.cameraview.Options;
import com.tatuas.android.cameraview.Shutter;
import com.tatuas.android.cameraview.Size;
import com.tatuas.android.cameraview.Thumbnail;
import com.tatuas.android.cameraview.Type;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap.CompressFormat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String savePath = createSavePath();
        final String thumbSavePath = savePath + ".thumb.jpg";

        final CameraLayout cl = (CameraLayout) findViewById(R.id.cl);

        final Options options = new Options();

        //size or calculateScale use after set
        options.setCalculateScale(4);
        options.setPictureSize(new Size(1600, 1200));

        options.setPictureType(Type.JPEG);
        options.setQuality(60);

//        final Shutter shutter = new Shutter(cl.getCameraView(), this);
        final Shutter shutter = new Shutter(cl.getCameraView(), this, options);
        shutter.setAfterShutterListener(new AfterShutterListener() {
            @Override
            public void afterShutter() {
                cl.showPreview(savePath);
            }
        });

        final Button shutterBtn = (Button) findViewById(R.id.button1);
        shutterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //shutter.exec(savePath);

                Thumbnail thumb = new Thumbnail(thumbSavePath, 12);
                thumb.setFormat(CompressFormat.JPEG);
                thumb.setQuality(50);
                shutter.exec(savePath, thumb);

                v.setClickable(false);
            }
        });

        Button restartBtn = (Button) findViewById(R.id.button2);
        restartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cl.removePreview();
                shutterBtn.setClickable(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public String createSavePath() {
        String savePath;
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String storage = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        String ext = ".jpg";
        String separator = File.separator;
        savePath = storage + separator + timeStamp + ext;
        return savePath;
    }
}
