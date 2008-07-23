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
def COLUMNS_WILL_BE_CONVERTED = ["Severity":"", "Class":"", "OwnerUID":"", "OwnerGID":""];
def defaultConversionColumnConfiguration = ["Class":"netcoolclass"]
def deleteColName = "isdeleted"
def baseDir = System.getProperty ("base.dir");
def netcoolConfigurationFile = new File("$baseDir/grails-app/conf/NetcoolFieldConfiguration.xml"); 
List netcoolDatasources = NetcoolDatasource.list();
if(netcoolDatasources.isEmpty())
{
    throw new Exception("No netcool datasource is defined");
}
NetcoolDatasource netcoolDs = netcoolDatasources[0];
def conversionParams = netcoolDs.getConversionParams();
conversionParams.each{Map params->
    if(COLUMNS_WILL_BE_CONVERTED.containsKey(params.colName))
    {
        NetcoolConversionParameter.add(keyField:params.keyfield, columnName:params.colName, value:params.value, conversion:params.conversion);
    }
}
def convertedColumnsArray = NetcoolConversionParameter.termFreqs("columnName");
def convertedColumnsMap = new CaseInsensitiveMap();
convertedColumnsArray.each{
    convertedColumnsMap[it.getTerm()] = it;
}
def netcoolFields = netcoolDs.getFieldMap();
def fileWriter = new FileWriter(netcoolConfigurationFile);
def netcoolConf = new MarkupBuilder(fileWriter);
netcoolConf.NetcoolConfiguration()
{
    netcoolConf.NetcoolEvent()
    {
        netcoolConf.Fields()
        {
            netcoolFields.each{String colName, String colType->
                if(COLUMNS_WILL_BE_CONVERTED.containsKey(colName))
                {
                    colType = ModelGenerator.STRING_TYPE;
                }
                def localName = defaultConversionColumnConfiguration[colName]==null?colName.toLowerCase(): defaultConversionColumnConfiguration[colName]
                def isKey = localName == "servername" || localName == "serverserial";
                netcoolConf.Field(NetcoolName:colName, LocalName:localName, IsDeleteMarker:colName==deleteColName, Type:colType, IsKey:isKey);
            }
        }
    }
    def journalFields = ["serial":"string", "keyfield":"string", "text":"string", "chrono":"number", servername:"string"];
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

web.redirect(uri:'/admin.gsp');