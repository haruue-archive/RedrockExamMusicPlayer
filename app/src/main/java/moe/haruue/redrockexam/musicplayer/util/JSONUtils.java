package moe.haruue.redrockexam.musicplayer.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class JSONUtils {

    public static int getInt(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            return -1;
        }
    }

    public static String getString(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

}
