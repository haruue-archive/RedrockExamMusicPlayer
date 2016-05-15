package moe.haruue.redrockexam.musicplayer.ui.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentBitmap;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlay;
import moe.haruue.redrockexam.musicplayer.ui.activity.MainActivity;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.notification.NotificationUtils;

public class MusicPlayService extends Service {

    MusicPlayBinder binder = new MusicPlayBinder();
    MediaPlayer player;
    Listener listener = new Listener();
    NotificationUtils notificationUtils;

    final static int NOTIFICATION_ID = 45532;


    public MusicPlayService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        MusicPlayerController.addToCurrentPlayMusicListeners(listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MusicPlayBinder extends Binder {

        public MediaPlayer getMediaPlayer() {
            return MusicPlayService.this.player;
        }

        public void cancelNotification() {
            notificationUtils.cancel();
        }

    }

    public void refreshNotification() {
        NotificationUtils.Builder builder = new NotificationUtils.Builder(StandardUtils.getApplication());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE);
        if (CurrentPlay.instance.data != null) {
            builder.setContentTitle(CurrentPlay.instance.data.songName);
            builder.setContentText(CurrentPlay.instance.data.singerName);
            if (CurrentBitmap.bitmap != null) {
                builder.setLargeIcon(CurrentBitmap.bitmap);
            } else {
                builder.setLargeIcon(R.drawable.default_album);
            }
        } else {
            builder.setContentTitle(getResources().getString(R.string.app_name));
            builder.setLargeIcon(R.drawable.default_album);
        }
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_music_note_white_24dp);
        notificationUtils = builder.build();
        notificationUtils.getNotification().flags = Notification.FLAG_ONGOING_EVENT;
        notificationUtils.setId(NOTIFICATION_ID);
        notificationUtils.show();
    }

    public class Listener implements MusicPlayerController.OnCurrentPlayMusicChangeListener {

        @Override
        public void onCurrentPlayMusicChange() {
            refreshNotification();
        }
    }


    public static void start(Context context) {
        Intent starter = new Intent(context, MusicPlayService.class);
        context.startService(starter);
    }

    public static void stop(Context context) {
        Intent stopper = new Intent(context, MusicPlayService.class);
        context.stopService(stopper);
    }

    public static void bind(Context context) {
        Intent binder = new Intent(context, MusicPlayService.class);
        context.bindService(binder, MusicPlayServiceConnection.getInstance(), BIND_AUTO_CREATE);
    }

    public static void unbind(Context context) {
        Intent unbinder = new Intent(context, MusicPlayService.class);
        context.unbindService(MusicPlayServiceConnection.getInstance());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationUtils.cancel();
        player.release();
    }
}
