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
package com.ifountain.comp.exception;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

public class RapidException extends RException{
	 protected Object [] customErrorMessages;
	    public final static ExceptionMessagesMap messagesMap = ExceptionMessagesMap.getExceptionMessageMap();
	    protected String errorId;
	    protected String message = "Unknown Error";
	    public RapidException(String errorId) {

	        this(errorId, null);
	    }
	    
	    public RapidException(String errorId, Object[] customMessages) {
	    	
	    	this.errorId = errorId;
	    	setCustomErrorMessages(customMessages);
	    }

	    public String getErrorId() {

	        return errorId;
	    }
	    
	    

	    public void setCustomErrorMessages(Object[] customErrorMessages) {
	        this.customErrorMessages = customErrorMessages;
	        String errorMessage = messagesMap.getExceptionMessage(errorId);
	        if(errorMessage != null)
	        {
	        	message = MessageFormat.format(errorMessage, customErrorMessages);
	        }
	    }

	    public String getMessage() {
	        return message;

	    }

	    public void setErrorId(String errorId) {

	        this.errorId = errorId;
	    }
	    
	    public static String[] getAllErrorIds()
	    {
	        String[] errorMessageIds = messagesMap.getMessageCodes();
	        return errorMessageIds;
	    }
	    
	    public String toString()
	    {
	        return "[" + errorId + "] - " + getMessage();
	    }
	    
	    public static void main(String[] args) throws IOException
	    {
	        FileOutputStream out = new FileOutputStream("exceptionmessages.txt");
	        RapidException.messagesMap.listMessages(out);
	        out.close();
	    }
	    
	    public boolean equals(Object anotherRapidException)
	    {
	    	if(anotherRapidException == null)	return false;
	    	if(!(anotherRapidException instanceof RapidException))	return false;
	    	RapidException e = (RapidException) anotherRapidException;
	    	return e.toString().equals(toString());
	    }

		public void setMessage(String message) {
			this.message = message;
		}
}
