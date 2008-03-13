/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
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
package com.ifountain.comp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class StringUtils {
	
	public static String escapeXML(Object obj) {
        if(obj == null)
        {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        String str = obj.toString();
        int lengthOfStr = str.length();
        for (int i = 0; i < lengthOfStr; i++) {
            char c = str.charAt(i);
            if(c == '&')
            {
                buffer.append("&amp;");
            }
            else if(c == '<')
            {
                buffer.append( "&lt;");
            }
            else if(c == '>')
            {
                buffer.append( "&gt;");
            }
            else if(c == '\"')
            {
                buffer.append("&quot;");
            }
            else if(c == '\'')
            {
                buffer.append("&apos;");
            }
            else
            {
                buffer.append(c);
            }
        }
    	return buffer.toString();
    }

    public static final String DATE_FORMAT_PATTERN = "HH:mm MMM dd, yyyy";

    public static String formatDate(long dateAsMillis)
    {
    	return new SimpleDateFormat(DATE_FORMAT_PATTERN).format(new Date(dateAsMillis));
    }
}	
