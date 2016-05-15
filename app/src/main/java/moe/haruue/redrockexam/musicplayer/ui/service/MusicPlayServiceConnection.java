package moe.haruue.redrockexam.musicplayer.ui.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class MusicPlayServiceConnection implements ServiceConnection {

    private MusicPlayServiceConnection() {

    }

    private static MusicPlayServiceConnection connection;

    private MusicPlayService.MusicPlayBinder binder;

    public static MusicPlayServiceConnection getInstance() {
        if (connection == null) {
            connection = new MusicPlayServiceConnection();
        }
        return connection;
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (MusicPlayService.MusicPlayBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public MusicPlayService.MusicPlayBinder getBinder() {
        return binder;
    }

    public static MediaPlayer getMediaPlayer() {
        return connection.binder.getMediaPlayer();
    }
}
