package com.ifountain.rcmdb.utils

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils
import connection.DatabaseConnection
import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder

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
        if(System.getProperty("base.dir") == null)
        {
            FileUtils.copyDirectory (new File("RapidCMDB/grails-app/templates/xml"), new File(baseDir));
        }
        else
        {
            FileUtils.copyDirectory (new File("grails-app/templates/xml"), new File(baseDir), true);    
        }

    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
//        FileUtils.deleteDirectory (new File(baseDir));
    }

    public void testExportConnections()
    {
        def configurationItems = [];
        configurationItems += new DatabaseConnection(name:"dbcon1", driver:"com.mysql.jdbc.Driver1", url:"localhost1", username:"root1", password:"password1");
        configurationItems += new DatabaseConnection(name:"dbcon2", driver:"com.mysql.jdbc.Driver2", url:"localhost2", username:"root2", password:"password2");
        configurationItems += new DatabaseConnection(name:"dbcon3", driver:"com.mysql.jdbc.Driver3", url:"localhost3", username:"root3", password:"password3");
        configurationItems += new DatabaseConnection(name:"dbcon4", driver:"com.mysql.jdbc.Driver4", url:"localhost4", username:"root4", password:"password4");
        configurationItems += new DatabaseConnection(name:"dbcon5", driver:"com.mysql.jdbc.Driver5", url:"localhost5", username:"root5", password:"password5");
        def exportDir = "${baseDir}/export"

        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(baseDir, exportDir);
        impExpUtils.export(configurationItems);

        File returnedExportFile = new File(exportDir + "/connections.xml")

        assertTrue (returnedExportFile.exists());

        xmlBuilder.Connections{
            configurationItems.each {DatabaseConnection connection->
                xmlBuilder.DatabaseConnection(name:connection.name, connectionClass:connection.connectionClass, driver:connection.driver, url:connection.url, username:connection.username, password:connection.password);
            }
        }
        assertEqualsXML(writer.toString(), returnedExportFile.getText());
    }

}