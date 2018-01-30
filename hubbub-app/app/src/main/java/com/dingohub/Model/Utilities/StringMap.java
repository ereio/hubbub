package com.dingohub.Model.Utilities;

/**
 * Created by ereio on 5/17/15.
 */
public class StringMap {

    static public class Status{
        static public String eventCreationSuccess =  "Event Creation Succeeded";
    }
    static public class Lists{
        static public final String hourBefore_0 = "at starting time";
        static public final String hourBefore_1 = "1 hour before";
        static public final String hourBefore_2 = "2 hour before";
        static public final String hourBefore_3 = "3 hour before";
        static public final String hourBefore_4 = "4 hour before";
        static public final String hourBefore_5 = "5 hour before";
        static public final String hourBefore_6 = "6 hour before";
        static public final String hourBefore_7 = "7 hour before";
        static public final String hourBefore_8 = "8 hour before";
        static public final String hourBefore_9 = "9 hour before";
        static public final String hourBefore_10 = "10 hour before";
        static public final String hourBefore_11 = "11 hour before";
        static public final String[] pingInInputList = new String[]{hourBefore_0, hourBefore_1, hourBefore_2, hourBefore_3, hourBefore_4,
                                                       hourBefore_5, hourBefore_6, hourBefore_7, hourBefore_8, hourBefore_9,
                                                       hourBefore_10, hourBefore_11};
    }

    static public class Format{
        static public final String date = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        static public final String INVALID_CHARS = ",~%&*{ }\\< >?/|+\";";
    }

    static public class CloudFunctions{
        static public final String pushNotifications = "pushNotification";
    }
}
