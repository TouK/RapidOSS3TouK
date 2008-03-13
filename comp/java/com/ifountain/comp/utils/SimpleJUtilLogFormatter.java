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
 * Created on Aug 31, 2006
 *
 */
package com.ifountain.comp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SimpleJUtilLogFormatter extends SimpleFormatter
{
    String lineSep = System.getProperty("line.separator");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    public synchronized String format(LogRecord record)
    {
             StringBuffer buf = new StringBuffer(180);
             buf.append(dateFormat.format(new Date(record.getMillis())));
             buf.append(' ');
             buf.append(record.getLevel());
             buf.append(" : ");
             buf.append(record.getSourceClassName());
             buf.append(' ');
             buf.append(record.getSourceMethodName());
             buf.append(' ');
             buf.append(formatMessage(record));
             buf.append(lineSep);
             Throwable throwable = record.getThrown();
             if (throwable != null)
               {
                 StringWriter sink = new StringWriter();
                 throwable.printStackTrace(new PrintWriter(sink, true));
                 buf.append(sink.toString());
        }
        return buf.toString();
    }
}
