package datasource

import com.ifountain.rcmdb.sametime.datasource.SametimeAdapter

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 21, 2009
* Time: 2:37:59 PM
*/
class SametimeDatasourceOperations extends BaseDatasourceOperations{
    SametimeAdapter adapter;
    def onLoad() {
       def ownConnection=getProperty("connection")
       if(ownConnection != null)
       {
            this.adapter = new SametimeAdapter(ownConnection.name, reconnectInterval * 1000, getLogger());
       }
    }

    def sendMessage(target, message){
        this.adapter.sendMessage(target, message);
    }
}