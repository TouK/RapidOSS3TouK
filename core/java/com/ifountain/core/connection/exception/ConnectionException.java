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
 * Created on Jan 17, 2008
 *
 */
package com.ifountain.core.connection.exception;

import com.ifountain.comp.exception.RException;

public class ConnectionException extends RException
{
    private static final long serialVersionUID = 1L;
    public ConnectionException(String message)
    {
        super(message);
    }
    public ConnectionException(Throwable cause)
    {
        super("An exception occurred while connecting.", cause);
    }

    public ConnectionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public static ConnectionException noConnectionException(String connectionName, Throwable reason)
    {
        return new ConnectionException("Connection "+connectionName+" is lost.", reason);
    }
}
