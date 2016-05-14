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
import moe.haruue.redrockexam.musicplayer.data.model.SongSearchModel;
import moe.haruue.redrockexam.musicplayer.data.network.Api;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.ThreadUtils;
import moe.haruue.redrockexam.util.network.ApiHelper;
import moe.haruue.redrockexam.util.network.ApiHelperListener;
import moe.haruue.redrockexam.util.network.NetworkError;
import moe.haruue.redrockexam.util.network.Request;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class SongSearchApiHelper implements ApiHelper {

    public final static int FIRST_PAGE = 1;

    public interface SongSearchApiHelperListener extends ApiHelperListener {
        void onGetSongSearchResultSuccess(SongSearchModel.ResBody.PageBean searchPage);
        void onGetSongSearchResultFailure(int code, String error);
    }

    public static void getSongSearchResult(Activity activity, final String keyword, final int page, final SongSearchApiHelperListener listener) {
        ThreadUtils.runOnNewThread(activity, new Runnable() {
            @Override
            public void run() {
                Map<String, String> paramMap = new HashMap<>(0);
                paramMap.put("showapi_appid", Api.SHOWAPI_APIID);
                paramMap.put("showapi_sign", Api.SHOWAPI_SIGN);
                paramMap.put("keyword", keyword);
                paramMap.put("page", page + "");
                String result;
                final SongSearchModel model = new SongSearchModel();
                try {
                    result = new Request(Api.MUSIC_SEARCH, paramMap).getConnectionStreams().getString();
                } catch (NetworkErrorException e) {
                    StandardUtils.printStack(e);
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetSongSearchResultFailure(NetworkError.NETWORK_ERROR, StandardUtils.getApplication().getResources().getString(R.string.network_error));
                        }
                    });
                    return;
                }
                JSONTokener tokener = new JSONTokener(result);
                try {
                    JSONObject object = (JSONObject) tokener.nextValue();
                    model.showapi_res_code = object.getInt("showapi_res_code");
                    if (model.showapi_res_code != 0) {
                        model.showapi_res_error = object.getString("showapi_res_error");
                        ThreadUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onGetSongSearchResultFailure(model.showapi_res_code, model.showapi_res_error);
                            }
                        });
                        return;
                    }
                    final SongSearchModel.ResBody.PageBean pageBean = new SongSearchModel.ResBody.PageBean();
                    JSONObject pageBeanObject = object.getJSONObject("showapi_res_body").getJSONObject("pagebean");
                    pageBean.allNum = pageBeanObject.getInt("allNum");
                    pageBean.allPages = pageBeanObject.getInt("allPages");
                    pageBean.contentList = new ArrayList<>(0);
                    pageBean.currentPage = pageBeanObject.getInt("currentPage");
                    pageBean.keyword = pageBeanObject.getString("keyword");
                    pageBean.maxResult = pageBeanObject.getInt("maxResult");
                    JSONArray array = pageBeanObject.getJSONArray("contentlist");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject itemObject = array.getJSONObject(i);
                        SongSearchModel.ResBody.PageBean.ContentItem contentItem = new SongSearchModel.ResBody.PageBean.ContentItem();
                        contentItem.albumid = itemObject.getString("albumid");
                        contentItem.albumname = itemObject.getString("albumname");
                        contentItem.albumpic_big = itemObject.getString("albumpic_big");
                        contentItem.albumpic_small = itemObject.getString("albumpic_small");
                        contentItem.downUrl = itemObject.getString("downUrl");
                        contentItem.m4a = itemObject.getString("m4a");
                        contentItem.singerid = itemObject.getString("singerid");
                        contentItem.singername = itemObject.getString("singername");
                        contentItem.songid = itemObject.getInt("songid");
                        contentItem.songname = itemObject.getString("songname");
                        pageBean.contentList.add(contentItem);
                    }
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetSongSearchResultSuccess(pageBean);
                        }
                    });

                } catch (JSONException e) {
                    StandardUtils.printStack(e);
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetSongSearchResultFailure(NetworkError.DATA_DECODE_EXCEPTION, StandardUtils.getApplication().getResources().getString(R.string.json_decode_exception));
                        }
                    });
                }
            }
        });
    }

}
