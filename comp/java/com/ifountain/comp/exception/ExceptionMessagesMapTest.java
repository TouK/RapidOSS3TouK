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


import junit.framework.TestCase;


public class ExceptionMessagesMapTest extends TestCase
{
	protected void setUp() throws Exception {
		
		super.setUp();
		ExceptionMessagesMap.getExceptionMessageMap().clearMessages();
	}
	
    public void testAddExceptionMessage() throws Exception
    {
        String messageCode = "RR-001";
        String messageContext ="This is an exception";
        
        ExceptionMessagesMap map = ExceptionMessagesMap.getExceptionMessageMap();
        
        assertNull(map.getExceptionMessage(messageCode));
        
        map.addExceptionMessage(messageCode, messageContext);
        
        assertEquals(messageContext, map.getExceptionMessage(messageCode));
    }
    
    
    
    public void testGetMessageCodes() throws Exception
    {
        String messageCode1 = "RR-001";
        String messageCode2 = "RR-002";
        ExceptionMessagesMap.getExceptionMessageMap().clearMessages();
        ExceptionMessagesMap map = ExceptionMessagesMap.getExceptionMessageMap();
        String messageContext1 = "error1";
        map.addExceptionMessage(messageCode1, messageContext1);
        assertEquals(messageContext1, map.getExceptionMessage(messageCode1));
        
        String[] codes = map.getMessageCodes();
        
        assertEquals(1, codes.length);
        assertEquals(messageCode1, codes[0]);
        
        String messageContext2 = "error2";
        map.addExceptionMessage(messageCode2, messageContext2);
        
       
       assertEquals(messageContext2, map.getExceptionMessage(messageCode2));
       
       codes = map.getMessageCodes();
       
       assertEquals(2, codes.length);
       boolean isCode1Found = false;
       boolean isCode2Found = false;
       for (int i = 0; i < codes.length; i++)
       {
            if(codes[i].equals("RR-001"))
            {
                isCode1Found = true;
            }
            else if(codes[i].equals("RR-002"))
            {
                isCode2Found = true;
            } 
       }
       assertTrue(isCode1Found);
       assertTrue(isCode2Found);
    }
}
