package varunbehl.staytuned.adapter;
/**
 * Created by varunbehl on 07/03/17.
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
import varunbehl.staytuned.pojo.TvDetails.TvInfo;

public class TvInfoAdapter extends ArrayAdapter<TvInfo> {

    private List<TvInfo> tvInfoList;
    private LayoutInflater inflater;
    private Context mContext;

    public TvInfoAdapter(Context context, List<TvInfo> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.tvInfoList = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (tvInfoList!=null)
        return tvInfoList.size();
        else
            return 0;
    }

    @Override
    public TvInfo getItem(int position) {
        return tvInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TvInfo tvInfo = tvInfoList.get(position);

        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.fav_movie_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.tvMovieTitle.setText(tvInfo.getName());
        holder.draweeView.setImageURI(getImageUri(tvInfo.getBackdropPath()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class)
                        .putExtra(DetailActivityFragment.DETAIL_TV, tvInfo.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private String getImageUri(String uri) {
        String IMAGE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
        return IMAGE_POSTER_BASE_URL + "/" + uri;
    }

    private static class ViewHolder {
        TextView tvMovieTitle;
        SimpleDraweeView draweeView;
        CardView cardView;

        ViewHolder(View itemView) {
            tvMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.img_movie_poster);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }
    }
}
