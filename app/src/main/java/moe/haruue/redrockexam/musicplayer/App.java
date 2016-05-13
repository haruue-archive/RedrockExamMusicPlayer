package moe.haruue.redrockexam.musicplayer;

import android.database.sqlite.SQLiteDatabase;

import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.abstracts.HaruueApplication;
import moe.haruue.redrockexam.util.database.DatabaseInfo;
import moe.haruue.redrockexam.util.database.DatabaseUtils;
import moe.haruue.redrockexam.util.file.FileUtils;
import moe.haruue.redrockexam.util.network.NetworkConfiguration;
import moe.haruue.redrockexam.util.network.NetworkUtils;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class App extends HaruueApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // Set DEBUG
        StandardUtils.setDebug(BuildConfig.DEBUG);
        // Initialize FileUtils
        FileUtils.initialize(this);
        // Initialize NetWorkUtils
        NetworkUtils.initialize(this, new NetworkConfiguration().setConnectTimeout(60).setReadTimeout(9999).setRequestMethod(NetworkConfiguration.RequestMethods.POST));
        // Initialize Database
        DatabaseUtils.initialize(this);
        new DatabaseUtils("music", 1, new DatabaseInfo() {
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
                        "downUrl TEXT)";
                db.execSQL(createSql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        });
    }
}
