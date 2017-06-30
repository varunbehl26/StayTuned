package varunbehl.showstime.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

import varunbehl.showstime.pojo.TvDetails.TvInfo;

/**
 * Created by varunbehl on 18/03/17.
 */

public class ShowsTimeDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "StayTuned.db";


    public ShowsTimeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void addintoDB(TvInfo tvInformation, Context context, int tvId) {

        ShowsTimeDBHelper dbHelper = new ShowsTimeDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!checkIfTvInfoExists(tvId, context)) {
            values.put(ShowsTimeContract.StayTunedEntry.RELEASE_DATE, tvInformation.getFirstAirDate());
            values.put(ShowsTimeContract.StayTunedEntry.RATING, tvInformation.getVoteCount());
            values.put(ShowsTimeContract.StayTunedEntry.DESC, tvInformation.getOverview());
            values.put(ShowsTimeContract.StayTunedEntry.SEASONS, tvInformation.getNumberOfSeasons());
            values.put(ShowsTimeContract.StayTunedEntry.IMAGE, tvInformation.getBackdropPath());
            values.put(ShowsTimeContract.StayTunedEntry.EPISODE_NO, 0);
            values.put(ShowsTimeContract.StayTunedEntry.SEASON_NO, 0);
            values.put(ShowsTimeContract.StayTunedEntry.IS_WATCHED, 0);
            values.put(ShowsTimeContract.StayTunedEntry.IS_FAVORITE, 1);
            values.put(ShowsTimeContract.StayTunedEntry.IS_NOTIFIED, 0);
            values.put(ShowsTimeContract.StayTunedEntry.DATE_ADDED, new Date().toString());
            values.put(ShowsTimeContract.StayTunedEntry.DATE_MODIFIED, new Date().toString());
            values.put(ShowsTimeContract.StayTunedEntry.USER_ID, Math.random());
            values.put(ShowsTimeContract.StayTunedEntry.TV_ID, tvId);
            values.put(ShowsTimeContract.StayTunedEntry.NAME, tvInformation.getName());

            long rowUpdated = db.insert(ShowsTimeContract.StayTunedEntry.TABLE_NAME, null, values);

            Log.v("Row Updated: ", rowUpdated + "");
        }
    }

    private static boolean checkIfTvInfoExists(int tvId, Context context) {
        ShowsTimeDBHelper dbHelper = new ShowsTimeDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String SQL_CHECK_TABLE = "Select " + ShowsTimeContract.StayTunedEntry._ID + " from " + ShowsTimeContract.StayTunedEntry.TABLE_NAME +
                " where " + ShowsTimeContract.StayTunedEntry.TV_ID
                + " = " + tvId + "";
        Cursor cursor = db.rawQuery(SQL_CHECK_TABLE, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + ShowsTimeContract.StayTunedEntry.TABLE_NAME + "("
                + ShowsTimeContract.StayTunedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ShowsTimeContract.StayTunedEntry.EPISODE_NO + " INTEGER NOT NULL,"
                + ShowsTimeContract.StayTunedEntry.SEASON_NO + " INTEGER NOT NULL,"
                + ShowsTimeContract.StayTunedEntry.IS_WATCHED + " INTEGER ,"
                + ShowsTimeContract.StayTunedEntry.IS_FAVORITE + " INTEGER ,"
                + ShowsTimeContract.StayTunedEntry.IS_NOTIFIED + " INTEGER ,"
                + ShowsTimeContract.StayTunedEntry.DATE_ADDED + " DATE NOT NULL,"
                + ShowsTimeContract.StayTunedEntry.DATE_MODIFIED + " DATE NOT NULL,"
                + ShowsTimeContract.StayTunedEntry.USER_ID + " INTEGER ,"
                + ShowsTimeContract.StayTunedEntry.RELEASE_DATE + " DATE ,"
                + ShowsTimeContract.StayTunedEntry.RATING + " INTEGER,"
                + ShowsTimeContract.StayTunedEntry.DESC + " TEXT,"
                + ShowsTimeContract.StayTunedEntry.SEASONS + " INTEGER,"
                + ShowsTimeContract.StayTunedEntry.IMAGE + " TEXT,"
                + ShowsTimeContract.StayTunedEntry.NAME + " TEXT NOT NULL ,"
                + ShowsTimeContract.StayTunedEntry.NEXT_AIR_DATE + " DATE ,"
                + ShowsTimeContract.StayTunedEntry.TV_ID + " INTEGER NOT NULL)";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ShowsTimeContract.StayTunedEntry.TABLE_NAME);
        onCreate(db);
    }


    public void insertIntoDb(Date date, int tvId, Context context) {

        ShowsTimeDBHelper dbHelper = new ShowsTimeDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShowsTimeContract.StayTunedEntry.NEXT_AIR_DATE, date.toString());
        db.update(ShowsTimeContract.StayTunedEntry.TABLE_NAME, values, ShowsTimeContract.StayTunedEntry.TV_ID + "=" + tvId, null);

    }
}
