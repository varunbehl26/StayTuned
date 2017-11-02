package varunbehl.showstime.adapter;
/**
 * Created by varunbehl on 02/04/16.
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
import varunbehl.showstime.pojo.Search.SearchResult;
import varunbehl.showstime.util.ImageUtil;

public class SearchEpisodeAdapter extends ArrayAdapter<SearchResult.Result> {

    private final FirebaseAnalytics mFirebaseAnalytics;
    private final List<SearchResult.Result> searchResultList;
    private final LayoutInflater inflater;
    private final Context mContext;
    private int tvId;

    public SearchEpisodeAdapter(Context context, List<SearchResult.Result> objects, int id) {
        super(context, 0, objects);
        this.mContext = context;
        this.searchResultList = objects;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (searchResultList != null)
            return searchResultList.size();
        else {
            return 0;
        }
    }

    @Override
    public SearchResult.Result getItem(int position) {
        return searchResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SearchResult.Result result = searchResultList.get(position);
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.episode_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.episodeTitle.setText(result.getName());
        holder.episodeDesc.setText(result.getOverview());
        holder.episode_date.setText(mContext.getString(R.string.rating)+result.getVoteAverage());
        if (result.getPosterPath()!=null) {
            ImageUtil.loadImage(mContext, holder.draweeView, result.getPosterPath());
            holder.draweeView.setVisibility(View.VISIBLE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvDetailActivity.class)
                        .putExtra(TvDetailActivityFragment.DETAIL_TV, result.getId())
                        .putExtra("ListToOpen",2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(tvId));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, searchResultList.get(position).getName());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "tv show");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        final TextView episodeTitle;
        final TextView episodeDesc;
        final TextView episode_date;
        final ImageView draweeView;
        final CardView cardView;

        ViewHolder(View itemView) {
            episodeTitle = itemView.findViewById(R.id.episode_name);
            episode_date = itemView.findViewById(R.id.episode_date);
            episodeDesc = itemView.findViewById(R.id.episode_desc);
            draweeView = itemView.findViewById(R.id.img_episode_poster);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}