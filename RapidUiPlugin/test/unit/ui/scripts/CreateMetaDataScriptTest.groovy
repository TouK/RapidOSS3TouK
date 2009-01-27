package ui.scripts

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import ui.designer.UiAction
import ui.designer.UiMetaData
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 26, 2009
* Time: 3:17:31 PM
* To change this template use File | Settings | File Templates.
*/
class CreateMetaDataScriptTest extends RapidCmdbWithCompassTestCase
{
    public void testRun()
    {

        //Create ui domain objects they should be located under package ui.designer and they should start with Ui
        def childModelName = "UiModel1";
        def relatedModelName = "UiModel2";
        def nonUiModelName1 = "Model3";
        def nonUiModelName2 = "Model4";

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
        def nonUiModelMetaProps1 = [name:nonUiModelName1]
        def nonUiModelMetaProps2 = [name:nonUiModelName2]

        def modelProps = [prop1, prop2, prop3, prop4, prop5, prop6];
        def keyPropList = [prop1];
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, modelProps, keyPropList, [rel1, rel2])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1, revrel2])
        String nonUiModelString1 = ModelGenerationTestUtils.getModelText(nonUiModelMetaProps1, modelProps, keyPropList, [])
        String nonUiModelString2 = ModelGenerationTestUtils.getModelText(nonUiModelMetaProps2, modelProps, keyPropList, [])
        gcl.parseClass ("package "+ UiMetaData.getPackage().name + ";"+childModelString+relatedModelString+nonUiModelString1);
        gcl.parseClass (nonUiModelString2);

        def domainObjectClasses = [UiMetaData];
        gcl.getLoadedClasses().each{domainObjectClasses.add(it)};
        initialize (domainObjectClasses, []);
        gcl.addClasspath (System.getProperty("base.dir", ".")+"/scripts");
        Class createMetaDataScriptClass = gcl.loadClass ("createMetaData");

        //Configured values will overwrite values defined in model. If no configuration exist description field be left empty 
        def model1Prop1ConfigurationProps = [description:"desc1"];
        def model1Prop6ConfigurationProps = [description:"desc2", type:"List", inList:"x,y,z", required:true];
        Script createMetaDataScript = createMetaDataScriptClass.newInstance();


        def metaDataConfiguration  = [
                "Model1.prop1":model1Prop1ConfigurationProps,
                "Model1.prop6":model1Prop6ConfigurationProps
        ]
        createMetaDataScript.createMetaData(metaDataConfiguration);
        
        def uiModel1 = ApplicationHolder.getApplication().getDomainClass("${UiMetaData.getPackage().name}.${childModelName}".toString()).clazz;
        def uiModel2 = ApplicationHolder.getApplication().getDomainClass("${UiMetaData.getPackage().name}.${relatedModelName}".toString()).clazz;

        List propertyListForUiModel1 = uiModel1.'getPropertiesList'().findAll {it.name != "id" && (!it.isRelation || it.isOneToOne())};
        List uiMetaDatasForUiModel1 = UiMetaData.search("componentName:Model1", sort:"name").results;
        assertEquals (propertyListForUiModel1.size(), uiMetaDatasForUiModel1.size());
        for(int i=0; i < propertyListForUiModel1.size(); i++){
            def prop = propertyListForUiModel1[i];
            assertEquals (prop.name, uiMetaDatasForUiModel1[i].name);
            assertEquals ("Model1", uiMetaDatasForUiModel1[i].componentName);
        }

        List propertyListForUiModel2 = uiModel2.'getPropertiesList'().findAll {it.name != "id" && (!it.isRelation || it.isOneToOne())};
        List uiMetaDatasForUiModel2 = UiMetaData.search("componentName:Model2", sort:"name").results;
        assertEquals (propertyListForUiModel2.size(), uiMetaDatasForUiModel2.size());
        for(int i=0; i < propertyListForUiModel2.size(); i++){
            def prop = propertyListForUiModel2[i];
            assertEquals (prop.name, uiMetaDatasForUiModel2[i].name);
            assertEquals ("Model2", uiMetaDatasForUiModel2[i].componentName);
        }

        assertEquals ("String", uiMetaDatasForUiModel1[0].type);
        assertEquals ("Number", uiMetaDatasForUiModel1[1].type);
        assertEquals ("Boolean", uiMetaDatasForUiModel1[2].type);
        assertEquals ("Date", uiMetaDatasForUiModel1[3].type);
        assertEquals ("Float", uiMetaDatasForUiModel1[4].type);
        assertEquals ("List", uiMetaDatasForUiModel1[5].type);
        assertEquals ("String", uiMetaDatasForUiModel1[6].type);

        assertEquals (true, uiMetaDatasForUiModel1[0].required);
        assertEquals (false, uiMetaDatasForUiModel1[1].required);
        assertEquals (false, uiMetaDatasForUiModel1[2].required);
        assertEquals (false, uiMetaDatasForUiModel1[3].required);
        assertEquals (false, uiMetaDatasForUiModel1[4].required);
        assertEquals (true, uiMetaDatasForUiModel1[5].required);
        assertEquals (false, uiMetaDatasForUiModel1[6].required);


        assertEquals ("desc1", uiMetaDatasForUiModel1[0].description);
        assertEquals ("", uiMetaDatasForUiModel1[1].description);
        assertEquals ("", uiMetaDatasForUiModel1[2].description);
        assertEquals ("", uiMetaDatasForUiModel1[3].description);
        assertEquals ("", uiMetaDatasForUiModel1[4].description);
        assertEquals ("desc2", uiMetaDatasForUiModel1[5].description);
        assertEquals ("", uiMetaDatasForUiModel1[6].description);

        assertEquals ("", uiMetaDatasForUiModel1[0].inList);
        assertEquals ("", uiMetaDatasForUiModel1[1].inList);
        assertEquals ("", uiMetaDatasForUiModel1[2].inList);
        assertEquals ("", uiMetaDatasForUiModel1[3].inList);
        assertEquals ("", uiMetaDatasForUiModel1[4].inList);
        assertEquals ("x,y,z", uiMetaDatasForUiModel1[5].inList);
        assertEquals ("", uiMetaDatasForUiModel1[6].inList);
    }
}