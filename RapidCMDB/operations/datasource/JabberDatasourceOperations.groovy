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
       def ownConnection=getProperty("connection")
       if(ownConnection != null)
       {
            this.adapter = new JabberAdapter(ownConnection.name, reconnectInterval * 1000, getLogger());
       }
    }

    def sendMessage(target, message) {
        this.adapter.sendMessage(target, message);
    }
}