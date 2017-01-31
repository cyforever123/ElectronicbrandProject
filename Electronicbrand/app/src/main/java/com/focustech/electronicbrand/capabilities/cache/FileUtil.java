package com.focustech.electronicbrand.capabilities.cache;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <文件公共类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class FileUtil {
    // 获取sdcard的目录
    public static String getSDPath(Context context) {
        // 判断sdcard是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获取根目录
            return  Environment.getExternalStorageDirectory().getPath();
        }
        return context.getFilesDir().getPath();
    }

    public static String createNewFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    // 复制文件
    public static void copyFile(InputStream inputStream, File targetFile)
            throws IOException {
        BufferedOutputStream outBuff = null;
        try {

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inputStream.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inputStream != null)
                inputStream.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    /**
     * 文件是否已存在
     *
     * @param file
     * @return
     */
    public static boolean isFileExit(File file) {
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 判断指定目录是否有文件存在
     *
     * @param path
     * @param fileName
     * @return
     */
    public static File getFiles(String path, String fileName) {
        File f = new File(path);
        File[] files = f.listFiles();
        if (files == null) {
            return null;
        }

        if (null != fileName && !"".equals(fileName)) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (fileName.equals(file.getName())) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * 根据文件路径获取文件名
     *
     * @return
     */
    public static String getFileName(String path) {
        if (path != null && !"".equals(path.trim())) {
            return path.substring(path.lastIndexOf("/"));
        }

        return "";
    }

    // 从asset中读取文件
    public static String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";

            while ((line = bufReader.readLine()) != null)
                result += line;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 删除目录（文件夹）下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static void deleteDirectory(String path) {
        File dirFile = new File(path);
        File[] files = dirFile.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                // 删除子文件
                if (files[i].isFile()) {
                    files[i].delete();
                }
                // 删除子目录
                else {
                    deleteDirectory(files[i].getAbsolutePath());
                }
            }
        }
    }

    // 保存序列化的对象到app目录
    public static void saveSeriObj(Context context, String fileName, Object o)
            throws Exception {

        String path = context.getFilesDir() + "/";

        File dir = new File(path);
        dir.mkdirs();

        File f = new File(dir, fileName);

        if (f.exists()) {
            f.delete();
        }
        FileOutputStream os = new FileOutputStream(f);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
        objectOutputStream.writeObject(o);
        objectOutputStream.close();
        os.close();
    }

    // 读取序列化的对象
    public static Object readSeriObject(Context context, String fileName)
            throws Exception {
        String path = context.getFilesDir() + "/";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(dir, fileName);
        InputStream is = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object o = objectInputStream.readObject();
        return o;
    }
}