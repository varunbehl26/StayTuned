package varunbehl.staytuned.adapter;
/**
 * Created by varunbehl on 07/03/17.
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import varunbehl.staytuned.R;
import varunbehl.staytuned.activity.DetailActivity;
import varunbehl.staytuned.activity.DetailActivityFragment;
import varunbehl.staytuned.pojo.TvDetails.TvInfo;

public class TvDataAdapter extends RecyclerView.Adapter<TvDataAdapter.ViewHolder> {

    private List<TvInfo> tvShows ;
    private LayoutInflater inflater;
    private Context mContext;
    private FirebaseAnalytics mFirebaseAnalytics;

    public TvDataAdapter(Context context, List<TvInfo> objects) {
        this.mContext = context;
        this.tvShows = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

    }


    @Override
    public TvDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.movie_layout, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(TvDataAdapter.ViewHolder holder, final int position) {
        holder.tvMovieTitle.setText(tvShows.get(position).getName());
        holder.draweeView.setImageURI(getImageUri(tvShows.get(position).getPosterPath()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class)
                        .putExtra(DetailActivityFragment.DETAIL_TV, tvShows.get(position).getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, tvShows.get(position).getId().toString());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvShows.get(position).getName());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "tv show");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (tvShows!=null)
        return tvShows.size();
        else{
            return 0;
        }
    }


    private String getImageUri(String uri) {
        String IMAGE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
        return IMAGE_POSTER_BASE_URL + "/" + uri;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle;
        CardView cardView;
        SimpleDraweeView draweeView;

        ViewHolder(View itemView) {
            super(itemView);
            tvMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.img_movie_poster);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}