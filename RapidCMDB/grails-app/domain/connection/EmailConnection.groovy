package connection
import datasource.EmailDatasource

import com.ifountain.core.domain.annotations.*;

class EmailConnection extends Connection
{

    //AUTO_GENERATED_CODE

    public static String SMTP = "Smtp";
    public static String SMTPS = "Smtps";

    static searchable = {
        except = [ "emailDatasources"];
    };
    static cascaded = ["emailDatasources":true]
    static datasources = [:]

    String connectionClass = "connection.EmailConnectionImpl";
    
    String smtpHost ="";
    Long smtpPort =25;
    String username ="";
    String userPassword ="";
    String protocol ="";

    List emailDatasources =[];


    static relations = [
            emailDatasources:[type:EmailDatasource, reverseName:"connection", isMany:true]
    ]

    static constraints={
     smtpHost(blank:false,nullable:false)
     smtpPort(nullable:false)
     username(blank:true,nullable:true)
     userPassword(blank:true,nullable:true)     
     protocol(inList:[SMTP, SMTPS]);
    }

    static propertyConfiguration= [:]
    static transients = [ "emailDatasources"];



    //AUTO_GENERATED_CODE
}