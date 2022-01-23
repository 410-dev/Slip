package localmsgr;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateManager {
    public static String getTimestamp() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        return timeFormatter.format(currentTime);
    }

    public static String getTimestamp(String format) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime currentTime = LocalDateTime.now();
        return timeFormatter.format(currentTime);
    }

    public static String getYear2Digit() {
        return getTimestamp("yyyy").substring(2);
    }

    public static String getYear4Digit() {
        return getTimestamp("yyyy");
    }

    public static String getMonth() {
        return getTimestamp("MM");
    }

    public static String getDate() {
        return getTimestamp("dd");
    }

    public static String getHour24Format() {
        return getTimestamp("HH");
    }

    public static String getHour12Format() {
        int integerForm = Integer.parseInt(getHour24Format());
        if (integerForm > 12) return (integerForm - 12) + "";
        return integerForm + "";
    }

    public static String getAMPM() {
        if (Integer.parseInt(getHour24Format()) >= 12) return "PM";
        return "AM";
    }

    public static String getMinute() {
        return getTimestamp("mm");
    }

    public static String getSecond() {
        return getTimestamp("ss");
    }
}
