package moe.haruue.redrockexam.musicplayer.data.network.helper;

import android.app.Activity;

import moe.haruue.redrockexam.util.ThreadUtils;
import moe.haruue.redrockexam.util.network.ApiHelper;
import moe.haruue.redrockexam.util.network.ApiHelperListener;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class LyricQueryApiHelper implements ApiHelper {

    public interface LyricQueryApiHelperListener extends ApiHelperListener {
        void onGetSongLyricSuccess(String lyric, String lyricText);
        void onGetSongLyricFailure(int code, String error);
    }

    public static void getSongLyric(Activity activity, String musicId, LyricQueryApiHelperListener listener) {
        ThreadUtils.runOnNewThread(activity, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

}
