package application

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import model.Model
import datasource.BaseDatasource
import model.ModelProperty
import model.ModelDatasource
import com.ifountain.rcmdb.domain.ModelGenerator
import com.ifountain.rcmdb.domain.ModelUtils
import model.ModelDatasourceKeyMapping

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 26, 2008
* Time: 2:48:38 AM
* To change this template use File | Settings | File Templates.
*/
class ApplicationControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        Model.list()*.delete(flush:true);
        BaseDatasource.list()*.delete(flush:true);
        System.setProperty (ApplicationController.RESTART_APPLICATION, "false");
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testIfModelMarkedApplicationControllerWillCreateModelResources()
    {
        String model1Name = "ApplicationModel1";
        String model2Name = "ApplicationModel2";
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model1Name);
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model2Name);
        try
        {
            def model1 = new Model(name:model1Name).save(flush:true);
            Model model2 = new Model(name:model2Name).save(flush:true);
            addMasterDatasource(model1);
            addMasterDatasource(model2);
            println model1.datasources.keyMappings
            ModelGenerator.getInstance().generateModel (model1);
            ModelGenerator.getInstance().generateModel (model2);
            model2.resourcesWillBeGenerated = false;
            model2.save(flush:true);

            def controller = new ApplicationController();
            controller.reload();

            assertTrue (new File("${System.getProperty ("base.dir")}/grails-app/controllers/${model1.name}Controller.groovy").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/operations/${model1.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/grails-app/views/${model1.name}/show.gsp").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/grails-app/views/${model1.name}/addTo.gsp").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/grails-app/views/${model1.name}/create.gsp").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/grails-app/views/${model1.name}/edit.gsp").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/grails-app/views/${model1.name}/list.gsp").exists());
            assertFalse (new File("${System.getProperty ("base.dir")}/grails-app/controllers/${model2.name}Controller.groovy").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/operations/${model2.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
            assertFalse (new File("${System.getProperty ("base.dir")}/grails-app/views/${model2.name}").exists());
            assertEquals("/application/reloading", controller.modelAndView.viewName);
            assertEquals("true", System.getProperty(ApplicationController.RESTART_APPLICATION));
        }
        finally
        {
            ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model1Name);
            ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model2Name);
        }
    }
    public void testApplicationControllerWillNotThrowExceptionIfModelFileContainsErrors()
    {
        String model1Name = "ApplicationModel3";
        String model2Name = "ApplicationModel4";
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model1Name);
        ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model2Name);
        try
        {
            def model1 = new Model(name:model1Name).save(flush:true);
            Model model2 = new Model(name:model2Name).save(flush:true);
            addMasterDatasource(model1);
            addMasterDatasource(model2);
            ModelGenerator.getInstance().generateModel (model1);
            model1.getModelFile().setText("\"");
            ModelGenerator.getInstance().generateModel (model2);
            model2.resourcesWillBeGenerated = false;
            model2.save(flush:true);

            def controller = new ApplicationController();
            controller.reload();

            assertFalse (new File("${System.getProperty ("base.dir")}/grails-app/controllers/${model1.name}Controller.groovy").exists());
            assertFalse (new File("${System.getProperty ("base.dir")}/grails-app/views/${model1.name}").exists());

            assertFalse (new File("${System.getProperty ("base.dir")}/grails-app/controllers/${model2.name}Controller.groovy").exists());
            assertTrue (new File("${System.getProperty ("base.dir")}/operations/${model2.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
            assertFalse (new File("${System.getProperty ("base.dir")}/grails-app/views/${model2.name}").exists());
            assertEquals("/application/reloading", controller.modelAndView.viewName);
            assertEquals("true", System.getProperty(ApplicationController.RESTART_APPLICATION));
        }
        finally
        {
            ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model1Name);
            ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model2Name);
        }
    }

    private def addMasterDatasource(Model model)
    {
        def datasource1 = new BaseDatasource(name:"ds1-sample_${model.name}").save(flush:true);
        def modelDatasource1 = new ModelDatasource(datasource:datasource1, master:true, model:model).save(flush:true);
        def keyProp = new ModelProperty(name:"keyprop", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:false,lazy:false).save(flush:true);
        def modelKey = new ModelDatasourceKeyMapping(property:keyProp, datasource:modelDatasource1, nameInDatasource:"keypropname").save(flush:true);
        modelDatasource1.refresh();
        model.refresh();
    }
    
}