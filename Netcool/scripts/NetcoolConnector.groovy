import connection.*
import datasource.*
def lastStateChange = 0;

NC_URL = 'jdbc:sybase:Tds:ossmuse:4100/?LITERAL_PARAMS=true';
USERNAME = 'root';
PSW = ''; 
def ncConName = "ncConn";
def ncDsName = "NCOMS";
generateNetcoolConnAndDS(ncConName, ncDsName);

def ncds = NetcoolDatasource.get(name:"NCOMS");
def nameMap = [:];
nameMap.putAll(NetcoolDatasource.NAMEMAP);
nameMap.remove("netcoolclass");
def cntr=1;
//while (true){
	def whereClause = "StateChange>$lastStateChange";
	def records= [:];
	records = ncds.getEvents(whereClause);
	def size =  records.size()
	for (rec in records){
		def newEvent = [:];
		nameMap.each(){key,val->
			val = rec[key.toUpperCase()];
			newEvent.put(key,val);
		}
		newEvent.serverserial = Long.parseLong(rec.serverserial);
		newEvent.put("netcoolclass",rec.class);
		NetcoolEvent.add(newEvent);
		def longStateChange = Long.parseLong(rec.StateChange);
		if (longStateChange>lastStateChange){
			lastStateChange = longStateChange;
		}
println "serverserial: $rec.serverserial & statechange:$longStateChange"		
		
	}
println "rec count:$size & lastStateChange: $lastStateChange for run $cntr"
	cntr++
//	sleep(30000);
//}
return "success"

// IF ADD() IS CALLED IN A LOOP, AFTER APPLICATION IS RESTARTED, THE DATA CAN NOT BE RETRIEVED. IT IS STILL IN DB BUT CAN NOT BE DISPLAYED.
// DEVDB.SCRIPT DOES NOT HAVE INSERTS FOR INSTANCES
// ADD() TURNS TO UPDATE() WITH NC DB UPDATE FOR EXISTING INSTANCES IN RCMDB

def generateNetcoolConnAndDS(ncConName, ncDsName){
	def conn1 = NetcoolConnection.findByName(ncConName);
	if(conn1 == null){
	    conn1 = NetcoolConnection.add(name: ncConName, url: NC_URL,username: USERNAME, password:PSW);
	}

	def ncDatasource= NetcoolDatasource.findByName(ncDsName);
	if (ncDatasource == null){
	    ncDatasource = NetcoolDatasource.add(connection:conn1, name:ncDsName);
	}
}