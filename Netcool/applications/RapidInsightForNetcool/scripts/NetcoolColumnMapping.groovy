import datasource.NetcoolDatasource
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import org.apache.log4j.Logger
import datasource.NetcoolColumn

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:33:27 AM
* To change this template use File | Settings | File Templates.
*/
Logger logger = Logger.getLogger("getConversionParameters");
def defaultConversionColumnConfiguration = ["Class": "ncclass", "Type": "nctype", "X733EventType": "ncx733eventtype",
        "X733ProbableCause": "ncx733probablecause", "X733SpecificProb": "ncx733specificprob", "X733CorrNotif": "ncx733corrnotif"]

def rsEventColumnConfiguration = ["Acknowledged": "acknowledged", "OwnerUID": "owner", "Severity": "severity", "FirstOccurrence": "createdAt",
         "SuppressEscl": "state", "ExpireTime": "willExpireAt", "StateChange": "changedAt", "Tally":"count"]
def deleteColName = "isdeleted"
def baseDir = System.getProperty("base.dir");
def netcoolConfigurationFile = new File("$baseDir/grails-app/conf/NetcoolModelConfiguration.xml");
List netcoolDatasources = NetcoolDatasource.list();
if (netcoolDatasources.isEmpty())
{
    logger.warn("No netcool datasource is defined");
    throw new Exception("No netcool datasource is defined");
}
NetcoolDatasource netcoolDs = netcoolDatasources[0];
def netcoolFields = netcoolDs.getFieldMap();
def netcoolProperties = [];
def tempMapping = [:];
tempMapping.putAll(defaultConversionColumnConfiguration)
tempMapping.putAll(rsEventColumnConfiguration)
netcoolFields.each {String colName, String colType ->
    if (NetcoolScriptConfigurationParams.COLUMNS_WILL_BE_CONVERTED.containsKey(colName))
    {
        colType = ModelGenerator.STRING_TYPE;
    }

    def localName = tempMapping[colName] == null ? colName.toLowerCase() : tempMapping[colName]
    if(!rsEventColumnConfiguration.containsKey(colName)){
        netcoolProperties.add([Name: localName, Type: colType]);
    }
    NetcoolColumn.add(netcoolName: colName, localName: localName, isDeleteMarker: colName == deleteColName, type: colType);
}
def fileWriter = new FileWriter(netcoolConfigurationFile);
def netcoolConf = new MarkupBuilder(fileWriter);
netcoolConf.RsModel() {
    netcoolConf.Models() {
        netcoolConf.Model(Name:"NetcoolEvent", Parent:"RsEvent") {
            netcoolConf.Properties() {
                netcoolProperties.each{propertyMap ->
                   netcoolConf.Property(Name: propertyMap.Name, Type: propertyMap.Type);     
                }
            }
        }
        netcoolConf.Model(Name:"NetcoolHistoricalEvent", Parent:"RsHistoricalEvent") {
            netcoolConf.Properties() {
                netcoolProperties.each{propertyMap ->
                   netcoolConf.Property(Name: propertyMap.Name, Type: propertyMap.Type);
                }
            }
        }
        def journalFields = ["serverserial": "number", "keyfield": "string", "text": "string", "chrono": "number", servername: "string", rsDatasource: "string"];
        netcoolConf.Model(Name:"NetcoolJournal") {
            netcoolConf.Properties() {
                journalFields.each {String colName, String colType ->
                    def localName = defaultConversionColumnConfiguration[colName] == null ? colName.toLowerCase() : defaultConversionColumnConfiguration[colName]
                    def isKey = localName == "servername" || localName == "keyfield";
                    netcoolConf.Property(Name: localName, Type: colType, IsKey: isKey);
                }
            }
        }
    }

}
fileWriter.flush();
fileWriter.close();
web.flash.message = "Columns imported successfully."
web.redirect(uri: '/synchronize.gsp');