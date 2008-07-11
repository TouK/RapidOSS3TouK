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
    conn1 = SmartsConnection.add(name:CONNAME, broker:BROKER, domain:DOMAIN, username:USERNAME, userPassword:PSW);
}

def eastRegionDs= SmartsTopologyDatasource.findByName(DS1);
if (eastRegionDs == null){
    eastRegionDs = SmartsTopologyDatasource.add(connection:conn1, name:DS1);
}

def westRegionDs= SmartsTopologyDatasource.findByName(DS2);
if (westRegionDs == null){
    westRegionDs = SmartsTopologyDatasource.add(connection:conn1, name:DS2);
}

CmdbScript.add(name:'ModelHelper');
CmdbScript.add(name:'SampleModelCreationUsingModelHelper');
/*
CmdbScript.add(name:'Sample3Setup');
CmdbScript.add(name:'Test1_1');
CmdbScript.add(name:'Test1_2');
CmdbScript.add(name:'Test1_3');
CmdbScript.add(name:'Test1_4');
CmdbScript.add(name:'Test2_1');
CmdbScript.add(name:'Test2_2');
CmdbScript.add(name:'Test2_3');
CmdbScript.add(name:'Test3_1');
CmdbScript.add(name:'Test3_2');
CmdbScript.add(name:'Test3_3');
CmdbScript.add(name:'Test4_1');
CmdbScript.add(name:'Test4_2');
CmdbScript.add(name:'Test4_3');
CmdbScript.add(name:'Test5_1');
CmdbScript.add(name:'Test5_2');
CmdbScript.add(name:'Test5_3');
CmdbScript.add(name:'Test6_1');
CmdbScript.add(name:'Test6_2');
CmdbScript.add(name:'Test6_3');
CmdbScript.add(name:'Test7_1');
CmdbScript.add(name:'Test7_2');
CmdbScript.add(name:'Test8_1');
CmdbScript.add(name:'Test8_2');
*/
return "Successfully created connection, datasources, and scripts!"