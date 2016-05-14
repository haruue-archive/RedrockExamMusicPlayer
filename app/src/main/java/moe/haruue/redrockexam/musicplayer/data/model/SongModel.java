package moe.haruue.redrockexam.musicplayer.data.model;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class SongModel implements Serializable {

    public int songId;
    public String songName;
    public int singerId;
    public String singerName;
    public int albumId;
    @Nullable public String albumName;
    public String albumPicBig;
    public String albumPicSmall;
    public String downUrl;
    public String m4aUrl;
    @Nullable public String file;
    @Nullable public String m4aCache;
    public int seconds = -1;
    public int size = -1;

    public static ArrayList<SongModel> getSongModelListFromTopSongItemList(List<TopModel.ResBody.PageBean.SongItem> songItemList) {
        ArrayList<SongModel> songModelArrayList = new ArrayList<>(0);
        for (TopModel.ResBody.PageBean.SongItem i: songItemList) {
            songModelArrayList.add(i.toSongModel());
        }
        return songModelArrayList;
    }

}
