package datasource

import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:45:11 AM
*/
class HypericAdapter extends HttpAdapter{
     public HypericAdapter(String connectionName, long reconnectInterval, Logger logger) {
        super(connectionName, reconnectInterval, logger);
    }
}