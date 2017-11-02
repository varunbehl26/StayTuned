package varunbehl.showstime.adapter;
/**
 * Created by varunbehl on 07/03/17.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.activity.TvSeasonDetail;
import varunbehl.showstime.pojo.TvDetails.CombinedTvDetail;
import varunbehl.showstime.util.ImageUtil;

public class TvSeasonsAdapter extends RecyclerView.Adapter<TvSeasonsAdapter.ViewHolder> {

    private final Context mContext;
    private List<CombinedTvDetail.Season> tvSeasonList;
    private LayoutInflater inflater;
    private int tvId;


    public TvSeasonsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public TvSeasonsAdapter(Context context, List<CombinedTvDetail.Season> objects, int id) {
        this.mContext = context;
        this.tvSeasonList = objects;
        this.tvId = id;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public TvSeasonsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.cast_layout, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final TvSeasonsAdapter.ViewHolder holder, int position) {

        holder.tvMovieTitle.setText(mContext.getString(R.string.season) + tvSeasonList.get(position).getSeasonNumber().toString());
        ImageUtil.loadImage(mContext, holder.draweeView, tvSeasonList.get(position).getPosterPath());

        holder.draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvSeasonDetail.class);
                intent.putExtra(TvSeasonDetail.TV_ID, tvId);
                intent.putExtra(TvSeasonDetail.SEASON_ID, tvSeasonList.get(holder.getAdapterPosition()).getSeasonNumber());
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
        if (tvSeasonList != null)
            return tvSeasonList.size();
        else
            return 0;
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMovieTitle;
        //        final CardView cardView;
        final ImageView draweeView;

        ViewHolder(View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tx_actual_name);
            draweeView = itemView.findViewById(R.id.img_movie_poster);
//            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}