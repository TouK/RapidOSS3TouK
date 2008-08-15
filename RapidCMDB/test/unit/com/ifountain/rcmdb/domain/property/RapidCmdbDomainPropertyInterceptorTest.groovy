package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.commons.io.FileUtils
import groovy.xml.MarkupBuilder
import java.lang.reflect.Method
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.BaseDatasource
import grails.spring.BeanBuilder
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 3:16:27 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidCmdbDomainPropertyInterceptorTest extends RapidCmdbWithCompassTestCase{

    def static base_directory = "../testoutput/";
    def datasourceClass;
    def static List getProperty;
    def static List getProperties;
    def static Exception exceptionWillBeThrown;
    def static result;
    public void setUp() {
        super.setUp();
        getProperty = [];
        getProperties = [];
        exceptionWillBeThrown = null;
        if(new File(".").getAbsolutePath().endsWith("RapidModules"))
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, "RcmdbCommons");
        }
        else
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, ".");
        }
        FileUtils.deleteDirectory (new File(base_directory));
        new File(base_directory).mkdirs();
    }


    public void testInterceptorWithNonFederatedProps()
    {
        def modelName = "Model1"
        def datasources = []
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)

        def instance = domainClass.newInstance();

        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();
        assertEquals("1", interceptor.getDomainClassProperty(instance, "prop1"));
        interceptor.setDomainClassProperty(instance, "prop1", "updatedProp1Value")
        assertEquals("updatedProp1Value", interceptor.getDomainClassProperty(instance, "prop1"));
    }

    public void testInterceptorWithFederatedProperties()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1", nameInDatasource:"prop2SeverName"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)       
        def instance = domainClass.newInstance();
        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

        def prop1Value = "prop1Value";
        interceptor.setDomainClassProperty(instance, "prop1", prop1Value);
        assertEquals (prop1Value, interceptor.getDomainClassProperty(instance, "prop1"));

        result = [prop2SeverName:"prop2Value", prop3:"prop3Value", prop1:"thisWillBeDiscarded"];
        
        assertEquals ("prop2Value", interceptor.getDomainClassProperty(instance, "prop2"));
        assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance, "prop3"));
        assertEquals (prop1Value, interceptor.getDomainClassProperty(instance, "prop1"));

        assertEquals (1, getProperties.size());
        assertEquals (1, getProperties[0][0].size());
        assertEquals (prop1Value, getProperties[0][0]["prop1"]);
        assertEquals (2, getProperties[0][1].size());
        assertTrue(getProperties[0][1].contains("prop2SeverName"));
        assertTrue(getProperties[0][1].contains("prop3"));


        result = [prop2SeverName:"updatedServerValue", prop3:"updatedServerValue", prop1:"updatedServerValue"];

        assertEquals ("prop2Value", interceptor.getDomainClassProperty(instance, "prop2"));
        assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance, "prop3"));
        assertEquals (prop1Value, interceptor.getDomainClassProperty(instance, "prop1"));
        assertEquals (1, getProperties.size());
    }

    public void testInterceptorWithLazyFederatedProperties()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1", lazy:true],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)

        def instance = domainClass.newInstance();
        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

        def prop1Value = "prop1Value";
        interceptor.setDomainClassProperty(instance, "prop1", prop1Value);
        assertEquals (prop1Value, interceptor.getDomainClassProperty(instance, "prop1"));

        result = "prop2Value";
        assertEquals ("prop2Value", interceptor.getDomainClassProperty(instance, "prop2"));
        result = [prop2:"prop2Value", prop3:"prop3Value", prop1:"thisWillBeDiscarded"];
        assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance, "prop3"));
        assertEquals (prop1Value, interceptor.getDomainClassProperty(instance, "prop1"));

        assertEquals (1, getProperties.size());
        assertEquals (1, getProperties[0][0].size());
        assertEquals (prop1Value, getProperties[0][0]["prop1"]);
        assertEquals (1, getProperties[0][1].size());
        assertTrue(getProperties[0][1].contains("prop3"));

        assertEquals (1, getProperty.size());
        assertEquals (1, getProperty[0][0].size());
        assertEquals (prop1Value, getProperty[0][0]["prop1"]);
        assertEquals ("prop2", getProperty[0][1]);


        result = "updatedServerValue";

        assertEquals ("updatedServerValue", interceptor.getDomainClassProperty(instance, "prop2"));
        result = [prop2:"updatedServerValue", prop3:"updatedServerValue", prop1:"updatedServerValue"];
        assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance, "prop3"));
        assertEquals (prop1Value, interceptor.getDomainClassProperty(instance, "prop1"));
        assertEquals (1, getProperties.size());
    }

    public void testInterceptorWithDynamicDatasourceName()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"prop2"],
        [name:"prop4", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"prop2", lazy:true]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)

        def instance = domainClass.newInstance();
        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

        instance.prop2 = "ds1";

        result = [prop2:"prop2Value", prop3:"prop3Value", prop4:"prop4Value", prop1:"thisWillBeDiscarded"];
        assertEquals ("ds1", interceptor.getDomainClassProperty(instance, "prop2"));
        assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance, "prop3"));
        result = "prop4Value"
        assertEquals ("prop4Value", interceptor.getDomainClassProperty(instance, "prop4"));

        assertEquals (1, getProperties.size());
        assertEquals (1, getProperties[0][0].size());
        assertEquals ("1", getProperties[0][0]["prop1"]);
        assertEquals (1, getProperties[0][1].size());
        assertTrue(getProperties[0][1].contains("prop3"));

        assertEquals (1, getProperty.size());
        assertEquals (1, getProperty[0][0].size());
        assertEquals ("1", getProperty[0][0]["prop1"]);
        assertEquals ("prop4", getProperty[0][1]);
    }

    public void testInterceptorFederatedPropertiesWithNonExistingDatasource()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"undefinedDatasource", keyProperties:[[name:"prop1"]]]
        ]
        def defaultValueForProp2 = "defaultVal"
        def defaultValueForProp3 = "defaultVal"
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:defaultValueForProp2, datasource:"undefinedDatasource"],
        [name:"prop3", lazy:true, type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:defaultValueForProp3, datasource:"undefinedDatasource"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)
        def instance = domainClass.newInstance();
        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

        assertEquals (defaultValueForProp2, interceptor.getDomainClassProperty(instance, "prop2"));
        assertEquals (defaultValueForProp3, interceptor.getDomainClassProperty(instance, "prop3"));
    }

    public void testInterceptorFederatedPropertiesWithDatasourceThrowingException()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"undefinedDatasource", keyProperties:[[name:"prop1"]]]
        ]
        def defaultValueForProp2 = "defaultVal"
        def defaultValueForProp3 = "defaultVal"
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:defaultValueForProp2, datasource:"ds1"],
        [name:"prop3", lazy:true, type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:defaultValueForProp3, datasource:"ds1"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)
        def instance = domainClass.newInstance();
        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();
        exceptionWillBeThrown = new Exception("An exception occurred");
        assertEquals (defaultValueForProp2, interceptor.getDomainClassProperty(instance, "prop2"));
        assertEquals (defaultValueForProp3, interceptor.getDomainClassProperty(instance, "prop3"));
    }


    def createModelAndInitializeCompass(modelName, datasources, properties)
    {
        datasourceClass = gcl.parseClass("""
            class AnotherDatasource extends ${BaseDatasource.name}{
                static searchable = true;
                def getProperty(Map keys, String propName)
                {
                    if(${this.class.name}.exceptionWillBeThrown)
                    {
                        throw ${this.class.name}.exceptionWillBeThrown;
                    }
                    ${this.class.name}.getProperty << [keys, propName];
                    return ${this.class.name}.result
                }

                def getProperties(Map keys, List properties)
                {
                    if(${this.class.name}.exceptionWillBeThrown)
                    {
                        throw ${this.class.name}.exceptionWillBeThrown;
                    }
                    ${this.class.name}.getProperties << [keys, properties];
                    return ${this.class.name}.result
                }
            }
        """)
        def classesTobeLoaded = [];
        classesTobeLoaded << BaseDatasource;
        classesTobeLoaded << datasourceClass;
        initialize(classesTobeLoaded, []);
        def propertyDatasourceManagerBean = new PropertyDatasourceManagerBean();
        propertyDatasourceManagerBean.afterPropertiesSet();
        ctx.registerMockBean(RapidCMDBConstants.PROPERTY_DATASOURCE_MANAGER_BEAN, propertyDatasourceManagerBean)


        datasourceClass.metaClass.invokeStaticMethod(datasourceClass, "add", [[name:"ds1"]] as Object[]);
        
        def modelText = ModelGenerator.getInstance().getModelText(createModel(modelName,datasources, properties))
        println modelText;
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class domainClass = gcl.parseClass(modelText);
        return domainClass;
    }


    def createModel(String name, List datasources, List properties)
    {
        def model = new StringWriter();
        def modelbuilder = new MarkupBuilder(model);
        modelbuilder.Model(name:name){
            modelbuilder.Datasources(){
                datasources.each{datasource->
                    modelbuilder.Datasource(name:datasource.name){
                        datasource.keyProperties.each{Map keyPropConfig->
                            modelbuilder.Key(propertyName:keyPropConfig.name)
                        }
                    }
                }
            }

            modelbuilder.Properties(){
                properties.each{Map propConfig->
                    modelbuilder.Property(propConfig)
                }
            }
        }
        return model.toString();
    }
}