import connection.HttpConnection
import datasource.HttpDatasource
import script.CmdbScript

CONNAME = 'hyperic';
BASEURL = 'http://localhost:7080';
DS = 'hyperic';

/*HypericServer.add(username:'hqadmin', password:'hqadmin');
println "hypServer added"*/
def conn = HttpConnection.findByName(CONNAME);
if(conn == null){
    conn = HttpConnection.add(name:CONNAME, baseUrl:BASEURL);
}

def ds= HttpDatasource.findByName(DS);
if (ds == null){
    ds = HttpDatasource.add(connection:conn, name:DS);
}


CmdbScript.add(name:'HypericStatusIntegration', type:'Scheduled');
CmdbScript.add(name:'HypericRelationIntegration', type:'Scheduled');
CmdbScript.add(name:'HypericEventIntegration', type:'Scheduled');
