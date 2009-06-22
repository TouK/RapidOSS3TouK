package com.ifountain.rui.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.method.GetPropertiesMethod
import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.converter.DateConverter
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.validation.BlankConstraint
import com.ifountain.rcmdb.domain.method.FederatedPropertyManagerImpl

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 27, 2009
* Time: 4:32:18 PM
* To change this template use File | Settings | File Templates.
*/
class DesignerUtilsTest extends RapidCmdbWithCompassTestCase{
    Closure metaDataGetter;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.enableGlobally();
        metaDataGetter = {component->
            return component.metaData();            
        }
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.disableGlobally();
    }

    public void testAddConfigurationParametersFromModel()
    {

        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        def childModelName = "UiModel1";
        def relatedModelName = "UiModel2";

        //All properties will be configured as metadata
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE];
        def prop2 = [name:"prop2", type:ModelGenerator.NUMBER_TYPE];
        def prop3 = [name:"prop3", type:ModelGenerator.BOOLEAN_TYPE];
        def prop4 = [name:"prop4", type:ModelGenerator.DATE_TYPE];
        def prop5 = [name:"prop5", type:ModelGenerator.FLOAT_TYPE];
        def prop6 = [name:"prop6", type:ModelGenerator.STRING_TYPE];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def rel2 = [name:"rel2",  reverseName:"revrel2", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:childModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];
        def revrel2 = [name:"revrel2",  reverseName:"rel2", toModel:childModelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];

        def childModelMetaProps = [name:childModelName]
        def relatedModelMetaProps = [name:relatedModelName]

        def modelProps = [prop1, prop2, prop3, prop4, prop5, prop6];
        def keyPropList = [prop1];
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, modelProps, keyPropList, [rel1, rel2])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1, revrel2])
        def gcl = new GroovyClassLoader();
        gcl.parseClass (childModelString+relatedModelString);

        def model1Class = gcl.getLoadedClasses().findAll {it.name == childModelName}[0];
        def model2Class = gcl.getLoadedClasses().findAll {it.name == relatedModelName}[0];

        //Configured values will overwrite values defined in model. If no configuration exist description field be left empty
        def model1Prop1ConfigurationProps = [descr:"desc1", inList:"x,y,z"];//inList property should not be overwritten
        def model1Prop2ConfigurationProps = [descr:"desc2"];
        def model1Prop3ConfigurationProps = [:]; //no description specified it should be empty
        def model1Prop4ConfigurationProps = [descr:"desc4"];
        def rel2ConfigurationProps = [descr:"rel2"];
        def model1Prop6ConfigurationProps = [descr:"desc6", type:"List", inList:"x,y,z", required:true, defaultValue:"defaultValue"];//inList is not defined in domain and should return configured values
        def undefinedPropertyConf = [descr:"undefinedProp", type:"String", inList:"x,y,z", required:false, defaultValue:"defaultValue"];
        def metaDataConfiguration  = [
                "prop1":model1Prop1ConfigurationProps,
                "prop2":model1Prop2ConfigurationProps,
                "prop3":model1Prop3ConfigurationProps,
                "prop4":model1Prop4ConfigurationProps,
                "prop6":model1Prop6ConfigurationProps,
                "rel2":rel2ConfigurationProps,
                "undefinedProperty":undefinedPropertyConf
        ]
        DefaultGrailsDomainClass grailsDomainClass = new DefaultGrailsDomainClass(model1Class);
        GetPropertiesMethod method = new GetPropertiesMethod(grailsDomainClass, new FederatedPropertyManagerImpl());
        model1Class.metaClass.'static'.getPropertiesList = {
            return method.getDomainObjectProperties();
        }

        def expectedProperties = metaDataConfiguration.keySet().sort {it}

        DesignerUtils.metaClass.'static'.getInList = {constrainedProps, propName->
            if(propName == prop4.name || propName == prop1.name)
            {
                return ["item1", "item2"]
            }
            else if(propName == prop2.name)
            {
                return [] 
            }
            return null;

        }
        def uiPropertyMetaDatasForUiModel1 = DesignerUtils.addConfigurationParametersFromModel(metaDataConfiguration, grailsDomainClass).entrySet().sort {it.key}.value;


        assertEquals (expectedProperties.size(), uiPropertyMetaDatasForUiModel1.size());
        for(int i=0; i < expectedProperties.size(); i++){
            assertEquals (expectedProperties[i], uiPropertyMetaDatasForUiModel1[i].name);
        }
        assertEquals ("String", uiPropertyMetaDatasForUiModel1[0].type);
        assertEquals ("Number", uiPropertyMetaDatasForUiModel1[1].type);
        assertEquals ("Boolean", uiPropertyMetaDatasForUiModel1[2].type);
        assertEquals ("Date", uiPropertyMetaDatasForUiModel1[3].type);
        assertEquals ("List", uiPropertyMetaDatasForUiModel1[4].type);
        assertEquals ("String", uiPropertyMetaDatasForUiModel1[5].type);
        assertEquals ("String", uiPropertyMetaDatasForUiModel1[6].type);
