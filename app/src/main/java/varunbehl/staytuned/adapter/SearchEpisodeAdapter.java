package varunbehl.staytuned.adapter;
/**
 * Created by varunbehl on 02/04/16.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import varunbehl.staytuned.R;
import varunbehl.staytuned.activity.DetailActivity;
import varunbehl.staytuned.activity.DetailActivityFragment;
import varunbehl.staytuned.pojo.Search.SearchResult;

public class SearchEpisodeAdapter extends ArrayAdapter<SearchResult.Result> {

    private List<SearchResult.Result> searchResultList;
    private LayoutInflater inflater;
    private Context mContext;

    public SearchEpisodeAdapter(Context context, List<SearchResult.Result> objects, int id) {
        super(context, 0, objects);
        this.mContext = context;
        this.searchResultList = objects;
        int tvId = id;
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
        holder.episode_date.setText("Rating:"+result.getVoteAverage());
        if (result.getPosterPath()!=null) {
            holder.draweeView.setImageURI(getImageUri(result.getPosterPath()));
            holder.draweeView.setVisibility(View.VISIBLE);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), EpisodeActivity.class);
//                intent.putExtra(EpisodeActivity.TV_ID, tvId);
//                intent.putExtra(EpisodeActivity.SEASON_ID, episode.getSeasonNumber());
//                intent.putExtra(EpisodeActivity.EPISODE_ID, episode.getEpisodeNumber());
//                mContext.startActivity(intent);
            }
        });


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class)
                        .putExtra(DetailActivityFragment.DETAIL_TV, result.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Bundle bundle = new Bundle();
//                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, result.getId().toString());
//                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, result.getName().toString());
//                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "tv show");
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    private String getImageUri(String uri) {
        String IMAGE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
        return IMAGE_POSTER_BASE_URL + "/" + uri;
    }

    static class ViewHolder {
        TextView episodeTitle, episodeDesc, episode_date;
        SimpleDraweeView draweeView;
        CardView cardView;

        ViewHolder(View itemView) {
            episodeTitle = (TextView) itemView.findViewById(R.id.episode_name);
            episode_date = (TextView) itemView.findViewById(R.id.episode_date);
            episodeDesc = (TextView) itemView.findViewById(R.id.episode_desc);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.img_episode_poster);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}