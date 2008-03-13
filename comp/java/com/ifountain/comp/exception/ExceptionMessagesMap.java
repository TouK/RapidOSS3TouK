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
/*
 * Created on Aug 17, 2006
 *
 */
package com.ifountain.comp.exception;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ExceptionMessagesMap
{
	private static ExceptionMessagesMap map;
	private ExceptionMessagesMap()
	{
	}
	
	public static ExceptionMessagesMap getExceptionMessageMap()
	{
		if(map == null)
		{
			map =  new ExceptionMessagesMap();
		}
		return map;
	}
    private Map exceptionMessages = new HashMap();

    public void addExceptionMessage(String messageCode, String messageContext)
    {
        exceptionMessages.put(messageCode, messageContext);
    }

    public String getExceptionMessage(String messageCode)
    {
        return (String)exceptionMessages.get(messageCode);
    }

    public String[] getMessageCodes()
    {
        return (String[])exceptionMessages.keySet().toArray(new String[0]);
    }
    
    public void listMessages(OutputStream out)
    {
        PrintWriter pw = new PrintWriter(out);
        Set keySet = exceptionMessages.keySet();
        String[] keys = (String[]) keySet.toArray(new String[0]);
        Arrays.sort(keys);
        for (int i = 0; i < keys.length; i++)
        {
            String value = exceptionMessages.get(keys[i]).toString();
            pw.println(keys[i] + "\t" + value);
        }
        pw.close();
    }

	public void clearMessages() {
		exceptionMessages.clear();
	}
    
}
