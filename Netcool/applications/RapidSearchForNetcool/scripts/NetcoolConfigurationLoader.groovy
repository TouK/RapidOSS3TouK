import configuration.NameMapping
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.domain.generation.ModelGenerator

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:44:46 AM
* To change this template use File | Settings | File Templates.
*/
def baseDir = System.getProperty ("base.dir");
def netcoolConfigurationFile = new File("$baseDir/conf/NetcoolFieldConfiguration.xml");
if(!netcoolConfigurationFile.exists())
{
    throw new Exception("Configuration file doesnot exist.");
}
def slurper = new XmlSlurper()
def res = slurper.parse (netcoolConfigurationFile);
def fields = res.Fields().Field();
def modelString = new StringWriter();
def eventModelBuilder = new MarkupBuilder(modelString);
eventModelBuilder.Model(name:"NetcoolEvent")
{
    eventModelBuilder.Datasources()
    {
        eventModelBuilder.Datasource(name:"RCMDB")
        {
            eventModelBuilder.Key(propertyName:"servername", nameInDatasource:"servername");
            eventModelBuilder.Key(propertyName:"serverserial", nameInDatasource:"serverserial");                
        }
    }
    eventModelBuilder.Properties()
    {
        fields.each{field->
            def netcoolName = field.@NetcoolName.text();
            def localName = field.@LocalName.text();
            def type = field.@Type.text();
            def isDelMarker = new Boolean(field.@IsDeleteMarker.text()).booleanValue();
            NameMapping.add(netcoolName:netcoolName, localName:localName, isDeleteMarker:isDelMarker);
            if(type == "number")
            {
                eventModelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"0", nameInDatasource:localName, lazy:false);
            }
            else
            {
                eventModelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"", nameInDatasource:localName, lazy:false);    
            }
        }
    }
}
def journalFields = ["serial":"string", "keyfield":"string", "text":"string", "chrono":"number"]
def journalemodelString = new StringWriter();
def journalModelBuilder = new MarkupBuilder(journalemodelString);

journalModelBuilder.Model(name:"NetcoolEvent")
{
    journalModelBuilder.Datasources()
    {
        journalModelBuilder.Datasource(name:"RCMDB")
        {
            journalModelBuilder.Key(propertyName:"servername", nameInDatasource:"servername");
            journalModelBuilder.Key(propertyName:"keyfield", nameInDatasource:"keyfield");                
        }
    }
    journalModelBuilder.Properties()
    {
        journalFields.each{localName, type->
            if(type == "number")
            {
                journalModelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"0", nameInDatasource:localName, lazy:false);
            }
            else
            {
                journalModelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"", nameInDatasource:localName, lazy:false);
            }
        }
    }
}

ModelGenerator.getInstance().generateModels ([modelString.toString(), journalemodelString.toString()]);
