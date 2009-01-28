package com.ifountain.rui.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.method.GetPropertiesMethod

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 27, 2009
* Time: 4:32:18 PM
* To change this template use File | Settings | File Templates.
*/
class DesignerUtilsTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.enableGlobally();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.disableGlobally();
    }


    public void testAddConfigurationParametersFromModel()
    {

        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        def childModelName = "UiModel1";
        def relatedModelName = "UiModel2";

        //All properties and only oneToOne relations will be configured as metadata
        //So rel1 should be ignored since it is many-to-many
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
        def model1Prop1ConfigurationProps = [descr:"desc1"];
        def model1Prop6ConfigurationProps = [descr:"desc2", type:"List", inList:"x,y,z", required:true];
        def metaDataConfiguration  = [
                "prop1":model1Prop1ConfigurationProps,
                "prop6":model1Prop6ConfigurationProps
        ]
        DefaultGrailsDomainClass grailsDomainClass = new DefaultGrailsDomainClass(model1Class);
        GetPropertiesMethod method = new GetPropertiesMethod(grailsDomainClass);
        model1Class.metaClass.'static'.getPropertiesList = {
            return method.getDomainObjectProperties();
        }

        def uiPropertyMetaDatasForUiModel1 = DesignerUtils.addConfigurationParametersFromModel(metaDataConfiguration, grailsDomainClass).values().sort {it.name};
        List propertyListForUiModel1 = model1Class.'getPropertiesList'().findAll {it.name != "id" && (!it.isRelation || it.isOneToOne())};
        assertEquals (propertyListForUiModel1.size(), uiPropertyMetaDatasForUiModel1.size());
        for(int i=0; i < propertyListForUiModel1.size(); i++){
            def prop = propertyListForUiModel1[i];
            assertEquals (prop.name, uiPropertyMetaDatasForUiModel1[i].name);
        }
        assertEquals ("String", uiPropertyMetaDatasForUiModel1[0].type);
        assertEquals ("Number", uiPropertyMetaDatasForUiModel1[1].type);
        assertEquals ("Boolean", uiPropertyMetaDatasForUiModel1[2].type);
        assertEquals ("Date", uiPropertyMetaDatasForUiModel1[3].type);
        assertEquals ("Float", uiPropertyMetaDatasForUiModel1[4].type);
        assertEquals ("List", uiPropertyMetaDatasForUiModel1[5].type);
        assertEquals ("String", uiPropertyMetaDatasForUiModel1[6].type);

        //prop1 is key prop and it could not be null or blank so it is a required property
        assertEquals (true, uiPropertyMetaDatasForUiModel1[0].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[1].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[2].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[3].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[4].required);
        //prop6 is specified as required property
        assertEquals (true, uiPropertyMetaDatasForUiModel1[5].required);
        assertEquals (false, uiPropertyMetaDatasForUiModel1[6].required);


        assertEquals ("desc1", uiPropertyMetaDatasForUiModel1[0].descr);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[1].descr);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[2].descr);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[3].descr);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[4].descr);
        assertEquals ("desc2", uiPropertyMetaDatasForUiModel1[5].descr);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[6].descr);

        assertEquals ("", uiPropertyMetaDatasForUiModel1[0].inList);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[1].inList);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[2].inList);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[3].inList);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[4].inList);
        assertEquals ("x,y,z", uiPropertyMetaDatasForUiModel1[5].inList);
        assertEquals ("", uiPropertyMetaDatasForUiModel1[6].inList);
    }
}