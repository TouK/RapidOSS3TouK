package model

import datasource.BaseDatasource
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
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelPropertyControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
     void setUp() {
        super.setUp();
        ModelProperty.list()*.delete();
        ModelDatasource.list()*.delete();
        Model.list()*.delete();
        DatasourceName.list()*.delete();
    }

    void testSuccessfulSave(){
        def model = new Model(name: "Customer").save();
        def rcmdb = new DatasourceName(name: "RCMDB").save();
        def ds1 = new DatasourceName(name: "ds1").save();
        def mpc = new ModelPropertyController();
        mpc.params["datasource.id"] = "" + rcmdb.id;
        mpc.params["model.id"] = "" + model.id;
        mpc.params["type"] = ModelProperty.stringType;
        mpc.params["name"] = "prop1";
        mpc.save();
        def modelDatasources = ModelDatasource.list();
        assertEquals(1, modelDatasources.size());
        def rcmdbModelDatasource = modelDatasources[0];
        assertEquals(rcmdb.id, rcmdbModelDatasource.datasource?.id);
        assertEquals(model.id, rcmdbModelDatasource.model?.id);

        def modelProps = ModelProperty.list();
        assertEquals(1, modelProps.size());
        def modelProp = modelProps[0];
        assertEquals("prop1", modelProp.name);
        assertEquals(model.id, modelProp.model?.id);
        assertEquals(rcmdbModelDatasource.id, modelProp.propertyDatasource?.id);
        
        rcmdbModelDatasource.master = true;
        rcmdbModelDatasource.save();

        mpc = new ModelPropertyController();
        resetController(mpc);
        mpc.params["datasource.id"] = "" + ds1.id;
        mpc.params["model.id"] = "" + model.id;
        mpc.params["type"] = ModelProperty.stringType;
        mpc.params["name"] = "prop2";
        mpc.save();

        modelDatasources = ModelDatasource.list();
        assertEquals(2, modelDatasources.size());

        def ds1ModelDatasource = ModelDatasource.findByDatasource(ds1);
        assertEquals(model.id, ds1ModelDatasource.model?.id);

        modelProps = ModelProperty.list();
        assertEquals(2, modelProps.size());
        modelProp = ModelProperty.findByName("prop2");
        
        assertEquals(model.id, modelProp.model?.id);
        assertEquals(ds1ModelDatasource.id, modelProp.propertyDatasource?.id);
    }

    def resetController(controller)
    {
        controller.request.removeAllParameters()
        controller.response.setCommitted(false)
        controller.response.reset()
        controller.flash.message = ""
        controller.params.clear()
    }
}