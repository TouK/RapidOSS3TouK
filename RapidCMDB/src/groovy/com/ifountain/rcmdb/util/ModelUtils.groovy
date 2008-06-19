package com.ifountain.rcmdb.util

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 18, 2008
* Time: 6:20:36 PM
* To change this template use File | Settings | File Templates.
*/
class ModelUtils {
    public static def deleteModelArtefacts(String baseDir, String modelName)
    {
        def modelFile = new File("$baseDir/grails-app/domain/${modelName}.groovy");
        def modelControllerFile = new File("$baseDir/grails-app/controllers/${modelName}Controller.groovy");
        def modelOperationsFile = new File("$baseDir/operations/${modelName}Operations.groovy");
        def modelViewsDir = new File("$baseDir/grails-app/views/${modelName}");
        modelFile.delete();
        modelControllerFile.delete();
        modelOperationsFile.delete();
        FileUtils.deleteDirectory (modelViewsDir);
    }

    public static def generateModelArtefacts(GrailsDomainClass domainClass, String baseDir)
    {
			def generator = new DefaultGrailsTemplateGenerator();
			generator.overwrite = true;
            generator.generateViews(domainClass,baseDir);
            def viewsDir = new File("${baseDir}/grails-app/views/${domainClass.propertyName}")
            DefaultGrailsTemplateGenerator.LOG.info("Generating create view for domain class [${domainClass.fullName}]")
            def addToFile = new File("${viewsDir}/addTo.gsp")
            addToFile.withWriter { w ->
                generator.generateView(domainClass, "addTo", w)
            }
            DefaultGrailsTemplateGenerator.LOG.info("AddTo view generated at ${addToFile.absolutePath}")
            generator.generateController(domainClass,baseDir)
    }
}