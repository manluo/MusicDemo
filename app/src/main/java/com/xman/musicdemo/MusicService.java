package com.xman.musicdemo;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by nieyunlong on 17/6/28.
 * 音乐服务
 */

public class MusicService extends Service {

    private Handler handler = new Handler();
    public Runnable refreshUiTask = new Runnable() {
        @Override
        public void run() {
            setMusicStatus(musicMission, MusicMission.MusicStatus.MUSIC_STATUS_PLAYING);
            sendMusicBroadCast(musicMission);
            handler.postDelayed(refreshUiTask, 1000);
        }
    };

    public static final String TAG = MusicService.class.getSimpleName();

    public static final String MUSIC_STATUS_ACTION = "music_status_action";

    public static final String MUSIC_STATUS_KEY = "music_status_key";

    public final IBinder binder = new MyBinder();


    private MediaPlayer mediaPlayer;
    private MusicMission musicMission;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        musicMission = new MusicMission();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "---->开始播放==>onPrepared" + mp.getDuration() + ",当前进度" + mp.getCurrentPosition());
                mediaPlayer.start();
                handler.removeCallbacks(refreshUiTask);
                handler.post(refreshUiTask);
                setMusicStatus(musicMission, MusicMission.MusicStatus.MUSIC_STATUS_START);
                sendMusicBroadCast(musicMission);
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i(TAG, "---->正在播放==>onBufferingUpdate===>网络视频流缓冲变化时调用" + percent);
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.i(TAG, "---->正在播放==>setOnSeekCompleteListener===>seekTo调用");
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "---->播放完毕==>onCompletion");
                setMusicStatus(musicMission, MusicMission.MusicStatus.MUSIC_STATUS_COMPLETION);
                sendMusicBroadCast(musicMission);
                handler.removeCallbacks(refreshUiTask);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "---->播放错误==>onError" + what + ",extra" + extra);
                setMusicStatus(musicMission, MusicMission.MusicStatus.MUSIC_STATUS_ERROR);
                sendMusicBroadCast(musicMission);
                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void onAddMusic(String musicPath) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicPath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "--->con't find music" + e.getMessage());
            }
        }
    }

    public void onPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setMusicStatus(musicMission, MusicMission.MusicStatus.MUSIC_STATUS_PAUSE);
            sendMusicBroadCast(musicMission);
            handler.removeCallbacks(refreshUiTask);
            Log.e(TAG, "---->暂停==>pause");
        } else {
            Log.e(TAG, "---->恢复==>pause");
            setMusicStatus(musicMission, MusicMission.MusicStatus.MUSIC_STATUS_RESUME);
            sendMusicBroadCast(musicMission);
            mediaPlayer.start();
            handler.post(refreshUiTask);
        }
    }

    public void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取进度
     *
     * @return
     */
    public int getProgress() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekToPos(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    /**
     * 获取最大
     */
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.e(TAG, "--->服务挂掉==>unbindService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "--->服务挂掉==>onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            musicMission = null;
            handler.removeCallbacks(refreshUiTask);
        }
    }

    /**
     * 设置音乐状态
     *
     * @param musicMission
     * @param musicStatus
     */
    public void setMusicStatus(MusicMission musicMission, MusicMission.MusicStatus musicStatus) {
        if (musicMission != null && mediaPlayer != null) {
            musicMission.setMusicStatus(musicStatus);
            musicMission.setCurrentDuration(mediaPlayer.getCurrentPosition());
            musicMission.setMaxDuration(mediaPlayer.getDuration());
        }
    }

    public void sendMusicBroadCast(MusicMission musicMission) {
        Intent intent = new Intent();
        intent.putExtra(MUSIC_STATUS_KEY, musicMission);
        intent.setAction(MUSIC_STATUS_ACTION);
        sendBroadcast(intent);
    }
}
