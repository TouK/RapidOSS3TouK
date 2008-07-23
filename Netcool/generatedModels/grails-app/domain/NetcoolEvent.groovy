
import com.ifountain.core.domain.annotations.*;
//import datasource.NetcoolDatasource // ADDED TO BE ABLE TO GET NCDS PER EVENT ONCE DURING onLoad()

class NetcoolEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["keys":["servername":["nameInDs":"servername"], "serverserial":["nameInDs":"serverserial"]]]]

    
    java.lang.String identifier ="";
    
    java.lang.Long serial =0;
    
    java.lang.String node ="";
    
    java.lang.String nodealias ="";
    
    java.lang.String manager ="";
    
    java.lang.String agent ="";
    
    java.lang.String alertgroup ="";
    
    java.lang.String alertkey ="";
    
    java.lang.String severity ="";
    
    java.lang.String summary ="";
    
    java.lang.Long statechange =0;
    
    java.lang.Long firstoccurrence =0;
    
    java.lang.Long lastoccurrence =0;
    
    java.lang.Long internallast =0;
    
    java.lang.Long poll =0;
    
    java.lang.Long type =0;
    
    java.lang.Long tally =0;
    
    java.lang.String netcoolclass ="";
    
    java.lang.Long grade =0;
    
    java.lang.String location ="";
    
    java.lang.String owneruid ="";
    
    java.lang.String ownergid ="";
    
    java.lang.Long acknowledged =0;
    
    java.lang.Long flash =0;
    
    java.lang.String eventid ="";
    
    java.lang.Long expiretime =0;
    
    java.lang.Long processreq =0;
    
    java.lang.Long suppressescl =0;
    
    java.lang.String customer ="";
    
    java.lang.String service ="";
    
    java.lang.Long physicalslot =0;
    
    java.lang.Long physicalport =0;
    
    java.lang.String physicalcard ="";
    
    java.lang.Long tasklist =0;
    
    java.lang.String nmosserial ="";
    
    java.lang.Long nmosobjinst =0;
    
    java.lang.Long nmoscausetype =0;
    
    java.lang.String localnodealias ="";
    
    java.lang.String localpriobj ="";
    
    java.lang.String localsecobj ="";
    
    java.lang.String localrootobj ="";
    
    java.lang.String remotenodealias ="";
    
    java.lang.String remotepriobj ="";
    
    java.lang.String remotesecobj ="";
    
    java.lang.String remoterootobj ="";
    
    java.lang.Long x733eventtype =0;
    
    java.lang.Long x733probablecause =0;
    
    java.lang.String x733specificprob ="";
    
    java.lang.String x733corrnotif ="";
    
    java.lang.String servername ="";
    
    java.lang.Long serverserial =0;
    
    java.lang.String url ="";
    

    static hasMany = [:]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    identifier(blank:true,nullable:true)
        
     serial(blank:true,nullable:true)
        
     node(blank:true,nullable:true)
        
     nodealias(blank:true,nullable:true)
        
     manager(blank:true,nullable:true)
        
     agent(blank:true,nullable:true)
        
     alertgroup(blank:true,nullable:true)
        
     alertkey(blank:true,nullable:true)
        
     severity(blank:true,nullable:true)
        
     summary(blank:true,nullable:true)
        
     statechange(blank:true,nullable:true)
        
     firstoccurrence(blank:true,nullable:true)
        
     lastoccurrence(blank:true,nullable:true)
        
     internallast(blank:true,nullable:true)
        
     poll(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     tally(blank:true,nullable:true)
        
     netcoolclass(blank:true,nullable:true)
        
     grade(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     owneruid(blank:true,nullable:true)
        
     ownergid(blank:true,nullable:true)
        
     acknowledged(blank:true,nullable:true)
        
     flash(blank:true,nullable:true)
        
     eventid(blank:true,nullable:true)
        
     expiretime(blank:true,nullable:true)
        
     processreq(blank:true,nullable:true)
        
     suppressescl(blank:true,nullable:true)
        
     customer(blank:true,nullable:true)
        
     service(blank:true,nullable:true)
        
     physicalslot(blank:true,nullable:true)
        
     physicalport(blank:true,nullable:true)
        
     physicalcard(blank:true,nullable:true)
        
     tasklist(blank:true,nullable:true)
        
     nmosserial(blank:true,nullable:true)
        
     nmosobjinst(blank:true,nullable:true)
        
     nmoscausetype(blank:true,nullable:true)
        
     localnodealias(blank:true,nullable:true)
        
     localpriobj(blank:true,nullable:true)
        
     localsecobj(blank:true,nullable:true)
        
     localrootobj(blank:true,nullable:true)
        
     remotenodealias(blank:true,nullable:true)
        
     remotepriobj(blank:true,nullable:true)
        
     remotesecobj(blank:true,nullable:true)
        
     remoterootobj(blank:true,nullable:true)
        
     x733eventtype(blank:true,nullable:true)
        
     x733probablecause(blank:true,nullable:true)
        
     x733specificprob(blank:true,nullable:true)
        
     x733corrnotif(blank:true,nullable:true)
        
     servername(blank:false,nullable:false)
        
     serverserial(blank:false,nullable:false,key:["servername"])
        
     url(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[servername:$servername, serverserial:$serverserial]";
    }
    
    //AUTO_GENERATED_CODE




}
