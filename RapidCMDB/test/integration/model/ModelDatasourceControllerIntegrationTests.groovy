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
 * User: Sezgin Kucukkaraaslan
 * Date: Mar 25, 2008
 * Time: 9:20:40 AM
 * To change this template use File | Settings | File Templates.
 */

package model

import datasource.BaseDatasource
class ModelDatasourceControllerIntegrationTests extends GroovyTestCase {

    void setUp() {
        super.setUp();
        BaseDatasource.list()*.delete();
        Model.list()*.delete();
        ModelDatasource.list()*.delete();
    }
    void testSaveWithSaveIsNotSuccessful() {
        def mdc = new ModelDatasourceController();
        def renderCalled = false
        def renderParams = [:]
        mdc._render = {params -> renderCalled = true; renderParams = params;};
        mdc.params["undefinedAtt"] = "undefined";
        mdc.save();
        assertTrue(renderCalled);
        assertEquals("create", renderParams.view);
        assertTrue(renderParams.model.modelDatasource instanceof ModelDatasource);
    }


    void testSuccessfulSave() {
        def model = new Model(name: "Customer").save();
        def datasource = new BaseDatasource(name: "RCMDB").save();
        def mdc = new ModelDatasourceController();
        mdc.params["datasource.id"] = datasource.id;
        mdc.params["model.id"] = model.id;
        mdc.params["master"] = "true";
        mdc.save();
        def modelDatasources = ModelDatasource.list();
        assertEquals(1, modelDatasources.size());
        def modelDatasource = modelDatasources[0];
        assertEquals(datasource.id, modelDatasource.datasource.id);
        assertEquals(model.id, modelDatasource.model.id);
        assertTrue(modelDatasource.master);
        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
        assertEquals("ModelDatasource ${modelDatasource.id} created", mdc.flash.message);
    }

    void testDeleteWhenModelDatasourceNotFound(){
        def mdc = new ModelDatasourceController();
        mdc.params["id"] = 5;
        mdc.delete();
        assertEquals("ModelDatasource not found with id 5", mdc.flash.message);
        assertEquals("/modelDatasource/list", mdc.response.redirectedUrl);
    }

    void testSuccessfullDelete(){
        def model = new Model(name: "Customer").save(flush:true);
        def datasource = new BaseDatasource(name: "RCMDB").save();
        def mdc = new ModelDatasourceController();
        mdc.params["datasource.id"] = datasource.id;
        mdc.params["model.id"] = model.id;
        mdc.params["master"] = "true";
        mdc.save();
        def modelDatasources = ModelDatasource.list();
        assertEquals(1, modelDatasources.size());
        def model1 = Model.get(model.id);
        assertEquals(1, Model.get(model.id)?.datasources.size());
        mdc.params.clear();
        def modelDatasourceId = modelDatasources[0].id;
        mdc.params["id"] = modelDatasourceId;
        mdc.delete();
        assertEquals(0, ModelDatasource.list().size());
        assertEquals("ModelDatasource ${modelDatasourceId} deleted", mdc.flash.message);
        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
        assertEquals(0,Model.get(model.id).datasources.size());
    }

}