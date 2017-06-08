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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.activity.DetailActivity;
import varunbehl.showstime.activity.MovieDetailActivityFragment;
import varunbehl.showstime.pojo.Picture.Pictures;

public class MoviesInfoAdapter extends ArrayAdapter<Pictures> {

    private final String IMAGE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private List<Pictures> movieArrayList;
    private LayoutInflater inflater;
    private Context mContext;
    private FirebaseAnalytics mFirebaseAnalytics;


    public MoviesInfoAdapter(Context context, List<Pictures> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.movieArrayList = objects;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return movieArrayList.size();
    }

    @Override
    public Pictures getItem(int position) {
        return movieArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Pictures movie = movieArrayList.get(position);
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.fav_movie_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.tvMovieTitle.setText(movie.getTitle());

        holder.draweeView.setImageURI(getImageUri(movie.getPosterPath()));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class)
                        .putExtra(MovieDetailActivityFragment.DETAIL_TV, movieArrayList.get(position).getId())
                        .putExtra("ListToOpen", 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movieArrayList.get(position).getId().toString());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieArrayList.get(position).getTitle());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "movies");

                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public String getImageUri(String uri) {
        return IMAGE_POSTER_BASE_URL + "/" + uri;
    }

    static class ViewHolder {
        TextView tvMovieTitle;
        CardView cardView;
        SimpleDraweeView draweeView;

        public ViewHolder(View itemView) {
            tvMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.img_movie_poster);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}