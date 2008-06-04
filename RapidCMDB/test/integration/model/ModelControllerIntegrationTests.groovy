package model

import com.ifountain.rcmdb.domain.generation.ModelGenerationException
import com.ifountain.rcmdb.domain.generation.ModelUtils;
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 28, 2008
 * Time: 9:07:45 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
   String modelName;
   String modelName2;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        modelName = "Model1";
        modelName2 = "Model2";
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), modelName);
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), modelName2);
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), modelName);
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), modelName2);
    }



    void testDeleteModel() {
        Model model  = createSimpleModel(modelName);
        def modelFile = new File(System.getProperty("base.dir") + "/grails-app/domain/${model.name}.groovy");
        def modelControllerFile = new File(System.getProperty("base.dir") + "/grails-app/controllers/${model.name}Controller.groovy");
        def modelViewsDir = new File(System.getProperty("base.dir") + "/grails-app/views/${model.name}");

        def mdc = new ModelController();
        mdc.params["id"] = model.id;
        mdc.generate();
        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
        assertTrue(modelFile.exists());

        IntegrationTestUtils.resetController(mdc);
        mdc.params["id"] = model.id;
        mdc.delete();
        assertNull (Model.get(model.id));
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertFalse(modelFile.exists());
        assertFalse(modelControllerFile.exists());
        assertFalse(modelViewsDir.exists());

        IntegrationTestUtils.resetController(mdc);
        mdc.params["id"] = model.id;
        mdc.delete();
        assertEquals(mdc.flash.message, ModelController.MODEL_DOESNOT_EXIST, mdc.flash.message);

        IntegrationTestUtils.resetController(mdc);
        mdc.delete();
        assertEquals("/model/list", mdc.response.redirectedUrl);
    }
    public void testReturnsErrorIfModelHasChildModels() {

        Model parentModel  = createSimpleModel(modelName);
        Model childModel  = createSimpleModel(modelName2);
        childModel.parentModel = parentModel;
        childModel = childModel.save();
        def mdc = new ModelController();
        mdc.params["id"] = parentModel.id;
        mdc.delete();

        assertNotNull(Model.get(parentModel.id));
        assertEquals("/model/show/"+parentModel.id, mdc.response.redirectedUrl);
        assertEquals(1, mdc.flash.errors.size());
        assertEquals("", mdc.flash.message);
    }

    public void testDeleteModelWithDependentModels() {
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), modelName);
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), modelName2);

        Model model1  = createSimpleModel(modelName);
        Model model2  = createSimpleModel(modelName2);
        ModelRelation rel1 = new ModelRelation(firstModel:model1, secondModel:model2, firstName:"rel1", secondName:"revrel1", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE);
        rel1 = rel1.save();
        model1.refresh();
        def mdc = new ModelController();
        mdc.params["id"] = model1.id;
        mdc.delete();

        assertNull (Model.get(model1.id));
        def modelFile = new File(System.getProperty("base.dir") + "/grails-app/domain/${modelName2}.groovy");
        assertTrue (modelFile.exists());
    }

    void testGenerateModel() {
        Model model  = createSimpleModel();
        def mdc = new ModelController();
        mdc.params["id"] = model.id;
        mdc.generate();
        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
        assertTrue(new File(System.getProperty("base.dir") + "/grails-app/domain/${model.name}.groovy").exists());
    }

    void testPrintsExceptionIfMasterDatasourceDoesnotExist() {
        def model = new Model(name:"Model1");
        model.save();

        def mdc = new ModelController();
        mdc.params["id"] = model.id;
        mdc.generate();
        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
        assertEquals(ModelGenerationException.masterDatasourceDoesnotExists(model.name).getMessage(), mdc.flash.message);
    }

    void testGeneratePrintsExceptionIfModelDoesnotExists() {
        def mdc = new ModelController();
        mdc.params["id"] = 1;
        mdc.generate();
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertEquals(mdc.flash.message, ModelController.MODEL_DOESNOT_EXIST, mdc.flash.message);
    }

    void testgenerateRedirectsToListIfModelIdNotSpecified() {
        def mdc = new ModelController();
        mdc.generate();
        assertEquals("/model/list", mdc.response.redirectedUrl);
    }

    private Model createSimpleModel(String modelName)
    {
        def datasourceName = new DatasourceName(name:"ds1");
        datasourceName.save();
        def model = new Model(name:modelName);
        model.save();
        def modelDs = new ModelDatasource(datasource:datasourceName, master:true)
        def modelProp = new ModelProperty(name:"prop1",type:ModelProperty.stringType, defaultValue:"defaultValue", datasource:modelDs)
        model.addToDatasources(modelDs);
        model.addToModelProperties(modelProp);
        model = model.save();
        modelDs.addToKeyMappings(new ModelDatasourceKeyMapping(property:modelProp, nameInDatasource:"Prop1"));
        modelDs.save();
        return  model;
    }
}