package com.xman.musicdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MUSIC_STATUS_ACTION);
        musicBroadCast = new MusicBroadCast();
        registerReceiver(musicBroadCast, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicBroadCast);
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
