package varunbehl.staytuned.data;

import android.provider.BaseColumns;

/**
 * Created by varunbehl on 18/03/17.
 */

public class StayTunedContract {

    private StayTunedContract() {
    }


    public static final class StayTunedEntry implements BaseColumns {
        public static final String TABLE_NAME = "stay_tuned";

        public static final String _ID = BaseColumns._ID;
        public static final String EPISODE_NO = "episode_no";
        public static final String SEASON_NO = "season_no";
        public static final String IS_WATCHED = "is_watched";
        public static final String IS_FAVORITE = "is_favorite";
        public static final String IS_NOTIFIED = "is_notified";
        public static final String DATE_ADDED = "date_added";
        public static final String DATE_MODIFIED = "date_modied";
        public static final String USER_ID = "user_id";
        public static final String TV_ID = "tv_id";
        public static final String RELEASE_DATE = "release_date";
        public static final String RATING = "rating";
        public static final String DESC = "desc";
        public static final String SEASONS = "seasons";
        public static final String IMAGE = "image";
        public static final String NAME = "name";
        public static final String NEXT_AIR_DATE = "next_air_date";
    }
}
