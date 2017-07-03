package com.xman.musicdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by nieyunlong on 17/6/28.
 */

public class SecondActivity extends AppCompatActivity {
    private SeekBar seekBar;
    public static final String TAG = SecondActivity.class.getSimpleName();
    private MusicBroadCast musicBroadCast;

    public boolean isConnected;
    public MusicService musicService;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
            if (myBinder != null) {
                musicService = myBinder.getService();
            }
            isConnected = true;
            LogUtils.e(TAG, "--->音乐服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e(TAG, "--->音乐服务连接失败");
            isConnected = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MUSIC_STATUS_ACTION);
        musicBroadCast = new MusicBroadCast();
        registerReceiver(musicBroadCast, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicBroadCast);
        unbindService(serviceConnection);
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
