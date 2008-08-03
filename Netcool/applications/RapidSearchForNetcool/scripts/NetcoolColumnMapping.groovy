import datasource.NetcoolDatasource
import groovy.xml.MarkupBuilder
import datasource.NetcoolConversionParameter
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.comp.utils.CaseInsensitiveMap

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:33:27 AM
* To change this template use File | Settings | File Templates.
*/
def defaultConversionColumnConfiguration = ["Class":"ncclass", "Type":"nctype"]
def deleteColName = "isdeleted"
def baseDir = System.getProperty ("base.dir");
def netcoolConfigurationFile = new File("$baseDir/grails-app/conf/NetcoolFieldConfiguration.xml"); 

def convertedColumnsArray = NetcoolConversionParameter.termFreqs("columnName");
def convertedColumnsMap = new CaseInsensitiveMap();
convertedColumnsArray.each{
    convertedColumnsMap[it.getTerm()] = it;
}
List netcoolDatasources = NetcoolDatasource.list();
if(netcoolDatasources.isEmpty())
{
    logger.warn("No netcool datasource is defined");
}
NetcoolDatasource netcoolDs = netcoolDatasources[0];
def netcoolFields = netcoolDs.getFieldMap();
netcoolFields["connectorname"] = ModelGenerator.STRING_TYPE;
def fileWriter = new FileWriter(netcoolConfigurationFile);
def netcoolConf = new MarkupBuilder(fileWriter);
netcoolConf.NetcoolConfiguration()
{
    netcoolConf.NetcoolEvent()
    {
        netcoolConf.Fields()
        {
            netcoolFields.each{String colName, String colType->
                if(convertedColumnsMap.containsKey(colName))
                {
                    colType = ModelGenerator.STRING_TYPE;
                }
                def localName = defaultConversionColumnConfiguration[colName]==null?colName.toLowerCase(): defaultConversionColumnConfiguration[colName]
                def isKey = localName == "servername" || localName == "serverserial";
                netcoolConf.Field(NetcoolName:colName, LocalName:localName, IsDeleteMarker:colName==deleteColName, Type:colType, IsKey:isKey);
            }
        }
    }
    def journalFields = ["serverserial":"number", "keyfield":"string", "text":"string", "chrono":"number", servername:"string", connectorname:"string"];
    netcoolConf.NetcoolJournal()
    {
        netcoolConf.Fields()
        {
            journalFields.each{String colName, String colType->
                def localName = defaultConversionColumnConfiguration[colName]==null?colName.toLowerCase(): defaultConversionColumnConfiguration[colName]
                def isKey = localName == "servername" || localName == "keyfield";
                netcoolConf.Field(NetcoolName:colName, LocalName:localName, Type:colType, IsDeleteMarker:false, IsKey:isKey);
            }
        }
    }
}
fileWriter.flush();
fileWriter.close();
web.flash.message = "Columns imported successfully."
web.redirect(uri:'/synchronize.gsp');