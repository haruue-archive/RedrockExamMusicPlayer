package moe.haruue.redrockexam.musicplayer.data.database.info;

import android.database.sqlite.SQLiteDatabase;

import moe.haruue.redrockexam.util.database.DatabaseInfo;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class MusicDatabaseInfo implements DatabaseInfo {

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE music (" +
                "songid INT PRIMARY KEY NOT NULL," +
                "songname TEXT NOT NULL," +
                "singerid INT," +
                "singername TEXT," +
                "albumid INT," +
                "albumname TEXT," +
                "albumpic_big TEXT," +
                "albumpic_small TEXT," +
                "m4a TEXT," +
                "downUrl TEXT," +
                "m4a_cache TEXT," +
                "file TEXT," +
                "second INT," +
                "size INT)";
        db.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
