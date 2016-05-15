package moe.haruue.redrockexam.musicplayer.ui.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import cn.com.caoyue.imageloader.ImageLoader;
import cn.com.caoyue.imageloader.ImageLoaderListener;
import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlay;
import moe.haruue.redrockexam.musicplayer.ui.util.MetricsUtils;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.notification.NotificationUtils;

public class MusicPlayService extends Service {

    MusicPlayBinder binder = new MusicPlayBinder();
    MediaPlayer player;
    RemoteViews contentView;
    Listener listener = new Listener();
    ImageView tempImageView;
    NotificationUtils notificationUtils;

    final static int NOTIFICATION_ID = 45532;


    public MusicPlayService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        NotificationUtils.Builder builder = new NotificationUtils.Builder(StandardUtils.getApplication());
        contentView = new RemoteViews(getPackageName(), R.layout.notification_content);
        builder.setContentView(contentView);
        notificationUtils = builder.build();
        notificationUtils.getNotification().flags = Notification.FLAG_ONGOING_EVENT;
        notificationUtils.setId(NOTIFICATION_ID);
        initContentView();
    }

    private void initContentView() {
        if (CurrentPlay.instance.data != null) {
            contentView.setTextViewText(R.id.notification_title, CurrentPlay.instance.data.songName);
            contentView.setTextViewText(R.id.notification_singer, CurrentPlay.instance.data.singerName);
            tempImageView = new ImageView(StandardUtils.getApplication());
            ViewGroup.LayoutParams params = tempImageView.getLayoutParams();
            params.width = MetricsUtils.dpToPx(70);
            params.height = MetricsUtils.dpToPx(70);
            ImageLoader.getInstance().loadImage(CurrentPlay.instance.data.albumPicSmall, tempImageView, listener);
        } else {
            contentView.setTextViewText(R.id.notification_title, StandardUtils.getApplication().getResources().getString(R.string.app_name));
            contentView.setTextViewText(R.id.notification_singer, "");
            contentView.setImageViewBitmap(R.id.notification_album_image, BitmapFactory.decodeResource(StandardUtils.getApplication().getResources(), R.drawable.default_album));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MusicPlayBinder extends Binder {

        public MediaPlayer getMediaPlayer() {
            return MusicPlayService.this.player;
        }

    }

    public class Listener implements ImageLoaderListener {

        @Override
        public void onImageLoadSuccess(String url) {
            tempImageView.setDrawingCacheEnabled(true);
            contentView.setImageViewBitmap(R.id.notification_album_image, ((BitmapDrawable) tempImageView.getDrawable()).getBitmap());
            tempImageView.setDrawingCacheEnabled(false);
            notificationUtils.show();
        }

        @Override
        public void onImageLoadFailure(String url, Throwable t) {

        }

        @Override
        public void onImageLoadCancel(String url) {

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
}
