package ui.designer

import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import com.ifountain.rui.util.DesignerUtils
import groovy.util.slurpersupport.GPathResult
import groovy.text.SimpleTemplateEngine
import org.apache.commons.io.FileUtils
import application.RsApplication

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 26, 2009
* Time: 3:11:26 PM
* To change this template use File | Settings | File Templates.
*/
class UiDesignerController {
    static Object uiDefinitionLock = new Object();
    def baseDir = System.getProperty("base.dir")
    def view= {
        synchronized (uiDefinitionLock)
        {
            def sw = new StringWriter();
            def markupBuilder = new MarkupBuilder(sw);
            def urls = UiUrl.list();
            markupBuilder.UiConfig{
                markupBuilder.UiElement(designerType:"Urls"){
                    createViewXml (urls, markupBuilder);
                }
            }
            render(text:sw.toString(), contentType:"text/xml");
        }
    }

    def save= {
        synchronized (uiDefinitionLock)
        {
            def uiDomainClasses = grailsApplication.getDomainClasses().findAll {it.clazz.name.startsWith("ui.designer")}
            uiDomainClasses.each{domainClassInstance->
                domainClassInstance.clazz.list().each{uiInstance->
                    uiInstance.isActive=false;
                }
            }
            def xmlConfigurationString = params.configuration
            def xmlConfiguration = new XmlSlurper().parseText(xmlConfigurationString);
            try
            {
                processUiElement(xmlConfiguration);
                def urlsAfterDelete = UiUrl.list();
                UiUrl.search("isActive:false").results.each{urlObject->
                    deleteUrlFiles(urlObject);
                }
                UiTab.search("isActive:false").results.each{UiTab tabObject->
                    deleteTabFile(tabObject.url.url, tabObject);
                }
                uiDomainClasses.each{domainClassInstance->
                    domainClassInstance.clazz.'removeAll'("isActive:false");
                }


                render(contentType:"text/xml")
                {
                    Successful("UI configuration saved successfully")
                }
            }
            catch(com.ifountain.rui.util.exception.UiElementCreationException ex)
            {
                uiDomainClasses.each{domainClassInstance->
                    domainClassInstance.clazz.'removeAll'("isActive:true");
                }
                uiDomainClasses.each{domainClass->
                    domainClass.clazz.'list'().each{instance->
                        instance.isActive = true;    
                    }
                }
                render(contentType:"text/xml")
                {
                    Errors{
                        Error(error:ex.message);
                    }
                }
            }

        }
    }

//    def createXml(xmlConfiguration, xmlBuilder)
//    {
//
//    }

    def deleteUrlFiles(url)
    {
        def urlLayoutFile = new File("${baseDir}/grails-app/views/layouts/${url.url}Layout.gsp");
        def urlRedirectFile = new File("${baseDir}/web-app/${url.url}.gsp");
        def tabsDir = new File("${baseDir}/web-app/${url.url}");
        urlRedirectFile.delete();
        urlLayoutFile.delete();
        if(tabsDir.exists())
        {
            FileUtils.deleteDirectory (tabsDir)
        }
    }

    def deleteTabFile(url, tab)
    {
        def tabFile = new File("${baseDir}/web-app/${url}/${tab.name}.gsp");
        tabFile.delete();
    }

    def generate = {
        synchronized (uiDefinitionLock)
        {
            def templateEngine = new SimpleTemplateEngine();
            def urlTemplate = templateEngine.createTemplate (new File("${baseDir}/grails-app/templates/ui/designer/Url.gsp"));
            def tabTemplate = templateEngine.createTemplate (new File("${baseDir}/grails-app/templates/ui/designer/Tab.gsp"));
            UiUrl.list().each{url->
                def urlLayoutFile = new File("${baseDir}/grails-app/views/layouts/${url.url}Layout.gsp");
                def content = urlTemplate.make (url:url).toString()
                urlLayoutFile.setText (content)
                def urlRedirectFile = new File("${baseDir}/web-app/${url.url}.gsp");
                if(!url.tabs.isEmpty())
                {
                    urlRedirectFile.setText ("""
                        <%
                            response.sendRedirect("${url.url}/${url.tabs[0].name}.gsp");
                        %>
                    """)
                }
                else
                {
                    urlRedirectFile.setText ("");
                }
                url.tabs.each{UiTab tab->
                    def tabOutputFile = new File("${baseDir}/web-app/${url.url}/${tab.name}.gsp");
                    tabOutputFile.parentFile.mkdirs();
                    StringBuffer tabContent = new StringBuffer();
                    tab.components.each{tabComponent->
                        tabContent.append(generateTag (tabComponent, templateEngine));
                    }
                    tab.actions.each{tabComponent->
                        tabContent.append(generateTag (tabComponent, templateEngine));
                    }
                    tab.dialogs.each{tabComponent->
                        tabContent.append(generateTag (tabComponent, templateEngine));
                    }
                    def layoutContent = "";
                    if(tab.layout)
                    {
                        layoutContent = generateTag (tab.layout, templateEngine);
                    }
                    def tabString = tabTemplate.make (tab:tab, tabContent:tabContent, layoutContent:layoutContent).toString()
                    tabOutputFile.setText (tabString)
                }
            }
            RsApplication.reloadViewsAndControllers();
            render(contentType:"text/xml")
            {
                Successful("UI generated successfully")
            }
        }
    }

