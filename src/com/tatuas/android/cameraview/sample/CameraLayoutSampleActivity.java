package com.tatuas.android.cameraview.sample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tatuas.android.cameraview.AfterShutterListener;
import com.tatuas.android.cameraview.BeforeShutterListener;
import com.tatuas.android.cameraview.CameraFailedListener;
import com.tatuas.android.cameraview.CameraLayout;
import com.tatuas.android.cameraview.CameraType;
import com.tatuas.android.cameraview.CameraView;
import com.tatuas.android.cameraview.Options;
import com.tatuas.android.cameraview.Shutter;
import com.tatuas.android.cameraview.PictureSize;
import com.tatuas.android.cameraview.ShutterFailedListener;
import com.tatuas.android.cameraview.Thumbnail;
import com.tatuas.android.cameraview.PictureType;
import com.tatuas.android.cameraview.Util;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CameraLayoutSampleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);

        final String savePath = createSavePath();
        final String thumbSavePath = savePath + ".thumb.jpg";
        final Options options = new Options();

        final CameraLayout cameraLayout = (CameraLayout) findViewById(R.id.cameraLayout1);

        final CameraView cameraView = cameraLayout.getCameraView();
        cameraView.setCameraType(CameraType.BACK);
        cameraView.setCameraPreviewFocus(Camera.Parameters.FOCUS_MODE_AUTO);
        cameraView.setCameraFailedListener(new CameraFailedListener() {
            @Override
            public void onFailed(String errorMsg) {
                Toast.makeText(cameraView.getContext(), errorMsg, Toast.LENGTH_LONG)
                        .show();
            }
        });

        // size or calculateScale option uses last set param.
        options.setCalculateScale(4);
        options.setPictureSize(new PictureSize(1600, 1200));
        options.setPictureType(PictureType.JPEG);
        options.setQuality(60);
        options.setExecAutoFocusWhenShutter(true);

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
                cameraLayout.showPreview(savePath);
            }
        });

        shutter.setShutterFailedListener(new ShutterFailedListener() {
            @Override
            public void onFailed(String errorMessage) {
                Toast.makeText(cameraView.getContext(), "Failed shutter",
                        Toast.LENGTH_LONG).show();
            }
        });

        final Button shutterBtn = (Button) findViewById(R.id.cameraLayoutShotBtn);
        shutterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // shutter.exec(savePath);
                Thumbnail thumb = new Thumbnail(thumbSavePath, 12, Util
                        .getDisplayRotationValue((Activity) v.getContext()), CameraType.BACK);
                thumb.setFormat(CompressFormat.JPEG);
                thumb.setQuality(50);
                shutter.exec(savePath, thumb);

                v.setClickable(false);
            }
        });

        Button restartBtn = (Button) findViewById(R.id.cameraLayoutRestartBtn);
        restartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraLayout.removePreview();
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
