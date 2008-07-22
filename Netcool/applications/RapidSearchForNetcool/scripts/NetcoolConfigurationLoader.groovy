import datasource.NetcoolColumn
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import datasource.NetcoolConversionParameter
import com.ifountain.comp.utils.CaseInsensitiveMap

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:44:46 AM
* To change this template use File | Settings | File Templates.
*/
def baseDir = System.getProperty ("base.dir");
def netcoolConfigurationFile = new File("$baseDir/grails-app/conf/NetcoolFieldConfiguration.xml");
if(!netcoolConfigurationFile.exists())
{
    throw new Exception("Configuration file doesnot exist.");
}
NetcoolColumn*.remove();

convertedColumnsArray = NetcoolConversionParameter.termFreqs("columnName");
convertedColumnsMap = new CaseInsensitiveMap();
convertedColumnsArray.each{
    convertedColumnsMap[it.getTerm()] = it;
}

def slurper = new XmlSlurper()
def res = slurper.parseText(netcoolConfigurationFile.getText());
def netcoolEventXml = getModelXml(res.NetcoolEvent, true, [[name:"journals", reverseName:"event", toModel:"NetcoolJournal", cardinality:"One", reverseCardinality:"Many", isOwner:true]]);
def netcoolJournalXml = getModelXml(res.NetcoolJournal, false, [[name:"event", reverseName:"journals", toModel:"NetcoolEvent", cardinality:"Many", reverseCardinality:"One", isOwner:false]]);
ModelGenerator.getInstance().generateModels ([netcoolEventXml, netcoolJournalXml]);
def getModelXml(modelXml, boolean createColumnObjects, relations)
{
    def modelString = new StringWriter();
    def eventModelBuilder = new MarkupBuilder(modelString);
    def fields = modelXml.Fields.Field;
    eventModelBuilder.Model(name:modelXml.name())
    {
        def keys = [];
        eventModelBuilder.Properties()
        {
            fields.each{field->
                def netcoolName = field.@NetcoolName.text();
                def localName = field.@LocalName.text();
                def type = field.@Type.text();
                if(convertedColumnsMap[netcoolName] != null)
                {
                    type = ModelGenerator.STRING_TYPE;                    
                }
                def isDelMarker = new Boolean(field.@IsDeleteMarker.text()).booleanValue();
                def isKey = new Boolean(field.@IsKey.text()).booleanValue();
                if(createColumnObjects)
                {
                    NetcoolColumn.add(netcoolName:netcoolName, localName:localName, isDeleteMarker:isDelMarker, type:type);
                }
                if(type == "number")
                {
                    eventModelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"0", nameInDatasource:localName, lazy:false);
                }
                else
                {
                    eventModelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"", nameInDatasource:localName, lazy:false);
                }
                if(isKey)
                {
                    keys += localName;
                }

            }
        }
        eventModelBuilder.Datasources()
        {
            eventModelBuilder.Datasource(name:"RCMDB")
            {
                keys.each{
                    eventModelBuilder.Key(propertyName:it, nameInDatasource:it);
                }
            }
        }

        eventModelBuilder.Relations()
        {
            relations.each{
                eventModelBuilder.Relation(it);
            }
        }

    }
    return modelString.toString();
}


