package varunbehl.showstime.adapter;
/**
 * Created by varunbehl on 02/04/16.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import varunbehl.showstime.FullscreenImageViewActivity;
import varunbehl.showstime.R;
import varunbehl.showstime.util.ImageUtil;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final ArrayList<String> movieArrayList;
    private final Context mContext;

    public ImageAdapter(Context context, ArrayList<String> objects) {
        this.mContext = context;
        this.movieArrayList = objects;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.image_layout, null);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImageUtil.loadImage(mContext, holder.draweeView, movieArrayList.get(position));

        holder.draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FullscreenImageViewActivity.class);
                intent.putExtra("Image_Path", movieArrayList.get(holder.getAdapterPosition()));
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
        if (movieArrayList != null) {
            return movieArrayList.size();
        } else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        final SimpleDraweeView draweeView;

        public ViewHolder(View itemView) {
            super(itemView);
            draweeView = itemView.findViewById(R.id.image_view);

        }
    }

}