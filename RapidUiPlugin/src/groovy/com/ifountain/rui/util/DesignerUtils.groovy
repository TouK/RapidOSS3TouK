package com.ifountain.rui.util

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder

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
        def clonedPropertiesConfiguration = componentMetaPropertiesConfiguration.clone();
        def constrainedProps = domainClass.getConstrainedProperties();
        def domainPropertiesList = domainClass.clazz.'getPropertiesList'();
        for(int i=0; i < domainPropertiesList.size(); i++){
            def domainProperty = domainPropertiesList[i]
            String propName = domainProperty.name;
            if (propName != "id" && (!domainProperty.isRelation || domainProperty.isOneToOne()))
            {
                Map config = clonedPropertiesConfiguration.get(propName);
                if (config == null)
                {
                    config = [:];
                    clonedPropertiesConfiguration.put(propName, config);
                }
                config.name = propName;
                config.type = config.type == null ? getType(domainProperty) : config.type
                def isRequired = config.required != null ? config.required : !(constrainedProps[propName].isBlank() || constrainedProps[propName].isNullable())
                config.required = isRequired;
                config.description = config.description != null ? config.description : "";
                //TODO: could not tested taking inList from constraints will be tested if an appropriate model is constructed
                def inlistConstraint = constrainedProps[propName].getInList();
                if (inlistConstraint == null) inlistConstraint = [];
                config.inList = config.inList != null ? config.inList : inlistConstraint.join(",")
            }
        }

        return clonedPropertiesConfiguration;
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
}