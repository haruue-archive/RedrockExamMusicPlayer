package moe.haruue.redrockexam.musicplayer.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.caoyue.imageloader.ImageLoader;
import cn.com.caoyue.util.time.Time;
import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.musicplayer.util.LocalUtils;
import moe.haruue.redrockexam.ui.recyclerview.HaruueAdapter;
import moe.haruue.redrockexam.ui.recyclerview.HaruueViewHolder;
import moe.haruue.redrockexam.ui.widget.CircleImageView;
import moe.haruue.redrockexam.util.humanunit.ByteHumanFormatter;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class SongItemAdapter extends HaruueAdapter<SongModel> {

    OnMoreInfoOptionButtonClickListener onMoreInfoOptionButtonClickListener;

    public SongItemAdapter(Context context) {
        super(context);
    }

    public interface OnMoreInfoOptionButtonClickListener {
        void onMoreInfoOptionButtonClickListener(View itemView, int position, SongModel model);
    }

    public void setOnMoreInfoOptionButtonClickListener(OnMoreInfoOptionButtonClickListener onMoreInfoOptionButtonClickListener) {
        this.onMoreInfoOptionButtonClickListener = onMoreInfoOptionButtonClickListener;
    }

    @Override
    public HaruueViewHolder<SongModel> onCreateHaruueViewHolder(ViewGroup parent, int viewType) {
        final SongItemViewHolder songItemViewHolder = new SongItemViewHolder(parent);
        if (onMoreInfoOptionButtonClickListener != null) {
            songItemViewHolder.setOnMoreOptionButtonClickListener(onMoreInfoOptionButtonClickListener);
        }
        return songItemViewHolder;
    }

    public class SongItemViewHolder extends HaruueViewHolder<SongModel> {

        CircleImageView albumPictureImageView;
        TextView titleTextView;
        TextView singerTextView;
        TextView m4aSignTextView;
        TextView mp3SignTextView;
        TextView moreInfoTextView;
        TextView moreOptionTextView;
        SongModel data;

        public SongItemViewHolder(View itemView) {
            super((ViewGroup) itemView, R.layout.item_song);
            albumPictureImageView = $(R.id.item_song_info_album_image);
            titleTextView = $(R.id.item_song_info_title);
            singerTextView = $(R.id.item_song_info_singer);
            m4aSignTextView = $(R.id.item_song_info_more_sign_m4a);
            mp3SignTextView = $(R.id.item_song_info_more_sign_mp3);
            moreInfoTextView = $(R.id.item_song_info_more_size_and_time);
            moreOptionTextView = $(R.id.item_song_more_option);
        }

        public void setOnMoreOptionButtonClickListener(final OnMoreInfoOptionButtonClickListener listener) {
            moreOptionTextView.setVisibility(View.VISIBLE);
            moreOptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMoreInfoOptionButtonClickListener(itemView, getModelPosition(), data);
                }
            });
        }

        @Override
        public void setData(SongModel data) {
            this.data = data;
            ImageLoader.getInstance().loadImage(data.albumPicSmall, albumPictureImageView);
            titleTextView.setText(data.songName);
            singerTextView.setText(data.singerName);
            setSign(data);
            setMoreInfo(data);
        }

        private void setSign(SongModel data) {
            if (LocalUtils.isLocal(data)) {
                setMP3Sign();
            } else {
                setM4ASign();
            }
        }

        private void setMoreInfo(SongModel data) {
            String moreInfo;
            moreInfoTextView.setVisibility(View.VISIBLE);
            if (data.size != -1 && data.seconds != -1) {
                moreInfo = formatSize(data.size) + " | " + formatTime(data.seconds);
            } else if (data.size != -1) {
                moreInfo = formatSize(data.size);
            } else if (data.seconds != -1) {
                moreInfo = formatTime(data.seconds);
            } else {
                moreInfoTextView.setVisibility(View.GONE);
            }
        }

        private String formatSize(int byteSize) {
            return ByteHumanFormatter.formatByteSize(byteSize, ByteHumanFormatter.BIN, ByteHumanFormatter.DEC_UNIT);
        }

        private String formatTime(int seconds) {
            Time.DeltaTime deltaTime = new Time.DeltaTime(seconds);
            return deltaTime.getMinute() + ":" + deltaTime.getSecond();
        }

        private void setMP3Sign() {
            m4aSignTextView.setVisibility(View.GONE);
            mp3SignTextView.setVisibility(View.VISIBLE);
        }

        private void setM4ASign() {
            m4aSignTextView.setVisibility(View.VISIBLE);
            mp3SignTextView.setVisibility(View.GONE);
        }

        private void clearQualitySign() {
            m4aSignTextView.setVisibility(View.GONE);
            mp3SignTextView.setVisibility(View.GONE);
        }
    }

}
