package com.tatuas.android.cameraview.sample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tatuas.android.cameraview.AfterShutterListener;
import com.tatuas.android.cameraview.BeforeShutterListener;
import com.tatuas.android.cameraview.CameraFailedListener;
import com.tatuas.android.cameraview.CameraView;
import com.tatuas.android.cameraview.Options;
import com.tatuas.android.cameraview.Shutter;
import com.tatuas.android.cameraview.PictureSize;
import com.tatuas.android.cameraview.PictureType;
import com.tatuas.android.cameraview.ShutterFailedListener;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CameraViewSampleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        final String savePath = createSavePath();
        final Options options = new Options();

        final CameraView cameraView = (CameraView) findViewById(R.id.cameraView1);
        cameraView.setCameraPreviewFocus(Camera.Parameters.FOCUS_MODE_AUTO);
        cameraView.setCameraFailedListener(new CameraFailedListener() {
            @Override
            public void onFailed(String errorMessage) {
                Toast.makeText(cameraView.getContext(), errorMessage, Toast.LENGTH_LONG)
                        .show();
            }
        });

        // size or calculateScale option uses last set param.
        options.setCalculateScale(4);
        options.setPictureSize(new PictureSize(1600, 1200));
        options.setPictureType(PictureType.PNG);
        options.setQuality(100);
        options.setExecAutoFocusWhenShutter(false);
        options.setRestartPreviewAfterShutter(true);

        final Shutter shutter = new Shutter(cameraView, this, options);
        shutter.setBeforeShutterListener(new BeforeShutterListener() {
            @Override
            public void beforeShutter() {
                Toast.makeText(cameraView.getContext(), "Before shutter",
                        Toast.LENGTH_LONG).show();
            }
        });

        shutter.setAfterShutterListener(new AfterShutterListener() {
            @Override
            public void afterShutter() {
                Toast.makeText(cameraView.getContext(), "After shutter",
                        Toast.LENGTH_LONG).show();
            }
        });

        shutter.setShutterFailedListener(new ShutterFailedListener() {
            @Override
            public void onFailed(String errorMessage) {
                Toast.makeText(cameraView.getContext(), "Failed shutter",
                        Toast.LENGTH_LONG).show();
            }
        });

        final Button cameraViewShotBtn = (Button) findViewById(R.id.cameraViewShotBtn);
        cameraViewShotBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shutter.exec(savePath);
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
        String ext = ".png";
        String separator = File.separator;
        savePath = storage + separator + timeStamp + ext;
        return savePath;
    }
}
