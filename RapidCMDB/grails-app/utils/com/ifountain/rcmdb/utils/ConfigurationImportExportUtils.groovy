package com.ifountain.rcmdb.utils

import connection.Connection
import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.apache.log4j.Logger;
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
                foundTemplate = templateEngine.createTemplate(templateFile)
                templates[templatePath] = foundTemplate;
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
        def connectionXmls = [];
        configurationObjects.each {configurationObject->
            def templatePath = "${configurationObject.class.name.replaceAll ("\\.", "/")}.xml"
            Template template = getTemplate(templatePath);
            if(template)
            {
                connectionXmls += template.make ([object:configurationObject]).toString();
            }
        }

        File connectionsFile = new File("${exportDir}/connections.xml");
        if(connectionsFile.parentFile)
        {
            connectionsFile.parentFile.mkdirs();
        }
        Template connectionsTemplate = getTemplate("connection/Connections.xml")
        connectionsTemplate.make([connectionXmls:connectionXmls]).writeTo(connectionsFile.newWriter());
    }

}