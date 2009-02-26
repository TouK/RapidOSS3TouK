package com.ifountain.rui.util

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import com.ifountain.rui.util.exception.UiElementCreationException
import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 27, 2009
* Time: 4:23:44 PM
* To change this template use File | Settings | File Templates.
*/
class DesignerUtils {
    public static Map addConfigurationParametersFromModel(Map componentMetaPropertiesConfiguration, org.codehaus.groovy.grails.commons.GrailsDomainClass domainClass)
    {
        def clonedPropertiesConfiguration = componentMetaPropertiesConfiguration != null ? componentMetaPropertiesConfiguration.clone() : [:];
        def constrainedProps = domainClass.getConstrainedProperties();
        def domainPropertiesMap = [:];
        domainClass.clazz.'getPropertiesList'().each {
            domainPropertiesMap[it.name] = it;
        }
        def domainInstance = domainClass.clazz.newInstance();
        componentMetaPropertiesConfiguration.each {String propName, Map config ->
            def domainProperty = domainPropertiesMap[propName]
            if (config == null)
            {
                config = [:];
                clonedPropertiesConfiguration.put(propName, config);
            }
            config.name = propName;
            if (domainProperty != null)
            {
                config.type = config.type == null ? getType(domainProperty) : config.type

                def isRequired = config.required != null ? config.required :isRequired(constrainedProps, propName);
                config.required = isRequired;
                config.descr = config.descr != null ? config.descr : "";
                //TODO: could not tested taking inList from constraints will be tested if an appropriate model is constructed
                def inlistConstraint = getInList(constrainedProps, propName);
                if (inlistConstraint != null && !inlistConstraint.isEmpty() && config.inList == null) {
                    config.inList = inlistConstraint.join(",")
                }
                if (!domainPropertiesMap[propName].isRelation)
                {
                    config.defaultValue = config.defaultValue != null ? config.defaultValue : domainInstance[propName] == null ? "" : String.valueOf(domainInstance[propName]);
                }
                else
                {
                    config.defaultValue = config.defaultValue != null ? config.defaultValue : "";
                }
            }
        }

        return clonedPropertiesConfiguration;
    }

    public static isRequired(Map constrainedProps, String propName)
    {
        def containedProp = constrainedProps[propName];
        if(containedProp)
        {
            return !containedProp.isBlank() || !containedProp.isNullable()
        }
        else
        {
            return false;
        }
    }

    public static getInList(constrainedProps, propName)
    {
        def containedProp = constrainedProps[propName];
        if(containedProp)
        {
            return constrainedProps[propName].getInList();
        }
        else
        {
            return [];
        }
    }

    private static String getType(prop)
    {
        if (prop.isRelation) return "String"
        if (String.class.isAssignableFrom(prop.type)) {return "String"}
        if (Double.class.isAssignableFrom(prop.type)) {return "Float"}
        if (Number.class.isAssignableFrom(prop.type)) {return "Number"}
        if (Boolean.class.isAssignableFrom(prop.type)) {return "Boolean"}
        if (Date.class.isAssignableFrom(prop.type)) {return "Date"}

    }

    public static Object addUiObject(Class uiElementClass, Map uiElementProperties, GPathResult xmlNode)
    {
        def domainProps = [:]
        uiElementClass.getPropertiesList().each {domainProps[it.name] = it}
        def propertiesToBeAdded = [:]
        uiElementProperties.each {String propName, propValue ->
            def propMetaData = domainProps[propName];
            if (propMetaData != null && propMetaData.isRelation && propValue instanceof String)
            {
                propValue = [];
            }
            propertiesToBeAdded[propName] = propValue;
        }

        def res = uiElementClass.'add'(propertiesToBeAdded);
        if (res.hasErrors())
        {
            def messageSource = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean("messageSource");
            def errorMessage = messageSource.getMessage(res.errors.getAllErrors()[0], Locale.ENGLISH);
            xmlNode.attributes().designerError = errorMessage;
            throw new UiElementCreationException(uiElementClass, errorMessage);
        }
        return res;
    }

    public static void generateXml(List components, MarkupBuilder builder, Closure metadataGetter)
    {
        components.each {component ->
            def defaultPropertyValueInstance = component.class.newInstance();
            def metaData = metadataGetter(component);
            def propsToBeSentToUi = metaData.propertyConfiguration;
            def children = metaData.childrenConfiguration
            def uiElementProperties = [designerType: metaData.designerType, id:component.id];
            propsToBeSentToUi.each {String propName, propConfig ->
                try {
                    def propValue = null;
                    def defaultPropValue =null;
                    if (propConfig.formatter != null)
                    {
                        propValue = propConfig.formatter(component);
                        defaultPropValue = propConfig.formatter(defaultPropertyValueInstance);
                    }
                    else
                    {
                        propValue = component.getProperty(propName);
                        defaultPropValue = defaultPropertyValueInstance.getProperty(propName);
                    }
                    if(propConfig.required == true || defaultPropValue != propValue)
                    {
                        uiElementProperties[propName] = propValue;
                    }
                } catch (groovy.lang.MissingPropertyException e) {}
            }
            builder.UiElement(uiElementProperties) {
                children.each {child ->
                    if (child.metaData == null)
                    {
                        def propName = child.propertyName;
                        try {
                            def childObjects = getChildObjects(component, propName, child);
                            generateXml(childObjects, builder, metadataGetter);
                        } catch (groovy.lang.MissingPropertyException e) {}
                    }
                    else
                    {
                        def designerType = child.designerType;
                        def childProps = [designerType: designerType]
                        child.metaData.propertyConfiguration.each {String propName, childProp ->
                            def propValue = childProp.defaultValue;
                            if(childProp.formatter)
                            {
                                propValue = childProp.formatter();
                            }
                            childProps[propName] = propValue;
                        }
                        builder.UiElement(childProps) {
                            child.metaData.childrenConfiguration.each {realChild ->
                                def propName = realChild.propertyName;
                                try {
                                    def childObjects = getChildObjects(component, propName, realChild);
                                    generateXml(childObjects, builder, metadataGetter);
                                } catch (groovy.lang.MissingPropertyException e) {}
                            }
                        }
                    }

                }
            }
        }
    }

    private static List getChildObjects(domainObject, propertyName, cildMetaDataConfiguration)
    {
        def childObjects = domainObject.getProperty(propertyName);
        if(childObjects == null)
        {
            childObjects = [];
        }
        else if(!(childObjects instanceof Collection))
        {
            childObjects = [childObjects];            
        }
        return childObjects.findAll {return cildMetaDataConfiguration.isVisible == null || cildMetaDataConfiguration.isVisible(it)}.sort{it.id};
    }
}