/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 11:30:34 AM
 * To change this template use File | Settings | File Templates.
 */
import com.ifountain.core.domain.annotations.*;
class NetcoolJournal {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["keys":["keyfield":["nameInDs":"keyfield"], "servername":["nameInDs":"servername"]]]]

    
    java.lang.Long serverserial =0;
    
    java.lang.String keyfield ="";
    
    java.lang.String text ="";
    
    java.lang.Long chrono =0;
    
    java.lang.String servername ="";
    
    java.lang.String connectorname ="";
    

    static hasMany = [:]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    serverserial(blank:true,nullable:true)
        
     keyfield(blank:false,nullable:false)
        
     text(blank:true,nullable:true)
        
     chrono(blank:true,nullable:true)
        
     servername(blank:false,nullable:false,key:["keyfield"])
        
     connectorname(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[keyfield:$keyfield, servername:$servername]";
    }
    
    //AUTO_GENERATED_CODE








}
