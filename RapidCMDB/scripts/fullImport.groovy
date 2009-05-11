import org.codehaus.groovy.grails.plugins.searchable.compass.config.SearchableCompassConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DefaultSearchableCompassClassMappingXmlBuilder
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfigurator
import org.compass.core.config.CompassConfiguration
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.compass.DefaultCompassConfiguration
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.converter.RapidConvertUtils
import org.apache.commons.beanutils.ConversionException

CONFIG=new fullExportConfiguration().getImportConfiguration();

//keeps  map of all the model classes , modelName -> model
ALL_MODELS=[:];

logger.info("*****************FULL IMPORT STARTING *************************")

generateAllModels();
importModelFiles();
return "imported successfuly";

logger.info("*****************FULL IMPORT ENDED *************************")



def generateAllModels()
{
    def domClasses = ApplicationHolder.application.getDomainClasses();
    domClasses.each{
        def modelName = it.clazz.name;
        def model = it.clazz;
        ALL_MODELS[modelName]=model;
    }
}

def importModelFiles()
{
    def ant=new AntBuilder();
    ant.delete(dir:CONFIG.dataDir);

    def compass=getCompass(CONFIG.dataDir);
    def session = compass.openSession()


    logger.info("   importing files")
    try{
        File dir=new File(CONFIG.xmlDir);
        dir.listFiles().each{ xmlFile ->
            importModelFile(session,xmlFile)
        }
    }
    finally{
        session.close()
        compass.close()
    }

}

def importModelFile(session,xmlFile)
{
    logger.info("   importing file ${xmlFile.getPath()}");
    def tx = session.beginTransaction()
    try{

        def resultXml = new XmlSlurper().parse(xmlFile);
        def modelName=resultXml.@'model'.toString();
        def model=ALL_MODELS[modelName];
        def propTypes=[:];
        def modelProperties=model.metaClass.getProperties();

        modelProperties.each{ prop->
            propTypes[prop.name] = prop.type;
        }
        println "${modelName} prop types : ${propTypes}";

        def objects=resultXml.Object;
        logger.info("      will import ${objects.size()} ${modelName} instances ")
        objects.each{ objectRow ->
            def newObj=model.newInstance();

            objectRow.attributes().each{ propName,propVal ->
                try{
                    def convertedVal=convertProperty(propTypes[propName],propVal);
                    newObj.setProperty(propName,convertedVal,false)
                }
                catch(ConversionException exception)
                {
                    logger.warn("cannot convert property ${modelName}.${propName} with val ${propVal}");
                }
                catch(Exception e)
                {
                    logger.warn("can not set property ${modelName}.${propName} with val ${propVal}");
                }
            }

            session.save(newObj);


        }
        logger.info("      imported ${objects.size()} ${modelName} instances ")
    }
    finally
    {
        tx.commit()
    }

    logger.info("   imported file ${xmlFile.getPath()}");
}
def convertProperty(fieldType,value)
{
    def converter = RapidConvertUtils.getInstance().lookup (fieldType);
    return converter.convert(fieldType, value);
}
def getCompass(dataDir)
{
    def configurator = SearchableCompassConfiguratorFactory.getDomainClassMappingConfigurator(
        ApplicationHolder.application,
        [SearchableGrailsDomainClassMappingConfiguratorFactory.getSearchableClassPropertyMappingConfigurator([:], [], new DefaultSearchableCompassClassMappingXmlBuilder())] as SearchableGrailsDomainClassMappingConfigurator[]
    )
    def config = new CompassConfiguration()

    config.setConnection(new StringBuffer(System.getProperty("base.dir")).append(File.separator).append(dataDir).toString());

    def defaultSettings=DefaultCompassConfiguration.getDefaultSettings(ConfigurationHolder.getConfig());
    defaultSettings.remove("compass.engine.store.indexDeletionPolicy.type");
    defaultSettings.each{ key , val ->
        config.getSettings().setSetting (key,val);
    }


    configurator.configure(config, [:])

    def compass = config.buildCompass()
    return compass;
}