//
//        //prop1 is key prop and it could not be null or blank so it is a required property
        assertEquals (true, uiPropertyMetaDatasForUiModel1[0].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[1].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[2].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[3].required);
        //prop6 is specified as required property
        assertEquals (true, uiPropertyMetaDatasForUiModel1[4].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[5].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[6].required);
//
//
        assertEquals ("desc1", uiPropertyMetaDatasForUiModel1[0].descr);
        assertEquals ("desc2", uiPropertyMetaDatasForUiModel1[1].descr);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[2].descr);
        assertEquals ("desc4", uiPropertyMetaDatasForUiModel1[3].descr);
        assertEquals ("desc6", uiPropertyMetaDatasForUiModel1[4].descr);
        assertEquals ("rel2", uiPropertyMetaDatasForUiModel1[5].descr);
        assertEquals ("undefinedProp", uiPropertyMetaDatasForUiModel1[6].descr);

        assertEquals ("", uiPropertyMetaDatasForUiModel1[0].defaultValue);
        assertEquals ("0", uiPropertyMetaDatasForUiModel1[1].defaultValue);
        assertEquals ("false", uiPropertyMetaDatasForUiModel1[2].defaultValue);
        assertEquals (String.valueOf(new Date(0)), uiPropertyMetaDatasForUiModel1[3].defaultValue);
        assertEquals ("defaultValue", uiPropertyMetaDatasForUiModel1[4].defaultValue);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[5].defaultValue);
        assertEquals ("defaultValue", uiPropertyMetaDatasForUiModel1[6].defaultValue);
