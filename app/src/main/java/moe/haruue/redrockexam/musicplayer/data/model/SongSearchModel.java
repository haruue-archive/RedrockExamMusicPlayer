package moe.haruue.redrockexam.musicplayer.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SongSearchModel implements Serializable {

    public int showapi_res_code;
    public String showapi_res_error;
    public ResBody showapi_res_body;

    public static class ResBody implements Serializable {

        public PageBean pageBean;

        public static class PageBean implements Serializable {

            public int allNum;
            public int allPages;
            public ArrayList<ContentItem> contentList = new ArrayList<>(0);

            public static class ContentItem implements Serializable {
                public String albumid;
                public String albumname;
                public String albumpic_big;
                public String albumpic_small;
                public String downUrl;
                public String m4a;
                public String singerid;
                public String singername;
                public int songid;
                public String songname;
            }

            public int currentPage;
            public String keyword;
            public int maxResult;
            public int ret_code;

        }

    }


}