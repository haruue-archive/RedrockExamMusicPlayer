package moe.haruue.redrockexam.musicplayer.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import moe.haruue.redrockexam.musicplayer.data.network.helper.TopMusicApiHelper;
import moe.haruue.redrockexam.musicplayer.ui.fragment.TopMusicFragment;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class TopMusicPagerAdapter extends FragmentStatePagerAdapter {

    int[] topIds = new int[]{
            TopMusicApiHelper.TopId.EUROPE_AMERICAN,        //欧美
            TopMusicApiHelper.TopId.CHINA_MAINLAND,         //大陆
            TopMusicApiHelper.TopId.CHINA_HK_TW,            //港台
            TopMusicApiHelper.TopId.KOREA,                  //韩国
            TopMusicApiHelper.TopId.JAPEN,                  //日本
            TopMusicApiHelper.TopId.CLASSIC,                //民谣
            TopMusicApiHelper.TopId.ROCK,                   //摇滚
            TopMusicApiHelper.TopId.SELL,                   //销量
            TopMusicApiHelper.TopId.HOT                     //热歌
    };

    String[] titles = new String[]{
            "欧美",
            "大陆",
            "港台",
            "韩国",
            "日本",
            "民谣",
            "摇滚",
            "销量",
            "热歌"
    };


    public TopMusicPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new TopMusicFragment().setTopId(topIds[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return 9;
    }
}
