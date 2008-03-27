package model

import groovy.mock.interceptor.StubFor
import datasource.BaseDatasource;
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
 * Date: Mar 27, 2008
 * Time: 9:42:41 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelDatasourceControllerTest extends GroovyTestCase{
    def params
    def redirectParams
    def renderParams
    def flash
    def session
    void setUp(){

        session = [ : ]
        ModelDatasourceController.metaClass.getSession = { -> session }

        params = [ : ]
        ModelDatasourceController.metaClass.getParams = { -> params }

        redirectParams = [ : ]
        ModelDatasourceController.metaClass.redirect = { Map args -> redirectParams = args  }

        renderParams = [ : ]
        ModelDatasourceController.metaClass.render = { Map args -> renderParams = args  }

        flash = [ : ]
        ModelDatasourceController.metaClass.getFlash = { -> flash }

        def logger = new Expando( debug: { println it }, info: { println it },
                                  warn: { println it }, error: { println it } )
        ModelDatasourceController.metaClass.getLog = { -> logger }
    }

    void tearDown(){
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
        remove ModelDatasourceController;
    }

    void testSaveWithSaveIsNotSuccessful() {
       def mockModelDatasource = new StubFor(ModelDatasource);
       mockModelDatasource.demand.hasErrors{
           return false;
       }
       mockModelDatasource.demand.save{
           return false;
       }

        def mdc = new ModelDatasourceController();
        mockModelDatasource.use{
            mdc.save();
            assertEquals("create", renderParams.view);
            assertTrue(renderParams.model.modelDatasource instanceof ModelDatasource);    
        }

    }

//     void testSuccessfulSave() {
//        def model = new Model(name: "Customer");
//        def datasource = new BaseDatasource(name: "RCMDB");
//        def mdc = new ModelDatasourceController();
//
//        def mockModelDatasource = new StubFor(ModelDatasource);
//        mockModelDatasource.demand.hasErrors{
//           return false;
//        }
//        mockModelDatasource.demand.save{
//           return true;
//        }
//        def modelId = 1;
//        params["datasource.id"] = 1;
//        params["model.id"] = 1;
//        params["master"] = "true";
//
//        mockModelDatasource.use{
//            mdc.save();
//
//        }
//        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
//        assertEquals("ModelDatasource ${modelDatasource.id} created", mdc.flash.message);
//    }
}