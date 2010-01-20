package com.ifountain.rui.util

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.model.UiAction
import com.ifountain.rui.designer.model.UiComponent
import com.ifountain.rui.designer.model.UiTab
import com.ifountain.rui.designer.model.UiWebPage
import groovy.text.SimpleTemplateEngine
import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import ui.designer.DesignerTrashPage

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 23, 2009
* Time: 11:23:49 AM
*/
class DesignerControllerUtils {
    private static final templateCache = [:];
    public static String view(String fileName) {
        File configurationFile = new File(fileName);
        if (configurationFile.exists()) {
            def confXml;
            try {
                confXml = new XmlSlurper().parseText(configurationFile.getText());
            }
            catch (e) {
                throw new Exception("Could not parse UIConfiguration file. Reason: ${e.getMessage()}")
            }
            def uiElements = confXml.depthFirst().findAll {it.name() == "UiElement"}
            def id = 0;
            uiElements.each {uiElement ->
                def uiClass = DesignerSpace.getInstance().getUiClass("${DesignerSpace.PACKAGE_NAME}.Ui${uiElement.@designerType}".toString())
                if(uiClass){
                    def metaData = uiClass.metaData();
                    def childrenConfiguration = metaData.childrenConfiguration;
                    childrenConfiguration.each{childConfig ->
                        def canBeDeleted = false;
                        def childMeta = childConfig.metaData;
                        if(childMeta){
                            canBeDeleted = childMeta.canBeDeleted == true
                        }
                        else{
                            def childClass = DesignerSpace.getInstance().getUiClass("${DesignerSpace.PACKAGE_NAME}.Ui${childConfig.designerType}".toString())
                            if(childClass){
                                canBeDeleted = childClass.metaData().canBeDeleted == true;
                            }
                        }
                        if(!canBeDeleted && uiElement.UiElement.findAll{it.@designerType.text() == childConfig.designerType}.size() == 0){
                            uiElement.appendNode({UiElement(designerType:childConfig.designerType, id:++id)})
                        }
                    }
                }
                uiElement.attributes()["id"] = ++id;
            }
            def outputBuilder = new StreamingMarkupBuilder()
            return outputBuilder.bind {mkp.yield confXml}.toString();

        }
        else {
            throw new Exception("UIConfiguration file ${fileName} does not exist.")
        }
        return configurationFile.getText();
    }

