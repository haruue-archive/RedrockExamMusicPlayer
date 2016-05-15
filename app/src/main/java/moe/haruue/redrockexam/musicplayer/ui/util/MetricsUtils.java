package moe.haruue.redrockexam.musicplayer.ui.util;

import moe.haruue.redrockexam.util.StandardUtils;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class MetricsUtils {

    public static int dpToPx(float dpValue) {
        final float scale = StandardUtils.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
