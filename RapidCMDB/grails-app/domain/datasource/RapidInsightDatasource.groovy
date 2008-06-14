package datasource;
import datasource.RapidInsightAdapter
import org.apache.log4j.Logger
import connection.RapidInsightConnection

class RapidInsightDatasource extends BaseDatasource{
    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    RapidInsightConnection connection ;
    

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
        
     
    }

    static mappedBy=["connection":"rapidInsightDatasources"]
    static belongsTo = []
    def adapter;
    static transients = ["adapter"];
    def onLoad = {
       this.adapter = new RapidInsightAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