//

        assertEquals ("x,y,z", uiPropertyMetaDatasForUiModel1[0].inList);
        assertNull (uiPropertyMetaDatasForUiModel1[1].inList);
        assertNull (uiPropertyMetaDatasForUiModel1[2].inList);
        assertEquals("item1,item2", uiPropertyMetaDatasForUiModel1[3].inList);
        assertEquals ("x,y,z", uiPropertyMetaDatasForUiModel1[4].inList);
        assertNull (uiPropertyMetaDatasForUiModel1[5].inList);
        assertEquals (undefinedPropertyConf.inList, uiPropertyMetaDatasForUiModel1[6].inList);
    }

    public void testIsRequired()
    {

        String propName = "prop1";
        assertFalse("Should return false since it is not a constrained property", DesignerUtils.isRequired ([:], propName));
        def prop = new ConstrainedProperty(Object.class, propName, String.class);
        prop.setNullable (false);
        prop.registerNewConstraint (ConstrainedProperty.BLANK_CONSTRAINT, BlankConstraint);
        def constrainedProps = [:]
        constrainedProps[propName] = prop;
        assertTrue("Should return true since it is not nullable", DesignerUtils.isRequired (constrainedProps,propName));

        //check if prop is not blank it will return true
        prop = new ConstrainedProperty(Object.class, propName, String.class);
        prop.setNullable (true);
        prop.registerNewConstraint (ConstrainedProperty.BLANK_CONSTRAINT, BlankConstraint);
        prop.applyConstraint(ConstrainedProperty.BLANK_CONSTRAINT, false);
        constrainedProps[propName] = prop;
        assertTrue("Should return true since it is not blank", DesignerUtils.isRequired (constrainedProps,propName));

        //check if prop is nullable and blank it will return false
        prop = new ConstrainedProperty(Object.class, propName, String.class);
        prop.setNullable (true);
        prop.registerNewConstraint (ConstrainedProperty.BLANK_CONSTRAINT, BlankConstraint);
        prop.applyConstraint(ConstrainedProperty.BLANK_CONSTRAINT, true);
        constrainedProps[propName] = prop;
        assertFalse("Should return false since it is nullable and blank", DesignerUtils.isRequired (constrainedProps,propName));

    }

    public void testInList()
    {
        assertFalse("Should return false since it is not a constrained property", DesignerUtils.isRequired ([:], "prop1"));
    }

    public void testGenerateXmlWithFormatter()
    {
        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        def modelName = "UiModel1";

        //All properties will be configured as metadata
        def prop1 = [name:"prop1", type:ModelGenerator.DATE_TYPE];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE];
        def prop3 = [name:"prop3", type:ModelGenerator.NUMBER_TYPE];

        def modelMetaProps = [name:modelName]

        def modelProps = [prop1, prop2, prop3];
        def keyPropList = [prop1];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        def modelClass = gcl.parseClass (modelString);
        SimpleDateFormat df = new SimpleDateFormat("yyyy MM")
        modelClass.metaClass.'static'.metaData = {
            return [
                designerType: "model1",
                propertyConfiguration: [
                        prop1: [descr: '', formatter:{object->return df.format(object["prop1"])}],
                        prop2: [descr: ''],
                        prop3: [descr: '']
                ]
            ];
        };
        RapidConvertUtils.getInstance().register(new DateConverter("yyyy MM"), Date.class)
        initialize ([modelClass], [], false);
        def model1Instance = modelClass.'add'(prop1:new Date(), prop2:"prop2Value", prop3:100);

        def sw = new StringWriter();
        def markupBuilder = new MarkupBuilder(sw);
        markupBuilder.UiElement{
            DesignerUtils.generateXml([model1Instance], markupBuilder, metaDataGetter);
        }

        def parser = new XmlParser().parseText(sw.toString());
        assertEquals(1, parser.UiElement.size());
        assertEquals(5, parser.UiElement[0].attributes().size());
        assertEquals (String.valueOf(model1Instance.id), parser.UiElement.'@id'.text());
        assertEquals ("model1", parser.UiElement.'@designerType'.text());
        assertEquals (df.format(model1Instance.prop1), parser.UiElement.'@prop1'.text());
        assertEquals (model1Instance.prop2, parser.UiElement.'@prop2'.text());
        assertEquals (String.valueOf(model1Instance.prop3), parser.UiElement.'@prop3'.text());
    }

    public void testGenerateXmlDoesnotAddOptionalPropertiesWithDefaultValueToXml()
    {
        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        def modelName = "UiModel1";

        //All properties will be configured as metadata
        def prop1 = [name:"prop1", type:ModelGenerator.DATE_TYPE];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, defaultValue:"defaultalue"];
        def prop3 = [name:"prop3", type:ModelGenerator.NUMBER_TYPE, defaultValue:100];
        def prop4 = [name:"prop4", type:ModelGenerator.NUMBER_TYPE, defaultValue:100];

        def modelMetaProps = [name:modelName]

        def modelProps = [prop1, prop2, prop3, prop4];
        def keyPropList = [prop1];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        def modelClass = gcl.parseClass (modelString);
        def dateFormatStr = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat df = new SimpleDateFormat(dateFormatStr)
        modelClass.metaClass.'static'.metaData = {
            return [
                designerType: "model1",
                propertyConfiguration: [
                        prop1: [descr: '', formatter:{object->return df.format(object["prop1"])}],
                        prop2: [descr: ''],
                        prop3: [descr: ''],
                        prop4: [descr: '', required:true]
                ]
            ];
        };
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormatStr), Date.class)
        initialize ([modelClass], [], false);
        def model1Instance = modelClass.'add'(prop1:new Date(), prop2:"prop2Value", prop3:0, prop4:0);

        def sw = new StringWriter();
        def markupBuilder = new MarkupBuilder(sw);
        markupBuilder.UiElement{
            DesignerUtils.generateXml([model1Instance], markupBuilder, metaDataGetter);
        }
        //Since all properties are different from their default value all properties will be added to xml
        def parser = new XmlParser().parseText(sw.toString());
        assertEquals(1, parser.UiElement.size());
        assertEquals(6, parser.UiElement[0].attributes().size());
        assertEquals (String.valueOf(model1Instance.id), parser.UiElement.'@id'.text());
        assertEquals ("model1", parser.UiElement.'@designerType'.text());
        assertEquals (df.format(model1Instance.prop1), parser.UiElement.'@prop1'.text());
        assertEquals (model1Instance.prop2, parser.UiElement.'@prop2'.text());
        assertEquals (String.valueOf(model1Instance.prop3), parser.UiElement.'@prop3'.text());
        assertEquals (String.valueOf(model1Instance.prop4), parser.UiElement.'@prop4'.text());


        //All default value optional properties will be discarded and only props different from default value and
        //all required properties equal to default value will be added to xml
        model1Instance = modelClass.'add'([prop1:new Date()]);

        sw = new StringWriter();
        markupBuilder = new MarkupBuilder(sw);
        markupBuilder.UiElement{
            DesignerUtils.generateXml([model1Instance], markupBuilder, metaDataGetter);
        }

        parser = new XmlParser().parseText(sw.toString());
        assertEquals(1, parser.UiElement.size());
        assertEquals(4, parser.UiElement[0].attributes().size());
        assertEquals (String.valueOf(model1Instance.id), parser.UiElement.'@id'.text());
        assertEquals (df.format(model1Instance.prop1), parser.UiElement.'@prop1'.text());
        assertEquals (String.valueOf(model1Instance.prop4), parser.UiElement.'@prop4'.text());
    }

    public void testGenerateXmlWithRelatedClass()
    {
        def modelClasses = createDomainClassesForRelationTest(ModelGenerator.RELATION_TYPE_MANY, ModelGenerator.RELATION_TYPE_MANY);
        def modelClass = modelClasses.findAll {it.name == "UiModel1"}[0];
        def relatedModelClass = modelClasses.findAll {it.name == "UiModel2"}[0];
        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        modelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model1",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[[designerType:"Model2", isMultiple:true, propertyName:"rel1"]]
            ];
        };

        relatedModelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model2",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[]
            ];
        };
        RapidConvertUtils.getInstance().register(new DateConverter("yyyy MM"), Date.class)
        initialize ([modelClass, relatedModelClass], [], false);
        def relatedModelInstance1 = relatedModelClass.'add'(prop1:"prop1", prop2:11);
        def relatedModelInstance2 = relatedModelClass.'add'(prop1:"prop2", prop2:17);
        def modelInstance = modelClass.'add'(prop1:"prop1", prop2:15, rel1:[relatedModelInstance1, relatedModelInstance2]);

        def sw = new StringWriter();
        def markupBuilder = new MarkupBuilder(sw);
        markupBuilder.UiElement{
            DesignerUtils.generateXml([modelInstance], markupBuilder, metaDataGetter);
        }
        def parser = new XmlParser().parseText(sw.toString());
        assertEquals(1, parser.UiElement.size());
        assertEquals(4, parser.UiElement[0].attributes().size());

        assertEquals (String.valueOf(modelInstance.id), parser.UiElement.'@id'.text());
        assertEquals ("Model1", parser.UiElement.'@designerType'.text());
        assertEquals (modelInstance.prop1, parser.UiElement.'@prop1'.text());
        assertEquals (String.valueOf(modelInstance.prop2), parser.UiElement.'@prop2'.text());
        
        def relatedModels = parser.UiElement.UiElement
        assertEquals(2, relatedModels.size());
        assertEquals(4, relatedModels[0].attributes().size())
        assertEquals(4, relatedModels[1].attributes().size())
        def relatedInstance1XmlNode = relatedModels.find {it.attributes().id==String.valueOf(relatedModelInstance1.id)}
        def relatedInstance2XmlNode = relatedModels.find {it.attributes().id==String.valueOf(relatedModelInstance2.id)}

        assertEquals (String.valueOf(relatedModelInstance1.id), relatedInstance1XmlNode.attributes().id);
        assertEquals ("Model2", relatedInstance1XmlNode.attributes().designerType);
        assertEquals (relatedModelInstance1.prop1, relatedInstance1XmlNode.attributes().prop1);
        assertEquals (String.valueOf(relatedModelInstance1.prop2), relatedInstance1XmlNode.attributes().prop2);

        assertEquals (String.valueOf(relatedModelInstance2.id), relatedInstance2XmlNode.attributes().id);
        assertEquals ("Model2", relatedInstance2XmlNode.attributes().designerType);
        assertEquals (relatedModelInstance2.prop1, relatedInstance2XmlNode.attributes().prop1);
        assertEquals (String.valueOf(relatedModelInstance2.prop2), relatedInstance2XmlNode.attributes().prop2);
    }

    public void testGenerateXmlWithOneToOneRelation()
    {
        def modelClasses = createDomainClassesForRelationTest(ModelGenerator.RELATION_TYPE_ONE, ModelGenerator.RELATION_TYPE_ONE);
        def modelClass = modelClasses.findAll {it.name == "UiModel1"}[0];
        def relatedModelClass = modelClasses.findAll {it.name == "UiModel2"}[0];
        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        modelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model1",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[[designerType:"Model2", isMultiple:true, propertyName:"rel1"]]
            ];
        };

        relatedModelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model2",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[]
            ];
        };
        RapidConvertUtils.getInstance().register(new DateConverter("yyyy MM"), Date.class)
        initialize ([modelClass, relatedModelClass], [], false);
        def relatedModelInstance1 = relatedModelClass.'add'(prop1:"prop1", prop2:11);
        def relatedModelInstance2 = relatedModelClass.'add'(prop1:"prop2", prop2:17);
        def modelInstance = modelClass.'add'(prop1:"prop1", prop2:15, rel1:[relatedModelInstance1, relatedModelInstance2]);

        def sw = new StringWriter();
        def markupBuilder = new MarkupBuilder(sw);
        markupBuilder.UiElement{
            DesignerUtils.generateXml([modelInstance], markupBuilder, metaDataGetter);
        }
        def parser = new XmlParser().parseText(sw.toString());
        assertEquals(1, parser.UiElement.size());
        assertEquals(4, parser.UiElement[0].attributes().size());
        def relatedModels = parser.UiElement.UiElement
        assertEquals(1, relatedModels.size());
        def relatedInstance1XmlNode = relatedModels[0]

        assertEquals (String.valueOf(relatedModelInstance1.id), relatedInstance1XmlNode.attributes().id);
    }

    public void testGenerateXmlWithRelatedClassWithVisibleClosure()
    {
        def modelClasses = createDomainClassesForRelationTest(ModelGenerator.RELATION_TYPE_MANY, ModelGenerator.RELATION_TYPE_MANY);
        def modelClass = modelClasses.findAll {it.name == "UiModel1"}[0];
        def relatedModelClass = modelClasses.findAll {it.name == "UiModel2"}[0];
        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        modelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model1",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[[designerType:"Model2", isMultiple:true, propertyName:"rel1", isVisible:{object-> return object.prop1 == "prop1"}]]
            ];
        };

        relatedModelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model2",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[]
            ];
        };
        RapidConvertUtils.getInstance().register(new DateConverter("yyyy MM"), Date.class)
        initialize ([modelClass, relatedModelClass], [], false);
        def relatedModelInstance1 = relatedModelClass.'add'(prop1:"prop1", prop2:11);
        def relatedModelInstance2 = relatedModelClass.'add'(prop1:"prop2", prop2:17);
        def modelInstance = modelClass.'add'(prop1:"prop1", prop2:15, rel1:[relatedModelInstance1, relatedModelInstance2]);

        def sw = new StringWriter();
        def markupBuilder = new MarkupBuilder(sw);
        markupBuilder.UiElement{
            DesignerUtils.generateXml([modelInstance], markupBuilder, metaDataGetter);
        }
        def parser = new XmlParser().parseText(sw.toString());
        assertEquals(1, parser.UiElement.size());
        assertEquals(4, parser.UiElement[0].attributes().size());
        def relatedModels = parser.UiElement.UiElement
        assertEquals(1, relatedModels.size());
        assertEquals(4, relatedModels[0].attributes().size())
        def relatedInstance1XmlNode = relatedModels[0];

        assertEquals (String.valueOf(relatedModelInstance1.id), relatedInstance1XmlNode.attributes().id);
    }

    public void testGenerateXmlWithRelatedClassWithGrouping()
    {
        def modelClasses = createDomainClassesForRelationTest(ModelGenerator.RELATION_TYPE_MANY, ModelGenerator.RELATION_TYPE_MANY);
        def modelClass = modelClasses.findAll {it.name == "UiModel1"}[0];
        def relatedModelClass = modelClasses.findAll {it.name == "UiModel2"}[0];
        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        modelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model1",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[
                    [
                                designerType: "AGroup",
                                isMultiple: false,
                                metaData: [
                                        designerType: "AGroup",
                                        propertyConfiguration:
                                        [
                                            aDefaultProperty:[formatter:{object->return "defaultValue"}]
                                        ],
                                        childrenConfiguration: [
                                                [designerType:"Model2", isMultiple:true, propertyName:"rel1", isVisible:{object-> return object.prop1 == "prop1"}]
                                        ]
                                ]
                    ]
                ]
            ];
        };

        relatedModelClass.metaClass.'static'.metaData = {
            return [
                designerType: "Model2",
                propertyConfiguration: [
                        prop1: [descr: ''],
                        prop2: [descr: '']
                ],
                childrenConfiguration:[]
            ];
        };
        RapidConvertUtils.getInstance().register(new DateConverter("yyyy MM"), Date.class)
        initialize ([modelClass, relatedModelClass], [], false);
        def relatedModelInstance1 = relatedModelClass.'add'(prop1:"prop1", prop2:11);
        def relatedModelInstance2 = relatedModelClass.'add'(prop1:"prop2", prop2:17);
        def modelInstance = modelClass.'add'(prop1:"prop1", prop2:15, rel1:[relatedModelInstance1, relatedModelInstance2]);

        def sw = new StringWriter();
        def markupBuilder = new MarkupBuilder(sw);
        markupBuilder.UiElement{
            DesignerUtils.generateXml([modelInstance], markupBuilder, metaDataGetter);
        }
        def parser = new XmlParser().parseText(sw.toString());
        assertEquals(1, parser.UiElement.size());
        assertEquals(4, parser.UiElement[0].attributes().size());
        def groupNode = parser.UiElement.UiElement
        assertEquals(1, groupNode.size());
        assertEquals("AGroup", groupNode.'@designerType'.text());
        assertEquals("autoGenerated1", groupNode.'@id'.text());
        assertEquals("defaultValue", groupNode.'@aDefaultProperty'.text());
        def relatedModels = parser.UiElement.UiElement.UiElement
        assertEquals(1, relatedModels.size());
        assertEquals(4, relatedModels[0].attributes().size())
        def relatedInstance1XmlNode = relatedModels[0];

        assertEquals (String.valueOf(relatedModelInstance1.id), relatedInstance1XmlNode.attributes().id);
    }




    private List createDomainClassesForRelationTest(relType, revRelType)
    {
        def modelName = "UiModel1";
        def relatedModelName = "UiModel2";

        //All properties will be configured as metadata
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE];
        def prop2 = [name:"prop2", type:ModelGenerator.NUMBER_TYPE];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:relType, reverseCardinality:revRelType, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName, cardinality:revRelType, reverseCardinality:relType, isOwner:false];

        def modelMetaProps = [name:modelName]
        def relatedModelMetaProps = [name:relatedModelName]

        def modelProps = [prop1, prop2];
        def keyPropList = [prop1];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [rel1])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1])
        gcl.parseClass (modelString+relatedModelString);
        return gcl.getLoadedClasses();        
    }
}