package datasource

import com.ifountain.rcmdb.sms.datasource.SmsAdapter

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 2:38:07 PM
*/
class SmsDatasourceOperations extends BaseDatasourceOperations {
    SmsAdapter adapter;
    def onLoad() {
        this.adapter = new SmsAdapter(getProperty("connection").name, reconnectInterval * 1000, getLogger());
    }

//    def sendMessage(target, message) {
//        this.adapter.sendMessage(target, message);
//    }
}