import snmp.SnmpUtils

public class SnmpTrapOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{

    def send(Map trapProps){
        def trapPort = trapProps["port"] != null ? trapProps["port"] : port;
        def transportAddress = "${destination}/${trapPort}"
        def varbinds = trapProps["varbinds"] != null ? trapProps["varbinds"]: [];
        def timestamp = trapProps["timestamp"] != null ? trapProps["timestamp"] : (long) Math.floor(System.currentTimeMillis()/1000);
        def trapCommunity = trapProps["community"] != null ? trapProps["community"] : community;
        def tVersion = trapProps["trapVersion"] != null ? trapProps["trapVersion"] : trapVersion;
        if(tVersion == "v1"){
            return SnmpUtils.sendV1Trap(transportAddress, trapProps["agent"], trapCommunity, trapProps["enterprise"], timestamp, trapProps["generic"], trapProps["specific"], varbinds)
        }
        else{
            return SnmpUtils.sendV2cTrap(transportAddress, trapProps["agent"], trapCommunity, timestamp,  trapProps["trapOid"], varbinds)
        }

    }
}
    