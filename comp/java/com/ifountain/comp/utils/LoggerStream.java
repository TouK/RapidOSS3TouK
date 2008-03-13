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
 * @author yekmer
 */

package com.ifountain.comp.utils;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;

public class LoggerStream extends PrintStream
{
	private Logger logger;

	public LoggerStream(Logger logger, boolean autoFlush)
	{
		super(System.out, autoFlush);
		this.logger = logger;
	}

    
    
	public void print(boolean b)
	{
		logger.info(String.valueOf(b));
	}


	public void print(char c)
	{
		logger.info(String.valueOf(c));
	}


	public void print(int i)
	{
		logger.info(String.valueOf(i));
	}

	public void print(long l)
	{
		logger.info(String.valueOf(l));
	}


	public void print(float f)
	{
		logger.info(String.valueOf(f));
	}


	public void print(double d)
	{
		logger.info(String.valueOf(d));
	}


	public void print(char s[])
	{
		logger.info(String.valueOf(s));
	}


	public void print(String s)
	{
		if (s == null)
		{
			s = "null";
		}
		logger.info(String.valueOf(s));
	}


	public void print(Object obj)
	{
		logger.info(String.valueOf(obj));
	}


	public void println()
	{
		logger.info("\n");
	}


	public void println(boolean x)
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(char x)
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(int x)
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(long x)
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(float x)
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(double x)
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(char x[])
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(String x)
	{
		logger.info(String.valueOf(x)+"\n");
	}


	public void println(Object x)
	{
		logger.info(String.valueOf(x)+"\n");
	}

    public void write(byte[] b) throws IOException
    {
        int end = b.length; 
        if(b[end-1] == '\n')
        {
            end -= 1;
        }
        logger.info(new String(b, 0, end));
    }
    public void write(byte[] buf, int off, int len)
    {
        int endIndex = off+len-1; 
        
        if(endIndex < buf.length && endIndex >=0 && buf[endIndex] == '\n')
        {
            len -= 1;
        }
        logger.info(new String(buf, off, len));
    }
    public void write(int b)
    {
        logger.info(""+(char)b);
    }

}