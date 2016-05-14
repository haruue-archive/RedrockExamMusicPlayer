package moe.haruue.redrockexam.musicplayer.data.database.helper;

import android.database.Cursor;

import moe.haruue.redrockexam.musicplayer.data.model.LyricModel;
import moe.haruue.redrockexam.util.database.DatabaseUtils;
import moe.haruue.redrockexam.util.database.ExecSqlListener;
import moe.haruue.redrockexam.util.database.QueryListener;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class LyricDatabaseHelper {

    public interface LyricDatabaseHelperListener {
        void onLyricDatabaseQueryResult(LyricModel data, int requestCode);
        void onLyricDatabaseSqlExecComplete(int requestCode);
        void onLyricDatabaseOperateFailure(Throwable t, int requestCode);
    }

    public static void getLyric(int songId, final LyricDatabaseHelperListener listener, final int requestCode) {
        DatabaseUtils lyricDatabaseUtil = DatabaseUtils.getDatabaseByName("lyric");
        if (lyricDatabaseUtil == null) {
            listener.onLyricDatabaseOperateFailure(new NullPointerException("Can't get lyric database"), requestCode);
        } else {
            lyricDatabaseUtil.query("SELECT * FROM lyric WHERE songid = ?", new String[]{songId + ""}, new QueryListener() {
                @Override
                public void onQueryResult(Cursor cursor) {
                    if (cursor.moveToFirst()) {
                        LyricModel data = new LyricModel();
                        data.songId = cursor.getInt(cursor.getColumnIndex("songid"));
                        data.lyric = cursor.getString(cursor.getColumnIndex("lyric"));
                        data.lyric = cursor.getString(cursor.getColumnIndex("lyric_text"));
                        listener.onLyricDatabaseQueryResult(data, requestCode);
                    }
                }

                @Override
                public void onQueryError(Throwable t) {
                    listener.onLyricDatabaseOperateFailure(t, requestCode);
                }
            });
        }

    }

    public static void insert(LyricModel model, final LyricDatabaseHelperListener listener, final int requestCode) {
        DatabaseUtils lyricDatabaseUtil = DatabaseUtils.getDatabaseByName("lyric");
        if (lyricDatabaseUtil == null) {
            listener.onLyricDatabaseOperateFailure(new NullPointerException("Can't get lyric database"), requestCode);
        } else {
            lyricDatabaseUtil.exec(new String[]{gainInsertSql(model)}, new ExecSqlListener() {
                @Override
                public void onExecSqlSuccess() {
                    listener.onLyricDatabaseSqlExecComplete(requestCode);
                }

                @Override
                public void onExecSqlError(Throwable t) {
                    listener.onLyricDatabaseOperateFailure(t, requestCode);
                }
            });
        }
    }

    private static String gainInsertSql(LyricModel data) {
        return "INSERT INTO lyric VALUE (" +
                data.songId + "," +
                "\'" + data.lyric + "\'," +
                "\'" +data.lyricText + "\'" +
                ")";
    }

    public static void delete(int songId, final LyricDatabaseHelperListener listener, final int requestCode) {
        DatabaseUtils lyricDatabaseUtil = DatabaseUtils.getDatabaseByName("lyric");
        if (lyricDatabaseUtil == null) {
            listener.onLyricDatabaseOperateFailure(new NullPointerException("Can't get lyric database"), requestCode);
        } else {
            lyricDatabaseUtil.exec(new String[]{gainDeleteSql(songId)}, new ExecSqlListener() {
                @Override
                public void onExecSqlSuccess() {
                    listener.onLyricDatabaseSqlExecComplete(requestCode);
                }

                @Override
                public void onExecSqlError(Throwable t) {
                    listener.onLyricDatabaseOperateFailure(t, requestCode);
                }
            });
        }

    }

    private static String gainDeleteSql(int songId) {
        return "DELETE FROM lyric WHERE songid = " + songId;
    }

}
