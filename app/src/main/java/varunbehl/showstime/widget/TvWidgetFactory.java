package varunbehl.showstime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.activity.MainActivity;
import varunbehl.showstime.data.ShowsTimeContract;
import varunbehl.showstime.data.ShowsTimeDBHelper;
import varunbehl.showstime.pojo.TvDetails.TvInfo;

/**
 * Created by varunbehl on 02/04/17.
 */
class TvWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor cursor;
    private Context context;
    private List<TvInfo> tvInfoList;

    public TvWidgetFactory(Context applicationContext, Intent intent) {
        context = applicationContext;
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        tvInfoList = new ArrayList<>();

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        cursor = new ShowsTimeDBHelper(context).getReadableDatabase().rawQuery("Select * from " + ShowsTimeContract.StayTunedEntry.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                TvInfo tvInfo = new TvInfo();
                tvInfo.setId(Integer.parseInt(getDataFromCursor(cursor, ShowsTimeContract.StayTunedEntry.TV_ID)));
                tvInfo.setBackdropPath(getDataFromCursor(cursor, ShowsTimeContract.StayTunedEntry.IMAGE));
                tvInfo.setName(getDataFromCursor(cursor, ShowsTimeContract.StayTunedEntry.NAME));
                tvInfoList.add(tvInfo);

            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
        tvInfoList.clear();
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_movie_layout);
        final TvInfo tvInfo = tvInfoList.get(position);
        rv.setTextViewText(R.id.tv_movie_title, tvInfo.getName());

        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);

        rv.setOnClickPendingIntent(R.id.button, pendingIntent);

        try {
            Bitmap b = Picasso.with(context).load(getImageUri(tvInfo.getBackdropPath())).get();
            rv.setImageViewBitmap(R.id.img_movie_poster, b);
        } catch (IOException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);

        }


        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private String getImageUri(String uri) {
        return "http://image.tmdb.org/t/p/w342" + "/" + uri;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private String getDataFromCursor(Cursor cursor, String Index) {
        return cursor.getString(cursor.getColumnIndex(Index));

    }
}
