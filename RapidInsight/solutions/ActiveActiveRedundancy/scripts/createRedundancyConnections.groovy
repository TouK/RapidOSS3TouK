import connection.*;
import datasource.*;

// ---------------------------------------
// CONFIGURATION STARTS

def ross_servers=[];
ross_servers.add([name:"ross_1",baseUrl:"http://192.168.1.107:12222/RapidSuite/"]);

// CONFIGURATION ENDS
// ---------------------------------------

ross_servers.each{ props -> 
	props.minTimeout=60;
	props.maxTimeout=120;
	def httpConn = HttpConnection.add(props)
	HttpDatasource.add(name:props.name, connection:httpConn);
}