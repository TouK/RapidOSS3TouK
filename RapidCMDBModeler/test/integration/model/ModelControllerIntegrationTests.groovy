package model

import com.ifountain.rcmdb.domain.generation.ModelGenerationException
import com.ifountain.rcmdb.domain.generation.ModelGenerationUtils
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerationUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

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
class ModelControllerIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;
    String modelName;
    String modelName2;
    String generatedModelDir;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        modelName = "Model1";
        modelName2 = "Model2";
        def lastException = null;
        boolean deletedAllModels = true;

        for(int i=0; i < 100; i++)
        {
            deletedAllModels = true;
            Model.list().each{
                try
                {
                    it.delete(flush:true);
                }catch(t)
                {
                    lastException = t;
                    deletedAllModels = false;
                }
            }
            if(deletedAllModels) break;
        }
        if(!deletedAllModels) throw lastException;
        DatasourceName.list()*.delete(flush: true);
        generatedModelDir = ConfigurationHolder.config.toProperties()["rapidCMDB.temp.dir"];
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }



    void testDeleteModel() {
        fail("Failing while running with alltests");
        Model model = createSimpleModel(modelName);
        def modelFile = new File(generatedModelDir+ "/grails-app/domain/${model.name}.groovy");
        def mdc = new ModelController();
        mdc.generate();
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertNull(mdc.flash.errors);
        assertTrue(modelFile.exists());

        IntegrationTestUtils.resetController(mdc);
        mdc.params["id"] = model.id;
        mdc.delete();
        assertNull(Model.get(model.id));
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertFalse(modelFile.exists());

        IntegrationTestUtils.resetController(mdc);
        mdc.params["id"] = model.id;
        mdc.delete();
        assertEquals(mdc.flash.message, ModelController.MODEL_DOESNOT_EXIST, mdc.flash.message);

        IntegrationTestUtils.resetController(mdc);
        mdc.delete();
        assertEquals("/model/list", mdc.response.redirectedUrl);
    }

    public void testReturnsErrorIfModelHasChildModels() {
        fail("Failing while running with alltests");
        Model parentModel = createSimpleModel(modelName);
        Model childModel = createSimpleModel(modelName2);
        childModel.parentModel = parentModel;
        childModel = childModel.save(flush:true);
        def mdc = new ModelController();
        mdc.params["id"] = parentModel.id;
        mdc.delete();

        assertNotNull(Model.findByName(parentModel.name));
        assertEquals("/model/show/" + parentModel.id, mdc.response.redirectedUrl);
        assertEquals(1, mdc.flash.errors.size());
    }


    void testGenerateModel() {
        fail("Failing while running with alltests");
        Model model = createSimpleModel(modelName);
        def mdc = new ModelController();
        mdc.generate();
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertTrue(new File(generatedModelDir + "/grails-app/domain/${model.name}.groovy").exists());
    }

    void testPrintsExceptionIfMasterDatasourceDoesnotExist() {
        fail("Failing while running with alltests");
        def model = new Model(name: "Model1");
        model.save();

        def mdc = new ModelController();
        mdc.generate();
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertEquals(ModelGenerationException.masterDatasourceDoesnotExists(model.name).getMessage(), mdc.flash.message);
    }

    private Model createSimpleModel(String modelName) {
        def datasourceName = new DatasourceName(name: "${modelName}ds1");
        datasourceName.save(flush:true);
        def model = new Model(name: modelName);
        model.save(flush:true);
        def modelDs = new ModelDatasource(datasource: datasourceName, master: true, model: model)
        modelDs.save(flush:true);
        modelDs.refresh();
        def modelProp = new ModelProperty(name: "prop1", type: ModelProperty.stringType, defaultValue: "defaultValue", datasource: modelDs, model: model)
        modelProp.save(flush:true);
        modelProp.refresh();
        def keyMapping = new ModelDatasourceKeyMapping(property: modelProp, nameInDatasource: "Prop1", datasource:modelDs)
        keyMapping.save(flush:true);
        modelDs.addToKeyMappings(keyMapping);
        model.refresh();
        println "DSS:"+model.datasources.keyMappings
        return model;
    }
}