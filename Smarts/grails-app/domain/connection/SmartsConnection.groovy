package connection

import datasource.SmartsNotificationDatasource
import datasource.SmartsTopologyDatasource

class SmartsConnection extends Connection {

    public static String AM = "AM";
    public static String SAM = "SAM";
    public static String OI = "OI";
    public static String MPLS = "MPLS";
    public static String BGP = "BGP";
    public static String OSPF = "OSPF";
    public static String SDH = "SDH";

    static searchable = {
        except = ["smartsTopologyDatasources", "smartsNotificationDatasources"];
    };
    static cascaded = ["smartsTopologyDatasources": true, "smartsNotificationDatasources": true]
    static datasources = [:]

    String connectionClass = "com.ifountain.smarts.connection.SmartsConnectionImpl";
    String username = "";
    String domain = "";
    String userPassword = "";
    String domainType = SAM;
    String brokerPassword = "";
    String brokerUsername = "";
    String broker = "";
    List smartsTopologyDatasources = [];
    List smartsNotificationDatasources = [];


    static relations = [
            smartsTopologyDatasources: [isMany: true, reverseName: "connection", type: SmartsTopologyDatasource],
            smartsNotificationDatasources: [isMany: true, reverseName: "connection", type: SmartsNotificationDatasource]
    ]
    static constraints = {
        username(blank: true, nullable: true)
        domain(blank: true, nullable: true)
        userPassword(blank: true, nullable: true)
        brokerUsername(blank: true, nullable: true)
        brokerPassword(blank: true, nullable: true)
        broker(blank: true, nullable: true)
        domainType(inList: [AM, OI, SAM, MPLS, BGP, OSPF, SDH])
        connectionClass(validator: {val, obj ->
            try
            {
                Class.forName("com.smarts.repos.MR_AnyVal")
                Class.forName("com.smarts.crypto.Digest")
            }
            catch (ClassNotFoundException e)
            {
                return 'smarts.jars.does.not.exist';
            }
        })
    }
    static transients = [];
}
