package cn.meshee.freechat.util;

import android.support.annotation.NonNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

public class TimeUtils {

    public static String getMsgFormatTime(long msgTimeMillis) {
        DateTime nowTime = new DateTime();
        DateTime msgTime = new DateTime(msgTimeMillis);
        int days = Math.abs(Days.daysBetween(msgTime, nowTime).getDays());
        if (days < 1) {
            return getTime(msgTime);
        } else if (days == 1) {
            return "昨天 " + getTime(msgTime);
        } else if (days <= 7) {
            switch(msgTime.getDayOfWeek()) {
                case DateTimeConstants.SUNDAY:
                    return "周日 " + getTime(msgTime);
                case DateTimeConstants.MONDAY:
                    return "周一 " + getTime(msgTime);
                case DateTimeConstants.TUESDAY:
                    return "周二 " + getTime(msgTime);
                case DateTimeConstants.WEDNESDAY:
                    return "周三 " + getTime(msgTime);
                case DateTimeConstants.THURSDAY:
                    return "周四 " + getTime(msgTime);
                case DateTimeConstants.FRIDAY:
                    return "周五 " + getTime(msgTime);
                case DateTimeConstants.SATURDAY:
                    return "周六 " + getTime(msgTime);
            }
            return "";
        } else {
            return msgTime.toString("MM月dd日 " + getTime(msgTime));
        }
    }

    @NonNull
    private static String getTime(DateTime msgTime) {
        int hourOfDay = msgTime.getHourOfDay();
        String when;
        if (hourOfDay >= 18) {
            when = "晚上";
        } else if (hourOfDay >= 13) {
            when = "下午";
        } else if (hourOfDay >= 11) {
            when = "中午";
        } else if (hourOfDay >= 5) {
            when = "早上";
        } else {
            when = "凌晨";
        }
        return when + " " + msgTime.toString("hh:mm");
    }
}
