package scriptTests

import com.ifountain.rcmdb.domain.generation.ModelGenerationException
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.RapidCmdbScriptTestCase
import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 7, 2009
* Time: 2:39:34 PM
* To change this template use File | Settings | File Templates.
*/
class ModelCreatorTest extends RapidCmdbScriptTestCase
{
    //TODO:modelcreator tests should be implemented this is an initial version of modelcreator script test
    def static base_directory = "../testoutput/";
    def modelXmlBuilder;
    def modelXml;
    def confDir = new File("${base_directory}/grails-app/conf");
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        FileUtils.deleteDirectory (new File(base_directory));
        modelXml = new StringWriter();
        modelXmlBuilder = new MarkupBuilder(modelXml);
        System.setProperty("base.dir", base_directory)
        ModelGenerator.getInstance().initialize(base_directory, base_directory, "${getWorkspaceDirectory()}/RapidModules/RcmdbCommons");
        ModelGenerator.getInstance().invalidNames = [];
        FileUtils.deleteDirectory (confDir);
        confDir.mkdirs();
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }



    public void testGenerateSimpleModel()
    {
        String modelName = "Model1"
        modelXmlBuilder.Rsmodel{
            modelXmlBuilder.Models{
                modelXmlBuilder.Model(Name:modelName)
                {
                    modelXmlBuilder.Properties()
                    {
                        modelXmlBuilder.Property(Name:"prop1", Type:"string")
                    }
                }
            }
        }
        def confFile = new File(confDir, "ModelConfiguration.xml");
        confFile.setText (modelXml.toString());
        def tmpDir = "${base_directory}/tmpDir"
        def web = [:]
        web.flash = [:]
        web.grailsApplication = [config:[toProperties:{->
            return ["rapidCMDB.temp.dir":tmpDir]
        }]]
        def props = [web:web]
        def script  = loadScript("RapidModules/RapidCMDB/scripts/modelCreator.groovy", props)
        script.run();
        def modelClass = gcl.parseClass(new File("${base_directory}/grails-app/domain/${modelName}.groovy"));
        def modelInstance = modelClass.newInstance();
        assertEquals (modelName, modelInstance.class.name);
        assertEquals ("", modelInstance.prop1);
    }
    public void testIfUnExpectedModelXmlPropertyOfModelPropertyExist()
    {
        modelXmlBuilder.Rsmodel{
            modelXmlBuilder.Models{
                modelXmlBuilder.Model(Name:"Model1")
                {
                    modelXmlBuilder.Properties()
                    {
                        modelXmlBuilder.Property(Name:"prop1", Type:"String", unexpectedProp:"unexpectedPropValue")
                    }
                }
            }
        }
        def confFile = new File(confDir, "ModelConfiguration.xml");
        confFile.setText (modelXml.toString());
        def web = [:]
        web.flash = [:]
        web.grailsApplication = [config:[toProperties:{->
            return ["rapidCMDB.temp.dir":"${base_directory}/tmpDir"]
        }]]
        def props = [web:web]
        def script  = loadScript("RapidModules/RapidCMDB/scripts/modelCreator.groovy", props)
        try
        {
            script.run();
            fail("Should throw exception since there is an unexpected property");
        } catch (ModelGenerationException e)
        {
            assertEquals(ModelGenerationException.unexpectedXmlProperty("Model1", "unexpectedProp").getMessage(), e.getMessage());
        }

    }

    public void testIfUnExpectedModelXmlPropertyOfModelExist()
    {
        modelXmlBuilder.Rsmodel{
            modelXmlBuilder.Models{
                modelXmlBuilder.Model(Name:"Model1", unexpectedModelProp:"unexpectedModelPropValue")
                {
                    modelXmlBuilder.Properties()
                    {
                        modelXmlBuilder.Property(Name:"prop1", Type:"String")
                    }
                }
            }
        }
        def confFile = new File(confDir, "ModelConfiguration.xml");
        confFile.setText (modelXml.toString());
        def web = [:]
        web.flash = [:]
        web.grailsApplication = [config:[toProperties:{->
            return ["rapidCMDB.temp.dir":"${base_directory}/tmpDir"]
        }]]
        def props = [web:web]
        def script  = loadScript("RapidModules/RapidCMDB/scripts/modelCreator.groovy", props)
        try
        {
            script.run();
            fail("Should throw exception since there is an unexpected property");
        } catch (ModelGenerationException e)
        {
            assertEquals(ModelGenerationException.unexpectedXmlProperty("Model1", "unexpectedModelProp").getMessage(), e.getMessage());
        }

    }
    public void testIfUnExpectedModelXmlPropertyOfDatasourceExist()
    {
        modelXmlBuilder.Rsmodel{
            modelXmlBuilder.Models{
                modelXmlBuilder.Model(Name:"Model1")
                {
                    modelXmlBuilder.Properties()
                    {
                        modelXmlBuilder.Property(Name:"prop1", Type:"String")
                    }
                    modelXmlBuilder.Datasources()
                    {
                        modelXmlBuilder.Datasource(Definition:"ds1", Name:"ds1", unexpectedModelDsProp:"unexpectedModelPropValue"){
                            modelXmlBuilder.Keys{
                                modelXmlBuilder.Key(Name:"prop1", NameInDatasource:"prop1")
                            }
                        }
                    }
                }
            }
        }
        def confFile = new File(confDir, "ModelConfiguration.xml");
        confFile.setText (modelXml.toString());
        def web = [:]
        web.flash = [:]
        web.grailsApplication = [config:[toProperties:{->
            return ["rapidCMDB.temp.dir":"${base_directory}/tmpDir"]
        }]]
        def props = [web:web]
        def script  = loadScript("RapidModules/RapidCMDB/scripts/modelCreator.groovy", props)
        try
        {
            script.run();
            fail("Should throw exception since there is an unexpected property");
        } catch (ModelGenerationException e)
        {
            assertEquals(ModelGenerationException.unexpectedXmlProperty("Model1", "unexpectedModelDsProp").getMessage(), e.getMessage());
        }
    }

    public void testIfUnExpectedModelXmlPropertyOfDatasourceKeyExist()
    {
        modelXmlBuilder.Rsmodel{
            modelXmlBuilder.Models{
                modelXmlBuilder.Model(Name:"Model1")
                {
                    modelXmlBuilder.Properties()
                    {
                        modelXmlBuilder.Property(Name:"prop1", Type:"String")
                    }
                    modelXmlBuilder.Datasources()
                    {
                        modelXmlBuilder.Datasource(Definition:"ds1", Name:"ds1"){
                            modelXmlBuilder.Keys{
                                modelXmlBuilder.Key(Name:"prop1", NameInDatasource:"prop1", unexpectedModelDsKeyProp:"unexpectedModelDsKeyPropValue")
                            }
                        }
                    }
                }
            }
        }
        def confFile = new File(confDir, "ModelConfiguration.xml");
        confFile.setText (modelXml.toString());
        def web = [:]
        web.flash = [:]
        web.grailsApplication = [config:[toProperties:{->
            return ["rapidCMDB.temp.dir":"${base_directory}/tmpDir"]
        }]]
        def props = [web:web]
        def script  = loadScript("RapidModules/RapidCMDB/scripts/modelCreator.groovy", props)
        try
        {
            script.run();
            fail("Should throw exception since there is an unexpected property");
        } catch (ModelGenerationException e)
        {
            assertEquals(ModelGenerationException.unexpectedXmlProperty("Model1", "unexpectedModelDsKeyProp").getMessage(), e.getMessage());
        }

    }


    public void testIfUnExpectedModelXmlPropertyOfRelationExist()
    {
        modelXmlBuilder.Rsmodel{
            modelXmlBuilder.Models{
                modelXmlBuilder.Model(Name:"Model1")
                {
                    modelXmlBuilder.Properties()
                    {
                        modelXmlBuilder.Property(Name:"prop1", Type:"String")
                    }
                }
                modelXmlBuilder.Model(Name:"Model2")
                {
                    modelXmlBuilder.Properties()
                    {
                        modelXmlBuilder.Property(Name:"prop1", Type:"String")
                    }
                }
            }
            modelXmlBuilder.Relations{
                modelXmlBuilder.Relation(From:"Model1", To:"Model2", Name:"rel1", ReverseName:"rel2", Type:"OneToMany", unexpectedModelRelationProp:"unexpectedModelRelationPropValue")
            }
        }
        def confFile = new File(confDir, "ModelConfiguration.xml");
        confFile.setText (modelXml.toString());
        def web = [:]
        web.flash = [:]
        web.grailsApplication = [config:[toProperties:{->
            return ["rapidCMDB.temp.dir":"${base_directory}/tmpDir"]
        }]]
        def props = [web:web]
        def script  = loadScript("RapidModules/RapidCMDB/scripts/modelCreator.groovy", props)
        try
        {
            script.run();
            fail("Should throw exception since there is an unexpected property");
        } catch (ModelGenerationException e)
        {
            assertEquals(ModelGenerationException.unexpectedXmlProperty("Model1", "unexpectedModelRelationProp").getMessage(), e.getMessage());
        }

    }
}