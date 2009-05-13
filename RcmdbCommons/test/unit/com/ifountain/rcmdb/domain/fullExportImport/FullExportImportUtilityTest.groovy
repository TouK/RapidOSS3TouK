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
import org.apache.log4j.Logger

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
        GroovySystem.metaClassRegistry.removeMetaClass(FullExportImportUtility)
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
        def fileList=generateDirectoryDeleteTestFiles(backupDir);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
        fullExport.backup(backupPath);

        assertEquals(callParams.directory,new File("${backupPath}/index").getPath());

        checkDirectoryIsDeletedWithFiles(fileList);

    }
    private def generateDirectoryDeleteTestFiles(File baseDir)
    {
        FileUtils.deleteDirectory(baseDir);
        assertTrue(baseDir.mkdir());

        def indexDir=new File("${baseDir.getPath()}/index");
        assertTrue(indexDir.mkdir());

        def file1=new File(baseDir.getPath()+"/f1.txt");
        file1.setText("file1");
        assertTrue(file1.exists());

        def file2=new File(indexDir.getPath()+"/f2.txt");
        file2.setText("file2");
        assertTrue(file2.exists());

        return [file1,file2];
    }
    private void checkDirectoryIsDeletedWithFiles(fileList)
    {
         fileList.each{ file ->
            assertFalse(file.exists())
         }
    }
    public void testGenerateModelsToExportWithSelectedModelWithChildsAndRelations()
    {
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","application.ObjectId","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"RsTopologyObject"]);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
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
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","application.ObjectId","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"RsTopologyObject",childModels:false,relations:false]);
        MODELS.add([model:"RsGroup",childModels:false,relations:false]);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
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
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","application.ObjectId","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"RsTopologyObject"]);
        MODELS.add([model:"RsEvent",childModels:false,relations:false]);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
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
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","application.ObjectId","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"all"]);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
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
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","application.ObjectId","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"conf"]);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
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
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsRiEvent","relation.Relation","application.ObjectId","auth.RsUser","auth.Group","connection.Connection","connection.DatabaseConnection","connection.HttpConnection"];
        def modelClasses=loadClasses(modelClassesNameList);


        initialize(modelClasses,[]);
        println ApplicationHolder.application.getDomainClasses();

        def MODELS=[];
        MODELS.add([model:"conf"]);
        MODELS.add([model:"RsTopologyObject"]);
        MODELS.add([model:"RsEvent",childModels:false,relations:false]);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
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

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());


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
    private def checkXmlFiles(File exportDir,filesToCheck)
    {
        def xmlData=[:];
        def xmlDataIds=[];

        assertEquals(filesToCheck.size(),exportDir.listFiles().size());

        filesToCheck.each{ fileInfo ->
            def xmlFile=new File(exportDir.getPath()+"/"+fileInfo.name);
            assertTrue(xmlFile.exists());

            def resultXml = new XmlSlurper().parse(xmlFile);
            def xmlModelName=resultXml.@'model'.toString();
            assertEquals(fileInfo.model,xmlModelName);

            def objects=resultXml.Object;
            assertEquals(fileInfo.objectCount,objects.size());

            objects.each{ objectRow ->
                xmlData[objectRow.@"id".toString()]=objectRow.attributes();
                xmlDataIds.add(objectRow.@"id".toLong());
            }
        }

        //check xml is sorted by id
        xmlDataIds.size().times { index ->
            if(index>0)
            {
                assertTrue(xmlDataIds[index]>xmlDataIds[index-1]);
            }
        }

        return xmlData;
    }
    def checkXmlData(xmlData,repoResults)
    {
       assertEquals(repoResults.size(),xmlData.size())

        repoResults.each{ repoObject ->
            assertTrue(xmlData.containsKey(repoObject.id.toString()));
            def objectAttributes=repoObject.asMap();
            def xmlAttributes=xmlData[repoObject.id.toString()];
            
            assertEquals(objectAttributes.size(),xmlAttributes.size())
            objectAttributes.each{ propName,propVal ->
                assertEquals(propVal.toString(),xmlAttributes[propName]);
            }
        }
    }
    public void testGetFileCount()
    {
       def fullExport=new FullExportImportUtility(Logger.getRootLogger());

       assertEquals(0,fullExport.getFileCount(0,0));
       assertEquals(0,fullExport.getFileCount(0,5));

       assertEquals(1,fullExport.getFileCount(1,5));
       assertEquals(1,fullExport.getFileCount(2,5));
       assertEquals(1,fullExport.getFileCount(5,5));

       assertEquals(2,fullExport.getFileCount(6,5));
       assertEquals(2,fullExport.getFileCount(8,5));
       assertEquals(2,fullExport.getFileCount(10,5));

       assertEquals(3,fullExport.getFileCount(11,5));

    }
    public void testExportModelWithoutRelationsExportOnlyTheModelDataAndDoesNotMarkRelationsToExport()
    {
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

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

        def tx=fullExport.beginCompassTransaction();
        try{
            fullExport.exportModel(exportDir.getPath(),100,"RsTopologyObject",false);
            assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

            def filesToCheck=[];
            filesToCheck.add([name:"RsTopologyObject_0.xml",model:"RsTopologyObject",objectCount:5])

            def xmlData=checkXmlFiles(exportDir,filesToCheck);

            //check xml properties are same as the object
            def modelAlias="RsTopologyObject";

            def repoResults=modelClassMap.RsTopologyObject.searchEvery("alias:${modelAlias.exactQuery()}")
            checkXmlData(xmlData,repoResults);
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportModelWithRelationsMarksRelationIdsToExportAndExcludesOtherRelations()
    {
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

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

        def tx=fullExport.beginCompassTransaction();


        try{
            fullExport.exportModel(exportDir.getPath(),100,"RsTopologyObject",true);

            

            def filesToCheck=[];
            filesToCheck.add([name:"RsTopologyObject_0.xml",model:"RsTopologyObject",objectCount:10])

            def xmlData=checkXmlFiles(exportDir,filesToCheck);

            //check xml properties are same as the object
            def modelAlias="RsTopologyObject";

            def repoResults=modelClassMap.RsTopologyObject.searchEvery("alias:${modelAlias.exactQuery()}")
            checkXmlData(xmlData,repoResults);

            def expectedRelationsIds=[:];

            repoResults.each{ repoObject ->
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
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","relation.Relation","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        5.times{
            def obj=modelClassMap.RsTopologyObject.add(name:"RsTopologyObject${it}");
            assertFalse(obj.hasErrors());
        }

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        File exportDir=new File("../exportDir");
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        try{
            fullExport.exportModel(exportDir.getPath(),2,"RsTopologyObject",false);

            def filesToCheck=[];
            filesToCheck.add([name:"RsTopologyObject_0.xml",model:"RsTopologyObject",objectCount:2])
            filesToCheck.add([name:"RsTopologyObject_1.xml",model:"RsTopologyObject",objectCount:2])
            filesToCheck.add([name:"RsTopologyObject_2.xml",model:"RsTopologyObject",objectCount:1])

            def xmlData=checkXmlFiles(exportDir,filesToCheck);

            //check xml properties are same as the object
            def modelAlias="RsTopologyObject";

            def repoResults=modelClassMap.RsTopologyObject.searchEvery("alias:${modelAlias.exactQuery()}")
            checkXmlData(xmlData,repoResults);
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportMarkedRelationsExportsOnlyMarkedRelations()
    {
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

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

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

            def filesToCheck=[];
            filesToCheck.add([name:"relation.Relation_0.xml",model:"relation.Relation",objectCount:3])
            def xmlData=checkXmlFiles(exportDir,filesToCheck);

            //check xml properties are same as the object

            def repoResults=relation.Relation.searchEvery("alias:*")
            assertEquals(markedRelations.size(),xmlData.size());
            assertEquals(addedRelations.size(),repoResults.size());

            def expectedResults=[];

            repoResults.each{ repoObject ->
                if(markedRelations.containsKey(repoObject.id))
                {
                    expectedResults.add(repoObject);
                }
                else
                {
                    assertFalse(xmlData.containsKey(repoObject.id.toString()));
                }
            }

            checkXmlData(xmlData,expectedResults);
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportMarkedRelationsPaginatesExportedXmlFiles()
    {
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

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

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

            def filesToCheck=[];
            filesToCheck.add([name:"relation.Relation_0.xml",model:"relation.Relation",objectCount:2])
            filesToCheck.add([name:"relation.Relation_1.xml",model:"relation.Relation",objectCount:2])
            filesToCheck.add([name:"relation.Relation_2.xml",model:"relation.Relation",objectCount:1])

            def xmlData=checkXmlFiles(exportDir,filesToCheck);


            //check xml properties are same as the object

            def repoResults=relation.Relation.searchEvery("alias:*")
            checkXmlData(xmlData,repoResults);
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testFullExportWithAllModels()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);


        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsTicket","relation.Relation","application.ObjectId","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);
        println System.getProperty("index.dir")
        
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


        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def CONFIG=[:];
        CONFIG.backupDir="../testbackup";
        CONFIG.exportDir="../testexport";
        CONFIG.objectsPerFile=5;
        CONFIG.MODELS=[];
        CONFIG.MODELS.add([model:"all"]);

        fullExport.fullExport(CONFIG);


        File exportDir=new File(CONFIG.exportDir);
        assertTrue(exportDir.exists());


        def filesToCheck=[];
        filesToCheck.add([name:"RsTopologyObject_0.xml",model:"RsTopologyObject",objectCount:5])
        filesToCheck.add([name:"RsTopologyObject_1.xml",model:"RsTopologyObject",objectCount:5])
        filesToCheck.add([name:"RsGroup_0.xml",model:"RsGroup",objectCount:2])
        filesToCheck.add([name:"RsTicket_0.xml",model:"RsTicket",objectCount:2])
        filesToCheck.add([name:"connection.Connection_0.xml",model:"connection.Connection",objectCount:1])
        filesToCheck.add([name:"relation.Relation_0.xml",model:"relation.Relation",objectCount:5])
        filesToCheck.add([name:"relation.Relation_1.xml",model:"relation.Relation",objectCount:4])


        def xmlData=checkXmlFiles(exportDir,filesToCheck);

        def repoResults=[];
        def modelsToCheck=["RsTopologyObject","RsGroup","RsTicket","connection.Connection","relation.Relation"];
        modelsToCheck.each{ modelName ->
           def modelAlias=fullExport.getModelAlias(modelName);
           repoResults.addAll(modelClassMap[modelName].searchEvery("alias:${modelAlias.exactQuery()}"))
        }

        checkXmlData(xmlData,repoResults);
    }
    public void testFullExportCallsExportModelsAndCallsExportMarkedRelationsIfExportMarkedRelationsTrue()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        initialize([],[]);

        def EXPORT_CONFIG_TO_RETURN=[:];
        EXPORT_CONFIG_TO_RETURN.MODELS_TO_EXPORT=[:];
        EXPORT_CONFIG_TO_RETURN.MODELS_TO_EXPORT["RsTopologyObject"]=[relations:true];
        EXPORT_CONFIG_TO_RETURN.MODELS_TO_EXPORT["RsGroup"]=[relations:false];
        EXPORT_CONFIG_TO_RETURN.EXPORT_MARKED_RELATIONS=true;
        
        FullExportImportUtility.metaClass.generateModelsToExport={ MODELS ->
            println "generateModelsToExport in test"
            return EXPORT_CONFIG_TO_RETURN;

        }

        def exportModelsCallParams=[:];

        FullExportImportUtility.metaClass.exportModels={ exportDir,objectsPerFile,MODELS_TO_EXPORT ->
            println "exportModels in test"
            exportModelsCallParams=[exportDir:exportDir,objectsPerFile:objectsPerFile,MODELS_TO_EXPORT:MODELS_TO_EXPORT]
        }

        def exportMarkedRelationsCallParams=[:];

        FullExportImportUtility.metaClass.exportMarkedRelations={ exportDir,objectsPerFile ->
            println "exportMarkedRelations in test"
            exportMarkedRelationsCallParams=[exportDir:exportDir,objectsPerFile:objectsPerFile]
        }

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
        fullExport.RELATION_IDS_TO_EXPORT[1]=true;
        fullExport.RELATION_IDS_TO_EXPORT[5]=true;

        assertEquals(2,fullExport.RELATION_IDS_TO_EXPORT.size())

        def CONFIG=[:];
        CONFIG.backupDir="../testbackup";
        CONFIG.exportDir="../testexport";
        CONFIG.objectsPerFile=5;
        CONFIG.MODELS=[];
        CONFIG.MODELS.add([model:"all"]);

        exportModelsCallParams.clear();
        exportMarkedRelationsCallParams.clear();

        fullExport.fullExport(CONFIG);

        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size())

        assertEquals(CONFIG.exportDir,exportModelsCallParams.exportDir);
        assertEquals(CONFIG.objectsPerFile,exportModelsCallParams.objectsPerFile);
        assertSame(EXPORT_CONFIG_TO_RETURN.MODELS_TO_EXPORT,exportModelsCallParams.MODELS_TO_EXPORT);
        assertEquals(EXPORT_CONFIG_TO_RETURN.MODELS_TO_EXPORT.size(),exportModelsCallParams.MODELS_TO_EXPORT.size());
        assertTrue(EXPORT_CONFIG_TO_RETURN.EXPORT_MARKED_RELATIONS);
        assertEquals(CONFIG.exportDir,exportMarkedRelationsCallParams.exportDir)
        assertEquals(CONFIG.objectsPerFile,exportMarkedRelationsCallParams.objectsPerFile)

        //when EXPORT_MARKED_RELATIONS false
        EXPORT_CONFIG_TO_RETURN.EXPORT_MARKED_RELATIONS=false;
        
        exportModelsCallParams.clear();
        exportMarkedRelationsCallParams.clear();
        fullExport.fullExport(CONFIG);

        assertEquals(CONFIG.exportDir,exportModelsCallParams.exportDir);
        assertEquals(CONFIG.objectsPerFile,exportModelsCallParams.objectsPerFile);
        assertSame(EXPORT_CONFIG_TO_RETURN.MODELS_TO_EXPORT,exportModelsCallParams.MODELS_TO_EXPORT);
        assertEquals(EXPORT_CONFIG_TO_RETURN.MODELS_TO_EXPORT.size(),exportModelsCallParams.MODELS_TO_EXPORT.size());
        assertFalse(EXPORT_CONFIG_TO_RETURN.EXPORT_MARKED_RELATIONS);
        assertEquals(0,exportMarkedRelationsCallParams.size());

    }
    public void testExportModelsCallsExportModelForEachModelAndDeletesExportDirectory()
    {
        def MODELS_TO_EXPORT=[:];
        MODELS_TO_EXPORT["RsTopologyObject"]=[relations:true];
        MODELS_TO_EXPORT["RsGroup"]=[relations:false];
        def expectedExportDir="../testexport";
        def expectedObjectsPerFile=5;

        def exportModelCallParams=[:];

        FullExportImportUtility.metaClass.exportModel={ exportDir,objectsPerFile,modelName,relations ->
            println "exportModel in test"
            exportModelCallParams[modelName]=[exportDir:exportDir,objectsPerFile:objectsPerFile,relations:relations]
        }

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def exportDir=new File(expectedExportDir);
        def fileList=generateDirectoryDeleteTestFiles(exportDir);

        fullExport.exportModels(expectedExportDir,expectedObjectsPerFile,MODELS_TO_EXPORT);

        checkDirectoryIsDeletedWithFiles (fileList);
        
        assertEquals(MODELS_TO_EXPORT.size(),exportModelCallParams.size());
        MODELS_TO_EXPORT.each{ modelName , modelData ->
            assertTrue(exportModelCallParams.containsKey(modelName));
            assertEquals(expectedExportDir,exportModelCallParams[modelName].exportDir)
            assertEquals(expectedObjectsPerFile,exportModelCallParams[modelName].objectsPerFile)
            assertEquals(MODELS_TO_EXPORT[modelName].relations,exportModelCallParams[modelName].relations)
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
        return this.class.classLoader.loadClass(className);
    }

}