package com.ifountain.rui.designer

import com.ifountain.comp.test.util.RCompTestCase
import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.model.*;
import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 21, 2009
* Time: 3:18:19 PM
*/
class UiElementTests extends RCompTestCase {
    StringWriter sw;
    MarkupBuilder builder;
    protected void setUp() {
        super.setUp();
        sw = new StringWriter()
        builder = new MarkupBuilder(sw);
    }

    public void testAddWebPage() {
        def webPageProps = [designerType: "WebPage", name: "page1"];
        def tabsProps = [[designerType: "Tab", name: "tab1"], [designerType: "Tab", name: "tab2"]]
        builder.UiElement(webPageProps) {
            builder.UiElement(designerType: "Tabs") {
                tabsProps.each {tab ->
                    builder.UiElement(tab) {
                        builder.UiElement(designerType: 'Layout') {
                            builder.UiElement(designerType: 'CenterUnit', component: '', contentFile: 'center.gsp', gutter: '', scroll: 'false', useShim: 'false');
                        }
                        builder.UiElement(designerType: 'Components')
                        builder.UiElement(designerType: 'Dialogs')
                        builder.UiElement(designerType: 'Actions')
                    }
                }
            }
        }
        def xmlNode = new XmlSlurper().parseText(sw.toString());
        UiWebPage.addUiElement(xmlNode, null);
        UiWebPage webPage = DesignerSpace.getInstance().getUiElement(UiWebPage, "page1");
        assertNotNull(webPage);
        def tabs = webPage.getTabs()
        assertEquals(2, tabs.size());
    }

    public void testAddTab() {
        UiWebPage webPage = DesignerSpace.getInstance().addUiElement(UiWebPage, [name: "page1"]);
        builder.UiElement([designerType: "Tab", name: "tab1", contentFile: "content.gsp"]) {
            builder.UiElement(designerType: 'Layout') {
                builder.UiElement(designerType: 'CenterUnit', component: '', contentFile: 'center.gsp', gutter: '', scroll: 'false', useShim: 'false');
            }
            builder.UiElement(designerType: 'Components') {
                builder.UiElement(designerType: 'Html', name: 'htmlForm')
            }
            builder.UiElement(designerType: 'Dialogs') {
                builder.UiElement(designerType: 'Dialog', component: 'htmlForm')
            }
            builder.UiElement(designerType: 'Actions')
        }
        def xmlNode = new XmlSlurper().parseText(sw.toString());
        UiTab.addUiElement(xmlNode, webPage);
        UiTab tab = DesignerSpace.getInstance().getUiElement(UiTab, "page1_tab1");

        assertNotNull(tab);
        assertEquals("tab1", tab.name)
        assertEquals("content.gsp", tab.contentFile)
        def layout = tab.getLayout()
        assertNotNull(layout);
        def layoutUnits = layout.getUnits();
        assertEquals(1, layoutUnits.size())
        assertTrue(layoutUnits[0] instanceof UiCenterUnit)

        assertEquals(1, tab.getComponents().size());
        assertEquals(1, tab.getDialogs().size());
    }

    public void testAddDialog() {
        UiTab tab = DesignerSpace.getInstance().addUiElement(UiTab, [name: "tab1", webPageId: "page1"])
        UiHtml comp = DesignerSpace.getInstance().addUiElement(UiHtml, [name: "htmlForm", tabId: tab._designerKey])

        def dialogProps = [designerType: "Dialog", component: "htmlForm", width: "500", height: "400", resizable: "true"]
        builder.UiElement(dialogProps)

        def xmlNode = new XmlSlurper().parseText(sw.toString());
        UiDialog.addUiElement(xmlNode, tab);

        UiDialog dialog = DesignerSpace.getInstance().getUiElement(UiDialog, "${comp._designerKey}");
        assertNotNull(dialog);
        assertEquals(500, dialog.width)
        assertEquals(400, dialog.height)
        assertEquals(true, dialog.resizable)

        assertEquals(1, tab.getDialogs().size())
        assertSame(comp, dialog.getComponent())
    }

    public void testAddAction() {
        UiTab tab = DesignerSpace.getInstance().addUiElement(UiTab, [name: "tab1", webPageId: "page1"])
        UiHtml comp = DesignerSpace.getInstance().addUiElement(UiHtml, [name: "htmlForm", tabId: tab._designerKey])
        UiMenuItem menuItem = DesignerSpace.getInstance().addUiElement(UiMenuItem, [name: "menuItem", componentId: comp._designerKey])

        builder.UiElement(designerType: "Actions") {
            builder.UiElement(designerType: "LinkAction", name: "linkAction1", url: "url1", target: "self")
            builder.UiElement(designerType: "LinkAction", name: "linkAction2", url: "url2", target: "blank") {
                builder.UiElement(designerType: "ActionTriggers") {
                    builder.UiElement(designerType: "ActionTrigger", type: "Component event", component: "htmlForm", event: "clicked")
                    builder.UiElement(designerType: "ActionTrigger", type: "Menu", component: "htmlForm", event: "menuItem")
                    builder.UiElement(designerType: "ActionTrigger", type: "Action event", triggeringAction: "linkAction1", event: "success")
                }
            }
        }
        def xmlNode = new XmlSlurper().parseText(sw.toString());
        xmlNode.UiElement.each {
            UiLinkAction.addUiElement(it, tab);
        }

        UiLinkAction action1 = DesignerSpace.getInstance().getUiElement(UiLinkAction, "${tab._designerKey}_linkAction1");
        assertNotNull(action1)

        UiLinkAction action2 = DesignerSpace.getInstance().getUiElement(UiLinkAction, "${tab._designerKey}_linkAction2");
        assertNotNull(action2)
        assertEquals("url2", action2.url)
        assertEquals("blank", action2.target)

        def subscribedEventsOfAction1 = action1.getSubscribedEvents();
        assertEquals(1, subscribedEventsOfAction1.size())
        UiActionTrigger subscribedEventOfAction1 = subscribedEventsOfAction1[0];
        assertEquals("success", subscribedEventOfAction1.event)

        def actionTriggersOfAction2 = action2.getTriggers();
        assertEquals(3, actionTriggersOfAction2.size());
        actionTriggersOfAction2.each {UiActionTrigger actionTrigger ->
            assertSame(action2, actionTrigger.getAction());
        }

        def allTriggersOfHtml = comp.getTriggers();
        assertEquals(2, allTriggersOfHtml.size());

        def actionTriggersOfHtml = comp.getActionTrigers();
        assertEquals(1, actionTriggersOfHtml.size())
        def componentEventTriggers = actionTriggersOfHtml.get("clicked");
        assertEquals(1, componentEventTriggers.size())

        def actionTriggersOfMenuItem = menuItem.getSubscribedEvents();
        assertEquals(1, actionTriggersOfMenuItem.size());
        assertEquals("menuItem", actionTriggersOfMenuItem[0].event)
    }

