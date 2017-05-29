package varunbehl.staytuned.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import varunbehl.staytuned.pojo.TvDetails.TvInfo;

/**
 * Created by varunbehl on 18/03/17.
 */

public class StayTunedProvider extends ContentProvider {

    private static final String AUTHORITY = "varunbehl.staytuned.data.StayTunedProvider";
    private static final int EPISODES= 100;
    private static final int EPISODE_ID = 110;

    private static final String EPISODES_BASE_PATH = "episodes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + EPISODES_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/mt-tutorial";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/mt-tutorial";

    private StayTunedDBHelper stayTunedDBHelper ;


    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, EPISODES_BASE_PATH, EPISODES);
        sURIMatcher.addURI(AUTHORITY, EPISODES_BASE_PATH + "/#", EPISODE_ID);
    }

    private List tvInfoList= new ArrayList();


    @Override
    public boolean onCreate() {
        stayTunedDBHelper = new StayTunedDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(StayTunedContract.StayTunedEntry.TABLE_NAME);
        StayTunedDBHelper dbHelper = new StayTunedDBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + StayTunedContract.StayTunedEntry.TABLE_NAME, null);
        cursor.moveToFirst();
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private String getDataFromCursor(Cursor cursor, String Index) {
        return cursor.getString(cursor.getColumnIndex(Index));

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case EPISODE_ID:
                return StayTunedContract.StayTunedEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        final SQLiteDatabase db =stayTunedDBHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriType) {
            case EPISODE_ID: {
                long _id = db.insert(StayTunedContract.StayTunedEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = StayTunedContract.StayTunedEntry.buildTvUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = stayTunedDBHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case EPISODE_ID:
                rowsDeleted = db.delete(
                        StayTunedContract.StayTunedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = stayTunedDBHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EPISODE_ID:
                rowsUpdated = db.update(StayTunedContract.StayTunedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
