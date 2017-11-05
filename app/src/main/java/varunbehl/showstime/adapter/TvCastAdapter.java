package varunbehl.showstime.adapter;
/**
 * Created by varunbehl on 07/03/17.
 */


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.activity.TvCastDetailActivity;
import varunbehl.showstime.activity.TvSeasonDetail;
import varunbehl.showstime.pojo.Cast.Cast;
import varunbehl.showstime.util.ImageUtil;

public class TvCastAdapter extends RecyclerView.Adapter<TvCastAdapter.ViewHolder> {

    private final Context mContext;
    private List<Cast> tvCastList;
    private LayoutInflater inflater;
    private int tvId;


    public TvCastAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public TvCastAdapter(Context context, ArrayList<Cast> objects, int id) {
        this.mContext = context;
        this.tvCastList = objects;
        this.tvId = id;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public TvCastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.cast_layout, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final TvCastAdapter.ViewHolder holder, int position) {
        if (tvCastList.get(position).getName() != null)
            holder.txActualName.setText(tvCastList.get(position).getName());
        holder.txCharName.setText(tvCastList.get(position).getCharacter());

        if (tvCastList.get(position).getProfilePath() != null) {
//            ImageUtil.loadImage(mContext, holder.draweeView, tvCastList.get(position).getProfilePath());
            holder.draweeView.setImageURI(Uri.parse(ImageUtil.getImageUri(tvCastList.get(position).getProfilePath())));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvCastDetailActivity.class);
                intent.putExtra(TvSeasonDetail.TV_ID, tvId);
                intent.putExtra(TvCastDetailActivity.CAST_ID, tvCastList.get(holder.getAdapterPosition()).getId());
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView txActualName, txCharName;
        final CardView cardView;
        final SimpleDraweeView draweeView;

        ViewHolder(View itemView) {
            super(itemView);
            txActualName = itemView.findViewById(R.id.tx_actual_name);
            txCharName = itemView.findViewById(R.id.tx_charachter_name);
            draweeView = itemView.findViewById(R.id.img_movie_poster);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}