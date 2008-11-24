package datasource
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Nov 24, 2008
 * Time: 1:22:51 PM
 * To change this template use File | Settings | File Templates.
 */
class OpenNMSHttpAdapter extends HttpAdapter{
     public OpenNMSHttpAdapter(String connectionName, long reconnectInterval, Logger logger) {
        super(connectionName, reconnectInterval, logger);
    }
}