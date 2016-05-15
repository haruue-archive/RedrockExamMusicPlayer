package moe.haruue.redrockexam.musicplayer.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;

import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.util.StandardUtils;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class LocalUtils {

    public static boolean isLocal(SongModel model) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return model.file != null && StandardUtils.getApplication().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && new File(model.file).exists();
        } else {
            return model.file != null && new File(model.file).exists();
        }
    }

}
