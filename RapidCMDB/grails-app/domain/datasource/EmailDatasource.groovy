package datasource
import connection.EmailConnection

import com.ifountain.core.domain.annotations.*;

class EmailDatasource extends BaseDatasource
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]


    Long reconnectInterval =0;
    EmailConnection connection ;


    static relations = [
        connection:[type:EmailConnection, reverseName:"emailDatasources", isMany:false]
    ]

    static constraints={
     reconnectInterval(nullable:false)
     connection(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["connection"];


    //AUTO_GENERATED_CODE
}