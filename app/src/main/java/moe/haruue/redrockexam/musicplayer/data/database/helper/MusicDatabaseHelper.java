package moe.haruue.redrockexam.musicplayer.data.database.helper;

import android.database.Cursor;

import java.util.ArrayList;

import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.util.database.DatabaseUtils;
import moe.haruue.redrockexam.util.database.ExecSqlListener;
import moe.haruue.redrockexam.util.database.QueryListener;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class MusicDatabaseHelper {

    public interface MusicDatabaseHelperListener {
        void onMusicDatabaseQueryResult(ArrayList<SongModel> songModels, int requestCode);
        void onMusicDatabaseSqlExecComplete(int requestCode);
        void onMusicDatabaseOperateFailure(Throwable t, int requestCode);
    }

    public static void queryAll(final MusicDatabaseHelperListener listener, final int requestCode) {
        DatabaseUtils musicDatabaseUtil = DatabaseUtils.getDatabaseByName("music");
        if (musicDatabaseUtil == null) {
            listener.onMusicDatabaseOperateFailure(new NullPointerException("Can't get music database"), requestCode);
        } else {
            musicDatabaseUtil.query("SELECT * FROM music", new String[]{}, new QueryListener() {

                @Override
                public void onQueryResult(Cursor cursor) {
                    ArrayList<SongModel> songModels = new ArrayList<>(0);
                    if (cursor.moveToFirst()) {
                        do {
                            SongModel model = new SongModel();
                            model.songId = cursor.getInt(cursor.getColumnIndex("songid"));
                            model.songName = cursor.getString(cursor.getColumnIndex("songname"));
                            model.singerId = cursor.getInt(cursor.getColumnIndex("singerid"));
                            model.singerName = cursor.getString(cursor.getColumnIndex("singername"));
                            model.albumId = cursor.getInt(cursor.getColumnIndex("albumid"));
                            model.albumName = cursor.getString(cursor.getColumnIndex("albumname"));
                            model.albumPicBig = cursor.getString(cursor.getColumnIndex("albumpic_big"));
                            model.albumPicSmall = cursor.getString(cursor.getColumnIndex("albumpic_small"));
                            model.m4aUrl = cursor.getString(cursor.getColumnIndex("m4a"));
                            model.downUrl = cursor.getString(cursor.getColumnIndex("downUrl"));
                            model.m4aCache = cursor.getString(cursor.getColumnIndex("m4a_cache"));
                            model.file = cursor.getString(cursor.getColumnIndex("file"));
                            model.seconds = cursor.getInt(cursor.getColumnIndex("second"));
                            model.size = cursor.getInt(cursor.getColumnIndex("size"));
                            songModels.add(model);
                        } while (cursor.moveToNext());
                    }
                    listener.onMusicDatabaseQueryResult(songModels, requestCode);
                }

                @Override
                public void onQueryError(Throwable t) {
                    listener.onMusicDatabaseOperateFailure(t, requestCode);
                }
            });
        }
    }

    public static void insert(SongModel model, final MusicDatabaseHelperListener listener, final int requestCode) {
        DatabaseUtils musicDatabaseUtil = DatabaseUtils.getDatabaseByName("music");
        if (musicDatabaseUtil == null) {
            listener.onMusicDatabaseOperateFailure(new NullPointerException("Can't get music database"), requestCode);
        } else {
            musicDatabaseUtil.exec(new String[]{gainInsertSql(model)}, new ExecSqlListener() {
                @Override
                public void onExecSqlSuccess() {
                    listener.onMusicDatabaseSqlExecComplete(requestCode);
                }

                @Override
                public void onExecSqlError(Throwable t) {
                    listener.onMusicDatabaseOperateFailure(t, requestCode);
                }
            });
        }

    }

    private static String gainInsertSql(SongModel model) {
        return "INSERT INTO music VALUES (" +
                model.songId + "," +
                "\'" + model.songName + "\'," +
                model.singerId + "," +
                "\'" + model.singerName + "\'," +
                model.albumId + "," +
                "\'" + model.albumName + "\'," +
                "\'" + model.albumPicBig + "\'," +
                "\'" + model.albumPicSmall + "\'," +
                "\'" + model.m4aUrl + "\'," +
                "\'" + model.downUrl + "\'," +
                "\'" + model.m4aCache + "\'," +
                "\'" + model.file + "\'," +
                model.seconds + "," +
                model.size +
                ")";
    }

    public static void delete(int songId, final MusicDatabaseHelperListener listener, final int requestCode) {
        DatabaseUtils musicDatabaseUtil = DatabaseUtils.getDatabaseByName("music");
        if (musicDatabaseUtil == null) {
            listener.onMusicDatabaseOperateFailure(new NullPointerException("Can't get music database"), requestCode);
        } else {
            musicDatabaseUtil.exec(new String[]{gainDeleteSql(songId)}, new ExecSqlListener() {
                @Override
                public void onExecSqlSuccess() {
                    listener.onMusicDatabaseSqlExecComplete(requestCode);
                }

                @Override
                public void onExecSqlError(Throwable t) {
                    listener.onMusicDatabaseOperateFailure(t, requestCode);
                }
            });
        }

    }

    private static String gainDeleteSql(int songId) {
        return "DELETE FROM music WHERE songid = " + songId;
    }

    public static void update(SongModel model, final MusicDatabaseHelperListener listener, final int requestCode) {
        DatabaseUtils musicDatabaseUtil = DatabaseUtils.getDatabaseByName("music");
        if (musicDatabaseUtil == null) {
            listener.onMusicDatabaseOperateFailure(new NullPointerException("Can't get music database"), requestCode);
        } else {
            musicDatabaseUtil.exec(new String[]{gainUpdateSql(model)}, new ExecSqlListener() {
                @Override
                public void onExecSqlSuccess() {
                    listener.onMusicDatabaseSqlExecComplete(requestCode);
                }

                @Override
                public void onExecSqlError(Throwable t) {
                    listener.onMusicDatabaseOperateFailure(t, requestCode);
                }
            });

        }

    }

    private static String gainUpdateSql(SongModel model) {
        return "UPDATE music SET " +
                "songname = " + "\'" + model.songName + "\'," +
                "singerid = " + model.singerId + "," +
                "singername = " + "\'" + model.singerName + "\'," +
                "albumid = " + model.albumId + "," +
                "albumname = " + "\'" + model.albumName + "\'," +
                "albumpic_big = " + "\'" + model.albumPicBig + "\'," +
                "albumpic_small = " + "\'" + model.albumPicSmall + "\'," +
                "m4a = " + "\'" + model.m4aUrl + "\'," +
                "downUrl = " + "\'" + model.downUrl + "\'," +
                "m4a_cache = " + "\'" + model.m4aCache + "\'," +
                "file = " + "\'" + model.file + "\'," +
                "second = " + model.seconds + "," +
                "size = " + model.size +
                " WHERE songid = " + model.songId;
    }

}
