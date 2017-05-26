package varunbehl.staytuned.adapter;
/**
 * Created by varunbehl on 07/03/17.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import varunbehl.staytuned.R;
import varunbehl.staytuned.activity.TvCastDetail;
import varunbehl.staytuned.activity.TvSeasonDetail;
import varunbehl.staytuned.pojo.Cast.CastInfo;

public class TvCrewAdapter extends RecyclerView.Adapter<TvCrewAdapter.ViewHolder> {

    private List<CastInfo.Crew> tvCastList;
    private LayoutInflater inflater;
    private Context mContext;
    private int tvId;


    public TvCrewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public TvCrewAdapter(Context context, List<CastInfo.Crew> objects, int id) {
        this.mContext = context;
        this.tvCastList = objects;
        this.tvId = id;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public TvCrewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.movie_layout, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(TvCrewAdapter.ViewHolder holder, final int position) {

        holder.tvMovieTitle.setText(tvCastList.get(position).getOriginalTitle());
        holder.draweeView.setImageURI(getImageUri(tvCastList.get(position).getPosterPath()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvCastDetail.class);
                intent.putExtra(TvSeasonDetail.TV_ID, tvId);
                intent.putExtra(TvCastDetail.CAST_ID, tvCastList.get(position).getId());
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
        if (tvCastList != null)
            return tvCastList.size();
        else
            return 0;
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