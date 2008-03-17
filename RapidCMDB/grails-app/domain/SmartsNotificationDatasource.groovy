import datasources.NotificationAdapter
import org.apache.log4j.Logger

class SmartsNotificationDatasource extends BaseDatasource{
    SmartsConnection connection;
    def adapter;
    static transients =  ['adapter']


    def onLoad = {
       this.adapter = new NotificationAdapter(connection.name, 0, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName)
     {
         def prop = this.adapter.getNotification (keys.ClassName, keys.InstanceName, keys.EventName, [propName]);
         if(prop)
         {
             return prop[propName];
         }
         return "";
     }

     def getProperties(Map keys, List properties)
     {
         def prop = this.adapter.getNotification (keys.ClassName, keys.InstanceName, keys.EventName);
         return prop;
     }
}
