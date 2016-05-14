package moe.haruue.redrockexam.musicplayer.data.database.info;

import android.database.sqlite.SQLiteDatabase;

import moe.haruue.redrockexam.util.database.DatabaseInfo;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class LyricDatabaseInfo implements DatabaseInfo {
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE lyric (" +
                "songid INT PRIMARY KEY NOT NULL," +
                "lyric TEXT," +
                "lyric_text TEXT)";
        db.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
