package com.ifountain.rcmdb.domain.generation

import model.*
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 26, 2008
* Time: 6:00:25 PM
* To change this template use File | Settings | File Templates.
*/
class ModelUtilsTest extends GroovyTestCase{
    def static base_directory = "../testoutput/";
    def  modelRelations;
    def  reverseModelRelations;
    def  childModels;
    def findAllByModelMethod;
    def findAllByFirstModelMethod;
    def findAllBySecondModelMethod;
    protected void setUp() {
        super.setUp();
        if(new File(System.getProperty("base.dir")?System.getProperty("base.dir"):".").getAbsolutePath().endsWith("RapidCMDB"))
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, System.getProperty("base.dir"));
        }
        else
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, "RapidCMDB");
        }
        FileUtils.deleteDirectory (new File(base_directory));
        new File(base_directory).mkdirs();
        modelRelations = [:];
        reverseModelRelations = [:];
        childModels = [:];
        findAllByModelMethod = Model.metaClass.'static'.&findAllByParentModel;
        findAllByFirstModelMethod = ModelRelation.metaClass.'static'.&findAllByFirstModel;
        findAllBySecondModelMethod = ModelRelation.metaClass.'static'.&findAllBySecondModel;
        Model.metaClass.'static'.findAllByParentModel = {Model model->
            return childModels[model.name];
        }
        ModelRelation.metaClass.'static'.findAllBySecondModel = {Model model->
            return reverseModelRelations[model.name];
        }
        ModelRelation.metaClass.'static'.findAllByFirstModel = {Model model->
            return modelRelations[model.name];
        }
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        Model.metaClass.'static'.findByModelMethod = findAllByModelMethod;
        Model.metaClass.'static'.findByFirstModelMethod = findAllByFirstModelMethod;
        Model.metaClass.'static'.findBySecondModelMethod = findAllBySecondModelMethod;
    }

    public void testGetAllDependentClasses()
    {
        def rootModel = new MockModel(name:"ClassRoot");
        def parentModel = new MockModel(name:"Class2", parentModel:rootModel);
        def childModel = new MockModel(name:"Class1", parentModel:parentModel);

        def relatedModel = new MockModel(name:"Class3");
        def childRelatedModel = new MockModel(name:"Class4", parentModel:relatedModel);

        ModelRelation relation1 = new ModelRelation(firstName:"relation1", secondName:"reverseRelation1", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE, firstModel:parentModel, secondModel:relatedModel);

        parentModel.fromRelations += relation1;
        relatedModel.toRelations += relation1;

        childModels[parentModel.name] = [childModel];
        childModels[relatedModel.name] = [childRelatedModel];
        modelRelations[parentModel.name] = [relation1];
        reverseModelRelations[relatedModel.name] = [relation1];

        def dependentModels = ModelUtils.getAllDependentModels (parentModel);
        assertSame(rootModel, dependentModels.get(rootModel.name))
        assertSame(parentModel, dependentModels.get(parentModel.name))
        assertSame(childModel, dependentModels.get(childModel.name))
        assertSame(relatedModel, dependentModels.get(relatedModel.name))
        assertSame(childRelatedModel, dependentModels.get(childRelatedModel.name))
        reverseModelRelations = [:]
        childModels = [:]
        modelRelations = [:]
        parentModel.parentModel = null;
        dependentModels = ModelUtils.getAllDependentModels (parentModel);
        assertEquals (1, dependentModels.size());
        assertSame(parentModel, dependentModels.get(parentModel.name));
    }


    public void testGetAllDependeeClasses()
    {
        def parentModel = new MockModel(name:"Class2");
        def childModel = new MockModel(name:"Class1", parentModel:parentModel);

        def relatedModel = new MockModel(name:"Class3");
        def childRelatedModel = new MockModel(name:"Class4", parentModel:relatedModel);
        ModelRelation relation1 = new ModelRelation(firstName:"relation1", secondName:"reverseRelation1", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE, firstModel:parentModel, secondModel:relatedModel);

        parentModel.fromRelations += relation1;
        relatedModel.toRelations += relation1;

        childModels[parentModel.name] = [childModel];
        childModels[relatedModel.name] = [childRelatedModel];
        modelRelations[parentModel.name] = [relation1];
        reverseModelRelations[relatedModel.name] = [relation1];

        def dependeeModels = ModelUtils.getDependeeModels(parentModel);
        assertSame(childModel, dependeeModels.get(childModel.name))
        assertSame(relatedModel, dependeeModels.get(relatedModel.name))
        reverseModelRelations = [:]
        childModels = [:]
        modelRelations = [:]
        parentModel.parentModel = null;
        dependeeModels = ModelUtils.getDependeeModels (parentModel);
        assertEquals (0, dependeeModels.size());
    }

    private def addMasterDatasource(Model model)
    {
        def datasource1 = new DatasourceName(name:"ds1-sample");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, master:true, model:model);
        model.datasources += modelDatasource1;
        def keyProp = new ModelProperty(name:"keyprop", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:false);
        model.modelProperties += keyProp;
        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp, datasource:modelDatasource1, nameInDatasource:"keypropname");
    }
}
