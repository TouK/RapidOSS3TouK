package connection

import datasource.SmartsNotificationDatasource
import datasource.SmartsTopologyDatasource

class SmartsConnection extends Connection{
    
    public static String AM = "AM";
    public static String SAM = "SAM";
    public static String OI = "OI";
    public static String MPLS = "MPLS";
    public static String BGP = "BGP";
    public static String OSPF = "OSPF";
    public static String SDH = "SDH";

     static searchable = {
        except = [];
    };
    static cascaded = ["smartsTopologyDatasources":true, "smartsNotificationDatasources":true]
    static datasources = [:]

    String connectionClass = "com.ifountain.smarts.connection.SmartsConnectionImpl";
    String username ="";
    
    String domain ="";
    
    String userPassword ="";
    String domainType = SAM;
    
    String broker ="";
    List smartsTopologyDatasources = [];
    List smartsNotificationDatasources = [];
    

    static hasMany = [smartsTopologyDatasources:SmartsTopologyDatasource, smartsNotificationDatasources:SmartsNotificationDatasource]
    
    static constraints={
    username(blank:true,nullable:true)
        
     domain(blank:true,nullable:true)
        
     userPassword(blank:true,nullable:true)
        
     broker(blank:true,nullable:true)
        
     domainType(inList: [AM, OI, SAM, MPLS, BGP,OSPF, SDH])
    }

    static mappedBy=["smartsTopologyDatasources":"connection", "smartsNotificationDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}
