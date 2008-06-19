package com.ifountain.rcmdb.util
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 18, 2008
 * Time: 6:21:25 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelUtilsTest extends GroovyTestCase{
    def base_directory = "../testOutput";
    public void testDeleteModelArtefacts()
    {
        def modelName = "ModelUtilsModel1";
        new File("${base_directory}/operations").mkdirs();
        new File("${base_directory}/grails-app/views/${modelName}").mkdirs();
        new File("${base_directory}/grails-app/controllers").mkdirs();
        new File("${base_directory}/grails-app/domain").mkdirs();

        new File("${base_directory}/grails-app/domain/${modelName}.groovy").createNewFile();
        new File("${base_directory}/grails-app/controllers/${modelName}Controller.groovy").createNewFile();
        new File("${base_directory}/grails-app/views/${modelName}/add.groovy").createNewFile()
        new File("${base_directory}/grails-app/views/${modelName}/edit.groovy").createNewFile()
        new File("${base_directory}/grails-app/views/${modelName}/list.groovy").createNewFile()
        new File("${base_directory}/grails-app/views/${modelName}/show.groovy").createNewFile()
        new File("${base_directory}/grails-app/views/${modelName}/addTo.groovy").createNewFile()
        new File("${base_directory}/operations/${modelName}Operations.groovy").createNewFile();

        ModelUtils.deleteModelArtefacts(base_directory, modelName);
        assertFalse (new File("${base_directory}/grails-app/domain/${modelName}.groovy").exists());
        assertFalse (new File("${base_directory}/grails-app/controllers/${modelName}Controller.groovy").exists());
        assertFalse (new File("${base_directory}/grails-app/views/${modelName}/add.groovy").exists());
        assertFalse (new File("${base_directory}/grails-app/views/${modelName}/edit.groovy").exists());
        assertFalse (new File("${base_directory}/grails-app/views/${modelName}/list.groovy").exists());
        assertFalse (new File("${base_directory}/grails-app/views/${modelName}/show.groovy").exists());
        assertFalse (new File("${base_directory}/grails-app/views/${modelName}/addTo.groovy").exists());
        assertFalse (new File("${base_directory}/operations/${modelName}Operations.groovy").exists());
    }

    public void testGenerateModelArtefacts()
    {
        fail("IMPLEMENT");
        /*def model = new MockModel(name:"ModelUtilsModel1");
        addMasterDatasource(model);

        ModelGenerator.getInstance().generateModel (model);

        GrailsAwareClassLoader classLoader = new GrailsAwareClassLoader();
        classLoader.addClasspath ("${base_directory}/${ModelGenerator.MODEL_FILE_DIR}");
        classLoader.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);

        def modelClass = classLoader.loadClass (model.name);
        def grailsDomainClass = new DefaultGrailsDomainClass(modelClass);
        ModelUtils.generateModelArtefacts (grailsDomainClass, base_directory);

        assertTrue (new File("${base_directory}/grails-app/controllers/${model.name}Controller.groovy").exists());
        assertTrue (new File("${base_directory}/grails-app/views/${model.name}/add.groovy").exists());
        assertTrue (new File("${base_directory}/grails-app/views/${model.name}/edit.groovy").exists());
        assertTrue (new File("${base_directory}/grails-app/views/${model.name}/list.groovy").exists());
        assertTrue (new File("${base_directory}/grails-app/views/${model.name}/show.groovy").exists());
        assertTrue (new File("${base_directory}/grails-app/views/${model.name}/addTo.groovy").exists());
        assertTrue (new File("${base_directory}/operations/${model.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());    */
    }
}