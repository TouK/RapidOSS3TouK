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
       def ownConnection=getProperty("connection")
       if(ownConnection != null)
       {
          this.adapter = new AolAdapter(ownConnection.name, reconnectInterval * 1000, getLogger());
       }
    }

    def sendMessage(target, message) {
        this.adapter.sendMessage(target, message);
    }
}