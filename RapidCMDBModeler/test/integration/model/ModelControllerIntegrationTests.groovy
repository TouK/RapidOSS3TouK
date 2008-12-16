package model

import com.ifountain.rcmdb.domain.generation.ModelGenerationException
import com.ifountain.rcmdb.domain.generation.ModelGenerationUtils
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerationUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.util.RapidCMDBConstants

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
                    it.remove();
                }catch(t)
                {
                    lastException = t;
                    deletedAllModels = false;
                }
            }
            if(deletedAllModels) break;
        }
        if(!deletedAllModels) throw lastException;
        DatasourceName.list()*.remove();
        generatedModelDir = ConfigurationHolder.config.toProperties()["rapidCMDB.temp.dir"];
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }



    void testDeleteModel() {
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

    void testGenerateModel() {
        Model model = createSimpleModel(modelName);
        def mdc = new ModelController();
        mdc.generate();
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertTrue(String.valueOf(mdc.flash.message), new File(generatedModelDir + "/grails-app/domain/${model.name}.groovy").exists());
    }

    void testPrintsExceptionIfMasterDatasourceDoesnotExist() {
        def model = Model.add(name: "Model1");

        def mdc = new ModelController();
        mdc.generate();
        assertEquals("/model/list", mdc.response.redirectedUrl);
        assertEquals(ModelGenerationException.masterDatasourceDoesnotExists(model.name).getMessage(), mdc.flash.message);
    }

    private Model createSimpleModel(String modelName) {
        def datasourceName = DatasourceName.add(name: RapidCMDBConstants.RCMDB);
        def model = Model.add(name: modelName);
        def modelDs = ModelDatasource.add(datasource: datasourceName, model: model)
        def modelProp = ModelProperty.add(name: "prop1", type: ModelProperty.stringType, defaultValue: "defaultValue", datasource: modelDs, model: model)
        def keyMapping = ModelDatasourceKeyMapping.add(property: modelProp, nameInDatasource: "Prop1", datasource:modelDs)
        modelDs.addRelation(keyMappings:keyMapping);
        return Model.get(name:modelName);
    }
}