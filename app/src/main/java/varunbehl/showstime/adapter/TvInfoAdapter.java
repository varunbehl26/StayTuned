package varunbehl.showstime.adapter;
/**
 * Created by varunbehl on 07/03/17.
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.activity.TvDetailActivity;
import varunbehl.showstime.fragment.TvDetailActivityFragment;
import varunbehl.showstime.pojo.Picture.Pictures;
import varunbehl.showstime.util.ImageUtil;

public class TvInfoAdapter extends ArrayAdapter<Pictures> {

    private final List<Pictures> tvInfoList;
    private final LayoutInflater inflater;
    private final Context mContext;
    private final FirebaseAnalytics mFirebaseAnalytics;


    public TvInfoAdapter(Context context, List<Pictures> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.tvInfoList = objects;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (tvInfoList != null)
            return tvInfoList.size();
        else
            return 0;
    }

    @Override
    public Pictures getItem(int position) {
        return tvInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Pictures tvInfo = tvInfoList.get(position);

        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.fav_movie_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.tvMovieTitle.setText(tvInfo.getName());
        ImageUtil.loadImage(mContext, holder.draweeView, tvInfo.getBackdropPath());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvDetailActivity.class)
                        .putExtra(TvDetailActivityFragment.DETAIL_TV, tvInfo.getId())
                        .putExtra("ListToOpen", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, tvInfo.getId().toString());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvInfo.getOriginalTitle());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "movies");

                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }


    private static class ViewHolder {
        final TextView tvMovieTitle;
        final ImageView draweeView;
        final CardView cardView;

        ViewHolder(View itemView) {
            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            draweeView = itemView.findViewById(R.id.img_movie_poster);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }
}
