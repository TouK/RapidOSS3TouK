package com.ifountain.rcmdb.utils

import connection.Connection
import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.apache.log4j.Logger
import datasource.BaseDatasource
import script.CmdbScript
import model.Model;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 8, 2008
 * Time: 3:58:08 PM
 * To change this template use File | Settings | File Templates.
 */
class ConfigurationImportExportUtils { 
    String basedir;
    String exportDir;
    SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
    Map templates = [:];
    Logger logger;
    public ConfigurationImportExportUtils(String baseDir, String exportDir) {
        this(baseDir, exportDir, Logger.getLogger(ConfigurationImportExportUtils.class));
    }
    public ConfigurationImportExportUtils(String baseDir, String exportDir, Logger logger) {
        this.basedir = baseDir;
        this.exportDir = exportDir;
        this.logger = logger;
    }

    private Template getTemplate(String templatePath)
    {
        Template foundTemplate = templates.get(templatePath);
        if(!foundTemplate)
        {
            File templateFile = new File("${basedir}/${templatePath}")
            if(templateFile.exists())
            {
                def inputStr = templateFile.newInputStream();
                foundTemplate = templateEngine.createTemplate(new InputStreamReader(inputStr))
                templates[templatePath] = foundTemplate;
                inputStr.close();
            }
            else
            {
                logger.info ("Could not find template ${templateFile.path}");
            }
            
        }
        return foundTemplate;

    }
    public void export(List configurationObjects)
    {
        new File(exportDir).mkdirs();
        new File(exportDir+"/models").mkdirs();
        def connectionXmls = [];
        def datasourceXmls = [];
        def scriptXmls = [];
        Template connectionsTemplate = getTemplate("connection/Connections.xml")
        Template datasourcesTemplate = getTemplate("datasource/Datasources.xml")
        Template scriptsTemplate = getTemplate("script/Scripts.xml")
        Template modelTemplate = getTemplate("model/Model.xml")
        configurationObjects.each {configurationObject->
            def templatePath = "${configurationObject.class.name.replaceAll ("\\.", "/")}.xml"
            Template template = getTemplate(templatePath);
            if(template)
            {
                if(configurationObject instanceof Connection)
                {
                    connectionXmls += template.make ([object:configurationObject]).toString();
                }
                else if(configurationObject instanceof BaseDatasource)
                {
                    datasourceXmls += template.make ([object:configurationObject]).toString();
                }
                else if(configurationObject instanceof CmdbScript)
                {
                    scriptXmls += template.make ([object:configurationObject]).toString();
                }
                else if(configurationObject instanceof Model)
                {
                    File modelFile = new File("${exportDir}/models/${configurationObject.name}.xml");
                    modelTemplate.make([model:configurationObject]).writeTo(modelFile.newWriter());
                }
            }
        }

        File connectionsFile = new File("${exportDir}/connections.xml");
        File datasourcesFile = new File("${exportDir}/datasources.xml");
        File scriptsFile = new File("${exportDir}/scripts.xml");


        connectionsFile.setText(connectionsTemplate.make([connectionXmls:connectionXmls]).toString());
        datasourcesFile.setText(datasourcesTemplate.make([datasourceXmls:datasourceXmls]).toString());
        scriptsFile.setText(scriptsTemplate.make([scriptXmls:scriptXmls]).toString());
    }

}