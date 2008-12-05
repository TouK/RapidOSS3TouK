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

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.ConnectionParameterSupplier;

public class MockConnectionParameterSupplierImpl implements ConnectionParameterSupplier
{
    String passedConnConfigName;
    ConnectionParam param;
    
    public ConnectionParam getConnectionParam(String connName)
    {
        return param;
    }
    
    public String getPassedConnConfigName()
    {
        return passedConnConfigName;
    }
    public void setParam(ConnectionParam param)
    {
        this.param = param;
    }
}