package ui.designer

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import org.apache.commons.io.FileUtils

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

    public void testSaveAndViewAndGenerate()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [url:"myUrl1", designerType:"Url"]
        def url2Props = [url:"myUrl2", designerType:"Url"]
        def tabsProps = [[name:"tab1", designerType:"Tab", javascriptFile:'x.gsp'], [name:"tab2", designerType:"Tab", javascriptFile:'y.gsp']];
        builder.UiConfig{
            builder.UiElement(designerType:"Urls"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                                builder.UiElement(designerType:'Layout')
                                builder.UiElement(designerType:'Components')
                                builder.UiElement(designerType:'Dialogs')
                                builder.UiElement(designerType:'Actions')
                            }
                        }
                    }
                }
                builder.UiElement(url2Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                                builder.UiElement(designerType:'Layout')
                                builder.UiElement(designerType:'Components')
                                builder.UiElement(designerType:'Dialogs')
                                builder.UiElement(designerType:'Actions')
                            }
                        }
                    }
                }
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        def url1 = UiUrl.get(url:url1Props.url, isActive:true);
        def url2 = UiUrl.get(url:url2Props.url, isActive:true);
        assertTrue (!url1.tabs.findAll {it.name == "tab1"}.isEmpty());
        assertTrue (!url1.tabs.findAll {it.name == "tab2"}.isEmpty());
        assertTrue (!url2.tabs.findAll {it.name == "tab1"}.isEmpty());
        assertTrue (!url2.tabs.findAll {it.name == "tab2"}.isEmpty());
        assertEqualsXML ("<Successful>UI configuration saved successfully</Successful>", controller.response.contentAsString);

        //test view
        IntegrationTestUtils.resetController (controller);
        controller.view();
        println sw.toString()
        println controller.response.contentAsString
        assertEqualsXML(sw.toString(), controller.response.contentAsString);

        //test generate gsp files 
        deleteGeneratedFiles();
        IntegrationTestUtils.resetController (controller);
        controller.generate();
        assertEqualsXML ("<Successful>UI generated successfully</Successful>", controller.response.contentAsString);
        checkGeneratedFiles();

        def tabsOfUrl1BeforeDelete = url1.tabs;
        def tabsOfUrl2BeforeDelete = url2.tabs;
        //test deletes old models
        sw = new StringWriter();
        builder = new MarkupBuilder(sw);
        builder.UiConfig{
            builder.UiElement(designerType:"Urls"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs")
                    {
                        builder.UiElement(tabsProps[0]){
                            builder.UiElement(designerType:"Layout");                            
                        }
                    }
                }
            }
        }
        IntegrationTestUtils.resetController (controller);
        controller.params.configuration = sw.toString()
        controller.save();
        assertEqualsXML ("<Successful>UI configuration saved successfully</Successful>", controller.response.contentAsString);
        def urlsAfterReSave = UiUrl.list();
        def tabsAfterReSave = UiTab.list();
        assertEquals (1, urlsAfterReSave.size())
        assertEquals (1, tabsAfterReSave.size())
        assertEquals (url1Props.url, urlsAfterReSave[0].url)
        assertEquals (tabsProps[0].name, tabsAfterReSave[0].name)

        //Test if tab does not exist generate will create redirect url page  with no content
        UiTab.removeAll();
        deleteGeneratedFiles();
        IntegrationTestUtils.resetController (controller);
        controller.generate();
        assertEqualsXML ("<Successful>UI generated successfully</Successful>", controller.response.contentAsString);
        checkGeneratedFiles();
        def baseDir = System.getProperty("base.dir");
        def urlRedirectFile = new File( baseDir + "/web-app/${url1.url}.gsp");
        assertEquals("", urlRedirectFile.getText());
        def url2LayoutFile = new File(baseDir + "/grails-app/views/layouts/"+url2.url+"Layout.gsp");
        assertFalse (url2LayoutFile.exists());
        assertFalse (new File(baseDir + "/web-app/${url2.url}.gsp").exists());
        assertFalse (new File(baseDir + "/web-app/${url2.url}").exists());
        tabsOfUrl2BeforeDelete.each{tab->
            def tabFile = new File(baseDir + "/web-app/${url2.url}/${tab.name}.gsp");
            assertFalse (tabFile.exists());
        }

        def url1Tab2File = new File(baseDir + "/web-app/${url1.url}/${tabsOfUrl1BeforeDelete[1].name}.gsp");
        assertFalse (url1Tab2File.exists());
        
    }

    public void testViewWithMultipleChildUsingSameProperty()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [url:"myUrl1", designerType:"Url"]
        def tabsProps = [[name:"tab1", designerType:"Tab", javascriptFile:'x.gsp']];
        builder.UiConfig{
            builder.UiElement(designerType:"Urls"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                                builder.UiElement(designerType:'Layout')
                                {
                                    builder.UiElement(designerType:'CenterUnit', component:'', gutter:'', scroll:'false', useShim:'false');
                                }
                                builder.UiElement(designerType:'Components')
                                builder.UiElement(designerType:'Dialogs')
                                builder.UiElement(designerType:'Actions')
                            }
                        }
                    }
                }
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        assertEqualsXML ("<Successful>UI configuration saved successfully</Successful>", controller.response.contentAsString);
        IntegrationTestUtils.resetController (controller);
        controller.view();
        println sw.toString()
        println  controller.response.contentAsString
        assertEqualsXML (sw.toString(), controller.response.contentAsString);
    }

    public void testSaveViewWithRelationProperty()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [url:"myUrl1", designerType:"Url"]
        def chart1Props = [designerType:'FlexPieChart', rootTag:"rootTag", url:"url1", pollingInterval:"10", name:"chart1", title:"title"]
        def chart2Props = [designerType:'FlexPieChart', rootTag:"rootTag", url:"url1", pollingInterval:"10", name:"chart2", title:"title"]
        def tabsProps = [[name:"tab1", designerType:"Tab", javascriptFile:'x.gsp']];
        builder.UiConfig{
            builder.UiElement(designerType:"Urls"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                                builder.UiElement(designerType:'Layout')
                                {
                                    builder.UiElement(designerType:'CenterUnit', component:'chart1', gutter:'', scroll:'false', useShim:'false');
                                }
                                builder.UiElement(designerType:'Components')
                                {
                                    builder.UiElement(chart1Props)
                                    builder.UiElement(chart2Props)
                                }
                                builder.UiElement(designerType:'Dialogs')
                                builder.UiElement(designerType:'Actions')
                                {
                                    builder.UiElement(designerType:'RequestAction', name:"action1", url:"url1", components:"${chart1Props.name},${chart2Props.name}", condition:"true", timeout:40)
                                    {
                                        builder.UiElement(designerType:'Events', designerHidden:"true")    
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        assertEqualsXML ("<Successful>UI configuration saved successfully</Successful>", controller.response.contentAsString);
        def url1 = UiUrl.get(url:url1Props.url, isActive:true);
        assertTrue (!url1.tabs.findAll {it.name == "tab1"}.isEmpty());
        def tab = url1.tabs[0];
        def tabComponents = tab.components;
        assertEquals (2, tabComponents.size());
        tabComponents = tabComponents.sort{it.name}
        UiFlexPieChart component = tabComponents[0];

        assertTrue (component instanceof UiFlexPieChart)
        assertEquals(chart1Props.rootTag, component.rootTag);
        assertEquals(chart1Props.url, component.url);
        assertEquals(new Long(chart1Props.pollingInterval), component.pollingInterval);
        assertEquals(chart1Props.name, component.name);
        assertEquals(chart1Props.title, component.title);
        assertEquals (UiCenterUnit.name, component.layoutUnit.class.name);
        UiRequestAction requestAction = UiAction.get(name:"action1", tab:tab, isActive:true);
        assertEquals (2, requestAction.components.size());

        //here we are expecting saved xml and returned xml to be same
        //also we are testing designerHidden property is added to Events tag which should not be visible in designer tree
        IntegrationTestUtils.resetController (controller);
        controller.view();
        assertEqualsXML (sw.toString(), controller.response.contentAsString);
    }

    public void testSaveWithErrors()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [url:"myUrl1", designerType:"Url"]
        def url2Props = [url:"myUrl2", designerType:"Url"]
        builder.UiConfig{
            builder.UiElement(designerType:"Urls"){
                builder.UiElement(url1Props)
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        assertEquals (1, UiUrl.count());
        def url1BeforeTryingToSaveWithError = UiUrl.list()[0];

        sw = new StringWriter();
        builder = new MarkupBuilder(sw);
        builder.UiConfig{
            builder.UiElement(designerType:"Urls"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        builder.UiElement(designerType:"Tab")
                    }
                }
                builder.UiElement(url2Props){
                }
            }
        }
        IntegrationTestUtils.resetController (controller);
        controller.params.configuration = sw.toString()
        controller.save();
        assertEquals (1, UiUrl.count());
        assertEquals (url1BeforeTryingToSaveWithError.id, UiUrl.list()[0].id);
        assertEquals (true, UiUrl.list()[0].isActive);
        assertEquals (0, UiTab.count());

        def responseXml = new XmlSlurper().parseText(controller.response.contentAsString);
        assertEquals(1, responseXml.Error.size());

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
        def classToBeExcluded = [UiLayoutUnit.name, UiComponent.name, UiAction.name]
        uiDomainClasses.each{grailsDomainClass->
            def domainClass = grailsDomainClass.clazz;
            if(!classToBeExcluded.contains(domainClass.name))
            {
                def component = componentMap[StringUtils.substringAfter(domainClass.simpleName, "Ui")];
                assertNotNull ("Undefined for ${StringUtils.substringAfter(domainClass.simpleName, "Ui")} in ${componentMap}", component);
            }
        }
        def urlsComponent = componentMap["Urls"];
        assertEquals("Urls", urlsComponent.'@designerType'.text());
        assertEquals("Urls", urlsComponent.'@display'.text());
        assertEquals(1, urlsComponent.Children.size());
        assertEquals("Url", urlsComponent.Children[0].Child[0].'@designerType'.text());
        assertEquals("true", urlsComponent.Children[0].Child[0].'@isMultiple'.text());
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

        //Test will not send information about components whose meta data does not have designerType
        assertNull (componentMap["Component"])
        assertNull (componentMap["Action"])
        assertNull (componentMap["LayoutUnit"])
        assertNull (componentMap[""])

    }

    def checkGeneratedFiles()
    {
        def baseDir =  System.getProperty("base.dir");
        UiUrl.list().each{url->
            def urlLayoutFile = new File(baseDir + "/grails-app/views/layouts/"+url.url+"Layout.gsp");
            assertTrue (urlLayoutFile.exists());
            def urlRedirectFile = new File(baseDir + "/web-app/${url.url}.gsp");
            assertTrue (urlRedirectFile.exists());
            url.tabs.each{tab->
                def url1WebAppDirectoryFile = new File(baseDir + "/web-app/${url.url}/${tab.name}.gsp");
                assertTrue (url1WebAppDirectoryFile.exists());
            }
        }        
    }
    def deleteGeneratedFiles()
    {
        def baseDir =  System.getProperty("base.dir");
        UiUrl.list().each{url->
            def urlLayoutFile = new File(baseDir + "/grails-app/views/layouts/"+url.url+"Layout.gsp");
            urlLayoutFile.delete();
            FileUtils.deleteDirectory (new File(baseDir + "/web-app/${url.url}"));
            new File(baseDir + "/web-app/${url.url}.gsp").delete();
        }
    }

}