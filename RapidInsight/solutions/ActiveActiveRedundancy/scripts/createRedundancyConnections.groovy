import connection.*;
import datasource.*;

// ---------------------------------------
// CONFIGURATION STARTS

def redundancy_servers=[];
redundancy_servers.add([name:"redundancy_1",baseUrl:"http://192.168.1.107:12222/RapidSuite/"]);

// CONFIGURATION ENDS
// ---------------------------------------

redundancy_servers.each{ props -> 
	props.minTimeout=60;
	props.maxTimeout=120;
	def httpConn = HttpConnection.add(props)
	HttpDatasource.add(name:props.name, connection:httpConn);
}