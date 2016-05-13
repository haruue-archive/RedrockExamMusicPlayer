package moe.haruue.redrockexam.musicplayer.data.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class TopModel implements Serializable {

    public int showapi_res_code;
    public String showapi_res_error;
    public ResBody showapi_res_body;

    public static class ResBody implements Serializable {

        public PageBean pageBean;

        public static class PageBean implements Serializable {

            public int ret_code;
            public ArrayList<SongItem> songList = new ArrayList<>(0);

            public static class SongItem implements Serializable {
                public int albumid;
                public String downUrl;
                public int seconds;
                public int singerid;
                public String singername;
                public int songid;
                public String songname;
                public String url;
            }

            public int totalpage;

        }

    }

}
