package moe.haruue.redrockexam.musicplayer.data.network.helper;

import android.accounts.NetworkErrorException;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.model.TopModel;
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
public class TopMusicApiHelper implements ApiHelper {

    public class TopId {
        public final static int EUROPE_AMERICAN = 3;    //欧美
        public final static int CHINA_MAINLAND = 5;     //大陆
        public final static int CHINA_HK_TW = 6;        //港台
        public final static int KOREA = 16;             //韩国
        public final static int JAPEN = 17;             //日本
        public final static int CLASSIC = 18;           //民谣
        public final static int ROCK = 19;              //摇滚
        public final static int SELL = 23;              //销量
        public final static int HOT = 26;               //热歌
    }

    public interface TopMusicApiHelperListener extends ApiHelperListener {
        void onGetTopMusicSuccess(ArrayList<TopModel.ResBody.PageBean.SongItem> songList);
        void onGetTopMusicFailure(int code, String error);
    }

    public static void getTopMusicList(Activity activity, final int topId, final TopMusicApiHelperListener listener) {
        ThreadUtils.runOnNewThread(activity, new Runnable() {
            @Override
            public void run() {
                Map<String, String> paramMap = new HashMap<>(0);
                paramMap.put("showapi_appid", Api.SHOWAPI_APIID);
                paramMap.put("showapi_sign", Api.SHOWAPI_SIGN);
                paramMap.put("topid", topId + "");
                String result;
                final TopModel model = new TopModel();
                try {
                    result = new Request(Api.TOP_LIST, paramMap).getConnectionStreams().getString();
                } catch (NetworkErrorException e) {
                    StandardUtils.printStack(e);
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetTopMusicFailure(NetworkError.NETWORK_ERROR, StandardUtils.getApplication().getResources().getString(R.string.network_error));
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
                                listener.onGetTopMusicFailure(model.showapi_res_code, model.showapi_res_error);
                            }
                        });
                        return;
                    }
                    JSONArray array = object.getJSONObject("showapi_res_body").getJSONObject("pagebean").getJSONArray("songlist");
                    final ArrayList<TopModel.ResBody.PageBean.SongItem> songList = new ArrayList<>(0);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject itemObject = array.getJSONObject(i);
                        TopModel.ResBody.PageBean.SongItem item = new TopModel.ResBody.PageBean.SongItem();
                        item.albumid = JSONUtils.getInt(itemObject, "albumid");
                        item.albummid = JSONUtils.getString(itemObject, "albummid");
                        item.albumpic_big = JSONUtils.getString(itemObject, "albumpic_big");
                        item.albumpic_small = JSONUtils.getString(itemObject, "albumpic_small");
                        item.downUrl = JSONUtils.getString(itemObject, "downUrl");
                        item.seconds = JSONUtils.getInt(itemObject, "seconds");
                        item.singerid = JSONUtils.getInt(itemObject, "singerid");
                        item.singername = JSONUtils.getString(itemObject, "singername");
                        item.songid = JSONUtils.getInt(itemObject, "songid");
                        item.songname = JSONUtils.getString(itemObject, "songname");
                        item.url = JSONUtils.getString(itemObject, "url");
                        songList.add(item);
                    }
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetTopMusicSuccess(songList);
                        }
                    });
                } catch (JSONException e) {
                    StandardUtils.printStack(e);
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetTopMusicFailure(NetworkError.DATA_DECODE_EXCEPTION, StandardUtils.getApplication().getResources().getString(R.string.json_decode_exception));
                        }
                    });
                }
            }
        });
    }

}
