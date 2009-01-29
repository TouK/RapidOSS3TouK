package ui.designer

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.test.util.IntegrationTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 27, 2009
* Time: 3:27:15 PM
* To change this template use File | Settings | File Templates.
*/
class UiDesignerControllerIntegrationTests extends RapidCmdbIntegrationTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        def uiDomainClasses = ApplicationHolder.application.getDomainClasses().findAll{
                if(it.clazz.name.startsWith(UiUrl.getPackage().name))
                {
                    it.clazz.'removeAll'();   
                }
        }
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        def uiDomainClasses = ApplicationHolder.application.getDomainClasses().findAll{
                if(it.clazz.name.startsWith(UiUrl.getPackage().name))
                {
                    it.clazz.'removeAll'();
                }
        }
    }

    public void testSaveAndView()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [url:"myUrl1", designerType:"Url"]
        def url2Props = [url:"myUrl2", designerType:"Url"]
        def tabsProps = [[name:"tab1", designerType:"Tab"], [name:"tab2", designerType:"Tab"]];
        builder.UiConfig{
            builder.UiElement(designerType:"Urls"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                            }
                        }
                    }
                }
                builder.UiElement(url2Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                            }
                        }
                    }
                }
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        def url1 = UiUrl.get(url:url1Props.url);
        def url2 = UiUrl.get(url:url2Props.url);
        assertTrue (!url1.tabs.findAll {it.name == "tab1"}.isEmpty());
        assertTrue (!url1.tabs.findAll {it.name == "tab2"}.isEmpty());
        assertTrue (!url2.tabs.findAll {it.name == "tab1"}.isEmpty());
        assertTrue (!url2.tabs.findAll {it.name == "tab2"}.isEmpty());
        IntegrationTestUtils.resetController (controller);
        controller.params.configuration = sw.toString()
        controller.view();

        assertEqualsXML(sw.toString(), controller.response.contentAsString);
        

    }
    public void testMetaData()
    {
        UiDesignerController controller = new UiDesignerController();
        controller.metaData();
        println controller.response.contentAsString
        def responseXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def components = responseXml.UiElement
        def uiDomainClasses = ApplicationHolder.application.getDomainClasses().findAll{
                if(it.clazz.name.startsWith(UiUrl.getPackage().name))
                {
                    try{
                        it.clazz.'metaData'();
                        return true;
                    }catch(groovy.lang.MissingMethodException e){}
                    return false;
                }
                return false;
        }
        def componentMap = [:]
        components.each{
            componentMap[it.'@designerType'.text()] = it;            
        }

        uiDomainClasses.each{grailsDomainClass->
            def domainClass = grailsDomainClass.clazz;
            def component = componentMap[StringUtils.substringAfter(domainClass.simpleName, "Ui")];
            assertNotNull ("Undefined for ${StringUtils.substringAfter(domainClass.simpleName, "Ui")} in ${componentMap}", component);
        }
        def urlComponent = componentMap["Url"];
        def urlPropertyMetaData = urlComponent.Properties.Property;
        def xmlProperties = [:]
        urlPropertyMetaData.each{
            xmlProperties[it.'@name'.text()] = it;
        }
        assertEquals("url", xmlProperties["url"].'@name'.text());
        assertEquals("String", xmlProperties["url"].'@type'.text());
        assertTrue(xmlProperties["url"].'@descr'.text() != null);
        assertEquals("true", xmlProperties["url"].'@required'.text());

        def urlChildMetaData = urlComponent.Children.Child;
        def xmlChildren = [:]
        urlChildMetaData.each{
            xmlChildren[it.'@designerType'.text()] = it;
        }
        assertEquals(1, xmlChildren.size());
        assertEquals("Tabs", xmlChildren["Tabs"].'@designerType'.text());
        assertEquals("false", xmlChildren["Tabs"].'@isMultiple'.text());

        def tabsComponent = componentMap["Tabs"];
        def tabsChildMetaData = tabsComponent.Children.Child;
        def tabsXmlChildren = [:]
        tabsChildMetaData.each{
            tabsXmlChildren[it.'@designerType'.text()] = it;
        }
        assertEquals(1, tabsXmlChildren.size());
        assertEquals("Tab", tabsXmlChildren["Tab"].'@designerType'.text());
        assertEquals("true", tabsXmlChildren["Tab"].'@isMultiple'.text());

    }

}