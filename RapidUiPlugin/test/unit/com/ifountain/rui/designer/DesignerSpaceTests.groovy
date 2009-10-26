package com.ifountain.rui.designer

import com.ifountain.comp.test.util.RCompTestCase
import org.apache.commons.lang.StringUtils
import com.ifountain.rui.designer.model.UiLayoutUnit
import com.ifountain.rui.designer.model.UiComponent
import com.ifountain.rui.designer.model.UiAction
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.converter.DateConverter
import com.ifountain.rcmdb.converter.LongConverter
import com.ifountain.rcmdb.converter.DoubleConverter
import com.ifountain.rcmdb.converter.BooleanConverter
import com.ifountain.rcmdb.converter.StringConverter
import java.text.SimpleDateFormat

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 1:25:35 PM
*/
class DesignerSpaceTests extends RCompTestCase {

    protected void setUp() {
        super.setUp();
        registerDefaultConverters();
    }

    protected void tearDown() {
        DesignerSpaceClassLoaderFactory.setDesignerClassLoader(null);
        DesignerSpace.destroy();
        super.tearDown();
    }
    public String getWorkspacePath() throws Exception
    {
        String canonicalPath = new java.io.File(".").getCanonicalPath();
        //to run in developer pc
        if (canonicalPath.endsWith("RapidModules"))
        {
            return "..";
        }
        else //to run in hudson
        {
            return "../../..";
        }
    }
    public void testGetClasses() {
        def files = new File(getWorkspacePath() + "/RapidModules/RapidUiPlugin/src/groovy/com/ifountain/rui/designer/model").listFiles().findAll {it.getName().endsWith(".groovy")};
        def classes = DesignerSpace.getInstance().getUiClasses();
        assertEquals(files.size(), classes.size());
        def classNames = files.collect {StringUtils.substringBefore(it.name, ".groovy")}
        classes.each {Class c ->
            assertTrue(classNames.contains(StringUtils.substringAfter(c.name, DesignerSpace.PACKAGE_NAME + ".")))
        }
    }

    public void testGetMetaDataOfAClass() {
        ClassLoader cl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());

        DesignerSpaceClassLoaderFactory.setDesignerClassLoader(cl);
        def uiModel1 = """
            package ${DesignerSpace.PACKAGE_NAME}
            import ${UiElmnt.class.name}

            class UiModel1 extends UiElmnt{
                String prop1 = "";
                Long prop2 = 0;
                Double prop3 = 0.0;
                Date prop4 = new Date(0);
                Boolean prop5 = false;
                String prop6 = "";
    
                public static Map metaData(){
                    def metaData = [
                        help: "Model1.html",
                        designerType: "Model1",
                        propertyConfiguration:[
                            prop1:[descr:"descr1", validators:[key:true]],
                            prop2:[descr:"descr2"],
                            prop3:[:],
                            prop4:[descr:"descr4"],
                            prop6:[descr:"descr6", type:"List", required:true, defaultValue:"defaultValue", validators:[inList:["x", "y", "z"]]],
                            undefinedProp:[descr:"undefinedProp", type:"String", required:false, defaultValue:"defaultValue", validators:[inList:["x", "y", "z"]]],
                            prop7:[isVisible:false]
                        ],
                        childrenConfiguration: [
                                [designerType: "ChildModel", propertyName: "children", isMultiple: true]
                        ]
                    ]
                    return metaData;
                }
            }
        """
        cl.parseClass(uiModel1);
        DesignerSpace ds = DesignerSpace.getInstance();
        def model1Class = cl.getLoadedClasses().findAll {StringUtils.substringAfter(it.name, DesignerSpace.PACKAGE_NAME + ".") == "UiModel1"}[0];
        def originalMetaData = model1Class.metaData();
        def metaData = ds.getMetaData(model1Class);

        assertEquals(originalMetaData.help, metaData.help)
        assertEquals(originalMetaData.designerType, metaData.designerType)
        assertTrue(metaData.containsKey("childrenConfiguration"))

        def expectedProperties = originalMetaData.propertyConfiguration.entrySet().findAll{it.value.isVisible == null || it.value.isVisible != false}.key.sort {it}
        assertEquals(expectedProperties.size(), metaData.propertyConfiguration.size())
        def propConfiguration = metaData.propertyConfiguration.values().sort {it.name};

        for (int i = 0; i < expectedProperties.size(); i++) {
            assertEquals(expectedProperties[i], propConfiguration[i].name);
        }

        assertEquals("String", propConfiguration[0].type);
        assertEquals("Number", propConfiguration[1].type);
        assertEquals("Float", propConfiguration[2].type);
        assertEquals("Date", propConfiguration[3].type);
        assertEquals("List", propConfiguration[4].type);
        assertEquals("String", propConfiguration[5].type);

