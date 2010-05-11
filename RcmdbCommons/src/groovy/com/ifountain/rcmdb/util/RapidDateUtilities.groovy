/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
            dateFormat.setLenient(false);
            return dateFormat.parse(formattedString)
        }

        Date.metaClass.'static'.toDate = {String formattedString, String pattern, Locale locale->
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
            dateFormat.setLenient(false);
            return dateFormat.parse(formattedString)
        }


        Date.metaClass.toString = {String pattern->
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(delegate)
        }
    }
}