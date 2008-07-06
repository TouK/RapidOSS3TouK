import datasource.NetcoolDatasource
import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:33:27 AM
* To change this template use File | Settings | File Templates.
*/
def defaultConversionColumnConfiguration = ["class":"netcoolclass"]
def deleteColName = "isdeleted"
def baseDir = System.getProperty ("base.dir");
def netcoolConfigurationFile = new File("$baseDir/conf/NetcoolFieldConfiguration.xml"); 
List netcoolDatasources = NetcoolDatasource.list();
if(netcoolDatasources.isEmpty())
{
    throw new Exception("No netcool datasource is defined");
}
NetcoolDatasource netcoolDs = netcoolDatasources[0];
def netcoolFields = netcoolDs.getFieldMap();
def fileWriter = new FileWriter(netcoolConfigurationFile);
def netcoolConf = new MarkupBuilder(fileWriter);
netcoolFields["servername"] = "string";
netcoolConf.Fields()
{
    netcoolFields.each{String colName, String colType->
        def localName = defaultConversionColumnConfiguration[colName]==null?colName: defaultConversionColumnConfiguration[colName]
        netcoolConf.Field(NetcoolName:colName, LocalName:localName, IsDeleteMarker:colName==deleteColName, Type:colType);
    }
}
fileWriter.flush();
fileWriter.close();