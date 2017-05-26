package varunbehl.staytuned.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import varunbehl.staytuned.R;
import varunbehl.staytuned.adapter.TvInfoAdapter;
import varunbehl.staytuned.data.StayTunedContract;
import varunbehl.staytuned.data.StayTunedDBHelper;
import varunbehl.staytuned.pojo.TvDetails.TvInfo;

public class FavouriteActivity extends AppCompatActivity {

    private List<TvInfo> tvInfoList = new ArrayList<>();

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prefs = this.getSharedPreferences(
                "varunbehl.staytuned", Context.MODE_PRIVATE);

        TextView textView = (TextView) findViewById(R.id.no_data_textView);

        GridView myGrid = (GridView) findViewById(R.id.grid_view);

        readFromDatabase();

        if (tvInfoList.size()<1){
            textView.setVisibility(View.VISIBLE);
            myGrid.setVisibility(View.GONE);
        }else{
            textView.setVisibility(View.GONE);
            myGrid.setVisibility(View.VISIBLE);
        }
        myGrid.setAdapter(new TvInfoAdapter(getApplicationContext(), tvInfoList));
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


    private void readFromDatabase() {
        StayTunedDBHelper dbHelper = new StayTunedDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        tvInfoList = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * from " + StayTunedContract.StayTunedEntry.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                TvInfo tvInfo = new TvInfo();
                tvInfo.setId(Integer.parseInt(getDataFromCursor(cursor, StayTunedContract.StayTunedEntry.TV_ID)));
                tvInfo.setBackdropPath(getDataFromCursor(cursor, StayTunedContract.StayTunedEntry.IMAGE));
                tvInfo.setName(getDataFromCursor(cursor, StayTunedContract.StayTunedEntry.NAME));
                String is_fav = cursor.getString(cursor.getColumnIndex(StayTunedContract.StayTunedEntry.IS_FAVORITE));

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("is_fav" + "_" + tvInfo.getId(), Integer.parseInt(is_fav));
                editor.apply();

                tvInfoList.add(tvInfo);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    private String getDataFromCursor(Cursor cursor, String Index) {
        return cursor.getString(cursor.getColumnIndex(Index));

    }
}
