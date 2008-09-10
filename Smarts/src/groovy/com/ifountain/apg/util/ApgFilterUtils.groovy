package com.ifountain.apg.util

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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 10:44:12 AM
 * To change this template use File | Settings | File Templates.
 */
class ApgFilterUtils {

    public static String sanitizeProperty(String property) {
        if (property == null) {
            return null;
        }
        StringBuffer ret = new StringBuffer(property.length());
        for (int i = 0; i < property.length(); i++) {
            char c = property.charAt(i);
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'
                    || Character.isDigit(c)) {
                ret.append(c);
            }
        }
        return ret.length() > 0 ? ret.toString() : null;
    }
    public static String makeFilter(String property, String value) {
        property = sanitizeProperty(property);
        if (property == null) {
            return null;
        }
        if (value == null) {
            return property;
        }
        StringBuffer ret = new StringBuffer(property).append("=='");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\r':
                case '\n':
                    // skip
                    break;
                case '\t':
                    ret.append('\\');
                    ret.append('t');
                    break;
                case '\'':
                    ret.append('\\');
                    ret.append('\'');
                    break;
                case '\\':
                    ret.append('\\');
                    ret.append('\\');
                    break;
                default:
                    ret.append(c);
                    break;
            }
        }
        return ret.append("'").toString();
    }

}