        //prop1 is key prop and it could not be null or blank so it is a required property
        assertEquals(true, propConfiguration[0].required);
        assertEquals(false, propConfiguration[1].required);
        assertEquals(false, propConfiguration[2].required);
        assertEquals(false, propConfiguration[3].required);
        //prop6 is specified as required property
        assertEquals(true, propConfiguration[4].required);
        assertEquals(false, propConfiguration[5].required);

        assertEquals("descr1", propConfiguration[0].descr);
        assertEquals("descr2", propConfiguration[1].descr);
        assertEquals("", propConfiguration[2].descr);
        assertEquals("descr4", propConfiguration[3].descr);
        assertEquals("descr6", propConfiguration[4].descr);
        assertEquals("undefinedProp", propConfiguration[5].descr);

        assertEquals("", propConfiguration[0].defaultValue);
        assertEquals("0", propConfiguration[1].defaultValue);
        assertEquals("0.0", propConfiguration[2].defaultValue);
        assertEquals(String.valueOf(new Date(0)), propConfiguration[3].defaultValue);
        assertEquals("defaultValue", propConfiguration[4].defaultValue);
        assertEquals("defaultValue", propConfiguration[5].defaultValue);

        assertNull(propConfiguration[0].inList);
        assertNull(propConfiguration[1].inList);
        assertNull(propConfiguration[2].inList);
        assertNull(propConfiguration[3].inList);
        assertEquals("x,y,z", propConfiguration[4].inList);
        assertEquals("x,y,z", propConfiguration[5].inList);
    }

    public void testGetMetaData() {
        def metaDataXml = DesignerSpace.getInstance().getMetaData();
        new File("metaData.xml").setText(metaDataXml)
        def responseXml = new XmlSlurper().parseText(metaDataXml);
        def components = responseXml.UiElement
        def uiElementClasses = DesignerSpace.getInstance().getUiClasses().findAll {
            try {
                it.'metaData'();
                return true;
            } catch (groovy.lang.MissingMethodException e) {}
            return false;
        }

        def componentMap = [:]
        components.each {
            componentMap[it.'@designerType'.text()] = it;
        }
        def classToBeExcluded = [UiLayoutUnit.name, UiComponent.name, UiAction.name]
        uiElementClasses.each {Class uiElementClass ->
            if (!classToBeExcluded.contains(uiElementClass.name))
            {
                def component = componentMap[StringUtils.substringAfter(uiElementClass.name, "${DesignerSpace.PACKAGE_NAME}.Ui")];
                assertNotNull("Undefined for ${StringUtils.substringAfter(uiElementClass.name, "${DesignerSpace.PACKAGE_NAME}.Ui")} in ${componentMap}", component);
            }
        }
        def urlsComponent = componentMap["WebPages"];
        assertEquals("WebPages", urlsComponent.'@designerType'.text());
        assertEquals("Web Pages", urlsComponent.'@display'.text());
        assertEquals("Web Pages.html", urlsComponent.'@help'.text());
        assertEquals(1, urlsComponent.Children.size());
        assertEquals("WebPage", urlsComponent.Children[0].Child[0].'@designerType'.text());
        assertEquals("true", urlsComponent.Children[0].Child[0].'@isMultiple'.text());
        def urlComponent = componentMap["WebPage"];
        def urlPropertyMetaData = urlComponent.Properties.Property;
        def xmlProperties = [:]
        urlPropertyMetaData.each {
            xmlProperties[it.'@name'.text()] = it;
        }
        assertEquals("name", xmlProperties["name"].'@name'.text());
        assertEquals("String", xmlProperties["name"].'@type'.text());
        assertTrue(xmlProperties["name"].'@descr'.text() != null);
        assertEquals("true", xmlProperties["name"].'@required'.text());

        def urlChildMetaData = urlComponent.Children.Child;
        def xmlChildren = [:]
        urlChildMetaData.each {
            xmlChildren[it.'@designerType'.text()] = it;
        }
        assertEquals(1, xmlChildren.size());
        assertEquals("Tabs", xmlChildren["Tabs"].'@designerType'.text());
        assertEquals("false", xmlChildren["Tabs"].'@isMultiple'.text());

        def tabsComponent = componentMap["Tabs"];
        def tabsChildMetaData = tabsComponent.Children.Child;
        def tabsXmlChildren = [:]
        tabsChildMetaData.each {
            tabsXmlChildren[it.'@designerType'.text()] = it;
        }
        assertEquals(1, tabsXmlChildren.size());
        assertEquals("Tab", tabsXmlChildren["Tab"].'@designerType'.text());
        assertEquals("true", tabsXmlChildren["Tab"].'@isMultiple'.text());

        //Test will not send information about components whose meta data does not have designerType
        assertNull(componentMap["Component"])
        assertNull(componentMap["Action"])
        assertNull(componentMap["LayoutUnit"])
        assertNull(componentMap[""])
    }

    public void testAddUiElementThrowsExceptionIfClassCannotBeFound() {
        ClassLoader cl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        DesignerSpaceClassLoaderFactory.setDesignerClassLoader(cl);
        try {
            DesignerSpace.getInstance().addUiElement(Date.class, [:])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Class ${Date.class.name} cannot be found among UiElement classes", e.getMessage());
        }
    }

    public void testPropertyConversion() {
        ClassLoader cl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());

        DesignerSpaceClassLoaderFactory.setDesignerClassLoader(cl);
        def uiModel1 = """
            package ${DesignerSpace.PACKAGE_NAME}
            import ${UiElmnt.class.name}

            class UiModel1 extends UiElmnt{
                String prop1 = "";
                Long prop2 = 0;
                Double prop3 = 0.0;
                Date prop4 = new Date(0);
                Boolean prop5 = false;

                public static Map metaData(){
                    return [
                        propertyConfiguration:[
                            prop1: [validators:[key:true]]
                        ]
                    ]
                }
            }
        """
        cl.parseClass(uiModel1);
        DesignerSpace ds = DesignerSpace.getInstance();
        def model1Class = cl.getLoadedClasses().findAll {StringUtils.substringAfter(it.name, DesignerSpace.PACKAGE_NAME + ".") == "UiModel1"}[0];

        SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss.SSS")
        def anyDate = new Date(System.currentTimeMillis());

        ds.addUiElement(model1Class, [prop1: 5, prop2: "12", prop3: "12.3", prop4: format.format(anyDate), prop5: "true"])
        def model1Instance = ds.getUiElement(model1Class, "5");

        assertNotNull(model1Instance);
        assertEquals("5", model1Instance._designerKey)
        assertEquals("5", model1Instance.prop1)
        assertEquals(12, model1Instance.prop2)
        assertEquals(new Double(12.3), model1Instance.prop3)
        assertEquals(anyDate, model1Instance.prop4)
        assertEquals(true, model1Instance.prop5)

        //Long conversion fails
        try {
            ds.addUiElement(model1Class, [prop1: "prop1", prop2: "prop2"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Cannot convert value <prop2> for property <prop2> to type ${Long.class.name} for Model1", e.getMessage())
        }

        //Double conversion fails
        try {
            ds.addUiElement(model1Class, [prop1: "prop1", prop3: "prop3"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Cannot convert value <prop3> for property <prop3> to type ${Double.class.name} for Model1", e.getMessage())
        }

        //Date conversion fails
        try {
            ds.addUiElement(model1Class, [prop1: "prop1", prop4: "prop4"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Cannot convert value <prop4> for property <prop4> to type ${Date.class.name} for Model1", e.getMessage())
        }

        //Boolean conversion fails
        try {
            ds.addUiElement(model1Class, [prop1: "prop1", prop5: "prop5"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Cannot convert value <prop5> for property <prop5> to type ${Boolean.class.name} for Model1", e.getMessage())
        }
    }

    public void testUniqueness() {
        ClassLoader cl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());

        DesignerSpaceClassLoaderFactory.setDesignerClassLoader(cl);
        def parentModel = """
            package ${DesignerSpace.PACKAGE_NAME}
            import ${UiElmnt.class.name}

            class UiParentModel extends UiElmnt{
                String prop1 = "";
                String prop2 = "";
                public static Map metaData(){
                    return [
                        propertyConfiguration:[
                            prop1: [validators:[key:true]],
                            prop2: [validators:[key:true]]
                        ]
                    ]
                }
            }
        """
        def childModel = """
            package ${DesignerSpace.PACKAGE_NAME}

            class UiChildModel extends UiParentModel{
                String prop3 = "";
                public static Map metaData(){
                    def metaData =  [
                        propertyConfiguration:[
                            prop3: [:]
                        ]
                    ]
                    def parentMetaData = UiParentModel.metaData();
                    metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
                    return metaData;
                }
            }
        """
        cl.parseClass(parentModel);
        cl.parseClass(childModel);
        def parentClass = cl.getLoadedClasses().findAll {StringUtils.substringAfter(it.name, DesignerSpace.PACKAGE_NAME + ".") == "UiParentModel"}[0];
        def childClass = cl.getLoadedClasses().findAll {StringUtils.substringAfter(it.name, DesignerSpace.PACKAGE_NAME + ".") == "UiChildModel"}[0];

        DesignerSpace ds = DesignerSpace.getInstance();
        ds.addUiElement(parentClass, [prop1: "value1", prop2: "value2"])
        def parentInstance1 = ds.getUiElement(parentClass, "value1_value2");
        assertNotNull(parentInstance1);
        assertEquals("value1_value2", parentInstance1._designerKey);
        assertEquals("value1", parentInstance1.prop1)
        assertEquals("value2", parentInstance1.prop2)

        try {
            ds.addUiElement(parentClass, [prop1: "value1", prop2: "value2"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Another instance of ParentModel with keys <prop1:value1> <prop2:value2> exists.", e.getMessage())
        }

        try {
            ds.addUiElement(childClass, [prop1: "value1", prop2: "value2"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Another instance of ParentModel with keys <prop1:value1> <prop2:value2> exists.", e.getMessage())
        }

        ds.addUiElement(childClass, [prop1: "value11", prop2: "value22"])
        assertNotNull(ds.getUiElement(parentClass, "value11_value22"));
        assertNotNull(ds.getUiElement(childClass, "value11_value22"));

        try {
            ds.addUiElement(parentClass, [prop1: "value11", prop2: "value22"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Another instance of ParentModel with keys <prop1:value11> <prop2:value22> exists.", e.getMessage())
        }

        try {
            ds.addUiElement(childClass, [prop1: "value11", prop2: "value22"])
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("Another instance of ChildModel with keys <prop1:value11> <prop2:value22> exists.", e.getMessage())
        }
    }

    public void testValidation() {
        ClassLoader cl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        DesignerSpaceClassLoaderFactory.setDesignerClassLoader(cl);
        def uiModel1 = """
            package ${DesignerSpace.PACKAGE_NAME}
            import ${UiElmnt.class.name}

            class UiModel1 extends UiElmnt{
                String prop1 = "";
                String prop2 = "";
                String prop3 = "";
                String prop4 = "";
                String prop5 = "";
                String prop6 = "";

                public static Map metaData(){
                    return [
                        propertyConfiguration:[
                            prop1: [validators:[key:true]],
                            prop2: [validators:[key:true]],
                            prop3: [validators:[blank:false]],
                            prop4: [validators:[nullable:false]],
                            prop5: [validators:[matches:"a.*"]],
                            prop6: [validators:[inList:["aa", "bb"]]]
                        ]
                    ]
                }
            }
        """
        cl.parseClass(uiModel1);
        DesignerSpace ds = DesignerSpace.getInstance();
        def model1Class = cl.getLoadedClasses().findAll {StringUtils.substringAfter(it.name, DesignerSpace.PACKAGE_NAME + ".") == "UiModel1"}[0];

        //keys cannot be blank
        try{
            ds.addUiElement(model1Class, [prop1: "", prop2: "prop2", prop3:"prop3", prop4:"prop4", prop5:"asd", prop6:"aa"]);
            fail("should throw exception")
        }
        catch(e){
            assertEquals("Property <prop1> of Model1 cannot be blank.", e.getMessage());
        }

        //keys cannot be null
        try{
            ds.addUiElement(model1Class, [prop1: null, prop2: "prop2", prop3:"prop3", prop4:"prop4", prop5:"asd", prop6:"aa"]);
            fail("should throw exception")
        }
        catch(e){
            assertEquals("Property <prop1> of Model1 cannot be null.", e.getMessage());
        }

        //blank constraint
        try{
            ds.addUiElement(model1Class, [prop1: "prop1", prop2: "prop2", prop3:"", prop4:"prop4", prop5:"asd", prop6:"aa"]);
            fail("should throw exception")
        }
        catch(e){
            assertEquals("Property <prop3> of Model1 cannot be blank.", e.getMessage());
        }

        //nullable constraint
        try{
            ds.addUiElement(model1Class, [prop1: "prop1", prop2: "prop2", prop3:"prop3", prop4:null, prop5:"asd", prop6:"aa"]);
            fail("should throw exception")
        }
        catch(e){
            assertEquals("Property <prop4> of Model1 cannot be null.", e.getMessage());
        }

        //matches constraint
        try{
            ds.addUiElement(model1Class, [prop1: "prop1", prop2: "prop2", prop3:"prop3", prop4:"prop4", prop5:"bbbb", prop6:"aa"]);
            fail("should throw exception")
        }
        catch(e){
            assertEquals("Property <prop5> of Model1 does not match expression <a.*>.", e.getMessage());
        }

        //inList constraint
        try{
            ds.addUiElement(model1Class, [prop1: "prop1", prop2: "prop2", prop3:"prop3", prop4:"prop4", prop5:"asd", prop6:"cc"]);
            fail("should throw exception")
        }
        catch(e){
            assertEquals("Property <prop6> of Model1 is not one of <aa, bb>.", e.getMessage());
        }
    }

    def registerDefaultConverters()
    {
        def dateFormat = "yyyy-dd-MM HH:mm:ss.SSS";
        RapidConvertUtils.getInstance().register(new StringConverter(dateFormat), String.class)
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
        RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)
    }
}