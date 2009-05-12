package com.ifountain.rcmdb.domain.fullExportImport

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import application.RsApplicationOperations
import com.ifountain.rcmdb.domain.FullExportImport
import application.RsApplication
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.compass.core.CompassQuery
import org.compass.core.CompassHit
import org.compass.core.CompassHits

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 12, 2009
* Time: 8:55:29 AM
* To change this template use File | Settings | File Templates.
*/
class FullExportImportTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp();
        clearMetaClasses();
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    private void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsApplication)
        GroovySystem.metaClassRegistry.removeMetaClass(RsApplicationOperations)
//        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
//        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript)
//        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScriptOperations)
        ExpandoMetaClass.enableGlobally();
    }

    public void testBackup()
    {
        initialize([RsApplication],[]);


        def callParams=[:];
        RsApplicationOperations.metaClass.static.backup = {String directory ->
            println "backup in test";
            callParams.directory = directory;

        }
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);
        

        def backupPath="testbackup";

        def backupDir=new File(backupPath);
        FileUtils.deleteDirectory(backupDir);
        assertTrue(backupDir.mkdir());

        def indexDir=new File("${backupPath}/index");
        assertTrue(indexDir.mkdir());


        def file1=new File(backupDir.getPath()+"/f1.txt");
        file1.setText("file1");
        assert(file1.exists());

        def file2=new File(indexDir.getPath()+"/f2.txt");
        file2.setText("file2");
        assert(file2.exists());


        def fullExport=new FullExportImport();
        fullExport.backup(backupPath);

        assertEquals(callParams.directory,indexDir.getPath());

        assertFalse(file1.exists());
        assertFalse(file2.exists());

    }
    public void testGenerateModelsToExportWithSelectedModelWithChildsAndRelations()
    {           
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection","application.ObjectId"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"RsTopologyObject"]);

        def fullExport=new FullExportImport();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertFalse(EXPORT_CONFIG.EXPORT_ALL_RELATIONS);

        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;

        def expectedModelList=["RsTopologyObject","RsGroup","RsCustomer","application.ObjectId"];


        assertEquals(expectedModelList.size(),MODELS_TO_EXPORT.size());
        expectedModelList.each{ modelName ->
            assertTrue(MODELS_TO_EXPORT.containsKey(modelName));
            def modelData=MODELS_TO_EXPORT[modelName];
            if(modelName == "application.ObjectId")
            {
                assertEquals(false,modelData.relations);
            }
            else
            {
                assertEquals(true,modelData.relations);
            }
        }
    }
    public void testGenerateModelsToExportWithSelectedModelWithoutChildsAndWitoutRelations()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection","application.ObjectId"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"RsTopologyObject",childModels:false,relations:false]);
        MODELS.add([model:"RsGroup",childModels:false,relations:false]);

        def fullExport=new FullExportImport();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertFalse(EXPORT_CONFIG.EXPORT_ALL_RELATIONS);

        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;

        def expectedModelList=["RsTopologyObject","RsGroup","application.ObjectId"];


        assertEquals(expectedModelList.size(),MODELS_TO_EXPORT.size());
        expectedModelList.each{ modelName ->
            assertTrue(MODELS_TO_EXPORT.containsKey(modelName));
            def modelData=MODELS_TO_EXPORT[modelName];
            if(modelName == "application.ObjectId")
            {
                assertEquals(false,modelData.relations);
            }
            else
            {
                assertEquals(false,modelData.relations);
            }
        }
    }
    public void testGenerateModelsToExportWithSelectedModelsOneWithChildsOtherWithoutChilds()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection","application.ObjectId"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"RsTopologyObject"]);
        MODELS.add([model:"RsEvent",childModels:false,relations:false]);

        def fullExport=new FullExportImport();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertFalse(EXPORT_CONFIG.EXPORT_ALL_RELATIONS);

        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;

        def expectedModelList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","application.ObjectId"];


        assertEquals(expectedModelList.size(),MODELS_TO_EXPORT.size());
        expectedModelList.each{ modelName ->
            assertTrue(MODELS_TO_EXPORT.containsKey(modelName));
            def modelData=MODELS_TO_EXPORT[modelName];
            if(modelName == "application.ObjectId" || modelName=="RsEvent")
            {
                assertEquals(false,modelData.relations);
            }
            else
            {
                assertEquals(true,modelData.relations);
            }
        }
    }
    
    public void testGenerateModelsToExportWithAllModels()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection","application.ObjectId"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"all"]);

        def fullExport=new FullExportImport();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertTrue(EXPORT_CONFIG.EXPORT_ALL_RELATIONS);

        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;

        def expectedModelList=modelClassesNameList.clone();
        expectedModelList.remove("relation.Relation");

        assertEquals(expectedModelList.size(),MODELS_TO_EXPORT.size());
        expectedModelList.each{ modelName ->
            assertTrue(MODELS_TO_EXPORT.containsKey(modelName));
            def modelData=MODELS_TO_EXPORT[modelName];
            assertEquals(false,modelData.relations);

        }

        //test with other models should return same result
        MODELS.clear();
        MODELS.add([model:"all"]);
        MODELS.add([model:"RsTopologyObject"])
        MODELS.add([model:"RsEvent"])

        def EXPORT_CONFIG2=fullExport.generateModelsToExport(MODELS);


        assertEquals(EXPORT_CONFIG.MODELS_TO_EXPORT.size(),EXPORT_CONFIG2.MODELS_TO_EXPORT.size());
        assertEquals(EXPORT_CONFIG.EXPORT_ALL_RELATIONS,EXPORT_CONFIG2.EXPORT_ALL_RELATIONS);
        assertEquals(EXPORT_CONFIG,EXPORT_CONFIG2);

        //test with other models should return same result
        MODELS.clear();
        MODELS.add([model:"RsTopologyObject"])
        MODELS.add([model:"all"]);
        MODELS.add([model:"RsEvent"])

        def EXPORT_CONFIG3=fullExport.generateModelsToExport(MODELS);

        assertEquals(EXPORT_CONFIG.MODELS_TO_EXPORT.size(),EXPORT_CONFIG3.MODELS_TO_EXPORT.size());
        assertEquals(EXPORT_CONFIG.EXPORT_ALL_RELATIONS,EXPORT_CONFIG3.EXPORT_ALL_RELATIONS);
        assertEquals(EXPORT_CONFIG,EXPORT_CONFIG3);
    }
    public void testGenerateModelsToExportWithConfModels()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection","application.ObjectId"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"conf"]);

        def fullExport=new FullExportImport();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertFalse(EXPORT_CONFIG.EXPORT_ALL_RELATIONS);

        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;

        def expectedModelList=modelClassesNameList.clone();

        expectedModelList.remove("relation.Relation");
        expectedModelList.remove("RsTopologyObject")
        expectedModelList.remove("RsGroup")
        expectedModelList.remove("RsCustomer")
        expectedModelList.remove("RsEvent")
        expectedModelList.remove("RsRiEvent")

        assertEquals(expectedModelList.size(),MODELS_TO_EXPORT.size());
        expectedModelList.each{ modelName ->
            println modelName
            assertTrue(MODELS_TO_EXPORT.containsKey(modelName));
            def modelData=MODELS_TO_EXPORT[modelName];
            if(modelName == "application.ObjectId")
            {
                assertEquals(false,modelData.relations);
            }
            else
            {
                assertEquals(true,modelData.relations);
            }
        }
    }
    public void testGenerateModelsToExportWithConfModelsAndWithSelectedModels()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection","application.ObjectId"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"conf"]);
        MODELS.add([model:"RsTopologyObject"]);
        MODELS.add([model:"RsEvent",childModels:false,relations:false]);

        def fullExport=new FullExportImport();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertFalse(EXPORT_CONFIG.EXPORT_ALL_RELATIONS);

        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;

        def expectedModelList=modelClassesNameList.clone();

        expectedModelList.remove("relation.Relation");
        expectedModelList.remove("RsRiEvent")

        assertEquals(expectedModelList.size(),MODELS_TO_EXPORT.size());
        expectedModelList.each{ modelName ->
            println modelName
            assertTrue(MODELS_TO_EXPORT.containsKey(modelName));
            def modelData=MODELS_TO_EXPORT[modelName];
            if(modelName == "application.ObjectId" || modelName == "RsEvent")
            {
                assertEquals(false,modelData.relations);
            }
            else
            {
                assertEquals(true,modelData.relations);
            }
        }
    }
    public void testBeginCompassStartsSuccessfully()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);
        
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        2.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"RsTopologyObject${it}");
            assertFalse(obj.hasErrors());
        }

        3.times{
            def obj=modelClassMap.RsGroup.add(name:"RsGroup${it}");
            assertFalse(obj.hasErrors());
        }

        4.times{
            def obj=modelClassMap.RsCustomer.add(name:"RsCustomer${it}");
            assertFalse(obj.hasErrors());
        }

        5.times{
            def obj=modelClassMap.RsEvent.add(name:"RsEvent${it}");
            assertFalse(obj.hasErrors());
        }

        3.times{
            def obj=connection.Connection.add(name:"Connection${it}");
            assertFalse(obj.hasErrors());
        }

        def fullExport=new FullExportImport();
        println System.getProperty("index.dir"); 

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        try{
            def modelNamesToCheck=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","connection.Connection"];
            def modelClassesToCheck=loadClasses(modelNamesToCheck);
            
            modelClassesToCheck.each{ modelClass ->
                def modelAlias=fullExport.getModelAlias(modelClass.name);
                def query="alias:*";
                CompassQuery queryObj = fullExport.getCompassSession().queryBuilder().queryString(query).toQuery();
                queryObj.addSort ("id")
                queryObj.setAliases ([modelAlias] as String[]);
                CompassHits hits = queryObj.hits();

                def compassModelData=[:];

                hits.length().times{ dataIndex->
                    def obj=hits.data(dataIndex);
                    compassModelData[obj.id]=obj;
                }


                def repoResults=modelClass.searchEvery("alias:\"(${modelAlias})\" ");
                assertEquals(repoResults.size(),compassModelData.size());
                repoResults.each{  resultObj ->
                    def compassResultObj=compassModelData[resultObj.id];
                    assertNotNull(compassResultObj);
                    assertEquals(resultObj.name,compassResultObj.name);
                    assertEquals(resultObj.asMap(),compassResultObj.asMap());
                    
                }



            }
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }
    }
    public def getClassMapFromClassList(classList)
    {
        def classMap=[:];
        classList.each { classInstance ->
            classMap[classInstance.name]=classInstance;
        }
        return classMap;
    }
    public def loadClasses(classNameList)
    {
        def classes=[];
        classNameList.each{  className ->
            classes.add(loadClass(className));
        }
        return classes;
    }
    public def loadClass(className)
    {
        return Class.forName(className);
    }

}