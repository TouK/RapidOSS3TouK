package com.ifountain.rcmdb.util

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator
import com.ifountain.rcmdb.domain.generation.ModelGenerationUtils
import groovy.text.SimpleTemplateEngine
import com.ifountain.rcmdb.domain.AbstractDomainOperation

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 18, 2008
* Time: 6:20:36 PM
* To change this template use File | Settings | File Templates.
*/
class ModelUtils {
    public static final String OPERATIONS_CLASS_EXTENSION = "Operations"
    private static final String operationsFileTemplateString = """
    <%
    DEFAULT_IMPORTS.each{
    %>
    import \${it};
    <%
    }
    %>
    class \${name}Operations extends \${parentClassName}
    {
    }
    """
    private static groovy.text.Template operationsFileTemplate = new SimpleTemplateEngine().createTemplate(operationsFileTemplateString);
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

    public static def generateModelArtefacts(GrailsDomainClass domainClass, String baseDir, String destDir)
    {
			def generator = new DefaultGrailsTemplateGenerator();
			generator.basedir = baseDir;
			generator.overwrite = true;
            generator.generateViews(domainClass,destDir);
            def viewsDir = new File("${destDir}/grails-app/views/${domainClass.propertyName}")
            DefaultGrailsTemplateGenerator.LOG.info("Generating create view for domain class [${domainClass.fullName}]")
            def addToFile = new File("${viewsDir}/addTo.gsp")
            addToFile.withWriter { w ->
                generator.generateView(domainClass, "addTo", w)
            }
            DefaultGrailsTemplateGenerator.LOG.info("AddTo view generated at ${addToFile.absolutePath}")
            generator.generateController(domainClass, destDir)
            createModelOperationsFile (domainClass.clazz, new File(destDir+"/operations"), []);
    }





    def static createModelOperationsFile(Class modelClass, File workingOperationsDir, List defaultImports)
    {
        workingOperationsDir.mkdirs();
        def operationsFileToBeGenerated =  getOperationsModelFile(workingOperationsDir, modelClass.name)
        def parentClassName = modelClass.superclass && modelClass.superclass != Object ?"${modelClass.superclass.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}":AbstractDomainOperation.class.name
        if(!operationsFileToBeGenerated.exists())
        {

            def operationsBindings = [name:modelClass.name, parentClassName:parentClassName];
            operationsBindings["DEFAULT_IMPORTS"] = defaultImports;
            operationsFileToBeGenerated.withWriter { w ->
                def x = operationsFileTemplate.make(operationsBindings);
                x.writeTo(w);
            }

        }
        else
        {
            def operationsText = ModelGenerationUtils.generateClassText(operationsFileToBeGenerated, modelClass.name+ModelUtils.OPERATIONS_CLASS_EXTENSION, parentClassName, "", defaultImports);
            operationsFileToBeGenerated.setText(operationsText);
        }
    }

    public static File getOperationsModelFile(File workingOperationsDir, String name)
    {
        return new File("${workingOperationsDir.path}/${name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy");
    }
}