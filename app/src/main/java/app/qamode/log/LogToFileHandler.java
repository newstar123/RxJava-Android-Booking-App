package app.qamode.log;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;

public class LogToFileHandler {
    private static final String LOGS_PATH = "sdcard/";
    private static final String BACKGROUND_RANGING = "BACKGROUND_RANGING.txt";

    public static boolean addLog(String text) {
        if (QaModeCache.getQaModeLogs().get(BaseCacheType.BOOLEAN)) {
            File logFile = new File(getPath() + getFileName());
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                String time = "||" + System.currentTimeMillis() + "||";
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(time);
                buf.append(text);
                buf.newLine();
                buf.close();
                return true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
       // Log.d("TEST_LOG", text);
        return false;
    }

    private static String getPath() {
        String path = QaModeCache.getQaModeLogsPath().get(BaseCacheType.STRING);
        if (TextUtils.isEmpty(path)){
            QaModeCache.getQaModeLogsPath().save(BaseCacheType.STRING, LOGS_PATH);
            path = LOGS_PATH;
        }
        return path;
    }

    private static String getFileName() {
        String fileName = QaModeCache.getQaModeLogsFile().get(BaseCacheType.STRING);
        if (TextUtils.isEmpty(fileName)){
            QaModeCache.getQaModeLogsFile().save(BaseCacheType.STRING, BACKGROUND_RANGING);
            fileName = BACKGROUND_RANGING;
        }
        return fileName;
    }
}
