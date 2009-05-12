package com.ifountain.rcmdb.domain.fullExportImport

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import application.RsApplicationOperations
import com.ifountain.rcmdb.domain.FullExportImportUtility
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
class FullExportImportUtilityTest extends RapidCmdbWithCompassTestCase{

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


        def fullExport=new FullExportImportUtility();
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

        def fullExport=new FullExportImportUtility();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertTrue(EXPORT_CONFIG.EXPORT_MARKED_RELATIONS);

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

        def fullExport=new FullExportImportUtility();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertTrue(EXPORT_CONFIG.EXPORT_MARKED_RELATIONS);

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

        def fullExport=new FullExportImportUtility();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertTrue(EXPORT_CONFIG.EXPORT_MARKED_RELATIONS);

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

        def fullExport=new FullExportImportUtility();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertFalse(EXPORT_CONFIG.EXPORT_MARKED_RELATIONS);

        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;

        def expectedModelList=modelClassesNameList.clone();        

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

        def fullExport=new FullExportImportUtility();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertTrue(EXPORT_CONFIG.EXPORT_MARKED_RELATIONS);

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

        def fullExport=new FullExportImportUtility();
        def EXPORT_CONFIG=fullExport.generateModelsToExport(MODELS);

        assertTrue(EXPORT_CONFIG.EXPORT_MARKED_RELATIONS);

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

        def fullExport=new FullExportImportUtility();


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


                def repoResults=modelClass.searchEvery("alias:${modelAlias.exactQuery()}");
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

    public void testExportModelWithoutRelationsExportOnlyTheModelDataAndDoesNotMarkRelationsToExport()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def addedObjects=[];

