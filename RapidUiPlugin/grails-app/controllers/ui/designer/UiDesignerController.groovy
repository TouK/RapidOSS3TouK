package ui.designer

import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import com.ifountain.rui.util.DesignerUtils
import groovy.util.slurpersupport.GPathResult
import groovy.text.SimpleTemplateEngine
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

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
    private static final templateCache = [:];
    static Object uiDefinitionLock = new Object();
    def baseDir = System.getProperty("base.dir")
    def view = {
        synchronized (uiDefinitionLock)
        {
            def sw = new StringWriter();
            def markupBuilder = new MarkupBuilder(sw);
            def urls = UiWebPage.list();
            try
            {
                markupBuilder.UiConfig {
                    markupBuilder.UiElement(designerType: "WebPages") {
                        com.ifountain.rui.util.DesignerUtils.generateXml(urls, markupBuilder);
                    }
                }
                render(text: sw.toString(), contentType: "text/xml");
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
        def builder=new MarkupBuilder(sw);
        def helpFileDirectory = new File("${System.getProperty ("base.dir")}/$HELP_FILE_DIRECTORY");
        builder.Helps(){
            helpFileDirectory.listFiles().each{File helpFile->
                builder.Help([id:helpFile.getName()], helpFile.getText());
            }
        }
        render(text: sw.toString(), contentType: "text/xml");
    }
    
    def save = {
        synchronized (uiDefinitionLock)
        {
            def uiDomainClasses = grailsApplication.getDomainClasses().findAll {it.clazz.name.startsWith("ui.designer")}
            uiDomainClasses.each {domainClassInstance ->
                domainClassInstance.clazz.list().each {uiInstance ->
                    uiInstance.isActive = false;
                }
            }
            def xmlConfigurationString = params.configuration
            def xmlConfiguration = new XmlSlurper().parseText(xmlConfigurationString);
            try
            {
                processUiElement(xmlConfiguration);
                def urlsAfterDelete = UiWebPage.list();
                UiWebPage.search("isActive:false").results.each {urlObject ->
                    deleteUrlFiles(urlObject);
                }
                UiTab.search("isActive:false").results.each {UiTab tabObject ->
                    deleteTabFile(tabObject.webPage.name, tabObject);
                }
                uiDomainClasses.each {domainClassInstance ->
                    domainClassInstance.clazz.'removeAll'("isActive:false");
                }


                render(contentType: "text/xml")
                        {
                            Successful("UI configuration saved successfully")
                        }
            }
            catch (Throwable ex)
            {
                uiDomainClasses.each {domainClassInstance ->
                    domainClassInstance.clazz.'removeAll'("isActive:true");
                }
                uiDomainClasses.each {domainClass ->
                    domainClass.clazz.'list'().each {instance ->
                        instance.isActive = true;
                    }
                }
                addError("designer.save.exception", [ex.message]);
                render(contentType: "text/xml", text: errorsToXml());
            }

        }
    }

    def deleteUrlFiles(url)
    {
        def urlLayoutFile = new File("${baseDir}/grails-app/views/layouts/${url.name}Layout.gsp");
        def urlRedirectFile = new File("${baseDir}/web-app/${url.name}.gsp");
        def tabsDir = new File("${baseDir}/web-app/${url.name}");
        urlRedirectFile.delete();
        urlLayoutFile.delete();
        if (tabsDir.exists())
        {
            FileUtils.deleteDirectory(tabsDir)
        }
    }

    def deleteTabFile(url, tab)
    {
        def tabFile = new File("${baseDir}/web-app/${url}/${tab.name}.gsp");
        tabFile.delete();
    }

    def reloadTemplates = {
        try
        {
            synchronized (templateCache)
            {
                templateCache.clear();
                templateCache.putAll (loadTemplates());
            }
            render(contentType: "text/xml")
            {
                Successful("Templates reloaded successfully")
            }

        }
        catch(Throwable e)
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
                def urlTemplate = getTemplate("WebPage");
                def tabTemplate = getTemplate("Tab");
                UiWebPage.list().each {url ->
                    def urlLayoutFile = new File("${baseDir}/grails-app/views/layouts/${url.name}Layout.gsp");
                    def content = urlTemplate.make(url: url).toString()
                    urlLayoutFile.setText(content)
                    def urlRedirectFile = new File("${baseDir}/web-app/${url.name}.gsp");
                    if (!url.tabs.isEmpty())
                    {
                        urlRedirectFile.setText("""
                        <%
                            response.sendRedirect("${url.name}/${url.tabs[0].name}.gsp");
                        %>
                    """)
                    }
                    else
                    {
                        urlRedirectFile.setText("");
                    }
                    url.tabs.each {UiTab tab ->
                        def tabOutputFile = new File("${baseDir}/web-app/${url.name}/${tab.name}.gsp");
                        tabOutputFile.parentFile.mkdirs();
                        StringBuffer tabContent = new StringBuffer();
                        def components = tab.components.sort {it.id};
                        components.each {tabComponent ->
                            tabContent.append(generateTag(tabComponent) + "\n\n");
                        }
                        def actions = tab.actions.sort {it.id};
                        actions.each {tabComponent ->
                            tabContent.append(generateTag(tabComponent) + "\n\n");
                        }
                        def dialogs = tab.dialogs.sort {it.id}
                        dialogs.each {tabComponent ->
                            tabContent.append(generateTag(tabComponent) + "\n\n");
                        }
                        def layoutContent = "";
                        if (tab.layout)
                        {
                            layoutContent = generateTag(tab.layout);
                        }
                        def tabString = tabTemplate.make(tab: tab, tabContent: tabContent, layoutContent: layoutContent).toString()
                        //                    println tabString
                        //                    def formattedStringWriter = new StringWriter()
                        //                    def parser = new XmlParser(false, false);
                        //                    def rootNode = parser.parseText(tabString);
                        //                    def xmlPrinter = new XmlNodePrinter(new PrintWriter(formattedStringWriter));
                        //                    xmlPrinter.setQuote ("'")
                        //
                        //                    xmlPrinter.print(rootNode)
                        tabOutputFile.setText(tabString)
                    }
                }
                application.RsApplication.reloadViewsAndControllers();
                render(contentType: "text/xml")
                        {
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

    def generateTag(model)
    {
        try
        {
            def baseDir = System.getProperty("base.dir")
            def tagTemplate = getTemplate(model.metaData().designerType);
            return tagTemplate.make(uiElement: model).toString();
        }
        catch (Exception e)
        {
            throw new Exception("An error occurred while generating html while processing template ${model.metaData().designerType + ".gsp"}", e);
        }
    }

    def processUiElement(GPathResult xmlConfiguration)
    {
        xmlConfiguration.UiElement[0].UiElement.each {
            UiWebPage.addUiElement(it, null);
        }
    }

    def update = {

    }

    def metaData = {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.UiElements {
            builder.UiElement(designerType: "WebPages", display: "Web Pages", help:"Web Pages.html", imageExpanded: "images/rapidjs/component/tools/folder_open.gif", imageCollapsed: "images/rapidjs/component/tools/folder.gif") {
                builder.Children {
                    builder.Child(isMultiple: true, designerType: "WebPage")
                }
            }
            def uiDomainClasses = grailsApplication.getDomainClasses().findAll {it.clazz.name.startsWith("ui.designer")}
            uiDomainClasses.each {grailsDomainClass ->
                Class domainClass = grailsDomainClass.clazz;
                def domainClassMetaData = null
                try
                {
                    domainClassMetaData = domainClass.'metaData'()
                } catch (groovy.lang.MissingMethodException prop) {
                    log.warn("No meta data information available for ${domainClass.name}");
                };
                if (domainClassMetaData)
                {
                    def metaProperties = domainClassMetaData.propertyConfiguration ? domainClassMetaData.propertyConfiguration : [:]
                    if (domainClassMetaData.designerType != null)
                    {
                        domainClassMetaData.propertyConfiguration = DesignerUtils.addConfigurationParametersFromModel(metaProperties, grailsDomainClass);
                        createMetaXml(builder, domainClassMetaData);
                    }
                }
            }
        }

        render(text: sw.toString(), contentType: "text/xml");
    }

    def createMetaXml(builder, domainClassMetaData)
    {
        def uiElementProperties = [:]
        def metaChildren = domainClassMetaData.remove("childrenConfiguration")
        def metaProperties = domainClassMetaData.remove("propertyConfiguration")
        domainClassMetaData.each {String propName, Object propValue ->
            if (propName != "properties") {
                uiElementProperties[propName] = propValue;
            }
        }

        def configuredChildren = [];
        builder.UiElement(uiElementProperties)
                {
                    builder.Properties {
                        metaProperties.each {String propName, metaPropertyConfiguration ->
                            builder.Property(metaPropertyConfiguration);
                        }
                    }
                    builder.Children {
                        metaChildren.each {metaChildConfiguration ->
                            if (metaChildConfiguration.metaData != null)
                            {
                                configuredChildren.add(metaChildConfiguration.remove("metaData"));
                            }
                            builder.Child(metaChildConfiguration);

                        }
                    }
                }
        configuredChildren.each {childConfiguration ->
            createMetaXml(builder, childConfiguration);
        }

    }

    private loadTemplates()
    {
        def templateEngine = new SimpleTemplateEngine(ApplicationHolder.application.classLoader);
        def templateDir = new File("${System.getProperty("base.dir")}/${TEMPLATES_DIRECTORY}");
        def templates = [:];
        templateDir.listFiles().each {File templateFile ->
            def template = templateEngine.createTemplate(templateFile);
            templates[templateFile.getName()] = template;
        }
        return templates;
    }
    private getTemplate(String templateName)
    {
        synchronized (templateCache)
        {
            if (templateCache.isEmpty())
            {
                templateCache.clear();
                templateCache.putAll (loadTemplates());
            }
            return templateCache[templateName + ".gsp"]
        }
    }
}