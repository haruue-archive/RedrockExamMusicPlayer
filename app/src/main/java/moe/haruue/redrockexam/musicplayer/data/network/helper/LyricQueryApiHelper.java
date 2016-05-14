package moe.haruue.redrockexam.musicplayer.data.network.helper;

import android.accounts.NetworkErrorException;
import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.model.LyricModel;
import moe.haruue.redrockexam.musicplayer.data.model.LyricQueryModel;
import moe.haruue.redrockexam.musicplayer.data.network.Api;
import moe.haruue.redrockexam.musicplayer.util.JSONUtils;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.ThreadUtils;
import moe.haruue.redrockexam.util.network.ApiHelper;
import moe.haruue.redrockexam.util.network.ApiHelperListener;
import moe.haruue.redrockexam.util.network.NetworkError;
import moe.haruue.redrockexam.util.network.Request;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class LyricQueryApiHelper implements ApiHelper {

    public interface LyricQueryApiHelperListener extends ApiHelperListener {
        void onGetSongLyricSuccess(LyricModel data);
        void onGetSongLyricFailure(int code, String error);
    }

    public static void getSongLyric(Activity activity, final int songId, final LyricQueryApiHelperListener listener) {
        ThreadUtils.runOnNewThread(activity, new Runnable() {
            @Override
            public void run() {
                Map<String, String> paramMap = new HashMap<>(0);
                paramMap.put("showapi_appid", Api.SHOWAPI_APIID);
                paramMap.put("showapi_sign", Api.SHOWAPI_SIGN);
                paramMap.put("musicid", songId + "");
                String result;
                final LyricQueryModel model = new LyricQueryModel();
                try {
                    result = new Request(Api.LYRIC_QUERY, paramMap).getConnectionStreams().getString();
                } catch (NetworkErrorException e) {
                    StandardUtils.printStack(e);
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetSongLyricFailure(NetworkError.NETWORK_ERROR, StandardUtils.getApplication().getResources().getString(R.string.network_error));
                        }
                    });
                    return;
                }
                JSONTokener tokener = new JSONTokener(result);
                try {
                    JSONObject object = (JSONObject) tokener.nextValue();
                    model.showapi_res_code = JSONUtils.getInt(object, "showapi_res_code");
                    if (model.showapi_res_code != 0) {
                        model.showapi_res_error = JSONUtils.getString(object, "showapi_res_error");
                        ThreadUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onGetSongLyricFailure(model.showapi_res_code, model.showapi_res_error);
                            }
                        });
                        return;
                    }
                    JSONObject resBodyObject = object.getJSONObject("showapi_res_body");
                    final String lyric = JSONUtils.getString(resBodyObject, "lyric");
                    final String lyricText = JSONUtils.getString(resBodyObject, "lyric_txt");
                    final LyricModel data = new LyricModel();
                    data.songId = songId;
                    data.lyric = lyric;
                    data.lyricText = lyricText;
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetSongLyricSuccess(data);
                        }
                    });
                } catch (JSONException e) {
                    StandardUtils.printStack(e);
                    listener.onGetSongLyricFailure(NetworkError.DATA_DECODE_EXCEPTION, StandardUtils.getApplication().getResources().getString(R.string.json_decode_exception));
                }
            }

        });
    }
}
