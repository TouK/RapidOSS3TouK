package connection
import datasource.JiraDatasource

import com.ifountain.core.domain.annotations.*;

public class JiraConnection extends Connection
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [ "jiraDatasources"];
    };
    static cascaded = ["jiraDatasources":true]
    static datasources = [:]

    String connectionClass = "connection.JiraConnectionImpl";
    
    String username ="";
    String userPassword ="";
    
    List jiraDatasources =[];
    org.springframework.validation.Errors errors ;
    static relations = [
            jiraDatasources:[type:JiraDatasource, reverseName:"connection", isMany:true]
    ]

    static constraints={
     username(blank:true,nullable:true)
     userPassword(blank:true,nullable:true)     
    }

    static propertyConfiguration= [:]
    static transients = ["jiraDatasources"];



    //AUTO_GENERATED_CODE
}