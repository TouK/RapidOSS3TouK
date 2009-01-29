package ui.designer

import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import com.ifountain.rui.util.DesignerUtils
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 26, 2009
* Time: 3:11:26 PM
* To change this template use File | Settings | File Templates.
*/
class UiDesignerController {
    def view= {
        def sw = new StringWriter();
        def markupBuilder = new MarkupBuilder(sw);
        def urls = UiUrl.list();
        markupBuilder.UiConfig{
            markupBuilder.UiElement(designerType:"Urls"){
                createXml (urls, markupBuilder);
            }
        }
        render(text:sw.toString(), contentType:"text/xml");
    }

    def save= {
        def xmlConfigurationString = params.configuration
        def xmlConfiguration = new XmlSlurper().parseText(xmlConfigurationString);
        processUiElement(xmlConfiguration);
    }
    def createXml(components, builder)
    {
        components.each{component->
            def metaData = component.metaData();
            def propsToBeSentToUi = metaData.propertyConfiguration
            def children = metaData.childrenConfiguration
            def uiElementProperties = [designerType:metaData.designerType];
            propsToBeSentToUi.each{String propName, propConfig->
                try{
                    uiElementProperties[propName] = component.getProperty (propName);
                }catch(groovy.lang.MissingPropertyException e){}
            }
            builder.UiElement(uiElementProperties){
                children.each{child->
                    if(child.metaData == null)
                    {
                        def propName = child.propertyName;
                        try{
                            createXml(component.getProperty (propName), builder);
                        }catch(groovy.lang.MissingPropertyException e){}
                    }
                    else
                    {
                        def designerType = child.designerType;
                        builder.UiElement(designerType:designerType){
                            child.metaData.childrenConfiguration.each{realChild->
                                def propName = realChild.propertyName;
                                try{
                                    createXml(component.getProperty (propName), builder);
                                }catch(groovy.lang.MissingPropertyException e){}
                            }
                        }
                    }
                    
                }
            }
        }
    }
    def walkXml(uiElement, parentDomainObject, relationFromParent)
    {
        def domainProperties = [:];
        def designerType = uiElement.attributes()["designerType"];
        def domainClass = grailsApplication.getDomainClass("ui.designer.Ui"+designerType);
        if(domainClass)
        {

            def addedObject = addUiElement(domainClass, uiElement, parentDomainObject, relationFromParent);
            def childrenConfiguration = [:];
            domainClass.clazz.'metaData'().childrenConfiguration.each{childrenConfiguration[it.designerType]=it;}
            uiElement.childNodes().each{childNode->
                def type = childNode.attributes()["designerType"]
                def childConfig = childrenConfiguration[type];
                if(childConfig.metaData == null)
                {
                    walkXml (childNode, addedObject, childConfig.propertyName);
                }
                else
                {
                    childrenConfiguration = [:];
                    childConfig.metaData.childrenConfiguration.each{childrenConfiguration[it.designerType]=it;}
                    childNode.childNodes().each{realChildNode->
                        type = realChildNode.attributes()["designerType"]
                        childConfig = childrenConfiguration[type];
                        walkXml (realChildNode, addedObject, childConfig.propertyName);
                    }
                }
            }
        }
        else
        {
            throw new Exception("Undefined model for "+designerType);
        }

    }

    def addUiElement(domainClass, uiElement, parentObject, relationFromParent)
    {
        def relationToParent = null;
        domainClass.clazz.'getPropertiesList'().each{
            if(it.isRelation && it.reverseName == relationFromParent)
            {
                relationToParent = it.name;
            }
        }
        def allProperties = [:];
        allProperties.putAll (uiElement.attributes());
        if(relationToParent != null)
        {
            allProperties[relationToParent] = parentObject;
        }
        return domainClass.clazz.'add'(allProperties);
    }
    def processUiElement(GPathResult xmlConfiguration)
    {
        xmlConfiguration.UiElement[0].childNodes().each{
            walkXml (it, null, null);
        }
    }

    def update= {

    }

    def metaData= {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.UiElements{
            builder.UiElement(designerType:"Urls", display:"Urls"){
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
                    domainClassMetaData.propertyConfiguration = DesignerUtils.addConfigurationParametersFromModel(metaProperties, grailsDomainClass);
                    createMetaXml(builder, domainClassMetaData);
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