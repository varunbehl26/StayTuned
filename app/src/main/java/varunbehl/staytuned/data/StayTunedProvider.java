package varunbehl.staytuned.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        StayTunedDBHelper dbHelper = null;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EPISODE_ID:
                queryBuilder.appendWhere(StayTunedContract.StayTunedEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            case EPISODES:
                // no filter
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        dbHelper = new StayTunedDBHelper(getContext());

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
