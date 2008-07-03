
import com.ifountain.core.domain.annotations.*;
//import datasource.NetcoolDatasource // ADDED TO BE ABLE TO GET NCDS PER EVENT ONCE DURING onLoad()

class NetcoolEvent {

    //AUTO_GENERATED_CODE


    static datasources = ["RCMDB":["master":true, "keys":["serverserial":["nameInDs":"serverserial"]]]]

    
    String grade ;
    
    String agent ;
    
    String processreq ;
    
    String x733eventtype ;
    
    String tally ;
    
    String type ;
    
    String servername ;
    
    String remoterootobj ;
    
    String tasklist ;
    
    String severity ;
    
    String manager ;
    
    String flash ;
    
    String location ;
    
    String summary ;
    
    String physicalcard ;
    
    String physicalport ;
    
    String physicalslot ;
    
    String lastoccurrence ;
    
    String customer ;
    
    String remotesecobj ;
    
    String url ;
    
    String ownergid ;
    
    String nmosobjinst ;
    
    String acknowledged ;
    
    String alertkey ;
    
    String serial ;
    
    String owneruid ;
    
    String x733corrnotif ;
    
    String nmosserial ;
    
    String firstoccurrence ;
    
    String alertgroup ;
    
    String x733specificprob ;
    
    String eventid ;
    
    Long serverserial ;
    
    String statechange ;
    
    String identifier ;
    
    String node ;
    
    String localpriobj ;
    
    String netcoolclass ;
    
    String localsecobj ;
    
    String expiretime ;
    
    String suppressescl ;
    
    String nmoscausetype ;
    
    String service ;
    
    String localrootobj ;
    
    String nodealias ;
    
    String localnodealias ;
    
    String poll ;
    
    String remotepriobj ;
    
    String remotenodealias ;
    
    String x733probablecause ;
    
    String internallast ;
    

    static hasMany = [:]

    static constraints={
    grade(blank:true,nullable:true)
        
     agent(blank:true,nullable:true)
        
     processreq(blank:true,nullable:true)
        
     x733eventtype(blank:true,nullable:true)
        
     tally(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     servername(blank:true,nullable:true)
        
     remoterootobj(blank:true,nullable:true)
        
     tasklist(blank:true,nullable:true)
        
     severity(blank:true,nullable:true)
        
     manager(blank:true,nullable:true)
        
     flash(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     summary(blank:true,nullable:true)
        
     physicalcard(blank:true,nullable:true)
        
     physicalport(blank:true,nullable:true)
        
     physicalslot(blank:true,nullable:true)
        
     lastoccurrence(blank:true,nullable:true)
        
     customer(blank:true,nullable:true)
        
     remotesecobj(blank:true,nullable:true)
        
     url(blank:true,nullable:true)
        
     ownergid(blank:true,nullable:true)
        
     nmosobjinst(blank:true,nullable:true)
        
     acknowledged(blank:true,nullable:true)
        
     alertkey(blank:true,nullable:true)
        
     serial(blank:true,nullable:true)
        
     owneruid(blank:true,nullable:true)
        
     x733corrnotif(blank:true,nullable:true)
        
     nmosserial(blank:true,nullable:true)
        
     firstoccurrence(blank:true,nullable:true)
        
     alertgroup(blank:true,nullable:true)
        
     x733specificprob(blank:true,nullable:true)
        
     eventid(blank:true,nullable:true)
        
     serverserial(unique:true)
        
     statechange(blank:true,nullable:true)
        
     identifier(blank:true,nullable:true)
        
     node(blank:true,nullable:true)
        
     localpriobj(blank:true,nullable:true)
        
     netcoolclass(blank:true,nullable:true)
        
     localsecobj(blank:true,nullable:true)
        
     expiretime(blank:true,nullable:true)
        
     suppressescl(blank:true,nullable:true)
        
     nmoscausetype(blank:true,nullable:true)
        
     service(blank:true,nullable:true)
        
     localrootobj(blank:true,nullable:true)
        
     nodealias(blank:true,nullable:true)
        
     localnodealias(blank:true,nullable:true)
        
     poll(blank:true,nullable:true)
        
     remotepriobj(blank:true,nullable:true)
        
     remotenodealias(blank:true,nullable:true)
        
     x733probablecause(blank:true,nullable:true)
        
     internallast(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];

    public String toString()
    {
    	return "${getClass().getName()}[serverserial:$serverserial]";
    }
    
    //AUTO_GENERATED_CODE

    // ADDED TO BE ABLE TO GET NCDS PER EVENT ONCE DURING onLoad()
//    NetcoolDatasource NCDS;
    
    def onLoad = {
	
		if (servername!=null){
			NCDS = NetcoolDatasource.get(name:servername);
		}
	}
}
