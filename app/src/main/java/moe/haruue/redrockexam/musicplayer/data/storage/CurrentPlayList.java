package moe.haruue.redrockexam.musicplayer.data.storage;

import java.io.Serializable;
import java.util.ArrayList;

import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.util.abstracts.InstanceSavable;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class CurrentPlayList extends InstanceSavable {

    public static CurrentPlayList instance = new CurrentPlayList();

    public ArrayList<SongModel> playList;
    public int mode = 0;

    public class PlayMode {
        public final static int ORDER_LIST = 0;
        public final static int RANDOM = 1;
        public final static int LOOP_ONE_SONG = 2;
    }

    @Override
    public Serializable getSavableInstance() {
        return instance;
    }

    @Override
    public void onRestoreInstance(Serializable instance) {
        CurrentPlayList.instance = (CurrentPlayList) instance;
    }

    public static boolean hasList() {
        return instance.playList != null && !instance.playList.isEmpty();
    }

    public static SongModel random() {
        int index = (int) (Math.random() * (instance.playList.size() - 1));
        return instance.playList.get(index);
    }

    public static SongModel getPrevious() {
        if (instance.mode != PlayMode.RANDOM && CurrentPlay.instance.data != null && instance.playList.contains(CurrentPlay.instance.data)) {
            return instance.playList.get(getNumberPrevious(instance.playList.indexOf(CurrentPlay.instance.data)));
        } else {
            return random();
        }
    }

    private static int getNumberPrevious(int current) {
        current--;
        if (current < 0) {
            current = instance.playList.size() - 1;
        }
        return current;
    }

    public static SongModel getNext() {
        if (instance.mode != PlayMode.RANDOM && CurrentPlay.instance.data != null && instance.playList.contains(CurrentPlay.instance.data)) {
            return instance.playList.get(getNumberNext(instance.playList.indexOf(CurrentPlay.instance.data)));
        } else {
            return random();
        }
    }

    private static int getNumberNext(int current) {
        current++;
        if (current >= instance.playList.size()) {
            current = 0;
        }
        return current;
    }
}
