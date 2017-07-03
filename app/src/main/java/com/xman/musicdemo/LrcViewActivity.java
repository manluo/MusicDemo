package com.xman.musicdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xman.musicdemo.view.ILrcBuilder;
import com.xman.musicdemo.view.ILrcViewListener;
import com.xman.musicdemo.view.impl.DefaultLrcBuilder;
import com.xman.musicdemo.view.impl.LrcRow;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by nieyunlong on 17/6/29.
 */

public class LrcViewActivity extends AppCompatActivity {
    public static final String TAG = LrcViewActivity.class.getSimpleName();
    private LrcView play_first_lrc;
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
    private com.xman.musicdemo.view.impl.LrcView mLrcView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        String lrc = getFromAssets("dawang.lrc");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MUSIC_STATUS_ACTION);
        musicBroadCast = new LrcViewActivity.MusicBroadCast();
        registerReceiver(musicBroadCast, intentFilter);
//        play_first_lrc = (LrcView) findViewById(R.id.play_first_lrc);
        mLrcView = (com.xman.musicdemo.view.impl.LrcView) findViewById(R.id.lrcView);
        //解析歌词构造器
        ILrcBuilder builder = new DefaultLrcBuilder();
        //解析歌词返回LrcRow集合
        List<LrcRow> rows = builder.getLrcRows(lrc);
        //将得到的歌词集合传给mLrcView用来展示
        mLrcView.setLrc(rows);
//        mLrcView.setBackground(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                LogUtils.i(TAG, "onLrcSeeked:" + row.time);
                musicService.seekToPos((int) row.time);
            }
        });
//        setLrc();
    }

    /**
     * 从assets目录下读取歌词文件内容
     *
     * @param fileName
     * @return
     */
    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setLrc() {
        play_first_lrc.setLrcPath(Environment.getExternalStorageDirectory() + File.separator + Config.MUSIC_DIR + File.separator + Config.MUSIC_LRC_DIR + "/dawang.lrc");
//        InputStream inputStream = getClass().getResourceAsStream("/assets/dawang" + ".lrc");
//        FileUtils.writeFile(inputStream, "dawang.lrc");
//        try {
//            String path = new String(inputStreamToByte(inputStream));
//            LogUtils.i(TAG, "--->path" + path);
//            play_first_lrc.setLrcPath(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private byte[] inputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
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
//                Log.e(TAG, "--->音乐" + musicMission.toString());
//                seekBar.setMax(musicMission.getMaxDuration());
//                seekBar.setProgress(musicMission.getCurrentDuration());
//                setLrc();
//                play_first_lrc.changeCurrent(musicMission.getCurrentDuration());
                mLrcView.seekLrcToTime(musicMission.getCurrentDuration());
            }
        }
    }
}
