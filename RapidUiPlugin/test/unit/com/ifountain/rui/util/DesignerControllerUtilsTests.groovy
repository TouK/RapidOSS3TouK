package com.ifountain.rui.util

import com.ifountain.comp.test.util.file.TestFile
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.model.UiWebPage
import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import ui.designer.DesignerTrashPage
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.model.UiSearchGrid

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 23, 2009
* Time: 11:24:26 AM
*/
class DesignerControllerUtilsTests extends RapidCmdbWithCompassTestCase {
    public static final String DIR = TestFile.TESTOUTPUT_DIR;
    public static final String CONF_FILE_NAME = "uiconfiguration";
    public static final String CONF_FILE = "${DIR}/${CONF_FILE_NAME}.xml";
    public static final String BACKUP_DIR = "${DIR}/UIConfigurations"
    public static final String TEMPLATE_DIR = "${DIR}/Templates"
    def sw;
    def builder;
    File backupDir;
    File templatesDir;
    public void setUp() {
        super.setUp();
        initialize([DesignerTrashPage], []);
        sw = new StringWriter();
        builder = new MarkupBuilder(sw)
        backupDir = new File(BACKUP_DIR);
        File confDir = new File(DIR);
        templatesDir = new File(TEMPLATE_DIR);
        if (!confDir.exists()) {
            confDir.mkdirs();
        }
        if (templatesDir.exists()) {
            FileUtils.deleteDirectory(templatesDir);
        }
        if (backupDir.exists()) {
            FileUtils.deleteDirectory(backupDir);
        }
        new File(CONF_FILE).delete();
    }

    public void tearDown() {
        DesignerSpace.destroy();
        super.tearDown();
    }


    public void testViewThrowsExceptionIfFileDoesNotExist() {
        try {
            DesignerControllerUtils.view(CONF_FILE);
            fail("should throw exception")
        }
        catch (e) {
            assertEquals("UIConfiguration file ${CONF_FILE} does not exist.", e.getMessage());
        }
    }

    public void testViewThrowsExceptionIfFileIsNotXml() {
        new File(CONF_FILE).setText("<asd");
        try {
            DesignerControllerUtils.view(CONF_FILE);
            fail("should throw exception")
        }
        catch (e) {
            assertTrue(e.getMessage().indexOf("Could not parse UIConfiguration file. Reason:") > -1)
        }
    }

