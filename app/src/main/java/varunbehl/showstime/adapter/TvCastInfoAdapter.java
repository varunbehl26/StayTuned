package varunbehl.showstime.adapter;
/**
 * Created by varunbehl on 07/03/17.
 */


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.pojo.Cast.Cast;
import varunbehl.showstime.util.ImageUtil;

public class TvCastInfoAdapter extends RecyclerView.Adapter<TvCastInfoAdapter.ViewHolder> {

    private final Context mContext;
    private List<Cast> tvCastList;
    private LayoutInflater inflater;


    public TvCastInfoAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public TvCastInfoAdapter(Context context, List<Cast> objects, int id) {
        this.mContext = context;
        this.tvCastList = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public TvCastInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.movie_layout, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(TvCastInfoAdapter.ViewHolder holder, final int position) {

        holder.tvMovieTitle.setText(tvCastList.get(position).getOriginalTitle());
        if (tvCastList.get(position).getPosterPath()!=null)
            ImageUtil.loadImageWithFullScreen(mContext, holder.draweeView, tvCastList.get(position).getPosterPath());
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



    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMovieTitle;
        final CardView cardView;
        final ImageView draweeView;

        ViewHolder(View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            draweeView = itemView.findViewById(R.id.img_movie_poster);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}