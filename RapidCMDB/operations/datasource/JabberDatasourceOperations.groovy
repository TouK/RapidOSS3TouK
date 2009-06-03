package datasource

import com.ifountain.rcmdb.jabber.datasource.JabberAdapter

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:49:41 AM
*/
class JabberDatasourceOperations extends BaseDatasourceOperations {
    JabberAdapter adapter;
    def onLoad() {
        this.adapter = new JabberAdapter(getProperty("connection").name, reconnectInterval * 1000, getLogger());
    }

    def sendMessage(target, message) {
        this.adapter.sendMessage(target, message);
    }
}