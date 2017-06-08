package varunbehl.showstime.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import varunbehl.showstime.R;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        int result = getIntent().getIntExtra("ListToOpen", 1);
        switch (result) {
            case 1:
                MovieDetailActivityFragment mFragment = new MovieDetailActivityFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, mFragment).commit();
                break;
            case 2:
                TvDetailActivityFragment tvShowFragment = new TvDetailActivityFragment();
                android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.frameLayout, tvShowFragment).commit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
