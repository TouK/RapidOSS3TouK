package ui.designer

import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import groovy.xml.MarkupBuilder
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 27, 2009
* Time: 3:27:15 PM
* To change this template use File | Settings | File Templates.
*/
class UiDesignerControllerIntegrationTests extends RapidCmdbIntegrationTestCase {

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DesignerTrashPage.removeAll();
    }

    public void tearDown() {
        DesignerSpace.destroy();
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testUiDesignerControllerCachesTemplates()
    {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def url1Props = [name: "myUrl1", designerType: "WebPage", id: ""]
        builder.UiConfig {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(url1Props) {
                }
            }
        }
        UiDesignerController controller = new UiDesignerController();
        controller.params.configuration = sw.toString()
        controller.save();
        assertEqualsXML("<Successful>UI configuration saved successfully</Successful>", controller.response.contentAsString);

        File uiUrlTemplate = new File("${System.getProperty("base.dir")}/${UiDesignerController.TEMPLATES_DIRECTORY}/WebPage.gsp");
        assertTrue(uiUrlTemplate.exists());
        def originalTemplate = uiUrlTemplate.getText();
        //test generate gsp files
        IntegrationTestUtils.resetController(controller);
        controller.generate();
        assertEqualsXML("<Successful>UI generated successfully</Successful>", controller.response.contentAsString);

        File generatedFile = new File("${System.getProperty("base.dir")}/grails-app/views/layouts/" + url1.name + "Layout.gsp");
        String generatedUrlFileContent = generatedFile.getText();

        uiUrlTemplate.setText("contentChanged");
        try
        {
            IntegrationTestUtils.resetController(controller);
            controller.generate();
            String generatedContentAfterTemplateChange = generatedFile.getText();
            assertEquals(generatedUrlFileContent, generatedContentAfterTemplateChange);

            //After reloading templates new content should be generated
            IntegrationTestUtils.resetController(controller);
            controller.reloadTemplates();
            assertEqualsXML("<Successful>Templates reloaded successfully</Successful>", controller.response.contentAsString);
            IntegrationTestUtils.resetController(controller);
            controller.generate();
            String textAfterReload = generatedFile.getText();
            assertEquals("contentChanged", textAfterReload);

            //if there are some errors in templates reload will not load templates
            uiUrlTemplate.setText("\${");
            IntegrationTestUtils.resetController(controller);
            controller.reloadTemplates();
            assertTrue(controller.response.contentAsString.indexOf("<Errors") >= 0);

            IntegrationTestUtils.resetController(controller);
            controller.generate();
            textAfterReload = generatedFile.getText();
            assertEquals("Since template could not be reloaded it should not change", "contentChanged", textAfterReload);

        } finally {
            uiUrlTemplate.setText(originalTemplate);
            IntegrationTestUtils.resetController(controller);
            controller.reloadTemplates();
        }

    }

    public void testGetHelpContent()
    {
        //The help content is html and it may include invalid characters for xml
        //These should be escaped
        String helpContent1 = "<This is a help file.>&";
        String helpContent2 = "<This is a help file.>&2";
        def helpFile1 = new File("${System.getProperty("base.dir")}/$UiDesignerController.HELP_FILE_DIRECTORY/TrialHelp1.html");
        def helpFile2 = new File("${System.getProperty("base.dir")}/$UiDesignerController.HELP_FILE_DIRECTORY/TrialHelp2.html");
        helpFile1.parentFile.mkdirs();
        helpFile1.setText(helpContent1);
        helpFile2.setText(helpContent2);

        UiDesignerController controller = new UiDesignerController();
        controller.help();

        def helpContentNode = new XmlParser().parseText(controller.response.contentAsString);
        assertEquals("Helps", helpContentNode.name());
        def helpItems = helpContentNode.Help;
        def helpFile1Node = helpItems.find {it.attributes()["id"] == helpFile1.getName()};
        assertEquals(helpContent1, helpFile1Node.text());

        def helpFile2Node = helpItems.find {it.attributes()["id"] == helpFile2.getName()};
        assertEquals(helpContent2, helpFile2Node.text());
        //        HELP_FILE_DIRECTORY
    }


}