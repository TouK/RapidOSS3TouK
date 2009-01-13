package connector
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 13, 2009
 * Time: 10:42:32 AM
 * To change this template use File | Settings | File Templates.
 */
class EmailConnectorOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def getEmailConnectionName(String connectorName)
    {
       return "${connectorName}";
    }
    static def getEmailDatasourceName(String connectorName)
    {
       return "${connectorName}connectorDs";
    }    
}