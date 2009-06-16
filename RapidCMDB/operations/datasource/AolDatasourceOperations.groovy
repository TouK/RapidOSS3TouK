package datasource

import com.ifountain.rcmdb.aol.datasource.AolAdapter

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 9, 2009
* Time: 4:53:02 PM
*/
class AolDatasourceOperations extends BaseDatasourceOperations {
    AolAdapter adapter;
    def onLoad() {
        this.adapter = new AolAdapter(getProperty("connection").name, reconnectInterval * 1000, getLogger());
    }

    def sendMessage(target, message) {
        this.adapter.sendMessage(target, message);
    }
}