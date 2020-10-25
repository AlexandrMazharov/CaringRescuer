package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    SeekBar sbSensitivity;
    TextView tv_currentSensitivity;
    MediaPlayer mPlayer;
    Button mainBtn;
    AudioManager audioManager;


    SensorManager sensorManager;
    Sensor sensor;
    boolean isPresent = false;
    boolean isActive = false;

    float oldx;
    float oldy;
    float oldz;


    private int getRandomMusic() {
        int a = 0;
        int b = 3;
        int r = a + (int) (Math.random() * b);
        int ress[] = {R.raw.m1, R.raw.m2, R.raw.m3, R.raw.m4};
        return ress[r];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sbSensitivity = (SeekBar) findViewById(R.id.sbSensitivity);
        sbSensitivity.setProgress(50);
        tv_currentSensitivity = (TextView) findViewById(R.id.tv_currentSensitivity);
        this.tv_currentSensitivity.setText(String.valueOf(sbSensitivity.getProgress()));

        mainBtn = (Button) findViewById(R.id.btnMain);

        mPlayer = MediaPlayer.create(this, getRandomMusic());
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
            }
        });
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {

            isPresent = true;
            sensor = sensors.get(0);
        }
        sbSensitivity.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private float currentSensitivity = 5;
    SensorEventListener sel = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float delta = currentSensitivity;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (mPlayer.isPlaying()) {
            } else {
                if ((oldx - x) > delta || (oldy - y) > delta || (oldz - z) > delta) {
                    try {

                        play(getWindow().getDecorView().getRootView());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                oldx = x;
                oldy = y;
                oldz = z;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void mainClick(View view) throws InterruptedException {
        if (isPresent) {//stop

            if (isActive) {
                stopPlay();
                sensorManager.unregisterListener(sel);
                mainBtn.setText("START");
            } else {
                // start
                sensorManager.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                play(view);
                mainBtn.setText("STOP");

            }
            isActive = !isActive;

        }
    }

    //audio
    private void stopPlay() {
        mPlayer.stop();

        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);

        } catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void play(View view) throws InterruptedException {
        if (mPlayer.isPlaying()) {
        } else {
            stopPlay();
            mPlayer = MediaPlayer.create(this, getRandomMusic());
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlay();
                }
            });
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //

            mPlayer.start();
        }

    }

    public void pause(View view) {
        mPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer.isPlaying()) {
            stopPlay();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.tv_currentSensitivity.setText(String.valueOf(seekBar.getProgress()));
        this.currentSensitivity = seekBar.getProgress()/10;
        float res =  Math.abs(100-seekBar.getProgress()/10);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}