    def generateTag(model, templateEngine)
    {
        def baseDir = System.getProperty("base.dir")
        def tagTemplate = templateEngine.createTemplate (new File("${baseDir}/grails-app/templates/ui/designer/${model.metaData().designerType}.gsp"));
        return tagTemplate.make(uiElement:model).toString();
    }
    def createViewXml(components, builder)
    {
        components.each{component->

            def metaData = component.metaData();
            def propsToBeSentToUi = metaData.propertyConfiguration
            def children = metaData.childrenConfiguration
            def uiElementProperties = [designerType:metaData.designerType];
            propsToBeSentToUi.each{String propName, propConfig->
                try{
                    def propValue = component.getProperty (propName);
                    if(propValue instanceof Collection)
                    {
                        propValue = propValue.join(",");
                    }
                    uiElementProperties[propName] = propValue;
                }catch(groovy.lang.MissingPropertyException e){}
            }
            builder.UiElement(uiElementProperties){
                children.each{child->
                    if(child.metaData == null)
                    {
                        def propName = child.propertyName;
                        try{
                            def childObjects = component.getProperty (propName).findAll {it.getClass().simpleName == "Ui"+child.designerType};
                            createViewXml(childObjects, builder);
                        }catch(groovy.lang.MissingPropertyException e){}
                    }
                    else
                    {
                        def designerType = child.designerType;
                        def childProps = [designerType:designerType]
                        child.metaData.propertyConfiguration.each{String propName, childProp->
                            childProps[propName] = childProp.defaultValue;                            
                        }
                        builder.UiElement(childProps){
                            child.metaData.childrenConfiguration.each{realChild->
                                def propName = realChild.propertyName;
                                try{
                                    def childObjects = component.getProperty (propName).findAll {it.getClass().simpleName == "Ui"+child.designerType};
                                    createViewXml(component.getProperty (propName), builder);
                                }catch(groovy.lang.MissingPropertyException e){}
                            }
                        }
                    }
                    
                }
            }
        }
    }

    def processUiElement(GPathResult xmlConfiguration)
    {
        xmlConfiguration.UiElement[0].UiElement.each{
            UiUrl.addUiElement(it, null);
        }
    }

    def update= {

    }

    def metaData= {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.UiElements{
            builder.UiElement(designerType:"Urls", display:"Urls", imageExpanded:"images/rapidjs/component/tools/folder_open.gif", imageCollapsed:"images/rapidjs/component/tools/folder.gif"){
                builder.Children{
                    builder.Child(isMultiple:true, designerType:"Url")                    
                }
            }
            def uiDomainClasses = grailsApplication.getDomainClasses().findAll {it.clazz.name.startsWith("ui.designer")}
            uiDomainClasses.each{grailsDomainClass->
                Class domainClass = grailsDomainClass.clazz;
                def domainClassMetaData = null
                try
                {
                    domainClassMetaData = domainClass.'metaData'()
                }catch(groovy.lang.MissingMethodException prop){};
                if(domainClassMetaData)
                {
                    def metaProperties = domainClassMetaData.propertyConfiguration?domainClassMetaData.propertyConfiguration:[:]
                    if(domainClassMetaData.designerType != null)
                    {
                        domainClassMetaData.propertyConfiguration = DesignerUtils.addConfigurationParametersFromModel(metaProperties, grailsDomainClass);
                        createMetaXml(builder, domainClassMetaData);
                    }
                }
            }
        }

        render(text:sw.toString(), contentType:"text/xml");
    }

    def createMetaXml(builder, domainClassMetaData)
    {
        def uiElementProperties = [:]
        def metaChildren = domainClassMetaData.remove("childrenConfiguration")
        def metaProperties = domainClassMetaData.remove("propertyConfiguration")
        domainClassMetaData.each{String propName, Object propValue->
            if(propName != "properties"){
                uiElementProperties[propName] = propValue;
            }
        }

        def configuredChildren = [];
        builder.UiElement(uiElementProperties)
        {
                builder.Properties{
                    metaProperties.each{String propName, metaPropertyConfiguration->
                        builder.Property(metaPropertyConfiguration);
                    }
                }
                builder.Children{
                    metaChildren.each{metaChildConfiguration->
                        if(metaChildConfiguration.metaData != null)
                        {
                            configuredChildren.add(metaChildConfiguration.remove("metaData"));
                        }
                        builder.Child(metaChildConfiguration);

                    }
                }
        }
        configuredChildren.each{childConfiguration->
            createMetaXml(builder, childConfiguration);
        }

    }
}