        5.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"RsTopologyObject${it}");
            assertFalse(obj.hasErrors());
            addedObjects.add(obj);
        }
        addedObjects[0].addRelation(parentObjects:[addedObjects[1],addedObjects[2]])
        addedObjects[1].addRelation(parentObjects:[addedObjects[3]])
        addedObjects[2].addRelation(parentObjects:[addedObjects[4]])

        assertEquals(4,relation.Relation.countHits("alias:*"));

        3.times{
            def obj=modelClassMap.RsGroup.add(name:"RsGroup${it}");
            assertFalse(obj.hasErrors());
        }

        2.times{
            def obj=modelClassMap.RsCustomer.add(name:"RsCustomer${it}");
            assertFalse(obj.hasErrors());
        }

        def fullExport=new FullExportImportUtility();

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

        def tx=fullExport.beginCompassTransaction();
        try{
            fullExport.exportModel(exportDir.getPath(),100,"RsTopologyObject",false);
            assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

            assertEquals(1,exportDir.listFiles().size());
            
            def xmlFile=new File(exportDir.getPath()+"/RsTopologyObject_0.xml");
            assertTrue(xmlFile.exists())

            def resultXml = new XmlSlurper().parse(xmlFile);

            def objects=resultXml.Object;
            def xmlData=[:];

            def xmlDataIds=[];
            objects.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }
            //check xml is sorted by id
            xmlDataIds.size().times { index ->
                if(index>0)
                {
                    assertTrue(xmlDataIds[index]>xmlDataIds[index-1]);
                }
            }

            //check xml properties are same as the object
            def modelAlias="RsTopologyObject";

            def repoResults=modelClassMap.RsTopologyObject.searchEvery("alias:${modelAlias.exactQuery()}")
            assertEquals(repoResults.size(),xmlData.size())


            repoResults.each{ repoObject ->
                assertTrue(xmlData.containsKey(repoObject.id.toString()));
                def xmlAttributes=xmlData[repoObject.id.toString()];
                xmlAttributes.each{ propName,propVal ->
                    assertEquals(repoObject.getProperty(propName).toString(),propVal);
                }
            }
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportModelWithRelationsMarksRelationIdsToExportAndExcludesOtherRelations()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsTicket","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def parentObjects=[];
        def childObjects=[];

        5.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"childObject${it}");
            assertFalse(obj.hasErrors());
            childObjects.add(obj);
        }

        5.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"parentObject${it}",childObjects:[childObjects[it]]);
            assertFalse(obj.hasErrors());
            assertEquals(1,obj.childObjects.size());
            parentObjects.add(obj);
        }

        def ticket1=modelClassMap.RsTicket.add(name:"ticket1");
        assertFalse(ticket1.hasErrors());
        def ticket2=modelClassMap.RsTicket.add(name:"ticket2",parentTicket:ticket1);
        assertFalse(ticket2.hasErrors());
        assertEquals(1,ticket1.subTickets.size());

        def group1=modelClassMap.RsGroup.add(name:"group1",relatedTickets:[ticket1]);
        assertFalse(group1.hasErrors())
        assertEquals(1,group1.relatedTickets.size());

        def group2=modelClassMap.RsGroup.add(name:"group2",relatedTickets:[ticket1,ticket2]);
        assertFalse(group2.hasErrors())
        assertEquals(2,group2.relatedTickets.size());

        assertEquals(9,relation.Relation.countHits("alias:*"))

        def fullExport=new FullExportImportUtility();

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

        def tx=fullExport.beginCompassTransaction();


        try{
            fullExport.exportModel(exportDir.getPath(),100,"RsTopologyObject",true);

            assertEquals(1,exportDir.listFiles().size());

            def xmlFile=new File(exportDir.getPath()+"/RsTopologyObject_0.xml");
            assertTrue(xmlFile.exists())

            def resultXml = new XmlSlurper().parse(xmlFile);

            def objects=resultXml.Object;
            def xmlData=[:];

            def xmlDataIds=[];
            objects.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }
            //check xml is sorted by id
            xmlDataIds.size().times { index ->
                if(index>0)
                {
                    assertTrue(xmlDataIds[index]>xmlDataIds[index-1]);
                }
            }

            //check xml properties are same as the object
            def modelAlias="RsTopologyObject";

            def repoResults=modelClassMap.RsTopologyObject.searchEvery("alias:${modelAlias.exactQuery()}")
            assertEquals(repoResults.size(),xmlData.size())

            def expectedRelationsIds=[:];

            repoResults.each{ repoObject ->
                assertTrue(xmlData.containsKey(repoObject.id.toString()));
                def xmlAttributes=xmlData[repoObject.id.toString()];
                xmlAttributes.each{ propName,propVal ->
                    assertEquals(repoObject.getProperty(propName).toString(),propVal);
                }
                def relations=relation.Relation.searchEvery("objectId:${repoObject.id} OR reverseObjectId:${repoObject.id}");
                relations.each{ relation ->
                    assertTrue(fullExport.RELATION_IDS_TO_EXPORT.containsKey(relation.id));
                    expectedRelationsIds[relation.id]=expectedRelationsIds[relation.id];
                }
            }
            assertEquals(5,fullExport.RELATION_IDS_TO_EXPORT.size());
            assertEquals(expectedRelationsIds.size(),fullExport.RELATION_IDS_TO_EXPORT.size());

        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportModelPaginatesExportedXmlFiles()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        5.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"RsTopologyObject${it}");
            assertFalse(obj.hasErrors());
        }



        def fullExport=new FullExportImportUtility();

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        try{
            fullExport.exportModel(exportDir.getPath(),2,"RsTopologyObject",false);

            assertEquals(3,exportDir.listFiles().size());
            def xmlData=[:];
            def xmlDataIds=[];

            def xmlFile0=new File(exportDir.getPath()+"/RsTopologyObject_0.xml");
            assertTrue(xmlFile0.exists())
            def resultXml0 = new XmlSlurper().parse(xmlFile0);
            def objects0=resultXml0.Object;
            assertEquals(2,objects0.size());

            objects0.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }

            def xmlFile1=new File(exportDir.getPath()+"/RsTopologyObject_1.xml");
            assertTrue(xmlFile1.exists())
            def resultXml1 = new XmlSlurper().parse(xmlFile1);
            def objects1=resultXml1.Object;
            assertEquals(2,objects1.size());

            objects1.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }

            def xmlFile2=new File(exportDir.getPath()+"/RsTopologyObject_2.xml");
            assertTrue(xmlFile2.exists())
            def resultXml2 = new XmlSlurper().parse(xmlFile2);
            def objects2=resultXml2.Object;
            assertEquals(1,objects2.size());

            objects2.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }

            //check xml is sorted by id
            xmlDataIds.size().times { index ->
                if(index>0)
                {
                    assertTrue(xmlDataIds[index]>xmlDataIds[index-1]);
                }
            }


            //check xml properties are same as the object
            def modelAlias="RsTopologyObject";

            def repoResults=modelClassMap.RsTopologyObject.searchEvery("alias:${modelAlias.exactQuery()}")
            assertEquals(repoResults.size(),xmlData.size())


            repoResults.each{ repoObject ->
                assertTrue(xmlData.containsKey(repoObject.id.toString()));
                def xmlAttributes=xmlData[repoObject.id.toString()];
                xmlAttributes.each{ propName,propVal ->
                    assertEquals(repoObject.getProperty(propName).toString(),propVal);
                }
            }
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportMarkedRelationsExportsOnlyMarkedRelations()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def addedRelations=[];
        5.times{
            def rel=relation.Relation.add("objectId":it,"reverseObjectId":it+1,"name":"n${it}","reverseName":"rn${it}","source":"");
            assertFalse(rel.hasErrors());
            addedRelations.add(rel);
        }
        def markedRelations=[:];
        3.times{
            markedRelations[addedRelations[it].id]=addedRelations[it];
        }


        assertEquals(5,relation.Relation.countHits("alias:*"));

        def fullExport=new FullExportImportUtility();

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

        markedRelations.each{ relId,rel ->
            fullExport.RELATION_IDS_TO_EXPORT[rel.id]=true;
        }

        assertEquals(3,fullExport.RELATION_IDS_TO_EXPORT.size());

        try{
            fullExport.exportMarkedRelations(exportDir.getPath(),100);


            assertEquals(1,exportDir.listFiles().size());

            def xmlFile=new File(exportDir.getPath()+"/relation.Relation_0.xml");
            assertTrue(xmlFile.exists())

            def resultXml = new XmlSlurper().parse(xmlFile);

            def objects=resultXml.Object;
            def xmlData=[:];

            def xmlDataIds=[];
            objects.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }
            //check xml is sorted by id
            xmlDataIds.size().times { index ->
                if(index>0)
                {
                    assertTrue(xmlDataIds[index]>xmlDataIds[index-1]);
                }
            }

            //check xml properties are same as the object

            def repoResults=relation.Relation.searchEvery("alias:*")
            assertEquals(markedRelations.size(),xmlData.size());
            assertEquals(addedRelations.size(),repoResults.size());

            repoResults.each{ repoObject ->
                if(markedRelations.containsKey(repoObject.id))
                {
                    assertTrue(xmlData.containsKey(repoObject.id.toString()));
                    def xmlAttributes=xmlData[repoObject.id.toString()];
                    xmlAttributes.each{ propName,propVal ->
                        assertEquals(repoObject.getProperty(propName).toString(),propVal);
                    }
                }
                else
                {
                    assertFalse(xmlData.containsKey(repoObject.id.toString()));
                }
            }
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportMarkedRelationsPaginatesExportedXmlFiles()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def addedRelations=[:];
        5.times{
            def rel=relation.Relation.add("objectId":it,"reverseObjectId":it+1,"name":"n${it}","reverseName":"rn${it}","source":"");
            assertFalse(rel.hasErrors());
            addedRelations[rel.id]=rel;
        }


        assertEquals(5,relation.Relation.countHits("alias:*"));

        def fullExport=new FullExportImportUtility();

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

        addedRelations.each{ relId,rel ->
            fullExport.RELATION_IDS_TO_EXPORT[rel.id]=true;
        }

        assertEquals(5,fullExport.RELATION_IDS_TO_EXPORT.size());

        try{
            fullExport.exportMarkedRelations(exportDir.getPath(),2);


            assertEquals(3,exportDir.listFiles().size());


            def xmlData=[:];
            def xmlDataIds=[];

            def xmlFile0=new File(exportDir.getPath()+"/relation.Relation_0.xml");
            assertTrue(xmlFile0.exists())

            def resultXml0 = new XmlSlurper().parse(xmlFile0);
            def objects0=resultXml0.Object;
            assertEquals(2,objects0.size());

            objects0.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }

            def xmlFile1=new File(exportDir.getPath()+"/relation.Relation_1.xml");
            assertTrue(xmlFile1.exists())

            def resultXml1 = new XmlSlurper().parse(xmlFile1);
            def objects1=resultXml1.Object;
            assertEquals(2,objects1.size());

            objects1.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }

            def xmlFile2=new File(exportDir.getPath()+"/relation.Relation_2.xml");
            assertTrue(xmlFile1.exists())

            def resultXml2 = new XmlSlurper().parse(xmlFile2);
            def objects2=resultXml2.Object;
            assertEquals(1,objects2.size());

            objects2.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }

            //check xml is sorted by id
            xmlDataIds.size().times { index ->
                if(index>0)
                {
                    assertTrue(xmlDataIds[index]>xmlDataIds[index-1]);
                }
            }

            //check xml properties are same as the object

            def repoResults=relation.Relation.searchEvery("alias:*")
            assertEquals(repoResults.size(),xmlData.size());


            repoResults.each{ repoObject ->
                assertTrue(xmlData.containsKey(repoObject.id.toString()));
                def xmlAttributes=xmlData[repoObject.id.toString()];
                xmlAttributes.each{ propName,propVal ->
                    assertEquals(repoObject.getProperty(propName).toString(),propVal);
                }
            }
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }
    public void testFullExportWithAllModels()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsTicket","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def parentObjects=[];
        def childObjects=[];

        5.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"childObject${it}");
            assertFalse(obj.hasErrors());
            childObjects.add(obj);
        }

        5.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"parentObject${it}",childObjects:[childObjects[it]]);
            assertFalse(obj.hasErrors());
            assertEquals(1,obj.childObjects.size());
            parentObjects.add(obj);
        }

        def ticket1=modelClassMap.RsTicket.add(name:"ticket1");
        assertFalse(ticket1.hasErrors());
        def ticket2=modelClassMap.RsTicket.add(name:"ticket2",parentTicket:ticket1);
        assertFalse(ticket2.hasErrors());
        assertEquals(1,ticket1.subTickets.size());

        def group1=modelClassMap.RsGroup.add(name:"group1",relatedTickets:[ticket1]);
        assertFalse(group1.hasErrors())
        assertEquals(1,group1.relatedTickets.size());

        def group2=modelClassMap.RsGroup.add(name:"group2",relatedTickets:[ticket1,ticket2]);
        assertFalse(group2.hasErrors())
        assertEquals(2,group2.relatedTickets.size());

        def con1=connection.Connection.add(name:"testcon1");
        assertFalse(con1.hasErrors());




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