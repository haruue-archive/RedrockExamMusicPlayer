package moe.haruue.redrockexam.musicplayer.data.storage;

import java.io.Serializable;

import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.util.abstracts.InstanceSavable;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class CurrentPlay extends InstanceSavable {

    public static CurrentPlay instance = new CurrentPlay();

    public SongModel data;
    public boolean isPlaying = false;

    public SongModel getData() {
        return data;
    }

    public void setData(SongModel data) {
        this.data = data;
    }

    @Override
    public Serializable getSavableInstance() {
        return instance;
    }

    @Override
    public void onRestoreInstance(Serializable instance) {
        CurrentPlay.instance = (CurrentPlay) instance;
    }
}
