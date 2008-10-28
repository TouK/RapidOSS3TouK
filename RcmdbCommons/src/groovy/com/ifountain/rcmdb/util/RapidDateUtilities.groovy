package com.ifountain.rcmdb.util

import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 28, 2008
 * Time: 6:46:53 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidDateUtilities {
    public static void registerDateUtils()
    {
        Date.metaClass.'static'.now = {
            return System.currentTimeMillis();
        }


        Date.metaClass.'static'.toDate = {long msecs->
            return new Date(msecs);
        }

        Date.metaClass.'static'.toDate = {String msecs->
            return new Date(msecs.toLong().longValue());
        }

        Date.metaClass.'static'.toDate = {String formattedString, String pattern->
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(formattedString)
        }


        Date.metaClass.toString = {String pattern->
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(delegate)
        }
    }
}