import datasource.SmartsModel
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import datasource.SmartsModelColumn
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:44:46 AM
* To change this template use File | Settings | File Templates.
*/
logger = Logger.getLogger(getClass().name);
logger.info ("Model creator started");
def baseDir = System.getProperty ("base.dir");
smartsModelConfigurationFile = new File("$baseDir/grails-app/conf/SmartsModelConfiguration.xml");
if(!smartsModelConfigurationFile.exists())
{
    logger.info ("Configuration file ${smartsModelConfigurationFile.absolutePath} doesnot exist.");
    throw new Exception("Configuration file doesnot exist.");
}
SmartsModel.list()*.remove();

def modelXmls = getModelXmls();


ModelGenerator.getInstance().generateModels (modelXmls);
web.flash?.message = "Models generated successfully."
def getModelXmls()
{
    def slurper = new XmlSlurper()
    def res = slurper.parseText(smartsModelConfigurationFile.getText());
    def modelsXml = res.Model;
    def modelXmlStrings = [];
    logger.info ("Will create ${modelsXml.size()} number of models.");
    modelsXml.each{modelXml->
        def modelString = new StringWriter();
        def modelBuilder = new MarkupBuilder(modelString);
        def fields = modelXml.Fields.Field;
        def relations = modelXml.Relations.Relation;
        def modelName = modelXml.@Name.text();
        def parentName = modelXml.@ParentName.text();
        def modelMetaProps = [name:modelName];
        logger.info ("Creating model ${modelName} with ${fields.size()} number of properties and ${relations.size()} number of relations");
        if(parentName != null)
        {
            logger.info ("Model ${modelName} is a child of ${parentName}");
            modelMetaProps["parentModel"] = parentName;
        }
        modelBuilder.Model(modelMetaProps)
        {
            def smartsModel = SmartsModel.add(name:modelName, parentName:parentName);
            if(!smartsModel.hasErrors())
            {
                def keys = [];
                modelBuilder.Properties()
                {
                    fields.each{field->
                        def smartsName = field.@SmartsName.text();
                        def localName = field.@LocalName.text();
                        def type = field.@Type.text();
                        def isDelMarker = new Boolean(field.@IsDeleteMarker.text()).booleanValue();
                        def isKey = new Boolean(field.@IsKey.text()).booleanValue();
                        logger.info ("Creating property ${localName} corresponding smarts property ${smartsName}");
                        SmartsModelColumn.add(smartsName:smartsName, localName:localName, isDeleteMarker:isDelMarker, type:type, model:smartsModel);

                        if(type == "number")
                        {
                            modelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"0", nameInDatasource:localName, lazy:false);
                        }
                        else
                        {
                            modelBuilder.Property(name:localName, type:type, datasource:"RCMDB", defaultValue:"", nameInDatasource:localName, lazy:false);
                        }
                        if(isKey)
                        {
                            keys += localName;
                        }

                    }
                }
                modelBuilder.Datasources()
                {
                    modelBuilder.Datasource(name:"RCMDB")
                    {
                        keys.each{
                            modelBuilder.Key(propertyName:it, nameInDatasource:it);
                        }
                    }
                }

                modelBuilder.Relations()
                {
                    relations.each{relation->
                        def relName = relation.@Name.text() ;
                        def reverseName = relation.@ReverseName.text() ;
                        def toModel = relation.@ToModel.text() ;
                        def type = relation.@Cardinality.text() ;
                        def reverseType = relation.@ReverseCardinality.text() ;
                        modelBuilder.Relation(name:relName, reverseName:reverseName, toModel:toModel, cardinality:type, reverseCardinality:reverseType, isOwner:true);
                    }
                }
            }
            else
            {
                logger.info ("Could not created SmartsModel. Reason: ${smartsModel.errors}");
            }

        }
        modelXmlStrings += modelString.toString();
    }

    def allSmartsModelObjects = [:]
    SmartsModel.list().each{
        allSmartsModelObjects[it.name] = it;
    }
    allSmartsModelObjects.each{key, obj->
        def parents = [];
        getParentObjects(allSmartsModelObjects, parents, obj);
        parents.each{parent->
            parent.columns.each{column->
                SmartsModelColumn.add(smartsName:column.smartsName, localName:column.localName, isDeleteMarker:column.isDeleteMarker, type:column.type, model:obj);
            }
        }

    }
    return modelXmlStrings;
}

def getParentObjects(allSmartsModelObjects, parents, obj)
{
    if(obj.parentName != null)
    {
        def parent = allSmartsModelObjects[obj.parentName];
        parents.add(parent);
        getParentObjects(allSmartsModelObjects, parents, parent);
    }
}


