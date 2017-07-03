package com.xman.musicdemo;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by nieyunlong on 17/6/29.
 */

public class FileUtils {

    public static void writeFile(File originFile, String saveName) {
        String lrcDirPath = getLrcDir();
        File toFile = new File(lrcDirPath + File.separator + saveName);
        RandomAccessFile fos = null;
        FileInputStream inputStream = null;
        if (!toFile.exists()) {
            try {
                toFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 读取文件内容 (输入流)
            try {
                byte[] bytes = new byte[128];
                fos = new RandomAccessFile(originFile, "rw");
                inputStream = new FileInputStream(originFile);
                int count;
                while (((count = inputStream.read(bytes)) != -1)) { //读到头是-1
                    fos.write(bytes, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("File", "-->" + e.getMessage());
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void writeFile(InputStream inputStream, String saveName) {
        String lrcDirPath = getLrcDir();
        File file = new File(lrcDirPath + File.separator + saveName);
        RandomAccessFile fos = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 读取文件内容 (输入流)
            try {
                byte[] bytes = new byte[128];
                fos = new RandomAccessFile(file, "rw");
                int count;
                while (((count = inputStream.read(bytes)) != -1)) { //读到头是-1
                    fos.write(bytes, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取应用程序使用的本地目录
     *
     * @return
     */
    public static String getAppLocalDir() {
        String dir = null;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
            dir = Environment.getExternalStorageDirectory() + File.separator
                    + Config.MUSIC_DIR + File.separator;
        } else {
            dir = Appctx.getInstance().getFilesDir() + File.separator + Config.MUSIC_DIR + File.separator;
        }

        return mkdirs(dir);
    }

    /**
     * 获取歌词存放目录
     *
     * @return
     */
    public static String getLrcDir() {
        String lrcDir = getAppLocalDir() + Config.MUSIC_LRC_DIR + File.separator;
        return mkdirs(lrcDir);
    }

    /**
     * 创建文件夹
     *
     * @param dir
     * @return
     */
    public static String mkdirs(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            for (int i = 0; i < 5; i++) {
                if (f.mkdirs()) return dir;
            }
            return null;
        }

        return dir;
    }
}
