import datasource.*
def lastStateChange = 0;

/*NC_URL = 'jdbc:sybase:Tds:ossmuse:4100/?LITERAL_PARAMS=true';
USERNAME = 'root';
PSW = ''; 
def ncConName = "ncConn";
def ncDsName = "NCOMS";
return "hey"
println "1"
generateNetcoolConnAndDS(ncConName, ncDsName);
println "2"
*/
def ncds = NetcoolDatasource.get(name:"NCOMS");
def nameMap = [:];
nameMap.putAll(NetcoolDatasource.NAMEMAP);
nameMap.remove("netcoolclass");
def cntr=1;
while (true){
	
	def whereClause = "StateChange>$lastStateChange";
	
	def records= [:];
	records = ncds.getEvents(whereClause);
	def size =  records.size()
println "rec count for $whereClause: $size"
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
	}
println "lastStateChange: $lastStateChange for run $cntr"
	cntr++
	sleep(60000);
}

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