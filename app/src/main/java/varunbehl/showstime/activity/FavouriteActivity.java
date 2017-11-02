package varunbehl.showstime.activity;

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

import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.adapter.TvInfoCursorAdapter;
import varunbehl.showstime.data.ShowsTimeContract;
import varunbehl.showstime.pojo.TvDetails.CombinedTvDetail;

public class FavouriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TvInfoCursorAdapter tvInfoCursorAdapter;
    private List<CombinedTvDetail> tvInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favourite Tv Shows");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();


        TextView textView = findViewById(R.id.no_data_textView);
        GridView myGrid = findViewById(R.id.grid_view);

        // Here we query database
        Cursor mCursor = null;
        try {
            mCursor = getContentResolver().query(
                    ShowsTimeContract.StayTunedEntry.CONTENT_URI,
                    new String[]{ShowsTimeContract.StayTunedEntry._ID, ShowsTimeContract.StayTunedEntry.NAME, ShowsTimeContract.StayTunedEntry.TV_ID,},
                    null,
                    null,
                    null);

            tvInfoCursorAdapter = new TvInfoCursorAdapter(this, mCursor);

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        } finally {
            assert mCursor != null;
            mCursor.close();
        }


        SharedPreferences prefs = this.getSharedPreferences(
                "varunbehl.showstime", Context.MODE_PRIVATE);


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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ShowsTimeContract.StayTunedEntry.CONTENT_URI,
                new String[]{ShowsTimeContract.StayTunedEntry._ID, ShowsTimeContract.StayTunedEntry.NAME, ShowsTimeContract.StayTunedEntry.TV_ID,}, null, null,
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
