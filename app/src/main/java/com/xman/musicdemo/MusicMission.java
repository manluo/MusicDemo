package com.xman.musicdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nieyunlong on 17/6/28.
 */

public class MusicMission implements Parcelable {
    /**
     * 文件最大
     */
    private int maxDuration;
    /**
     * 当前进度
     */
    private int currentDuration;

    private MusicStatus musicStatus;

    public enum MusicStatus {

        MUSIC_STATUS_START, MUSIC_STATUS_PLAYING, MUSIC_STATUS_PAUSE, MUSIC_STATUS_RESUME, MUSIC_STATUS_COMPLETION, MUSIC_STATUS_ERROR;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxDuration);
        dest.writeInt(this.currentDuration);
        dest.writeInt(this.musicStatus == null ? -1 : this.musicStatus.ordinal());
    }

    public MusicMission() {
    }

    protected MusicMission(Parcel in) {
        this.maxDuration = in.readInt();
        this.currentDuration = in.readInt();
        int tmpMusicStatus = in.readInt();
        this.musicStatus = tmpMusicStatus == -1 ? null : MusicStatus.values()[tmpMusicStatus];
    }

    public static final Creator<MusicMission> CREATOR = new Creator<MusicMission>() {
        @Override
        public MusicMission createFromParcel(Parcel source) {
            return new MusicMission(source);
        }

        @Override
        public MusicMission[] newArray(int size) {
            return new MusicMission[size];
        }
    };

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int currentDuration) {
        this.currentDuration = currentDuration;
    }

    public MusicStatus getMusicStatus() {
        return musicStatus;
    }

    public void setMusicStatus(MusicStatus musicStatus) {
        this.musicStatus = musicStatus;
    }

    @Override
    public String toString() {
        return "MusicMission{" +
                "maxDuration=" + maxDuration +
                ", currentDuration=" + currentDuration +
                ", musicStatus=" + musicStatus +
                '}';
    }
}
