import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import ui.designer.UiMetaData
import org.codehaus.groovy.grails.validation.ConstrainedProperty


def metaDataConfiguration =
[
        "Action.condition": [description: "", type: "String", required: true, inList: "x,y,z"],
        "Action.name": [description: "", type: "String", required: true],
        "Action.tab": [description: "", type: "String", required: true]
]









createMetaData(metaDataConfiguration);




def createMetaData(metaDataConfiguration)
{
    def domainClasses = ApplicationHolder.getApplication().getDomainClasses();
    domainClasses.each {GrailsDomainClass domainClass ->
        if (domainClass.clazz.name.startsWith(UiMetaData.getPackage().name) && domainClass.clazz.name != UiMetaData.name)
        {
            def constrainedProps = domainClass.getConstrainedProperties();
            List domainProperties = domainClass.clazz.'getPropertiesList'();
            String componentName = domainClass.clazz.simpleName.substring(2);
            domainProperties.each {property ->
                String propName = property.name;
                if (propName != "id" && (!property.isRelation || property.isOneToOne()))
                {
                    Map config = metaDataConfiguration.get("${componentName}.${propName}".toString());
                    if (config == null)
                    {
                        config = [:];
                    }
                    config.name = propName;
                    config.componentName = componentName;
                    config.type = config.type == null?getType(property):config.type
                    def isRequired = config.required != null?config.required:!(constrainedProps[propName].isBlank() || constrainedProps[propName].isNullable())
                    config.required = isRequired;
                    config.description = config.description != null?config.description:"";
                    //TODO: could not tested taking inList from constraints will be tested if an appropriate model is constructed
                    def inlistConstraint = constrainedProps[propName].getInList();
                    if(inlistConstraint == null)inlistConstraint = [];
                    config.inList = config.inList != null?config.inList:inlistConstraint.join (",")
                    UiMetaData.add(config);
                }
            }
        }
    }
}

private String getType(prop)
{
    if(prop.isRelation) return "String"
    if(String.class.isAssignableFrom(prop.type)){return "String"}
    if(Double.class.isAssignableFrom(prop.type)){return "Float"}
    if(Number.class.isAssignableFrom(prop.type)){return "Number"}
    if(Boolean.class.isAssignableFrom(prop.type)){return "Boolean"}
    if(Date.class.isAssignableFrom(prop.type)){return "Date"}

}

