package moe.haruue.redrockexam.musicplayer.ui.service;

import android.accounts.NetworkErrorException;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.StringRes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.database.helper.MusicDatabaseHelper;
import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.ThreadUtils;
import moe.haruue.redrockexam.util.network.NetworkConfiguration;
import moe.haruue.redrockexam.util.network.Request;
import moe.haruue.redrockexam.util.notification.NotificationUtils;

public class DownloadService extends Service {

    Handler handler;

    Listener listener = new Listener();

    int serialNumber = 60000;

    HashMap<Integer, Thread> threadMap = new HashMap<>(0);
    HashMap<Integer, DownloadStatus> statusMap = new HashMap<>(0);

    public DownloadService() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        handler = new Handler(getMainLooper());
        SongModel model = (SongModel) intent.getSerializableExtra("model");
        String filePath = intent.getStringExtra("filePath");
        downloadMusic(model, filePath);
    }

    @Override
    public IBinder onBind(Intent intent) {
        handler = new Handler(getMainLooper());
        SongModel model = (SongModel) intent.getSerializableExtra("model");
        String filePath = intent.getStringExtra("filePath");
        int signNumber = downloadMusic(model, filePath);
        return new DownloadBinder().setSignNumber(signNumber);
    }

    public class DownloadStatus {
        public int fileSize = 0;
        public int downloadedSize = 0;
        public boolean isComplate = false;
        public boolean isFailed = false;
    }


    public int downloadMusic(final SongModel data, final String filePath) {
        final int signNumber = serialNumber++;
        final DownloadStatus status = new DownloadStatus();
        statusMap.put(signNumber, status);
        threadMap.put(signNumber, ThreadUtils.runOnNewThread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(data.downUrl, new HashMap<String, Object>(0), new NetworkConfiguration().setRequestMethod("GET").setConnectTimeout(10000).setReadTimeout(10000));
                try {
                    Request.Streams stream = request.getConnectionStreams();
                    status.fileSize = stream.connection.getContentLength();
                    if (stream.in == null) {
                        toast(R.string.in_stream_null);
                        status.isFailed = true;
                        return;
                    }
                    File file = new File(filePath);
                    if (!file.getParentFile().exists() && file.mkdirs()) {
                        toast(R.string.file_system_error);
                        status.isFailed = true;
                        return;
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte buf[] = new byte[1024];
                    status.downloadedSize = 0;
                    do
                    {
                        int numread = stream.in.read(buf);
                        if (numread == -1)
                        {
                            break;
                        }
                        fileOutputStream.write(buf, 0, numread);
                        status.downloadedSize += numread;
                    } while (true);
                    status.isComplate = true;
                    stream.closeAll();
                } catch (NetworkErrorException e) {
                    StandardUtils.printStack(e);
                    toast(R.string.network_error);
                    status.isFailed = true;
                } catch (IOException e) {
                    toast(R.string.file_system_error);
                    StandardUtils.printStack(e);
                    status.isFailed = true;
                } catch (Exception e) {
                    StandardUtils.printStack(e);
                    toast(R.string.unknow_exception);
                    status.isFailed = true;
                }
            }
        }));
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!status.isComplate && !status.isFailed) {
                    NotificationUtils.Builder builder = new NotificationUtils.Builder(StandardUtils.getApplication())
                            .setSmallIcon(R.drawable.ic_get_app_white_24dp)
                            .setLargeIcon(R.drawable.ic_get_app_black_24dp)
                            .setContentTitle(getResources().getString(R.string.downloading) + ": " + data.songName)
                            .setContentText(status.fileSize == 0 ? "0%" : (int) (((double) status.downloadedSize / (double) status.fileSize) * 100) + "%");
                    NotificationUtils notificationUtils = builder.build();
                    notificationUtils.setId(signNumber);
                    notificationUtils.getNotification().flags = Notification.FLAG_ONGOING_EVENT;
                    notificationUtils.show();
                    handler.postDelayed(this, 1000);
                } else {
                    if (status.isComplate) {
                        NotificationUtils.Builder builder = new NotificationUtils.Builder(StandardUtils.getApplication())
                                .setSmallIcon(R.drawable.ic_get_app_white_24dp)
                                .setLargeIcon(R.drawable.ic_check_black_24dp)
                                .setContentTitle(getResources().getString(R.string.downloading) + ": " + data.songName)
                                .setContentText(getResources().getString(R.string.download_complete));
                        NotificationUtils notificationUtils = builder.build();
                        notificationUtils.setId(signNumber);
                        notificationUtils.getNotification().flags = Notification.FLAG_AUTO_CANCEL;
                        notificationUtils.show();
                        data.file = filePath;
                        MusicDatabaseHelper.update(data, listener, serialNumber);
                    } else if (status.isFailed) {
                        NotificationUtils.Builder builder = new NotificationUtils.Builder(StandardUtils.getApplication())
                                .setSmallIcon(R.drawable.ic_get_app_white_24dp)
                                .setLargeIcon(R.drawable.ic_clear_black_24dp)
                                .setContentTitle(getResources().getString(R.string.downloading) + ": " + data.songName)
                                .setContentText(getResources().getString(R.string.download_failure));
                        NotificationUtils notificationUtils = builder.build();
                        notificationUtils.setId(signNumber);
                        notificationUtils.getNotification().flags = Notification.FLAG_AUTO_CANCEL;
                        threadMap.remove(signNumber);
                        if (threadMap.isEmpty()) {
                            stopSelf();
                        }
                    }
                }
            }
        });
        return signNumber;
    }

    public class Listener implements MusicDatabaseHelper.MusicDatabaseHelperListener {

        @Override
        public void onMusicDatabaseQueryResult(ArrayList<SongModel> songModels, int requestCode) {

        }

        @Override
        public void onMusicDatabaseSqlExecComplete(int requestCode) {
            MusicPlayerController.notifyCurrentPlayMusicChange();
            threadMap.remove(requestCode);
            if (threadMap.isEmpty()) {
                stopSelf();
            }
        }

        @Override
        public void onMusicDatabaseOperateFailure(Throwable t, int requestCode) {
            threadMap.remove(requestCode);
            if (threadMap.isEmpty()) {
                stopSelf();
            }
        }
    }

    public class DownloadBinder extends Binder {

        int signNumber;

        DownloadBinder setSignNumber(int signNumber) {
            this.signNumber = signNumber;
            return this;
        }

        public int getSignNumber() {
            return signNumber;
        }

        public void stop() {
            statusMap.get(signNumber).isFailed = true;
            threadMap.get(signNumber).interrupt();
        }

        public DownloadStatus getStatus() {
            return statusMap.get(signNumber);
        }
    }

    private void toast(@StringRes final int res) {
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                StandardUtils.toast(res);
            }
        });
    }

    public static void start(Context context, SongModel model, String filePath) {
        Intent starter = new Intent(context, DownloadService.class);
        starter.putExtra("model", model);
        starter.putExtra("filePath", filePath);
        context.startService(starter);
    }

    public static void stop(Context context) {
        Intent starter = new Intent(context, DownloadService.class);
        context.stopService(starter);
    }


}
