package com.xman.musicdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();

    private MusicService musicService;

    private String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/1.mp3";

    private boolean isConnected = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
            if (myBinder != null) {
                musicService = myBinder.getService();
            }
            isConnected = true;
            Log.i(TAG, "--->音乐服务连接成功" + musicPath);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "--->音乐服务连接失败");
            isConnected = false;
        }
    };
    private SeekBar seekBar;
    private MusicBroadCast musicBroadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicService.seekToPos(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MUSIC_STATUS_ACTION);
        musicBroadCast = new MainActivity.MusicBroadCast();
        registerReceiver(musicBroadCast, intentFilter);
    }


    public void add_music(View view) {
        if (musicService != null && isConnected) {
            musicService.onAddMusic(musicPath);
            seekBar.setMax(musicService.getDuration());
        }
    }

    public void pause_music(View view) {
        if (musicService != null && isConnected) {
            musicService.onPause();
        }
    }

    public void resume_music(View view) {
        if (musicService != null && isConnected) {
            musicService.onPause();
        }
    }

    public void start_second_activity(View view) {
        Intent intent = new Intent(MainActivity.this, LrcViewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        unregisterReceiver(musicBroadCast);
        super.onDestroy();
    }

    public class MusicBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicService.MUSIC_STATUS_ACTION.equals(intent.getAction())) {
                MusicMission musicMission = intent.getParcelableExtra(MusicService.MUSIC_STATUS_KEY);
                Log.e(TAG, "--->音乐" + musicMission.toString());
                seekBar.setMax(musicMission.getMaxDuration());
                seekBar.setProgress(musicMission.getCurrentDuration());
            }
        }
    }
}
