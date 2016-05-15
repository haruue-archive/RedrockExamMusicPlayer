package moe.haruue.redrockexam.musicplayer;

import cn.com.caoyue.imageloader.ImageLoaderConfig;
import moe.haruue.redrockexam.musicplayer.data.database.info.LyricDatabaseInfo;
import moe.haruue.redrockexam.musicplayer.data.database.info.MusicDatabaseInfo;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlay;
import moe.haruue.redrockexam.util.InstanceSaver;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.abstracts.HaruueApplication;
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
        // Initialize ImageLoader
        ImageLoaderConfig.start(this)
                .setDefaultDrawableOnLoading(R.drawable.default_album)
                .setDefaultDrawableOnFailure(R.drawable.default_album)
                .build();
        // Initialize Database
        DatabaseUtils.initialize(this);
        new DatabaseUtils("music", 1, new MusicDatabaseInfo());
        new DatabaseUtils("lyric", 1, new LyricDatabaseInfo());
        // Initialize InstanceSaver
        InstanceSaver.add(new CurrentPlay());
    }
}
