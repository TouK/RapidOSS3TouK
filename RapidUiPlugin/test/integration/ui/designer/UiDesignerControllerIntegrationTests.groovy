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
                if(it.clazz.name.startsWith(UiWebPage.getPackage().name))
                {
                    it.clazz.'removeAll'();   
                }
        }
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        def uiDomainClasses = ApplicationHolder.application.getDomainClasses().findAll{
                if(it.clazz.name.startsWith(UiWebPage.getPackage().name))
                {
                    it.clazz.'removeAll'();
                }
        }
    }

    public void testSaveViewAndGenerate()
    {
        new File("${System.getProperty ("base.dir")}/web-app/x.gsp").setText ("");
        new File("${System.getProperty ("base.dir")}/web-app/y.gsp").setText ("");
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [name:"myUrl1", designerType:"WebPage", id:""]
        def url2Props = [name:"myUrl2", designerType:"WebPage", id:""]
        def tabsProps = [[name:"tab1", designerType:"Tab", contentFile:'x.gsp', title:"tab1", id:""], [name:"tab2", designerType:"Tab", contentFile:'y.gsp', title:"tab2", id:""]];
        builder.UiConfig{
            builder.UiElement(designerType:"WebPages"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                                builder.UiElement(designerType:'Layout', id:"")
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
                                builder.UiElement(designerType:'Layout', id:"")
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
        def url1 = UiWebPage.get(name:url1Props.name, isActive:true);
        def url2 = UiWebPage.get(name:url2Props.name, isActive:true);
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
        assertEqualsXML(sw.toString(), controller.response.contentAsString, ["id"]);

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
            builder.UiElement(designerType:"WebPages"){
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
        def urlsAfterReSave = UiWebPage.list();
        def tabsAfterReSave = UiTab.list();
        assertEquals (1, urlsAfterReSave.size())
        assertEquals (1, tabsAfterReSave.size())
        assertEquals (url1Props.name, urlsAfterReSave[0].name)
        assertEquals (tabsProps[0].name, tabsAfterReSave[0].name)

        //Test if tab does not exist generate will create redirect url page  with no content
        UiTab.removeAll();
        deleteGeneratedFiles();
        IntegrationTestUtils.resetController (controller);
        controller.generate();
        assertEqualsXML ("<Successful>UI generated successfully</Successful>", controller.response.contentAsString);
        checkGeneratedFiles();
        def baseDir = System.getProperty("base.dir");
        def urlRedirectFile = new File( baseDir + "/web-app/${url1.name}.gsp");
        assertEquals("", urlRedirectFile.getText());
        def url2LayoutFile = new File(baseDir + "/grails-app/views/layouts/"+url2.name+"Layout.gsp");
        assertFalse (url2LayoutFile.exists());
        assertFalse (new File(baseDir + "/web-app/${url2.name}.gsp").exists());
        assertFalse (new File(baseDir + "/web-app/${url2.name}").exists());
        tabsOfUrl2BeforeDelete.each{tab->
            def tabFile = new File(baseDir + "/web-app/${url2.name}/${tab.name}.gsp");
            assertFalse (tabFile.exists());
        }

        def url1Tab2File = new File(baseDir + "/web-app/${url1.name}/${tabsOfUrl1BeforeDelete[1].name}.gsp");
        assertFalse (url1Tab2File.exists());
    }

    public void testSaveWithRelationProperty()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [name:"myUrl1", designerType:"WebPage"]
        def chart1Props = [designerType:'FlexPieChart', rootTag:"rootTag", url:"url1", pollingInterval:"10", name:"chart1", title:"title"]
        def chart2Props = [designerType:'FlexPieChart', rootTag:"rootTag", url:"url1", pollingInterval:"10", name:"chart2", title:"title"]
        def tabsProps = [[name:"tab1", designerType:"Tab", javascriptFile:'x.gsp']];
        builder.UiConfig{
            builder.UiElement(designerType:"WebPages"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        tabsProps.each{tab->
                            builder.UiElement(tab){
                                builder.UiElement(designerType:'Layout')
                                {
                                    builder.UiElement(designerType:'CenterUnit', component:'chart1', contentFile:'', gutter:'', scroll:'false', useShim:'false');
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
        def url1 = UiWebPage.get(name:url1Props.name, isActive:true);
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
    }

    public void testSaveWithErrors()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [name:"myUrl1", designerType:"WebPage"]
        def url2Props = [name:"myUrl2", designerType:"WebPage"]
        builder.UiConfig{
            builder.UiElement(designerType:"WebPages"){
                builder.UiElement(url1Props){
                    builder.UiElement(designerType:"Tabs"){
                        builder.UiElement(designerType:"Tab", name:"tab1")
                    }
                }
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        assertEquals (1, UiWebPage.count());
        def url1BeforeTryingToSaveWithError = UiWebPage.list()[0];

        sw = new StringWriter();
        builder = new MarkupBuilder(sw);
        builder.UiConfig{
            builder.UiElement(designerType:"WebPages"){
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
        assertEquals (1, UiWebPage.count());
        assertEquals (url1BeforeTryingToSaveWithError.id, UiWebPage.list()[0].id);
        assertEquals (true, UiWebPage.list()[0].isActive);
        assertEquals (1, UiTab.count());

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
                if(it.clazz.name.startsWith(UiWebPage.getPackage().name))
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
        def urlsComponent = componentMap["WebPages"];
        assertEquals("WebPages", urlsComponent.'@designerType'.text());
        assertEquals("Web Pages", urlsComponent.'@display'.text());
        assertEquals(1, urlsComponent.Children.size());
        assertEquals("WebPage", urlsComponent.Children[0].Child[0].'@designerType'.text());
        assertEquals("true", urlsComponent.Children[0].Child[0].'@isMultiple'.text());
        def urlComponent = componentMap["WebPage"];
        def urlPropertyMetaData = urlComponent.Properties.Property;
        def xmlProperties = [:]
        urlPropertyMetaData.each{
            xmlProperties[it.'@name'.text()] = it;
        }
        assertEquals("name", xmlProperties["name"].'@name'.text());
        assertEquals("String", xmlProperties["name"].'@type'.text());
        assertTrue(xmlProperties["name"].'@descr'.text() != null);
        assertEquals("true", xmlProperties["name"].'@required'.text());

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

    public void testUiDesignerControllerCachesTemplates()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [name:"myUrl1", designerType:"WebPage", id:""]
        builder.UiConfig{
            builder.UiElement(designerType:"WebPages"){
                builder.UiElement(url1Props){
                }
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        def url1 = UiWebPage.get(name:url1Props.name, isActive:true);
        assertNotNull (url1);
        assertEqualsXML ("<Successful>UI configuration saved successfully</Successful>", controller.response.contentAsString);

        File uiUrlTemplate = new File("${System.getProperty("base.dir")}/${UiDesignerController.TEMPLATES_DIRECTORY}/WebPage.gsp");
        assertTrue (uiUrlTemplate.exists());
        def originalTemplate  = uiUrlTemplate.getText();
        //test generate gsp files
        deleteGeneratedFiles();
        IntegrationTestUtils.resetController (controller);
        controller.generate();
        assertEqualsXML ("<Successful>UI generated successfully</Successful>", controller.response.contentAsString);

        File generatedFile = new File("${System.getProperty("base.dir")}/grails-app/views/layouts/"+url1.name+"Layout.gsp");
        String generatedUrlFileContent = generatedFile.getText();
        
        uiUrlTemplate.setText ("contentChanged");
        try
        {
            IntegrationTestUtils.resetController (controller);
            controller.generate();
            String generatedContentAfterTemplateChange = generatedFile.getText();
            assertEquals (generatedUrlFileContent, generatedContentAfterTemplateChange);

            //After reloading templates new content should be generated
            IntegrationTestUtils.resetController (controller);
            controller.reloadTemplates();
            IntegrationTestUtils.resetController (controller);
            controller.generate();
            String textAfterReload = generatedFile.getText();
            assertEquals ("contentChanged",  textAfterReload);

            //if there are some errors in templates reload will not load templates
            uiUrlTemplate.setText ("\${");
            IntegrationTestUtils.resetController (controller);
            controller.reloadTemplates();
            assertTrue(controller.response.contentAsString.indexOf("<Errors")>=0);

            IntegrationTestUtils.resetController (controller);
            controller.generate();
            textAfterReload = generatedFile.getText();
            assertEquals ("Since template could not be reloaded it should not change", "contentChanged",  textAfterReload);
            
        }finally {
            uiUrlTemplate.setText (originalTemplate);
            IntegrationTestUtils.resetController (controller);
            controller.reloadTemplates();
        }

    }

    def checkGeneratedFiles()
    {
        def baseDir =  System.getProperty("base.dir");
        UiWebPage.list().each{url->
            def urlLayoutFile = new File(baseDir + "/grails-app/views/layouts/"+url.name+"Layout.gsp");
            assertTrue (urlLayoutFile.exists());
            def urlRedirectFile = new File(baseDir + "/web-app/${url.name}.gsp");
            assertTrue (urlRedirectFile.exists());
            url.tabs.each{tab->
                def url1WebAppDirectoryFile = new File(baseDir + "/web-app/${url.name}/${tab.name}.gsp");
                assertTrue (url1WebAppDirectoryFile.exists());
            }
        }        
    }
    def deleteGeneratedFiles()
    {
        def baseDir =  System.getProperty("base.dir");
        UiWebPage.list().each{url->
            def urlLayoutFile = new File(baseDir + "/grails-app/views/layouts/"+url.name+"Layout.gsp");
            urlLayoutFile.delete();
            FileUtils.deleteDirectory (new File(baseDir + "/web-app/${url.name}"));
            new File(baseDir + "/web-app/${url.name}.gsp").delete();
        }
    }


}