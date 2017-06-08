package varunbehl.staytuned.util;

import android.util.Log;

import java.util.Date;

/**
 * Created by varunbehl on 13/03/17.
 */

public class DateTimeHelper {

    public static boolean getDifference(Long hours) {

        if (hours == 0) {
            return true;
        }
        Date date = new Date(hours);
        Date dt2 = new Date();

        long diff = dt2.getTime() - date.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) ((dt2.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));

        if (diffInDays > 1) {
            Log.v("Diff in number of days" , diffInDays+"");
            return true;
        } else if (diffHours > 3) {
            Log.v("Diff in number of hours" , diffInDays+"");
            return true;
        }
        return false;
    }

}
