import datasources.NotificationAdapter
import org.apache.log4j.Logger

class SmartsNotificationDatasource extends BaseDatasource{
    def adapter;
    static transients =  ['adapter']


    def onLoad = {
       this.adapter = new NotificationAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
