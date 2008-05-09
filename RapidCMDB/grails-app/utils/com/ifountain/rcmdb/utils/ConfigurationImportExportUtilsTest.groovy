package com.ifountain.rcmdb.utils

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils
import connection.DatabaseConnection
import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import connection.SmartsConnection
import connection.HttpConnection
import connection.NetcoolConnection
import connection.RapidInsightConnection
import datasource.DatabaseDatasource
import datasource.SmartsNotificationDatasource
import datasource.HttpDatasource
import datasource.NetcoolDatasource
import datasource.SmartsTopologyDatasource
import datasource.SingleTableDatabaseDatasource
import datasource.RCMDBDatasource
import datasource.RapidInsightDatasource
import script.CmdbScript
import model.ModelDatasource
import model.ModelProperty
import model.ModelDatasourceKeyMapping
import model.Model
import model.ModelRelation

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 8, 2008
* Time: 3:58:58 PM
* To change this template use File | Settings | File Templates.
*/
class ConfigurationImportExportUtilsTest extends RapidCmdbTestCase{
    String baseDir = "../../testOutput";
    StringWriter writer;
    def xmlBuilder;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        writer = new StringWriter();
        xmlBuilder = new MarkupBuilder(writer);
        FileUtils.deleteDirectory (new File(baseDir));    
        if(System.getProperty("base.dir") == null || System.getProperty("base.dir") == ".")
        {
            FileUtils.copyDirectory (new File("RapidCMDB/grails-app/templates/xml"), new File(baseDir), true);
        }
        else
        {
            FileUtils.copyDirectory (new File("grails-app/templates/xml"), new File(baseDir), true);    
        }

    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        GroovySystem.metaClassRegistry.removeMetaClass (Model);
        GroovySystem.metaClassRegistry.removeMetaClass (ModelDatasource);
    }

    public void testExportConnections()
    {
        def configurationItems = [];
        configurationItems += new DatabaseConnection(name:"dbcon1", driver:"com.mysql.jdbc.Driver1", url:"localhost1", username:"root1", password:"password1");
        configurationItems += new DatabaseConnection(name:"dbcon2", driver:"com.mysql.jdbc.Driver2", url:"localhost2", username:"root2", password:"password2");
        configurationItems += new SmartsConnection(name:"smartcon1", broker:"localhost:426", domain:"INCHARGE-SA", username:"user1", password:"password1");
        configurationItems += new SmartsConnection(name:"smartcon2", broker:"localhost:427", domain:"INCHARGE-SA", username:"user2", password:"password2");
        configurationItems += new HttpConnection(name:"httpcon1", baseUrl:"www.google.com");
        configurationItems += new HttpConnection(name:"httpcon2", baseUrl:"www.google.com");
        configurationItems += new NetcoolConnection(name:"netcool1", url:"localhost1", username:"root1", password:"password1");
        configurationItems += new NetcoolConnection(name:"netcool2", url:"localhost2", username:"root2", password:"password2");
        configurationItems += new RapidInsightConnection(name:"ri1", baseUrl:"localhost:9191", username:"root1", password:"password1");
        configurationItems += new RapidInsightConnection(name:"ri2", baseUrl:"localhost:9192", username:"root2", password:"password2");
        def exportDir = "${baseDir}/export"

        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(baseDir);
        impExpUtils.export(exportDir, configurationItems);

        File returnedExportFile = new File(exportDir + "/connections.xml")

        assertTrue (returnedExportFile.exists());

        xmlBuilder.Connections{
            xmlBuilder.DatabaseConnection(name:configurationItems[0].name, connectionClass:configurationItems[0].connectionClass, driver:configurationItems[0].driver, url:configurationItems[0].url, username:configurationItems[0].username, password:configurationItems[0].password);
            xmlBuilder.DatabaseConnection(name:configurationItems[1].name, connectionClass:configurationItems[1].connectionClass, driver:configurationItems[1].driver, url:configurationItems[1].url, username:configurationItems[1].username, password:configurationItems[1].password);
            xmlBuilder.SmartsConnection(name:configurationItems[2].name, connectionClass:configurationItems[2].connectionClass, broker:configurationItems[2].broker, domain:configurationItems[2].domain, username:configurationItems[2].username, password:configurationItems[2].password);
            xmlBuilder.SmartsConnection(name:configurationItems[3].name, connectionClass:configurationItems[3].connectionClass, broker:configurationItems[3].broker, domain:configurationItems[3].domain, username:configurationItems[3].username, password:configurationItems[3].password);
            xmlBuilder.HttpConnection(name:configurationItems[4].name, connectionClass:configurationItems[4].connectionClass, baseUrl:configurationItems[4].baseUrl);
            xmlBuilder.HttpConnection(name:configurationItems[5].name, connectionClass:configurationItems[5].connectionClass, baseUrl:configurationItems[5].baseUrl);
            xmlBuilder.NetcoolConnection(name:configurationItems[6].name, connectionClass:configurationItems[6].connectionClass, driver:configurationItems[6].driver, url:configurationItems[6].url, username:configurationItems[6].username, password:configurationItems[6].password);
            xmlBuilder.NetcoolConnection(name:configurationItems[7].name, connectionClass:configurationItems[7].connectionClass, driver:configurationItems[7].driver, url:configurationItems[7].url, username:configurationItems[7].username, password:configurationItems[7].password);
            xmlBuilder.RapidInsightConnection(name:configurationItems[8].name, connectionClass:configurationItems[8].connectionClass, baseUrl:configurationItems[8].baseUrl, username:configurationItems[8].username, password:configurationItems[8].password);
            xmlBuilder.RapidInsightConnection(name:configurationItems[9].name, connectionClass:configurationItems[9].connectionClass, baseUrl:configurationItems[9].baseUrl, username:configurationItems[9].username, password:configurationItems[9].password);
        }
        assertEqualsXML(writer.toString(), returnedExportFile.getText());
    }

    public void testExportDatasources()
    {
        DatabaseConnection dbCon1 = new DatabaseConnection(name:"dbcon1", driver:"com.mysql.jdbc.Driver1", url:"localhost1", username:"root1", password:"password1");
        DatabaseConnection dbCon2 = new DatabaseConnection(name:"dbcon2", driver:"com.mysql.jdbc.Driver2", url:"localhost2", username:"root2", password:"password2");
        SmartsConnection  smCon = new SmartsConnection(name:"smartcon1", broker:"localhost:426", domain:"INCHARGE-SA", username:"user1", password:"password1");
        HttpConnection httpCon1 = new HttpConnection(name:"httpcon1", baseUrl:"www.google.com");
        NetcoolConnection netcoolCon1 = new NetcoolConnection(name:"netcool1", url:"localhost1", username:"root1", password:"password1");
        RapidInsightConnection riCon1 = new RapidInsightConnection(name:"ri1", baseUrl:"localhost:9191", username:"root1", password:"password1");

        def configurationItems = [];
        configurationItems += new DatabaseDatasource(name:"dbds1", connection:dbCon1);
        configurationItems += new DatabaseDatasource(name:"dbds2", connection:dbCon2);
        configurationItems += new SingleTableDatabaseDatasource(name:"dbds3", connection:dbCon2);
        configurationItems += new SmartsNotificationDatasource(name:"smartsDs1", connection:smCon);
        configurationItems += new SmartsTopologyDatasource(name:"smartsDs2", connection:smCon);
        configurationItems += new HttpDatasource(name:"httpDs1", connection:httpCon1);
        configurationItems += new NetcoolDatasource(name:"netcoolDs1", connection:netcoolCon1);
        configurationItems += new RapidInsightDatasource(name:"riDs1", connection:riCon1);
        configurationItems += new RCMDBDatasource(name:"cmdbDs1");

        def exportDir = "${baseDir}/export"

        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(baseDir);
        impExpUtils.export(exportDir, configurationItems);

        File returnedExportFile = new File(exportDir + "/datasources.xml")

        assertTrue (returnedExportFile.exists());

        xmlBuilder.Datasources{
            xmlBuilder.DatabaseDatasource(name:configurationItems[0].name, connection:dbCon1.name);
            xmlBuilder.DatabaseDatasource(name:configurationItems[1].name, connection:dbCon2.name);
            xmlBuilder.SingleTableDatabaseDatasource(name:configurationItems[2].name, connection:dbCon2.name);
            xmlBuilder.SmartsNotificationDatasource(name:configurationItems[3].name, connection:smCon.name);
            xmlBuilder.SmartsTopologyDatasource(name:configurationItems[4].name, connection:smCon.name);
            xmlBuilder.HttpDatasource(name:configurationItems[5].name, connection:httpCon1.name);
            xmlBuilder.NetcoolDatasource(name:configurationItems[6].name, connection:netcoolCon1.name);
            xmlBuilder.RapidInsightDatasource(name:configurationItems[7].name, connection:riCon1.name);
            xmlBuilder.RCMDBDatasource(name:configurationItems[8].name);
        }
        assertEqualsXML(writer.toString(), returnedExportFile.getText());
    }

    public void testExportScripts()
    {
        def scripts = [];
        scripts += new CmdbScript(name:"script1.groovy");
        scripts += new CmdbScript(name:"script2.groovy");
        def exportDir = "${baseDir}/export"

        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(baseDir);
        impExpUtils.export(exportDir, scripts);

        File returnedExportFile = new File(exportDir + "/scripts.xml")

        assertTrue (returnedExportFile.exists());

        xmlBuilder.Scripts{
            xmlBuilder.Script(name:scripts[0].name);
            xmlBuilder.Script(name:scripts[1].name);
        }
        assertEqualsXML(writer.toString(), returnedExportFile.getText());
    }

    public void testExportModel()
    {
        ModelDatasource.metaClass.keyMappings = [];
        Model.metaClass.modelProperties = [];
        Model.metaClass.datasources = [];
        Model.metaClass.fromRelations = [];
        Model.metaClass.toRelations = [];
        DatabaseConnection dbCon1 = new DatabaseConnection(name:"dbcon1", driver:"com.mysql.jdbc.Driver1", url:"localhost1", username:"root1", password:"password1");
        DatabaseDatasource databaseDatasource = new DatabaseDatasource(name:"dbds1", connection:dbCon1);
        RCMDBDatasource cmdbDatasource = new RCMDBDatasource(name:"dbds1");
        ModelDatasource ds1 = new ModelDatasource(datasource:databaseDatasource, master:false);
        ModelDatasource ds2 = new ModelDatasource(datasource:cmdbDatasource, master:true);
        ModelProperty prop1 = new ModelProperty(name:"prop1", propertyDatasource:ds1, blank:true, lazy:false, defaultValue:"default", nameInDatasource:"prop1nameinds", type:ModelProperty.stringType);
        ModelProperty prop2 = new ModelProperty(name:"prop1", propertyDatasource:ds1, type:ModelProperty.stringType);
        ModelProperty prop3 = new ModelProperty(name:"prop1", propertySpecifyingDatasource:prop2, type:ModelProperty.stringType);
        ModelDatasourceKeyMapping keyMap1 = new ModelDatasourceKeyMapping(property:prop1, nameInDatasource:"keyprop1nameinds");
        ModelDatasourceKeyMapping keyMap2 = new ModelDatasourceKeyMapping(property:prop2);
        ds1.keyMappings += keyMap1;
        ds1.keyMappings += keyMap2;
        
        def model = new Model(name:"Model1")
        model.datasources.add(ds1);
        model.datasources.add(ds2);
        model.modelProperties.add(prop1);
        model.modelProperties.add(prop2);
        model.modelProperties.add(prop3);
        def exportDir = "${baseDir}/export"

        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(baseDir);
        impExpUtils.export(exportDir, [model]);

        File returnedExportFile = new File(exportDir + "/models/${model.name}.xml")
        assertTrue (returnedExportFile.exists());
        def builderClosure = {
            xmlBuilder.Model(name:model.name, parentModel:model.parentModel?model.parentModel.name:""){
                xmlBuilder.Datasources{
                    model.datasources.each{ds->
                        xmlBuilder.Datasource(name:ds.datasource.name, master:ds.master)
                        {
                            xmlBuilder.Keys{
                                ds.keyMappings.each{ModelDatasourceKeyMapping keyMapping->
                                    xmlBuilder.Key(name:keyMapping.property.name, nameInDatasource:keyMapping.nameInDatasource?keyMapping.nameInDatasource:"");
                                }
                            }
                        }
                    }
                }

                xmlBuilder.Properties{
                    model.modelProperties.each{ModelProperty property->
                        xmlBuilder.Property(name:property.name, type:property.type, defaultValue:property.defaultValue?property.defaultValue:"",
                                blank:property.blank, lazy:property.lazy, nameInDatasource:property.nameInDatasource, propertyDatasource:property.propertyDatasource?property.propertyDatasource.datasource.name:"",
                                propertySpecifyingDatasource:property.propertySpecifyingDatasource?property.propertySpecifyingDatasource.name:"");
                    }
                }

                xmlBuilder.Relations();
            }
        }
        builderClosure();
        String expectedModelXml = writer.toString();
        assertEqualsXML(expectedModelXml, returnedExportFile.getText());

        writer = new StringWriter();
        xmlBuilder = new MarkupBuilder(writer);

        Model parentModel = new Model(name:"ParentModel")
        model.parentModel = parentModel;

        impExpUtils.export(exportDir, [model]);

        builderClosure();
        expectedModelXml = writer.toString();

        
        assertEqualsXML(expectedModelXml, returnedExportFile.getText());
    }

    public void testExportModelWithRelations()
    {
        Model.metaClass.modelProperties = [];
        Model.metaClass.datasources = [];
        Model.metaClass.fromRelations = [];
        Model.metaClass.toRelations = [];
        def model1 = new Model(name:"Model1")
        def model2 = new Model(name:"Model2")
        def modelRelation1 = new ModelRelation(firstName:"rel1", secondName:"revRel1", firstModel:model1, secondModel:model2, firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.MANY);
        def modelRelation2 = new ModelRelation(firstName:"rel2", secondName:"revRel2", firstModel:model2, secondModel:model1, firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE);
        def modelRelation3 = new ModelRelation(firstName:"rel3", secondName:"revRel3", firstModel:model1, secondModel:model2, firstCardinality:ModelRelation.MANY, secondCardinality:ModelRelation.MANY);


        model1.fromRelations.add(modelRelation1);
        model1.fromRelations.add(modelRelation3);
        model1.toRelations.add(modelRelation2);

        xmlBuilder.Model(name:model1.name, parentModel:""){
            xmlBuilder.Datasources();
            xmlBuilder.Properties();
            xmlBuilder.Relations{
                model1.fromRelations.each{ModelRelation fromRelation->
                    xmlBuilder.Relation(name:fromRelation.firstName, reverseName:fromRelation.secondName, toModel:fromRelation.secondModel.name, cardinality:fromRelation.firstCardinality, reverseCardinality:fromRelation.secondCardinality);
                }
            }
        }
        String expectedModelXml = writer.toString();

        def exportDir = "${baseDir}/export"

        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(baseDir);
        impExpUtils.export(exportDir, [model1]);

        File returnedExportFile = new File(exportDir + "/models/${model1.name}.xml")
        assertTrue (returnedExportFile.exists());
        
        
        assertEqualsXML(expectedModelXml, returnedExportFile.getText());
    }
}
