package datasource

import org.apache.log4j.Logger
import com.ifountain.rcmdb.tcp.datasource.TcpListeningAdapter

/**
* Created by Sezgin Kucukkaraaslan
* Date: Oct 28, 2010
* Time: 4:04:44 PM
*/
class TcpListeningDatasourceOperations extends BaseListeningDatasourceOperations {
    def getListeningAdapter(Map params, Logger adapterLogger) {
        def adapter = new TcpListeningAdapter(getProperty("connection").name, adapterLogger);
        if (params.containsKey("endOfEntry")) {
            adapter.setEndOfEntry(params.endOfEntry);
        }
        return adapter;
    }
}