    public static void save(String uiConfigXml, String filePath, String backupDirPath) {
        def configXml = new XmlSlurper().parseText(uiConfigXml);
        try {
            def webPagesNode = configXml."${UiElmnt.UIELEMENT_TAG}"[0]
            UiElmnt.removeUnneccessaryAttributes(webPagesNode);
            webPagesNode."${UiElmnt.UIELEMENT_TAG}".each {
                UiElmnt.create(it, null);
            }

            File configFile = new File(filePath);
            String fileNamePrefix = StringUtils.substringBefore(configFile.getName(), ".xml");
            File backupDir = new File(backupDirPath);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
                if (configFile.exists()) {
                    File originalBackupFile = new File("${backupDirPath}/${fileNamePrefix}_original.xml");
                    originalBackupFile.setText(configFile.getText());
                }
            }
            def stringWriter = new StringWriter()
            def printWriter = new PrintWriter(stringWriter)

            def outputBuilder = new StreamingMarkupBuilder()
            uiConfigXml = outputBuilder.bind {mkp.yield configXml}.toString();
            def node = new XmlParser().parseText(uiConfigXml)
            new XmlNodePrinter(printWriter).print(node)
            uiConfigXml = stringWriter.toString()
            configFile.setText(uiConfigXml)
            new File("${backupDirPath}/${fileNamePrefix}_${new SimpleDateFormat('yyMMddHHmm').format(System.currentTimeMillis())}.xml").setText(uiConfigXml)
        }
        finally {
            DesignerSpace.destroy();
        }
    }

    public static void generate(String filePath, String templatePath, String baseDir) {
        File configurationFile = new File(filePath);
        if (configurationFile.exists()) {
            def confXml;
            try {
                confXml = new XmlSlurper().parseText(configurationFile.getText());
            }
            catch (e) {
                throw new Exception("Could not parse UIConfiguration file. Reason: ${e.getMessage()}")
            }
            try {
                confXml."${UiElmnt.UIELEMENT_TAG}"[0]."${UiElmnt.UIELEMENT_TAG}".each {
                    UiElmnt.create(it, null);
                }
                def webPageTemplate = getTemplate("WebPage", templatePath);
                def tabTemplate = getTemplate("Tab", templatePath);
                def helpTemplate = getTemplate("Help", templatePath);

                def webPages = DesignerSpace.getInstance().getUiElements(UiWebPage).values()
                def webPageMap = [:]
                webPages.each {UiWebPage page ->
                    webPageMap.put(page.name, page);
                    def webPageLayoutFile = new File(getPageLayoutFilePath(baseDir, page.name));
                    webPageLayoutFile.parentFile.mkdirs();
                    def content = webPageTemplate.make(url: page).toString()
                    webPageLayoutFile.setText(content)
                    def webPageRedirectFile = new File(getPageFilePath(baseDir, page.name));
                    webPageRedirectFile.parentFile.mkdirs()
                    def tabs = page.getTabs();
                    if (!tabs.isEmpty())
                    {
                        webPageRedirectFile.setText("""
                        <%
                            response.sendRedirect("${page.name}/${tabs[0].name}.gsp");
                        %>
                    """)
                    }
                    else
                    {
                        webPageRedirectFile.setText("");
                    }
                    def webPageHelpFile = new File(getPageHelpFilePath(baseDir, page.name))
                    webPageHelpFile.parentFile.mkdirs()
                    if (!webPageHelpFile.exists()) {
                        webPageHelpFile.parentFile.mkdirs();
                        def helpContent = helpTemplate.make(url: page).toString()
                        webPageHelpFile.setText(helpContent)
                    }
                    tabs.each {UiTab tab ->
                        def tabOutputFile = new File(getTabFilePath(baseDir, page.name, tab.name));
                        tabOutputFile.parentFile.mkdirs();
                        StringBuffer tabContent = new StringBuffer();
                        def components = tab.getComponents();
                        components.each {UiComponent tabComponent ->
                            tabContent.append(generateTag(tabComponent, templatePath) + "\n\n");
                        }
                        def actions = tab.getActions();
                        actions.each {UiAction tabComponent ->
                            tabContent.append(generateTag(tabComponent, templatePath) + "\n\n");
                        }
                        def dialogs = tab.getDialogs();
                        dialogs.each {tabComponent ->
                            tabContent.append(generateTag(tabComponent, templatePath) + "\n\n");
                        }
                        def layoutContent = "";
                        def tabLayout = tab.getLayout();
                        if (tabLayout)
                        {
                            layoutContent = generateTag(tabLayout, templatePath);
                        }
                        def tabString = tabTemplate.make(tab: tab, tabContent: tabContent, layoutContent: layoutContent).toString()

                        tabOutputFile.setText(tabString)
                    }
                }
                DesignerTrashPage.list().each {trashPage ->
                    def webPage = trashPage.webPage;
                    if (!webPageMap.containsKey(webPage)) {
                        new File(getPageFilePath(baseDir, webPage)).delete();
                        FileUtils.deleteDirectory(new File(getPageDirectory(baseDir, webPage)));
                        new File(getPageLayoutFilePath(baseDir, webPage)).delete()
                    }
                    else {
                        def pageTabs = webPageMap[webPage].getTabs().name;
                        new File(getPageDirectory(baseDir, webPage)).listFiles().findAll {!pageTabs.contains(StringUtils.substringBefore(it.getName(), ".gsp"))}.each {
                            it.delete();
                        }
                    }
                    trashPage.remove();
                }
                webPages.each {UiWebPage page ->
                    DesignerTrashPage.add(webPage: page.name);
                }
            }
            finally {
                DesignerSpace.destroy();
            }
        }
        else {
            throw new Exception("UIConfiguration file ${filePath} does not exist.")
        }
    }
    public static void reloadTemplates(String templatesDirectory) {
        synchronized (templateCache)
        {
            templateCache.clear();
            templateCache.putAll(loadTemplates(templatesDirectory));
        }
    }
    private static loadTemplates(String templatesDirectory)
    {
        def templateEngine = new SimpleTemplateEngine(ApplicationHolder.application.classLoader);
        def templateDir = new File(templatesDirectory);
        def templates = [:];
        templateDir.listFiles().each {File templateFile ->
            if (!templateFile.isDirectory()) {
                def reader = new FileReader(templateFile);
                try {
                    def template = templateEngine.createTemplate(reader);
                    templates[templateFile.getName()] = template;
                } finally {
                    reader.close();
                }
            }
        }
        return templates;
    }
    private static getTemplate(String templateName, String templatesDirectory)
    {
        synchronized (templateCache)
        {
            if (templateCache.isEmpty())
            {
                templateCache.clear();
                templateCache.putAll(loadTemplates(templatesDirectory));
            }
            return templateCache[templateName + ".gsp"]
        }
    }

    public static String generateTag(UiElmnt uiElement, String templatesDirectory)
    {
        try
        {
            def tagTemplate = getTemplate(uiElement.metaData().designerType, templatesDirectory);
            return tagTemplate.make(uiElement: uiElement).toString();
        }
        catch (Exception e)
        {
            throw new Exception("An error occurred while generating html while processing template ${uiElement.metaData().designerType + ".gsp"}. Reason: ${e.getMessage()}", e);
        }
    }

    public static String getPageLayoutFilePath(String baseDir, String webPage)
    {
        return "${baseDir}/grails-app/views/layouts/${webPage}Layout.gsp".toString()
    }

    public static String getPageHelpFilePath(String baseDir, String webPage)
    {
        return "${baseDir}/web-app/help/${webPage}Help.gsp".toString()
    }

    public static String getPageFilePath(String baseDir, String webPage)
    {
        return "${baseDir}/web-app/${webPage}.gsp".toString()
    }
    public static String getPageDirectory(String baseDir, String webPage)
    {
        return "${baseDir}/web-app/${webPage}".toString()
    }

    public static String getTabFilePath(String baseDir, String webPage, String tabName)
    {
        return "${getPageDirectory(baseDir, webPage)}/${tabName}.gsp".toString()
    }
}