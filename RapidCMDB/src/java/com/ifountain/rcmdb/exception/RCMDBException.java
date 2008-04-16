package com.ifountain.rcmdb.exception;

import com.ifountain.comp.exception.RapidException;

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
public class RCMDBException extends RapidException{
    private static final String RCMDB_MESSAGE_PREFIX = "RCMDB";
       public static final String FILE_NOT_FOUND = RCMDB_MESSAGE_PREFIX + "-0001";
       public static final String ERROR_CONNECT_SERVER = RCMDB_MESSAGE_PREFIX + "-0003";
       public static final String FILE_IO_EXCEPTION = RCMDB_MESSAGE_PREFIX + "-0004";

       static
       {
           messagesMap.addExceptionMessage(FILE_NOT_FOUND, "The file {0} cannot be found");
           messagesMap.addExceptionMessage(ERROR_CONNECT_SERVER, "Could not connect to Rapid Server.");
           messagesMap.addExceptionMessage(FILE_IO_EXCEPTION, "An error occurred while reading file {0}. Reason : {1}");
       }
    

    public RCMDBException(String errorId) {
        super(errorId);
    }

    public RCMDBException(String errorId, Object[] customMessages) {
        super(errorId, customMessages);
    }
}
