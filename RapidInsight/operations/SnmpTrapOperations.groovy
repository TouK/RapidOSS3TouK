import snmp.SnmpUtils

public class SnmpTrapOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{

    def send(Map trapProps){
        def transportAddress = "${destination}/${port}"
        def varbinds = trapProps["varbinds"] != null ? trapProps["varbinds"]: [];
        def timestamp = trapProps["timestamp"] != null ? trapProps["timestamp"] : 0
        def trapCommunity = trapProps["community"] != null ? trapProps["community"] : community;
        if(trapVersion == "v1"){
            return SnmpUtils.sendV1Trap(transportAddress, trapProps["agent"], trapCommunity, trapProps["enterprise"], timestamp, trapProps["generic"], trapProps["specific"], varbinds)
        }
        else{
            return SnmpUtils.sendV2cTrap(transportAddress, trapProps["agent"], trapCommunity, timestamp,  trapProps["trapOid"], varbinds)
        }

    }
}
    