package model

import org.codehaus.groovy.grails.commons.GrailsApplication
import com.ifountain.domain.ModelGenerationException
import org.apache.commons.io.FileUtils
import datasource.BaseDatasource

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
class ModelControllerIntegrationTest extends GroovyTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        deleteModelFiles("Model1");
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        deleteModelFiles("Model1");
    }

    def deleteModelFiles(modelName)
    {
        new File(System.getProperty("base.dir") + "/grails-app/domain/${modelName}.groovy").delete();
        new File(System.getProperty("base.dir") + "/grails-app/controllers/${modelName}Controller.groovy").delete();
        FileUtils.deleteDirectory(new File(System.getProperty("base.dir") + "/grails-app/views/${modelName}"));
    }


    void testGenerateModel() {
        def datasource = new BaseDatasource(name:"ds1");
        datasource.save();
        def model = new Model(name:"Model1");
        model.save();
        model.addToDatasources(new ModelDatasource(datasource:datasource, master:true));
        model.save();
        model.addToModelProperties(new ModelProperty(name:"prop1",type:ModelProperty.stringType, defaultValue:"defaultValue"));
        model.save();
        println Model.findByName("Model1").datasources;
        def mdc = new ModelController();
        mdc.params["id"] = model.id;
        mdc.generate();
        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
        assertNotNull(getClass().getClassLoader().loadClass (model.name));
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
}