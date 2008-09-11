package connection

import com.ifountain.core.connection.ConnectionManager

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 2:52:34 PM
 * To change this template use File | Settings | File Templates.
 */
class ConnectionOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    def beforeDelete = {
          ConnectionManager.removeConnection(this.name);
    }
}