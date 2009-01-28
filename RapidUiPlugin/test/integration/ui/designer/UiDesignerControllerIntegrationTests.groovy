package ui.designer

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.commons.lang.StringUtils
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 27, 2009
* Time: 3:27:15 PM
* To change this template use File | Settings | File Templates.
*/
class UiDesignerControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    public void testMetaData()
    {
        UiDesignerController controller = new UiDesignerController();
        controller.metaData();
        def responseXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def components = responseXml.UiElement
        def uiDomainClasses = ApplicationHolder.application.getDomainClasses().findAll {it.clazz.name.startsWith(UiUrl.getPackage().name)}
        assertEquals (uiDomainClasses.size(), components.size());
        def componentMap = [:]
        components.each{
            componentMap[it.'@name'.text()] = it;            
        }

        uiDomainClasses.each{grailsDomainClass->
            def domainClass = grailsDomainClass.clazz;
            def component = componentMap[StringUtils.substringAfter(domainClass.simpleName, "Ui")];
            assertNotNull ("Undefined for ${StringUtils.substringAfter(domainClass.simpleName, "Ui")} in ${componentMap}", component);
            def metaProps = component.Properties.Property;
            def propertiesListFromGrailsDomain =domainClass.'getPropertiesList'().findAll{it.name != "id" &&(!it.isRelation || it.isOneToOne())}
            assertEquals("Properties are not equals for ${domainClass.name}", propertiesListFromGrailsDomain.size(), metaProps.size());
            for(int i=0; i < propertiesListFromGrailsDomain.size(); i++)
            {
                def domainClassProperty = propertiesListFromGrailsDomain[i];
                assertEquals(domainClassProperty.name, metaProps[i].'@name'.text());
            }
        }

        def component = componentMap[StringUtils.substringAfter(UiUrl.simpleName, "Ui")];
        def urlPropertyMetaData = component.Properties.Property;
        def xmlProperties = [:]
        urlPropertyMetaData.each{
            xmlProperties[it.'@name'.text()] = it;
        }
        assertEquals("url", xmlProperties["url"].'@name'.text());
        assertEquals("String", xmlProperties["url"].'@type'.text());
        assertTrue(xmlProperties["url"].'@descr'.text() != null);
        assertEquals("true", xmlProperties["url"].'@required'.text());

        def urlChildMetaData = component.Children.Child;
        def xmlChildren = [:]
        urlChildMetaData.each{
            xmlChildren[it.'@name'.text()] = it;
        }
        assertEquals(1, xmlChildren.size());
        assertEquals("Tabs", xmlChildren["Tabs"].'@name'.text());
        assertEquals("false", xmlChildren["Tabs"].'@isMultiple'.text());        

    }

}