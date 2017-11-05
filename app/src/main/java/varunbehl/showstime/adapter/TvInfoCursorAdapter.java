package varunbehl.showstime.adapter;
/**
 * Created by varunbehl on 07/03/17.
 */


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import varunbehl.showstime.R;
import varunbehl.showstime.activity.TvDetailActivity;
import varunbehl.showstime.fragment.TvDetailActivityFragment;
import varunbehl.showstime.util.ImageUtil;

public class TvInfoCursorAdapter extends CursorAdapter {


    private final Context mContext;

    public TvInfoCursorAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fav_movie_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView tvMovieTitle;
        SimpleDraweeView draweeView;
        CardView cardView;

        tvMovieTitle = view.findViewById(R.id.tv_movie_title);
        draweeView = view.findViewById(R.id.img_movie_poster);
        cardView = view.findViewById(R.id.card_view);


        tvMovieTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        ImageUtil.loadImage(context, draweeView, cursor.getString(cursor.getColumnIndexOrThrow("image")));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvDetailActivity.class)
                        .putExtra(TvDetailActivityFragment.DETAIL_TV, cursor.getInt(cursor.getColumnIndexOrThrow("tv_id")))
                        .putExtra("ListToOpen",2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }

}
