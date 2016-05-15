package moe.haruue.redrockexam.musicplayer.ui.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.HashMap;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class DownloadServiceConnection implements ServiceConnection {

    HashMap<Integer, DownloadService.DownloadBinder> binderMap = new HashMap<>(0);

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
        binderMap.put(binder.signNumber, binder);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