    public void testViewGeneratesUniqueIdsForAllNodes() {
        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(designerType: "WebPage", name: "page1", id: "page1") {
                    builder.UiElement(designerType: "Tabs")
                }
            }
        }
        new File(CONF_FILE).setText(sw.toString());

        def viewResult = DesignerControllerUtils.view(CONF_FILE);
        def viewXml = new XmlSlurper().parseText(viewResult);

        def uiElements = viewXml.depthFirst().findAll {it.name() == "UiElement"}
        def idMap = [:]
        uiElements.each {
            def id = it.@id.toString();
            assertTrue(id.length() > 0)
            idMap[id] = id
        }
        assertEquals(uiElements.size(), idMap.size())
    }

    public void testViewCreatesTagsForUndeletableChildsIfTheyDontExist() {
        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(designerType: "WebPage", name: "page1", id: "page1")
            }
        }

        new File(CONF_FILE).setText(sw.toString());

        def viewResult = DesignerControllerUtils.view(CONF_FILE);
        def viewXml = new XmlSlurper().parseText(viewResult);
        def webPageNode = viewXml.depthFirst().find {it.@designerType == "WebPage"}
        def childNodes = webPageNode.UiElement;
        assertEquals(1, childNodes.size())
        assertEquals("Tabs", childNodes[0].@designerType.toString());
    }

    public void testSave() {
        new File(CONF_FILE).setText("<UiConfig/>")
        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(designerType: "WebPage", name: "page1") {
                    builder.UiElement(designerType: "Tabs")
                }
            }
        }
        def uiConfig = sw.toString()
        DesignerControllerUtils.save(uiConfig, CONF_FILE, BACKUP_DIR);

        assertEquals(0, DesignerSpace.getInstance().getUiElements(UiWebPage).size())
        assertTrue(backupDir.exists());
        def originalBackupFile = new File("${BACKUP_DIR}/${CONF_FILE_NAME}_original.xml")
        assertTrue(originalBackupFile.exists())
        assertEquals("<UiConfig/>", originalBackupFile.getText());
        originalBackupFile.delete();

        def backupFiles = backupDir.listFiles().toList();
        assertEquals(1, backupFiles.size());
        def backupFileName = backupFiles[0].getName();
        assertTrue(backupFileName.startsWith("${CONF_FILE_NAME}_"))
        def saveDateString = StringUtils.substringBetween(backupFileName, "_", ".xml")
        Date saveDate = new SimpleDateFormat("yyMMddHHmm").parse(saveDateString)
        assertTrue((System.currentTimeMillis() - saveDate.getTime()) < 60000)

        assertEqualsXML(uiConfig, new File(CONF_FILE).getText())
    }

    public void testSaveThrowsExceptionIfXmlIsNotValid() {
        new File(CONF_FILE).setText("<UiConfig/>")
        //webpage should have a name
        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(designerType: "WebPage") {
                    builder.UiElement(designerType: "Tabs")
                }
            }
        }
        def uiConfig = sw.toString()
        try {
            DesignerControllerUtils.save(uiConfig, CONF_FILE, BACKUP_DIR);
            fail("should throw exception")
        }
        catch (e) {
            assertEquals("Property <name> of WebPage cannot be blank.", e.getMessage());
        }

        assertEquals("<UiConfig/>", new File(CONF_FILE).getText());
    }

    public void testSaveRemovesUnnecessaryUiDesignerInternalAttributes() {
        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages", id:"1") {
                builder.UiElement(designerType: "WebPage", id:"2", name:"page") {
                    builder.UiElement(designerType: "Tabs", id:"3")
                }
            }
        }
        def uiConfig = sw.toString()
        DesignerControllerUtils.save(uiConfig, CONF_FILE, BACKUP_DIR);
        def newUiConfig = new XmlSlurper().parseText(new File(CONF_FILE).getText())
        newUiConfig.depthFirst().each{
            assertNull(it.attributes().get("id"));
        }
        assertEquals("page", newUiConfig.UiElement[0].UiElement[0].@name.toString())
    }

    public void testGenerateThrowsExceptionIfFileDoesNotExist() {
        try {
            DesignerControllerUtils.generate(CONF_FILE, TEMPLATE_DIR, DIR);
            fail("should throw exception")
        }
        catch (e) {
            assertEquals("UIConfiguration file ${CONF_FILE} does not exist.", e.getMessage());
        }
    }

    public void testGenerateThrowsExceptionIfFileIsNotXml() {
        new File(CONF_FILE).setText("<asd");
        try {
            DesignerControllerUtils.generate(CONF_FILE, TEMPLATE_DIR, DIR);
            fail("should throw exception")
        }
        catch (e) {
            assertTrue(e.getMessage().indexOf("Could not parse UIConfiguration file. Reason:") > -1)
        }
    }

    public void testGenerateChecksTheValidityOfConfiguration() {
        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(designerType: "WebPage") {
                    builder.UiElement(designerType: "Tabs")
                }
            }
        }
        new File(CONF_FILE).setText(sw.toString());
        try {
            DesignerControllerUtils.generate(CONF_FILE, TEMPLATE_DIR, DIR);
            fail("should throw exception")
        }
        catch (e) {
            assertEquals("Property <name> of WebPage cannot be blank.", e.getMessage());
        }
    }
    public void testGenerate() {
        createTemplates();
        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(designerType: "WebPage", name: "page1") {
                    builder.UiElement(designerType: "Tabs") {
                        builder.UiElement(designerType: "Tab", name: "tab1")
                        builder.UiElement(designerType: "Tab", name: "tab2")
                    }
                }
                builder.UiElement(designerType: "WebPage", name: "page2") {
                    builder.UiElement(designerType: "Tabs") {
                        builder.UiElement(designerType: "Tab", name: "tab3")
                        builder.UiElement(designerType: "Tab", name: "tab4")
                    }
                }
            }
        }
        new File(CONF_FILE).setText(sw.toString());

        DesignerControllerUtils.generate(CONF_FILE, TEMPLATE_DIR, DIR);

        def trashFiles = DesignerTrashPage.list();
        assertEquals(2, trashFiles.size())
        assertNotNull(DesignerTrashPage.get(webPage: "page1"))
        assertNotNull(DesignerTrashPage.get(webPage: "page2"))

        assertTrue(new File(DesignerControllerUtils.getPageFilePath(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageDirectory(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageHelpFilePath(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getTabFilePath(DIR, "page1", "tab1")).exists())
        assertTrue(new File(DesignerControllerUtils.getTabFilePath(DIR, "page1", "tab2")).exists())

        assertTrue(new File(DesignerControllerUtils.getPageFilePath(DIR, "page2")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageDirectory(DIR, "page2")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageHelpFilePath(DIR, "page2")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page2")).exists())
        assertTrue(new File(DesignerControllerUtils.getTabFilePath(DIR, "page2", "tab3")).exists())
        assertTrue(new File(DesignerControllerUtils.getTabFilePath(DIR, "page2", "tab4")).exists())

        assertEquals(0, DesignerSpace.getInstance().getUiElements(UiWebPage).size());
    }

    public void testTrashFileDeletionWithGenerate() {
        createTemplates();
        DesignerTrashPage.add(webPage: "page1")
        DesignerTrashPage.add(webPage: "page2")

        new File(DesignerControllerUtils.getPageDirectory(DIR, "page1")).mkdirs();
        new File(DesignerControllerUtils.getPageFilePath(DIR, "page1")).createNewFile();
        new File(DesignerControllerUtils.getPageHelpFilePath(DIR, "page1")).createNewFile()
        new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page1")).parentFile.mkdirs();
        new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page1")).createNewFile();
        new File(DesignerControllerUtils.getTabFilePath(DIR, "page1", "tab1")).createNewFile()
        new File(DesignerControllerUtils.getTabFilePath(DIR, "page1", "tab3")).createNewFile()

        new File(DesignerControllerUtils.getPageDirectory(DIR, "page2")).mkdirs();
        new File(DesignerControllerUtils.getPageFilePath(DIR, "page2")).createNewFile();
        new File(DesignerControllerUtils.getPageHelpFilePath(DIR, "page2")).createNewFile()
        new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page2")).parentFile.mkdirs();
        new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page2")).createNewFile();
        new File(DesignerControllerUtils.getTabFilePath(DIR, "page2", "tab4")).createNewFile()
        new File(DesignerControllerUtils.getTabFilePath(DIR, "page2", "tab5")).createNewFile()

        builder.UiConfig() {
            builder.UiElement(designerType: "WebPages") {
                builder.UiElement(designerType: "WebPage", name: "page1") {
                    builder.UiElement(designerType: "Tabs") {
                        builder.UiElement(designerType: "Tab", name: "tab1")
                        builder.UiElement(designerType: "Tab", name: "tab2")
                    }
                }
            }
        }
        new File(CONF_FILE).setText(sw.toString());

        DesignerControllerUtils.generate(CONF_FILE, TEMPLATE_DIR, DIR);

        def trashFiles = DesignerTrashPage.list();
        assertEquals(1, trashFiles.size())
        assertNotNull(DesignerTrashPage.get(webPage: "page1"))

        assertTrue(new File(DesignerControllerUtils.getPageFilePath(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageDirectory(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageHelpFilePath(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page1")).exists())
        assertTrue(new File(DesignerControllerUtils.getTabFilePath(DIR, "page1", "tab1")).exists())
        assertTrue(new File(DesignerControllerUtils.getTabFilePath(DIR, "page1", "tab2")).exists())

        assertFalse(new File(DesignerControllerUtils.getTabFilePath(DIR, "page1", "tab3")).exists())

        assertFalse(new File(DesignerControllerUtils.getPageFilePath(DIR, "page2")).exists())
        assertFalse(new File(DesignerControllerUtils.getPageDirectory(DIR, "page2")).exists())

        assertTrue(new File(DesignerControllerUtils.getPageHelpFilePath(DIR, "page2")).exists())

        assertFalse(new File(DesignerControllerUtils.getPageLayoutFilePath(DIR, "page2")).exists())
        assertFalse(new File(DesignerControllerUtils.getTabFilePath(DIR, "page2", "tab4")).exists())
        assertFalse(new File(DesignerControllerUtils.getTabFilePath(DIR, "page2", "tab5")).exists())
    }

    private void createTemplates() {
        if (!templatesDir.exists()) {
            templatesDir.mkdirs();
        }
        new File("${TEMPLATE_DIR}/WebPage.gsp").setText("<html></html>")
        new File("${TEMPLATE_DIR}/Tab.gsp").setText("TabContent")
        new File("${TEMPLATE_DIR}/Help.gsp").setText("HelpContent")
        new File("${TEMPLATE_DIR}/Layout.gsp").setText("LayoutContent")
    }
}