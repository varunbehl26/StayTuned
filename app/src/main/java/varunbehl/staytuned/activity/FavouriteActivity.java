package varunbehl.staytuned.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import varunbehl.staytuned.R;
import varunbehl.staytuned.adapter.TvInfoCursorAdapter;
import varunbehl.staytuned.data.StayTunedContract;
import varunbehl.staytuned.pojo.TvDetails.TvInfo;

public class FavouriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TvInfoCursorAdapter tvInfoCursorAdapter;
    private List<TvInfo> tvInfoList = new ArrayList<>();
    private SharedPreferences prefs;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();


        // Here we query database
        mCursor = getContentResolver().query(
                StayTunedContract.StayTunedEntry.CONTENT_URI,
                new String[]{StayTunedContract.StayTunedEntry._ID, StayTunedContract.StayTunedEntry.NAME, StayTunedContract.StayTunedEntry.TV_ID,},
                null,
                null,
                null);


        tvInfoCursorAdapter = new TvInfoCursorAdapter(this, mCursor);

        prefs = this.getSharedPreferences(
                "varunbehl.staytuned", Context.MODE_PRIVATE);

        TextView textView = (TextView) findViewById(R.id.no_data_textView);

        GridView myGrid = (GridView) findViewById(R.id.grid_view);

//        readFromDatabase();

        if (mCursor.getCount() < 1) {
            textView.setVisibility(View.VISIBLE);
            myGrid.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            myGrid.setVisibility(View.VISIBLE);
        }
        myGrid.setAdapter(tvInfoCursorAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    private void readFromDatabase() {
//        StayTunedDBHelper dbHelper = new StayTunedDBHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        tvInfoList = new ArrayList<>();
//        Cursor cursor = db.rawQuery("Select * from " + StayTunedContract.StayTunedEntry.TABLE_NAME, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                TvInfo tvInfo = new TvInfo();
//                tvInfo.setId(Integer.parseInt(getDataFromCursor(cursor, StayTunedContract.StayTunedEntry.TV_ID)));
//                tvInfo.setBackdropPath(getDataFromCursor(cursor, StayTunedContract.StayTunedEntry.IMAGE));
//                tvInfo.setName(getDataFromCursor(cursor, StayTunedContract.StayTunedEntry.NAME));
//                String is_fav = cursor.getString(cursor.getColumnIndex(StayTunedContract.StayTunedEntry.IS_FAVORITE));
//
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putInt("is_fav" + "_" + tvInfo.getId(), Integer.parseInt(is_fav));
//                editor.apply();
//
//                tvInfoList.add(tvInfo);
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//    }


    private String getDataFromCursor(Cursor cursor, String Index) {
        return cursor.getString(cursor.getColumnIndex(Index));

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, StayTunedContract.StayTunedEntry.CONTENT_URI,
                new String[]{StayTunedContract.StayTunedEntry._ID, StayTunedContract.StayTunedEntry.NAME, StayTunedContract.StayTunedEntry.TV_ID,}, null, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        tvInfoCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
