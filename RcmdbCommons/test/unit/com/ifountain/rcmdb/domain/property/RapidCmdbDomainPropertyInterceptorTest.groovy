/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.util.RapidCMDBConstants
import datasource.BaseDatasource
import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils
import org.springframework.validation.Errors
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError

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
    public void setUp() {
        super.setUp();
        DataStore.clear();
        DataStore.put ("getProperty", []);
        DataStore.put ("getProperties", []);
        DataStore.put ("getOnDemand", []);

        if(new File(".").getCanonicalPath().endsWith("RapidModules"))
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
        instance.invokeBeforeEventTriggerOperation{
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();
            assertEquals("1", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
            interceptor.setDomainClassProperty(instance.metaClass, instance.class, instance, "prop1", "updatedProp1Value")
            assertEquals("updatedProp1Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
            assertEquals (0, DataStore.get("getOnDemand").size());
            assertFalse(instance.hasErrors());
        }
    }

    public void testPropertyAccessPerformance()
    {
        def modelName = "Model1"
        def datasources = []
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)

        def instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();
            MetaClass mtcls = instance.metaClass;
            Class instanceCls = instance.class
            long t = System.nanoTime();
            100000.times{
                interceptor.getDomainClassProperty(mtcls, instanceCls, instance, "prop1");
            }
            def totalDuration = (System.nanoTime()-t) / Math.pow(10,9);
            println totalDuration
            assertTrue (totalDuration < 1.2);
        }


    }
    

    public void testInterceptorDoesNotThrowConversionExceptionWithFederatedProperties()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1", nameInDatasource:"prop2SeverName"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop4", type:ModelGenerator.NUMBER_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];

        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)
        def instance = domainClass.newInstance();
        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();
        instance.invokeBeforeEventTriggerOperation{
            instance.errors = new BeanPropertyBindingResult(instance, domainClass.name)

            DataStore.put("result", [prop2SeverName:"prop2Value", prop3:"prop3Value", prop1:"thisWillBeDiscarded",prop4:"abcd"]);
            assertEquals (new Long(1), interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop4"));
            assertEquals(1, instance.errors.allErrors.size());
            FieldError error = instance.errors.allErrors[0]
            assertEquals ("default.federation.property.conversion.exception", error.getCode());
            assertEquals ("prop4", error.getField());
        }
        instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            assertEquals (new Long(1), interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop4"));
            assertEquals(1, instance.errors.allErrors.size());
            def error = instance.errors.allErrors[0]
            assertEquals ("default.federation.property.conversion.exception", error.getCode());
            assertEquals ("prop4", error.getField());
        }
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
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop4", type:ModelGenerator.FLOAT_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];
        
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)       
        def instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

            def prop1Value = "prop1Value";
            interceptor.setDomainClassProperty(instance.metaClass, instance.class, instance, "prop1", prop1Value);
            assertEquals (prop1Value, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));

            DataStore.put("result", [prop2SeverName:"prop2Value", prop3:"prop3Value", prop1:"thisWillBeDiscarded",prop4:"15.88"]);

            assertEquals ("prop2Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertEquals (prop1Value, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
            assertEquals (new Double(15.88), interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop4"));

            assertEquals ("Should call getOnDemand to get a on demand datasource", 1, DataStore.get("getOnDemand").size());
            assertEquals ([name:"ds1"], DataStore.get("getOnDemand")[0]);
            assertEquals (1, DataStore.get("getProperties").size());
            assertEquals (1, DataStore.get("getProperties")[0][0].size());
            assertEquals (prop1Value, DataStore.get("getProperties")[0][0]["prop1"]);
            assertEquals (3, DataStore.get("getProperties")[0][1].size());
            assertTrue(DataStore.get("getProperties")[0][1].contains("prop2SeverName"));
            assertTrue(DataStore.get("getProperties")[0][1].contains("prop3"));


            DataStore.put("result", [prop2SeverName:"updatedServerValue", prop3:"updatedServerValue", prop1:"updatedServerValue"]);

            assertEquals ("prop2Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertEquals (prop1Value, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
            assertEquals (1, DataStore.get("getProperties").size());

            assertFalse(instance.hasErrors());
        }
    }


    public void testInterceptorWithFederatedPropertiesReturningNullPropertyvalue()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1", nameInDatasource:"prop2SeverName"]]

        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)
        def instance = domainClass.newInstance();
        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();
        instance.invokeBeforeEventTriggerOperation{

            DataStore.put("result", [prop2SeverName:null]);

            assertEquals (instance.prop2, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
        }
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
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop4", type:ModelGenerator.FLOAT_TYPE, blank:false, defaultValue:"1", datasource:"ds1", lazy:true]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)

        def instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

            def prop1Value = "prop1Value";
            interceptor.setDomainClassProperty(instance.metaClass, instance.class, instance, "prop1", prop1Value);
            assertEquals (prop1Value, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));

            DataStore.put("result", "prop2Value");

            assertEquals ("prop2Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            DataStore.put("result", [prop2:"prop2Value", prop3:"prop3Value", prop1:"thisWillBeDiscarded"]);
            assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertEquals (prop1Value, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
            DataStore.put("result", "15.88");
            assertEquals (new Double(15.88), interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop4"));

            assertEquals (1, DataStore.get("getProperties").size());
            assertEquals (1, DataStore.get("getProperties")[0][0].size());
            assertEquals (prop1Value, DataStore.get("getProperties")[0][0]["prop1"]);
            assertEquals (1, DataStore.get("getProperties")[0][1].size());
            assertTrue(DataStore.get("getProperties")[0][1].contains("prop3"));

            assertEquals (2, DataStore.get("getProperty").size());
            assertEquals (1, DataStore.get("getProperty")[0][0].size());
            assertEquals (prop1Value, DataStore.get("getProperty")[0][0]["prop1"]);
            assertEquals ("prop2", DataStore.get("getProperty")[0][1]);

            assertEquals (1, DataStore.get("getProperty")[1][0].size());
            assertEquals (prop1Value, DataStore.get("getProperty")[1][0]["prop1"]);
            assertEquals ("prop4", DataStore.get("getProperty")[1][1]);


            DataStore.put("result", "updatedServerValue");
            DataStore.get("getOnDemand").clear();
            assertEquals ("updatedServerValue", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("Should call getOnDemand to get a on demand datasource", [name:"ds1"], DataStore.get("getOnDemand")[0]);
            DataStore.put("result", [prop2:"updatedServerValue", prop3:"updatedServerValue", prop1:"updatedServerValue"]);
            assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertEquals (prop1Value, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
            assertEquals (1, DataStore.get("getProperties").size());

            assertFalse(instance.hasErrors());
        }
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
        instance.invokeBeforeEventTriggerOperation{
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

            instance.prop2 = "ds1";

            DataStore.put("result", [prop2:"prop2Value", prop3:"prop3Value", prop4:"prop4Value", prop1:"thisWillBeDiscarded"]);
            assertEquals ("ds1", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            DataStore.put("result", "prop4Value")
            assertEquals ("prop4Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop4"));

            assertEquals (1, DataStore.get("getProperties").size());
            assertEquals (1, DataStore.get("getProperties")[0][0].size());
            assertEquals ("1", DataStore.get("getProperties")[0][0]["prop1"]);
            assertEquals (1, DataStore.get("getProperties")[0][1].size());
            assertTrue(DataStore.get("getProperties")[0][1].contains("prop3"));

            assertEquals (1, DataStore.get("getProperty").size());
            assertEquals (1, DataStore.get("getProperty")[0][0].size());
            assertEquals ("1", DataStore.get("getProperty")[0][0]["prop1"]);
            assertEquals ("prop4", DataStore.get("getProperty")[0][1]);
            assertFalse(instance.hasErrors());
        }
    }


    public void testReturnsErrorIfDynamicDatasourceNameDoesNotExist()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"prop2"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)

        def instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

            instance.prop2 = "ds1";
            instance.prop3 = "defaultValue";

            DataStore.put("result", [prop2:"prop2Value", prop3:"prop3Value", prop4:"prop4Value", prop1:"thisWillBeDiscarded"]);
            assertEquals ("ds1", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals(instance.prop3, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertTrue(instance.errors.hasErrors());
            def prop3Error = instance.errors.allErrors.find{it.getField() == "prop3"}
            assertEquals ("default.federation.property.datasource.definition.exception", prop3Error.code);
        }
    }


    public void testInterceptorWithMappedNameProperty()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"datasourceAliasName", keyProperties:[[name:"prop1"]], mappedNameProperty:"prop2"]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"datasourceAliasName"],
        [name:"prop4", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"datasourceAliasName", lazy:true]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)


        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();


        def instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            instance.prop2 = "ds1";
            DataStore.put("result", [prop2:"prop2Value", prop3:"prop3Value", prop4:"prop4Value", prop1:"thisWillBeDiscarded"]);
            assertEquals ("ds1", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
        }
        
        instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            instance.prop2 = "ds1";
            DataStore.put("result", "prop4Value");
            assertEquals ("ds1", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("prop4Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop4"));
            assertFalse(instance.hasErrors());
        }
    }

    public void testInterceptorWithMappedNamePropertyAndDynamicDatasourceProperty()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"datasourceAliasName", keyProperties:[[name:"prop1"]], mappedNameProperty:"prop2"]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop5", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"prop5"],
        [name:"prop4", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"prop5", lazy:true]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)


        RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();


        def instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            instance.prop2 = "ds1";
            instance.prop5 = "datasourceAliasName";
            DataStore.put("result", [prop2:"prop2Value", prop3:"prop3Value", prop4:"prop4Value", prop1:"thisWillBeDiscarded"]);
            assertEquals ("ds1", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("prop3Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertFalse(instance.hasErrors());
        }
        instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            instance.prop2 = "ds1";
            instance.prop5 = "datasourceAliasName";
            DataStore.put("result", "prop4Value");
            assertEquals ("ds1", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals ("prop4Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop4"));
            assertFalse(instance.hasErrors());
        }
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
        instance.invokeBeforeEventTriggerOperation{
            def errorsBeforeFederationException = new BeanPropertyBindingResult(instance, domainClass.name);
            instance.errors = errorsBeforeFederationException
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();

            assertEquals (defaultValueForProp2, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertEquals(1, instance.errors.allErrors.size());
            FieldError prop2Error = instance.errors.allErrors.find{it.getField() == "prop2"}
            assertEquals ("default.federation.property.datasource.not.exist", prop2Error.code);
            assertNotSame (errorsBeforeFederationException, instance.errors);

            errorsBeforeFederationException = new BeanPropertyBindingResult(instance, domainClass.name);
            instance.errors = errorsBeforeFederationException
            assertEquals (defaultValueForProp3, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertEquals(1, instance.errors.allErrors.size());
            def prop3Error = instance.errors.allErrors.find{it.getField() == "prop3"}
            assertEquals ("default.federation.property.datasource.not.exist", prop3Error.code);
            assertNotSame (errorsBeforeFederationException, instance.errors);
        }


    }

    public void testInterceptorFederatedPropertiesWithDatasourceThrowingException()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def defaultValueForProp2 = "defaultVal"
        def defaultValueForProp3 = "defaultVal"
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:defaultValueForProp2, datasource:"ds1"],
        [name:"prop3", lazy:true, type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:defaultValueForProp3, datasource:"ds1"]];
        Class domainClass = createModelAndInitializeCompass(modelName, datasources, properties)
        def instance = domainClass.newInstance();
        instance.invokeBeforeEventTriggerOperation{
            def errorsBeforeFederationException = new BeanPropertyBindingResult(instance, domainClass.name);
            instance.errors = errorsBeforeFederationException
            RapidCmdbDomainPropertyInterceptor interceptor = new RapidCmdbDomainPropertyInterceptor();
            DataStore.put("exception", new Exception("An exception occurred"));
            assertEquals (defaultValueForProp2, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop2"));
            assertNotSame (errorsBeforeFederationException, instance.errors);
            assertEquals(1, instance.errors.allErrors.size());
            FieldError getPropertiesError = instance.errors.allErrors[0]
            assertEquals ("default.federation.property.get.properties.exception", getPropertiesError.code);

            errorsBeforeFederationException = new BeanPropertyBindingResult(instance, domainClass.name);
            instance.errors = errorsBeforeFederationException
            assertEquals (defaultValueForProp3, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop3"));
            assertEquals(1, instance.errors.allErrors.size());
            assertNotSame (errorsBeforeFederationException, instance.errors);
            def getPropertyError = instance.errors.allErrors[0]
            assertEquals ("default.federation.property.datasource.exception", getPropertyError.code);
            assertEquals ("prop3", getPropertyError.getField());
        }
    }


    def createModelAndInitializeCompass(modelName, datasources, properties)
    {
        datasourceClass = gcl.parseClass("""
            class AnotherDatasource extends ${BaseDatasource.name}{
                static searchable = true;
                def getProperty(Map keys, String propName)
                {
                    if(${DataStore.class.name}.get("exception"))
                    {
                        throw ${DataStore.class.name}.get("exception");
                    }
                    ${DataStore.class.name}.get("getProperty") << [keys, propName];
                    return ${DataStore.class.name}.get("result");
                }

                    def getProperties(Map keys, List properties)
                {
                    if(${DataStore.class.name}.get("exception") != null)
                    {
                        throw ${DataStore.class.name}.get("exception");
                    }
                    ${DataStore.class.name}.get("getProperties") << [keys, properties];
                    return ${DataStore.class.name}.get("result");
                }
            }
        """)
        def classesTobeLoaded = [];
        classesTobeLoaded << BaseDatasource;
        classesTobeLoaded << datasourceClass;




        
        def modelText = ModelGenerator.getInstance().getModelText(createModel(modelName,datasources, properties))
        Class domainClass = gcl.parseClass(modelText);
        classesTobeLoaded.add (domainClass)
        initialize(classesTobeLoaded, []);
        BaseDatasource.metaClass.static.getOnDemand = {params->
            DataStore.get("getOnDemand").add(params);
            return BaseDatasource.get(params);
        }
        def propertyDatasourceManagerBean = new PropertyDatasourceManagerBean();
        propertyDatasourceManagerBean.afterPropertiesSet();
        ctx.registerMockBean(RapidCMDBConstants.PROPERTY_DATASOURCE_MANAGER_BEAN, propertyDatasourceManagerBean)
        datasourceClass.metaClass.invokeStaticMethod(datasourceClass, "add", [[name:"ds1"]] as Object[]);
        return domainClass;
    }


    def createModel(String name, List datasources, List properties)
    {
        def model = new StringWriter();
        def modelbuilder = new MarkupBuilder(model);
        modelbuilder.Model(name:name){
            modelbuilder.Datasources(){
                datasources.each{datasource->
                    def tmpDsConf = new HashMap(datasource);
                    def keys = tmpDsConf.remove("keyProperties");
                    modelbuilder.Datasource(tmpDsConf){
                        keys.each{Map keyPropConfig->
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