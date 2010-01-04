package ui.designer

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.util.DesignerControllerUtils
import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 26, 2009
* Time: 3:11:26 PM
* To change this template use File | Settings | File Templates.
*/
class UiDesignerController {
    public static final String TEMPLATES_DIRECTORY = "grails-app/templates/ui/designer"
    public static final String HELP_FILE_DIRECTORY = "web-app/help/uidesigner"
    public static final String CONF_FILE = "grails-app/conf/uiconfiguration.xml"
    public static final String BACKUP_DIR = "grails-app/conf/UIConfigurations"
    static Object uiDefinitionLock = new Object();
    def baseDir = System.getProperty("base.dir")
    def view = {
        synchronized (uiDefinitionLock)
        {
            try
            {
                render(text: DesignerControllerUtils.view("${System.getProperty("base.dir")}/$CONF_FILE"), contentType: "text/xml");
            }
            catch (Throwable t)
            {
                log.warn("Exception occurred while viewing ui configuration", t);
                addError("designer.view.exception", [t.message]);
                render(contentType: "text/xml", text: errorsToXml());
            }
        }
    }

    def help = {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def helpFileDirectory = new File("${System.getProperty("base.dir")}/$HELP_FILE_DIRECTORY");
        builder.Helps() {
            helpFileDirectory.listFiles().each {File helpFile ->
                builder.Help([id: helpFile.getName()], helpFile.getText());
            }
        }
        render(text: sw.toString(), contentType: "text/xml");
    }

    def save = {
        synchronized (uiDefinitionLock)
        {
            def xmlConfigurationString = params.configuration
            try
            {
                DesignerControllerUtils.save(xmlConfigurationString, "${System.getProperty("base.dir")}/$CONF_FILE", "${System.getProperty("base.dir")}/$BACKUP_DIR")
                render(contentType: "text/xml") {
                    Successful("UI configuration saved successfully")
                }
            }
            catch (Throwable ex)
            {
                addError("designer.save.exception", [ex.message]);
                render(contentType: "text/xml", text: errorsToXml());
            }

        }
    }

    def reloadTemplates = {
        try
        {
            DesignerControllerUtils.reloadTemplates("${System.getProperty("base.dir")}/${TEMPLATES_DIRECTORY}")
            render(contentType: "text/xml") {
                Successful("Templates reloaded successfully")
            }

        }
        catch (Throwable e)
        {
            log.warn("Exception occurred while reloading designer templates", e);
            addError("designer.reload.remplate.exception", [e.message]);
            render(contentType: "text/xml", text: errorsToXml());
        }
    }

    def generate = {
        synchronized (uiDefinitionLock)
        {
            try
            {
                DesignerControllerUtils.generate("${baseDir}/${CONF_FILE}", "${baseDir}/${TEMPLATES_DIRECTORY}", baseDir)
                application.RapidApplication.reloadViewsAndControllers();
                render(contentType: "text/xml") {
                    Successful("UI generated successfully")
                }
            } catch (Throwable t)
            {
                log.warn("Exception occurred while generating ui", t);
                addError("designer.generate.exception", [t.message]);
                render(contentType: "text/xml", text: errorsToXml());
            }
        }
    }


    def metaData = {
        render(text: DesignerSpace.getInstance().getMetaData(), contentType: "text/xml");
    }

}