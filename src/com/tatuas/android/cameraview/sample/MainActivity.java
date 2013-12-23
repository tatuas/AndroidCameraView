package com.tatuas.android.cameraview.sample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tatuas.android.cameraview.AfterShutterListener;
import com.tatuas.android.cameraview.CameraLayout;
import com.tatuas.android.cameraview.Shutter;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
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
        // final String thumbSavePath = savePath + ".thumb.jpg";

        final CameraLayout cl = (CameraLayout) findViewById(R.id.cl);

        final Shutter shutter = new Shutter(cl.getCameraView(), this, null);
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
                // Thumbnail thumb = new Thumbnail(thumbSavePath, 8);
                // shutter.exec(savePath, thumb);
                shutter.exec(savePath);
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
