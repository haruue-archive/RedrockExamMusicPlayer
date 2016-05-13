package moe.haruue.redrockexam.musicplayer.data.model;

import java.io.Serializable;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class LyricQueryModel implements Serializable {


    public int showapi_res_code;
    public String showapi_res_error;
    public ResBody showapi_res_body;

    public static class ResBody implements Serializable {

        public String lyric;
        public String lyric_txt;
        public String ret_code;

    }

}