    public void testAddLayoutUnit() {
        UiTab tab = DesignerSpace.getInstance().addUiElement(UiTab, [name: "tab1", webPageId: "page1"])
        UiHtml comp = DesignerSpace.getInstance().addUiElement(UiHtml, [name: "htmlForm", tabId: tab._designerKey])
        UiLayout pageLayout = DesignerSpace.getInstance().addUiElement(UiLayout, [tabId: tab._designerKey])

        builder.UiElement(designerType: "CenterUnit", component: "htmlForm") {
            builder.UiElement(designerType: "Layout") {
                builder.UiElement(designerType: "CenterUnit", contentFile: "content.gsp")
            }
        }
        def xmlNode = new XmlSlurper().parseText(sw.toString());
        UiLayoutUnit.addUiElement(xmlNode, pageLayout);

        def unitsOfPageLayout = pageLayout.getUnits();
        assertEquals(1, unitsOfPageLayout.size());
        UiLayoutUnit unitOfPageLayout = unitsOfPageLayout[0]
        assertTrue(unitOfPageLayout instanceof UiCenterUnit)
        assertSame(comp, unitOfPageLayout.getComponent())

        UiLayout childLayout = unitOfPageLayout.getChildLayout();
        assertNotNull(childLayout);

        def unitsOfChildLayout = childLayout.getUnits();
        assertEquals(1, unitsOfChildLayout.size())

        UiLayoutUnit unitOfChildLayout = unitsOfChildLayout[0];
        assertNull(unitOfChildLayout.getComponent());
        assertEquals("content.gsp", unitOfChildLayout.contentFile)
    }

    public void testAddRequestAction() {
        UiTab tab = DesignerSpace.getInstance().addUiElement(UiTab, [name: "tab1", webPageId: "page1"])
        builder.UiElement(designerType: "RequestAction", name: "requestAction", components: "htmlForm1, htmlForm2", url: "url") {
            builder.UiElement(designerType: "RequestParameters") {
                builder.UiElement(designerType: "RequestParameter", key: "key1", value: "value1")
                builder.UiElement(designerType: "RequestParameter", key: "key2", value: "value2")
            }
        }
        def xmlNode = new XmlSlurper().parseText(sw.toString());
        try {
            UiRequestAction.addUiElement(xmlNode, tab)
            fail("should throw exception")
        }
        catch (e) {
           assertEquals("Component htmlForm1 could not found for request action requestAction", e.getMessage())
        }

        DesignerSpace.getInstance().addUiElement(UiHtml, [name: "htmlForm1", tabId: tab._designerKey])
        DesignerSpace.getInstance().addUiElement(UiHtml, [name: "htmlForm2", tabId: tab._designerKey])
        
        UiRequestAction requestAction = UiRequestAction.addUiElement(xmlNode, tab)
        assertEquals("requestAction", requestAction.name)
        assertEquals("url", requestAction.url)
        assertEquals("htmlForm1, htmlForm2", requestAction.components)
        assertEquals(2, requestAction.getParameters().size());
    }

    public void testAddFunctionAction(){
        UiTab tab = DesignerSpace.getInstance().addUiElement(UiTab, [name: "tab1", webPageId: "page1"])
        builder.UiElement(designerType: "FunctionAction", name: "functionAction", component: "htmlForm", function: "show") {
            builder.UiElement(designerType: "FunctionArguments") {
                builder.UiElement(designerType: "FunctionArgument", value: "value1")
                builder.UiElement(designerType: "FunctionArgument", value: "value2")
            }
        }
        def xmlNode = new XmlSlurper().parseText(sw.toString());
        try {
            UiFunctionAction.addUiElement(xmlNode, tab)
            fail("should throw exception")
        }
        catch (e) {
           assertEquals("Component <htmlForm> cannot be found for function action functionAction", e.getMessage())
        }

        DesignerSpace.getInstance().addUiElement(UiHtml, [name: "htmlForm", tabId: tab._designerKey])

        UiFunctionAction funcAction = UiFunctionAction.addUiElement(xmlNode, tab)
        assertEquals("functionAction", funcAction.name)
        assertEquals("htmlForm", funcAction.component)
        assertEquals("show", funcAction.function)
        assertEquals(2, funcAction.getArguments().size())
    }


    protected void tearDown() {
        DesignerSpace.destroy();
        super.tearDown();
    }

}