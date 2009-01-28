package ui.designer

import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 26, 2009
* Time: 3:11:26 PM
* To change this template use File | Settings | File Templates.
*/
class UiDesignerController {
    def create = {
        
    }

    def edit= {

    }

    def save= {

    }

    def update= {

    }

    def metaData= {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.UiElements{
            def uiDomainClasses = grailsApplication.getDomainClasses().findAll {it.clazz.name.startsWith("ui.designer")}
            uiDomainClasses.each{grailsDomainClass->
                Class domainClass = grailsDomainClass.clazz;
                def domainClassMetaData = [:]
                try
                {
                domainClassMetaData = domainClass.'metaData'()
                }catch(groovy.lang.MissingMethodException prop){println "No prop for ${domainClass}"};
                def simpleProps = [name:StringUtils.substringAfter(domainClass.simpleName, "Ui")]
                def subProps = [:]
                domainClassMetaData.each{String propName, Object propValue->
                    if(!(propValue instanceof Map))
                    {
                        simpleProps[propName, propValue];
                    }
                }
                def metaChildren = domainClassMetaData.children?domainClassMetaData.children:[:]
                def metaProperties = domainClassMetaData.properties?domainClassMetaData.properties:[:]
                metaProperties = DesignerUtils.addConfigurationParametersFromModel(metaProperties, grailsDomainClass)
                builder.UiElement(simpleProps)
                {
                        builder.Properties{
                            metaProperties.each{String propName, metaPropertyConfiguration->
                                builder.Property(metaPropertyConfiguration);        
                            }
                        }
                        builder.Children{
                            metaChildren.each{String childName, metaChildConfiguration->
                                metaChildConfiguration["name"] = childName;
                                builder.Child(metaChildConfiguration);
                            }
                        }
                }
            }
        }

        render(text:sw.toString(), contentType:"text/xml");
    }
}