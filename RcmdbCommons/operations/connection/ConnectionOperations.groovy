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
package connection

import com.ifountain.rcmdb.connection.RcmdbConnectionManagerAdapter
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.connection.IConnection
import com.ifountain.core.connection.exception.ConnectionException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 2:52:34 PM
 * To change this template use File | Settings | File Templates.
 */
class ConnectionOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    def afterDelete(){
          RcmdbConnectionManagerAdapter.getInstance().removeConnection(this.name);
    }

    def afterUpdate(){
          RcmdbConnectionManagerAdapter.getInstance().addConnection(this.domainObject);
    }
    def afterInsert(){
          RcmdbConnectionManagerAdapter.getInstance().addConnection(this.domainObject);
    }

    public boolean checkConnection()
    {
        try
        {
            IConnection con = ConnectionManager.getConnection(name);
            ConnectionManager.releaseConnection (con);
            return con != null;
        }catch(ConnectionException t)
        {
            throw t.getCause();                
        }
    }
}