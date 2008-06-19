import connection.SmartsConnection
import datasource.SmartsTopologyDatasource
import script.CmdbScript

CONNAME = 'smartsconn';
BROKER = '192.168.1.102:426';
DOMAIN = 'INCHARGE-SA';
USERNAME = 'admin';
PSW = 'rcpass';
DS1 = 'eastRegionDs';
DS2 = 'westRegionDs';

def conn1 = SmartsConnection.findByName(CONNAME);
if(conn1 == null){
    conn1 = new SmartsConnection(name:CONNAME, broker:BROKER, domain:DOMAIN, username:USERNAME, password:PSW).save();
}

def eastRegionDs= SmartsTopologyDatasource.findByName(DS1);
if (eastRegionDs == null){
    eastRegionDs = new SmartsTopologyDatasource(connection:conn1, name:DS1).save();
}

def westRegionDs= SmartsTopologyDatasource.findByName(DS2);
if (westRegionDs == null){
    westRegionDs = new SmartsTopologyDatasource(connection:conn1, name:DS2).save();
}

new CmdbScript(name:'ModelHelper').save();
new CmdbScript(name:'SampleModelCreationUsingModelHelper').save();
/*
new CmdbScript(name:'Sample3Setup').save();
new CmdbScript(name:'Test1_1').save();
new CmdbScript(name:'Test1_2').save();
new CmdbScript(name:'Test1_3').save();
new CmdbScript(name:'Test1_4').save();
new CmdbScript(name:'Test2_1').save();
new CmdbScript(name:'Test2_2').save();
new CmdbScript(name:'Test2_3').save();
new CmdbScript(name:'Test3_1').save();
new CmdbScript(name:'Test3_2').save();
new CmdbScript(name:'Test3_3').save();
new CmdbScript(name:'Test4_1').save();
new CmdbScript(name:'Test4_2').save();
new CmdbScript(name:'Test4_3').save();
new CmdbScript(name:'Test5_1').save();
new CmdbScript(name:'Test5_2').save();
new CmdbScript(name:'Test5_3').save();
new CmdbScript(name:'Test6_1').save();
new CmdbScript(name:'Test6_2').save();
new CmdbScript(name:'Test6_3').save();
new CmdbScript(name:'Test7_1').save();
new CmdbScript(name:'Test7_2').save();
new CmdbScript(name:'Test8_1').save();
new CmdbScript(name:'Test8_2').save();
*/
return "Successfully created connection, datasources, and scripts!"