package com.ifountain.rcmdb.util

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.apache.commons.lang.StringUtils

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
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 * To change this template use File | Settings | File Templates.
 */
 class Utils {
     public static long now(){
		long sec = (System.currentTimeMillis())/1000;
	 	return sec;
	}

	public static String substringAfter(String string, String separatorString) {
	    return StringUtils.substringAfter(string, separatorString);
	}

	public static String substringAfterLast(String string, String separatorString) {
	    return StringUtils.substringAfterLast(string, separatorString);
	}

	public static String substringBefore(String string, String separatorString) {
	    return StringUtils.substringBefore(string, separatorString);
	}

	public static String substringBeforeLast(String string, String separatorString) {
	    return StringUtils.substringBeforeLast(string, separatorString);
	}

	public static String substringBetween(String string, String tagString) {
	    String result = StringUtils.substringBetween(string, tagString);
	    if (result == null)
	        return "";
	    return result;
	}

	public static boolean containsNone(String string, String invalidChars) {
	    return StringUtils.containsNone(string, invalidChars);
	}

	public static boolean containsOnly(String string, String validChars) {
	    return StringUtils.containsOnly(string, validChars);
	}

	public static long countMatches(String string, String subString) {
	    return new Long(StringUtils.countMatches(string, subString));
	}

	public static String difference(String string, String str2) {
	    return StringUtils.difference(string, str2);
	}

	public static boolean isBlank(String string) {
	    return StringUtils.isBlank(string);
	}

	public static String mid(String string, int arg1, int arg2) {
	    return StringUtils.mid(string, arg1, arg2);
	}

	public static long toUnixDate(String dateString, String pattern) throws Exception{
	    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
	    Date date = sdf.parse(dateString);
	    long sec = (date.getTime())/1000;
	    return sec;
	}

	def fromUnixDate(long dateNumber, String pattern) throws NumberFormatException {
	    def longVal = dateNumber.toLong()*1000;
		def date = new Date(longVal);
		def dateFormatter = new SimpleDateFormat(pattern);
		return dateFormatter.format(date);
	}

	public static String substitute(String string, String patternString, int groupIndex, String replacementString)
	{
		Matcher m = getMatcher(patternString, string, groupIndex);
		if(m.matches())
		{
			int startOfMatch = m.start(groupIndex);
	    	int endOfMatch = m.end(groupIndex);
	    	StringBuffer substituted = new StringBuffer("");
	    	substituted.append(string.substring(0, startOfMatch));
	    	substituted.append(replacementString);
	    	if(endOfMatch < string.length())
	    	{
	    		substituted.append(string.substring(endOfMatch, string.length()));
	    	}
	    	return substituted.toString();
		}
		return string;
	}

	public static String extract(String stringToBeParsed, String pattern, int groupIndex)
	    {
	    	Matcher m = getMatcher(pattern, stringToBeParsed, groupIndex);
			if(m.matches())
			{
				return m.group(groupIndex);
			}
			return "";
	    }

	private static Matcher getMatcher(String pattern, String input, int groupIndex)
	{
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input);
		if(m.groupCount() < groupIndex)
		{
			throw new Exception("groupIndex <" + groupIndex+ "> should be less than or equal to number of groups <" + m.groupCount() + ">");
		}
		return m;
	}
}