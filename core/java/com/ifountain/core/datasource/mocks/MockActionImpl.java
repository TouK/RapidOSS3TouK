/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.datasource.mocks;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;

public class MockActionImpl implements Action
{
    private IConnection conn;
    private boolean executed = false;
    private Exception exceptionWillBeThrown ; 
    public void execute(IConnection conn) throws Exception
    {
        executed = true;
        this.conn = conn;
        if(exceptionWillBeThrown != null)
        {
            throw exceptionWillBeThrown;
        }
    }
    public IConnection getConnection()
    {
        return conn;
    }
    public boolean isExecuted()
    {
        return executed;
    }
    public void setExceptionWillBeThrown(Exception exceptionWillBeThrown)
    {
        this.exceptionWillBeThrown = exceptionWillBeThrown;
    }
}