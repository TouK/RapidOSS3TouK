package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import application.RsApplicationOperations
import com.ifountain.rcmdb.domain.FullExportImportUtility
import application.RsApplication
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.compass.core.CompassQuery
import org.compass.core.CompassHits
import org.apache.log4j.Logger
import org.apache.lucene.search.BooleanQuery
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 12, 2009
* Time: 8:55:29 AM
* To change this template use File | Settings | File Templates.
*/
class FullExportImportUtilityTest extends RapidCmdbWithCompassTestCase{

    def directoryPaths=[exportDir:"../testexport",importDir:"../testimport",backupDir:"..${File.separator}testbackup".toString()]
    public void setUp() {
        super.setUp();
        clearMetaClasses();
        clearDirectories();
    }

    public void tearDown() {
        clearMetaClasses();
        clearDirectories();
        super.tearDown();
    }
    private void clearDirectories()
    {
       directoryPaths.each{ dirName,dirPath ->
            FileUtils.deleteDirectory (new File(dirPath));
       }
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

        def backupPath=directoryPaths.backupDir;

        def backupDir=new File(backupPath);
        def fileList=generateDirectoryDeleteTestFiles(backupDir);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
        fullExport.backup(backupPath);

        assertEquals(callParams.directory,new File("${backupPath}${File.separator}index").getPath());

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
    private def checkXmlFiles(File exportDir,filesToCheck,checkDirectoryFileSize=true)
    {
        def xmlData=[:];
        def xmlDataIds=[];
        if(checkDirectoryFileSize)
        {
            assertEquals(filesToCheck.size(),exportDir.listFiles().size());
        }

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
            def objectAttributes=repoObject.asMap(repoObject.getNonFederatedPropertyList().name);
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

        File exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());
        assertEquals(0,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());

        def tx=fullExport.beginCompassTransaction();
        try{
            fullExport.exportModel(exportDir.getPath(),100,"RsTopologyObject",false);
            assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());
            assertEquals(0,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());

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
            //note that even RsRicket export or not ExportModel marks all relations , unused relations will be removed later
            def ticketForObj=modelClassMap.RsTicket.add(name:"ticketForObj${it}");
            assertFalse(ticketForObj.hasErrors());

            def obj=modelClassMap.RsTopologyObject.add(name:"parentObject${it}",childObjects:[childObjects[it]],relatedTickets:[ticketForObj]);
            assertFalse(obj.hasErrors());
            assertEquals(1,obj.childObjects.size());
            parentObjects.add(obj);
        }
        assertEquals(10,relation.Relation.countHits("alias:*"))
        
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

        assertEquals(14,relation.Relation.countHits("alias:*"))

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        File exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());
        assertEquals(0,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());

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
                    assertEquals(relation.objectId,fullExport.RELATION_IDS_TO_EXPORT[relation.id].objectId)
                    assertEquals(relation.reverseObjectId,fullExport.RELATION_IDS_TO_EXPORT[relation.id].reverseObjectId)
                    expectedRelationsIds[relation.id]=expectedRelationsIds[relation.id];
                }
                
                assertTrue(fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.containsKey(repoObject.id));
            }
            assertEquals(10,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());
            assertEquals(10,fullExport.RELATION_IDS_TO_EXPORT.size());
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

        File exportDir=new File(directoryPaths.exportDir);
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


    public void testExportModelDiscardsFederatedProperties()
    {
        def model1Name = "Model1";
        def datasource = [name:"ds1", keys:[[propertyName:"prop1"]]]
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE];
        def prop2 = [name: "prop2", type: ModelGenerator.STRING_TYPE, datasource:"ds1"];
        def model1MetaProps = [name: model1Name]
        def modelProps = [prop1, prop2];
        def keyPropList = [prop1];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, [datasource], modelProps, keyPropList, []);
        def modelClass = gcl.parseClass (model1Text);

        initialize([modelClass],[],true);

        5.times{
            def obj=modelClass.add(prop1:"model1Instance${it}");
            assertFalse(obj.hasErrors());
        }

        FullExportImportUtility fullExport=new FullExportImportUtility(Logger.getRootLogger());

        File exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        try{
            fullExport.exportModel(exportDir.getPath(),5,modelClass.name,false);

            def filesToCheck=[];
            filesToCheck.add([name:"${modelClass.name}_0.xml",model:modelClass.name,objectCount:5])
            def xmlData=checkXmlFiles(exportDir,filesToCheck);

            //check xml properties are same as the object
            def modelAlias=modelClass.name;

            def repoResults=modelClass.searchEvery("alias:${modelAlias.exactQuery()}")
            checkXmlData(xmlData,repoResults);
        }
        finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
        }

    }

    public void testExportMarkedRelationsExportsOnlyMarkedRelationsAndExcludesRelationsOfUnexportedObjects()
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

        File exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());

        markedRelations.each{ relId,rel ->
            fullExport.RELATION_IDS_TO_EXPORT[rel.id]=[objectId:rel.objectId,reverseObjectId:rel.reverseObjectId];
        }

        assertEquals(3,fullExport.RELATION_IDS_TO_EXPORT.size());

        try{
             //with no objects exported no relations will be exported
            assertEquals(0,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());

            fullExport.exportMarkedRelations(exportDir.getPath(),100);
            def filesToCheck=[];
            filesToCheck.add([name:"relation.Relation_0.xml",model:"relation.Relation",objectCount:0])
            def xmlData=checkXmlFiles(exportDir,filesToCheck);

             
            //with exported objects marked the relations will be exported
            addedRelations.each{   rel ->
                 fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS[rel.objectId]=true;
                 fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS[rel.reverseObjectId]=true;
            }
            assertEquals(6,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());
            assertEquals(3,fullExport.RELATION_IDS_TO_EXPORT.size());

            fullExport.exportMarkedRelations(exportDir.getPath(),100);

            filesToCheck=[];
            filesToCheck.add([name:"relation.Relation_0.xml",model:"relation.Relation",objectCount:3])
            xmlData=checkXmlFiles(exportDir,filesToCheck);

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

        File exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        fullExport.beginCompass(System.getProperty("index.dir"));
        def tx=fullExport.beginCompassTransaction();
        assertEquals(0,fullExport.RELATION_IDS_TO_EXPORT.size());
        assertEquals(0,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());

        addedRelations.each{ relId,rel ->
            fullExport.RELATION_IDS_TO_EXPORT[rel.id]=[objectId:rel.objectId,reverseObjectId:rel.reverseObjectId];
        }

        addedRelations.each{   relId,rel ->
                     fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS[rel.objectId]=true;
                     fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS[rel.reverseObjectId]=true;
        }
        assertEquals(5,fullExport.RELATION_IDS_TO_EXPORT.size());
        assertEquals(6,fullExport.MODEL_IDS_TO_EXPORTED_WITH_RELATIONS.size());



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
    public void testMarkRelationsOfObjectIdsDoesNotGenerateQueryExceptionWithHugeNumberOfObjects()
    {
       def modelClassesNameList=["relation.Relation"];
       def modelClasses=loadClasses(modelClassesNameList);
       initialize(modelClasses,[],true);

       def objectIds=[]
       BooleanQuery.getMaxClauseCount().times{
            objectIds.add(it);
       }

       def fullExport=new FullExportImportUtility(Logger.getRootLogger());
       fullExport.beginCompass(System.getProperty("index.dir"));
       def tx=fullExport.beginCompassTransaction();
        
       try{
             fullExport.markRelationsOfObjectIds(objectIds);
       }
       finally{
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
       }

       //now test exception is generated
       fullExport.beginCompass(System.getProperty("index.dir"));
       tx=fullExport.beginCompassTransaction();
        
       def oldValue=fullExport.MAX_NUMBER_OF_OBJECT_TO_BE_PROCESSED_IN_MARKRELATIONS;
       try{
            fullExport.MAX_NUMBER_OF_OBJECT_TO_BE_PROCESSED_IN_MARKRELATIONS=BooleanQuery.getMaxClauseCount();
            fullExport.markRelationsOfObjectIds(objectIds);
            fail("Should throw org.compass.core.engine.SearchEngineQueryParseException");
       }
       catch(org.compass.core.engine.SearchEngineQueryParseException e){}
       finally{
            fullExport.MAX_NUMBER_OF_OBJECT_TO_BE_PROCESSED_IN_MARKRELATIONS=oldValue;
            fullExport.endCompassTransaction(tx);
            fullExport.endCompass();
       }

    }
    private def initializeFullExportModels()
    {
        def modelClassesNameList=["RsTopologyObject","RsGroup","RsCustomer","RsEvent","RsTicket","relation.Relation","application.ObjectId","connection.Connection"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        return modelClassMap;
    }
    private void initializeFullExportInstances(modelClassMap)
    {
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

        //activate below lines of code in order to try tests without xml parse problem for the relation model
        relation.Relation.searchEvery("alias:*").each{ rel ->
            rel.update(source:"relsource");
            assertFalse(rel.hasErrors());
        }
    }

    public void testFullExportWithAllModels()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassMap=initializeFullExportModels();
        initializeFullExportInstances(modelClassMap)

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def CONFIG=[:];
        CONFIG.backupDir=directoryPaths.backupDir;
        CONFIG.exportDir=directoryPaths.exportDir;
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

        assertEquals(filesToCheck.size(),exportDir.listFiles().size());

        def xmlData=[:];

        xmlData.putAll checkXmlFiles(exportDir,[filesToCheck[0],filesToCheck[1]],false);
        xmlData.putAll checkXmlFiles(exportDir,[filesToCheck[2]],false);
        xmlData.putAll checkXmlFiles(exportDir,[filesToCheck[3]],false);
        xmlData.putAll checkXmlFiles(exportDir,[filesToCheck[4]],false);
        xmlData.putAll checkXmlFiles(exportDir,[filesToCheck[5],filesToCheck[6]],false);


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
        CONFIG.backupDir=directoryPaths.backupDir;
        CONFIG.exportDir=directoryPaths.exportDir;
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
    public void testFullExportGeneratesExceptionWhenParametersAreWrong()
    {
        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        //backupdir missing
        try{
            def CONFIG=[:];
            CONFIG.exportDir=directoryPaths.exportDir;
            CONFIG.objectsPerFile=5;
            CONFIG.MODELS=[];
            CONFIG.MODELS.add([model:"all"]);
            fullExport.fullExport(CONFIG);
            fail("Should throw exception");
        }
        catch(e){assertTrue(e.getMessage().indexOf("backupDir")>=0)}

        //exportDir missing
        try{
            def CONFIG=[:];
            CONFIG.backupDir=directoryPaths.backupDir;
            CONFIG.objectsPerFile=5;
            CONFIG.MODELS=[];
            CONFIG.MODELS.add([model:"all"]);
            fullExport.fullExport(CONFIG);
            fail("Should throw exception");
        }
        catch(e){assertTrue(e.getMessage().indexOf("exportDir")>=0)}

        //objectsPerFile missing
        try{
            def CONFIG=[:];
            CONFIG.backupDir=directoryPaths.backupDir;
            CONFIG.exportDir=directoryPaths.exportDir;
            CONFIG.MODELS=[];
            CONFIG.MODELS.add([model:"all"]);
            fullExport.fullExport(CONFIG);
            fail("Should throw exception");
        }
        catch(e){assertTrue(e.getMessage().indexOf("objectsPerFile")>=0)}

        //MODELS missing
        try{
            def CONFIG=[:];
            CONFIG.backupDir=directoryPaths.backupDir;
            CONFIG.exportDir=directoryPaths.exportDir;
            CONFIG.objectsPerFile=5;
            fullExport.fullExport(CONFIG);
            fail("Should throw exception");
        }
        catch(e){ assertTrue(e.getMessage().indexOf("MODELS")>=0)}

        //MODELS empty
        try{
            def CONFIG=[:];
            CONFIG.backupDir=directoryPaths.backupDir;
            CONFIG.exportDir=directoryPaths.exportDir;
            CONFIG.objectsPerFile=5;
            CONFIG.MODELS=[];
            fullExport.fullExport(CONFIG);
            fail("Should throw exception");
        }
        catch(e){ assertTrue(e.getMessage().indexOf("MODELS")>=0)}
    }
    public void testFullImportGeneratesExceptionWhenParametersAreWrong()
    {
        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        //importDir missing
        try{
            def CONFIG=[:];
            CONFIG.exportDir=directoryPaths.exportDir;
            fullExport.fullImport(CONFIG);
            fail("Should throw exception");
        }
        catch(e){assertTrue(e.getMessage().indexOf("importDir")>=0)}

        //exportDir missing
        try{
            def CONFIG=[:];
            CONFIG.importDir=directoryPaths.importDir;
            fullExport.fullImport(CONFIG);
            fail("Should throw exception");
        }
        catch(e){assertTrue(e.getMessage().indexOf("exportDir")>=0)}

    }
    public void testCheckParameterGeneratesException()
    {
        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def paramName="testParam";
        def paramValue=null;
        def paramClass=null;

        try{
            paramValue="asdad";
            paramClass=null;
            fullExport.checkParameter(paramName,paramValue,paramClass);
        }
        catch(e)
        {
            fail("should not throw exception");
        }

        try{
            paramValue=null;
            paramClass=null;
            fullExport.checkParameter(paramName,paramValue,paramClass);
            fail("should throw exception");
        }
        catch(e){}

        try{
            paramValue="";
            paramClass=null;
            fullExport.checkParameter(paramName,paramValue,paramClass);
            fail("should throw exception");
        }
        catch(e){}


        try{
            def tempMap=[:];
            paramValue=tempMap.tempValue;
            paramClass=Long;
            fullExport.checkParameter(paramName,paramValue,paramClass);
            fail("should throw exception");
        }
        catch(e){}

        try{
            paramValue=5;
            paramClass=String;
            fullExport.checkParameter(paramName,paramValue,paramClass);
            fail("should throw exception");
        }
        catch(e){}


        try{
            paramValue="5";
            paramClass=Long;
            fullExport.checkParameter(paramName,paramValue,paramClass);
            fail("should throw exception");
        }
        catch(e){}

    }
    public void testFullExportGeneratesExceptionWhenInvalidModelIsInMODELS()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        initialize([],[],true);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        //importDir missing
        try{
            def CONFIG=[:];
            CONFIG.backupDir=directoryPaths.backupDir;
            CONFIG.exportDir=directoryPaths.exportDir;
            CONFIG.objectsPerFile=5;
            CONFIG.MODELS=[];
            CONFIG.MODELS.add([model:"RsEvent_XX_YY"]);
            fullExport.fullExport(CONFIG);
            fail("should throw exception");
        }
        catch(e){
            assertTrue(e.getMessage().indexOf("RsEvent_XX_YY")>=0)

        }

    }
    public void testImportModelFileGeneratesExceptionWhenXmlDataHasUnExistingModelOrProperty()
    {
        def modelClassesNameList=["RsEvent"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def importDir=directoryPaths.importDir;

        def exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        File xmlFile=new File("${exportDir.getPath()}/RsEvent_0.xml");
        xmlFile.setText("""
            <Objects model='RsEvent_XX_YY'>
              <Object acknowledged='false' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y1' id='2009' inMaintenance='false' name='ev1' owner='' rsDatasource='' severity='10' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y2' id='2010' inMaintenance='false' name='ev2' owner='' rsDatasource='' severity='20' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='10' createdAt='0' elementDisplayName='' elementName='y3' id='2011' inMaintenance='false' name='ev3' owner='' rsDatasource='' severity='30' source='' state='0' willExpireAt='0' />
            </Objects>
        """);
        assertTrue(xmlFile.exists());

        fullExport.beginCompass(importDir)
        try{
            fullExport.importModelFile(xmlFile);
            fail("should throw exception");
        }
        catch(Exception e)
        {
            assertTrue(e.getMessage().indexOf("RsEvent_XX_YY")>=0);
        }
        finally{
            fullExport.endCompass();
        }


        xmlFile.setText("""
            <Objects model='RsEvent'>
              <Object acknowledged='false' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y1' id='2009' inMaintenance='false' name='ev1' owner='' rsDatasource='' severity='10' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y2' id='2010' inMaintenance='false' name='ev2' owner='' rsDatasource='' severity='20' source='' state='0' willExpireAt='0' />
              <Object acknowledged_XX_YY='true' changedAt='0' clearedAt='0' count='10' createdAt='0' elementDisplayName='' elementName='y3' id='2011' inMaintenance='false' name='ev3' owner='' rsDatasource='' severity='30' source='' state='0' willExpireAt='0' />
            </Objects>
        """);


        fullExport.beginCompass(importDir)
        try{
            fullExport.importModelFile(xmlFile);
            fail("should throw exception");
        }
        catch(Exception e)
        {
            assertTrue(e.getMessage().indexOf("RsEvent.acknowledged_XX_Y")>=0);
        }
        finally{
            fullExport.endCompass();
        }

    }
    public void testFullExportDeletesExportDirWhenExceptionIsGenerated()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        initialize([loadClass("RsEvent")],[]);


        def exceptionToThrow=new Exception("testException");

        FullExportImportUtility.metaClass.exportModel={ exportDir,objectsPerFile,modelName,relations ->
            throw exceptionToThrow;
        }


        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def CONFIG=[:];
        CONFIG.backupDir=directoryPaths.backupDir;
        CONFIG.exportDir=directoryPaths.exportDir;
        CONFIG.objectsPerFile=5;
        CONFIG.MODELS=[];
        CONFIG.MODELS.add([model:"RsEvent"]);
        try{
            fullExport.fullExport(CONFIG);
            fail("should throw exception");
        }
        catch(e)
        {
            assertSame(exceptionToThrow,e);
        }

        def exportDir=new File(CONFIG.exportDir);
        assertFalse(exportDir.exists());
    }
     public void testFullImportDeletesExportDirWhenExceptionIsGenerated()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        initialize([loadClass("RsEvent")],[]);


        def exceptionToThrow=new Exception("testException");

        FullExportImportUtility.metaClass.importModelFiles={ exportDir ->
            throw exceptionToThrow;
        }

        def CONFIG=[:];
        CONFIG.importDir=directoryPaths.importDir;
        CONFIG.exportDir=directoryPaths.exportDir;

        def importDir=new File(CONFIG.importDir);
        assertTrue(importDir.mkdir());
        assertTrue(importDir.exists());

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());


        try{
            fullExport.fullImport(CONFIG);
            fail("should throw exception");
        }
        catch(e)
        {
            assertSame(exceptionToThrow,e);
        }
        assertFalse(importDir.exists());
    }
    public void testExportModelsCallsExportModelForEachModelAndDeletesExportDirectory()
    {
        def MODELS_TO_EXPORT=[:];
        MODELS_TO_EXPORT["RsTopologyObject"]=[relations:true];
        MODELS_TO_EXPORT["RsGroup"]=[relations:false];
        def expectedExportDir=directoryPaths.exportDir;
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

    public void testImportModelFilesCallsImportModelFileForEachFileInExportDirectory()
    {
        def importModelFileCallParams=[:];

        FullExportImportUtility.metaClass.importModelFile={ xmlFile->
            println "importModelFile in test"
            importModelFileCallParams[xmlFile.getPath()]=xmlFile;
        }

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        5.times{
            File file=new File("${exportDir.getPath()}/f${it}.xml");
            file.setText("dummyfile");
            assertTrue(file.exists());
        }
        fullExport.importModelFiles(exportDir.getPath());

        assertEquals(5,exportDir.listFiles().size());
        assertEquals(5,importModelFileCallParams.size());
        exportDir.listFiles().each{ file ->
            assertTrue(importModelFileCallParams.containsKey(file.getPath()));
            assertEquals(file.getCanonicalPath(),importModelFileCallParams[file.getPath()].getCanonicalPath());
        }
    }

    public void testImportModelFileImportsAndConvertsDataInXmlFileSuccessfully()
    {

        def modelClassesNameList=["RsEvent"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def importDir=directoryPaths.importDir;

        def exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        File xmlFile=new File("${exportDir.getPath()}/RsEvent_0.xml");
        xmlFile.setText("""
            <Objects model='RsEvent'>
              <Object acknowledged='false' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y1' id='2009' inMaintenance='false' name='ev1' owner='' rsDatasource='' severity='10' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y2' id='2010' inMaintenance='false' name='ev2' owner='' rsDatasource='' severity='20' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='10' createdAt='0' elementDisplayName='' elementName='y3' id='2011' inMaintenance='false' name='ev3' owner='' rsDatasource='' severity='30' source='' state='0' willExpireAt='0' />
            </Objects>
        """);
        assertTrue(xmlFile.exists());

        fullExport.beginCompass(importDir)
        try{
            fullExport.importModelFile(xmlFile);

            def tx=fullExport.beginCompassTransaction();

            try{
                def hits=fullExport.getModelHits("RsEvent","alias:*");
                assertEquals(3,hits.length());

                def object0=hits.data(0);
                assertEquals((Long)2009,object0.id);
                assertEquals("ev1",object0.name);
                assertEquals("y1",object0.elementName);
                assertEquals(false,object0.acknowledged);
                assertEquals((Long)10,object0.severity);
                assertEquals((Long)1,object0.count);

                def object1=hits.data(1);
                assertEquals((Long)2010,object1.id);
                assertEquals("ev2",object1.name);
                assertEquals("y2",object1.elementName);
                assertEquals(true,object1.acknowledged);
                assertEquals((Long)20,object1.severity);
                assertEquals((Long)1,object1.count);

                def object2=hits.data(2);
                assertEquals((Long)2011,object2.id);
                assertEquals("ev3",object2.name);
                assertEquals("y3",object2.elementName);
                assertEquals(true,object2.acknowledged);
                assertEquals((Long)30,object2.severity);
                assertEquals((Long)10,object2.count);
            }
            finally
            {
                fullExport.endCompassTransaction(tx);
            }

        }
        finally{
            fullExport.endCompass();
        }

    }
    public void testImportModelFileGeneratesExceptionWhenXmlDataHasInvalidModelPropertyOrType()
    {
        def modelClassesNameList=["RsEvent"];
        def modelClasses=loadClasses(modelClassesNameList);
        def modelClassMap=getClassMapFromClassList(modelClasses);
        initialize(modelClasses,[],true);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def importDir=directoryPaths.importDir;

        def exportDir=new File(directoryPaths.exportDir);
        FileUtils.deleteDirectory (exportDir);
        assertTrue(exportDir.mkdirs());

        File xmlFile=new File("${exportDir.getPath()}/RsEvent_0.xml");
        xmlFile.setText("""
            <Objects model='RsEvent_XX_YY'>
              <Object acknowledged='false' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y1' id='2009' inMaintenance='false' name='ev1' owner='' rsDatasource='' severity='10' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y2' id='2010' inMaintenance='false' name='ev2' owner='' rsDatasource='' severity='20' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='10' createdAt='0' elementDisplayName='' elementName='y3' id='2011' inMaintenance='false' name='ev3' owner='' rsDatasource='' severity='30' source='' state='0' willExpireAt='0' />
            </Objects>
        """);
        assertTrue(xmlFile.exists());

        fullExport.beginCompass(importDir)
        try{
            fullExport.importModelFile(xmlFile);
            fail("should throw exception");
        }
        catch(Exception e)
        {
            assertTrue(e.getMessage().indexOf("RsEvent_XX_YY")>=0);
        }
        finally{
            fullExport.endCompass();
        }


        xmlFile.setText("""
            <Objects model='RsEvent'>
              <Object acknowledged='false' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y1' id='2009' inMaintenance='false' name='ev1' owner='' rsDatasource='' severity='10' source='' state='0' willExpireAt='0' />
              <Object acknowledged='true' changedAt='0' clearedAt='0' count='1' createdAt='0' elementDisplayName='' elementName='y2' id='2010' inMaintenance='false' name='ev2' owner='' rsDatasource='' severity='20' source='' state='0' willExpireAt='0' />
              <Object acknowledged_XX_YY='true' changedAt='0' clearedAt='0' count='10' createdAt='0' elementDisplayName='' elementName='y3' id='2011' inMaintenance='false' name='ev3' owner='' rsDatasource='' severity='30' source='' state='0' willExpireAt='0' />
            </Objects>
        """);


        fullExport.beginCompass(importDir)
        try{
            fullExport.importModelFile(xmlFile);
            fail("should throw exception");
        }
        catch(Exception e)
        {               
            assertTrue(e.getMessage().indexOf("RsEvent.acknowledged_XX_Y")>=0);
        }
        finally{
            fullExport.endCompass();
        }

    }
    public void testConvertPropertyGeneratesSameResultAsRapidConvertUtils()
    {
        initialize([],[]);
        def object=loadClass("RsEvent").newInstance();

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
        assertEquals((Long)5,fullExport.convertProperty(Long,"5"));
        assertEquals((Double)5,fullExport.convertProperty(Double,"5"));
        assertEquals(true,fullExport.convertProperty(Boolean,"true"));
        assertEquals(false,fullExport.convertProperty(Boolean,"false"));


    }
    public void testConvertAndSetPropertyGeneratesExceptionWhenPropertyIsMissingOrConversionErrorOccurs()
    {
        initialize([loadClass("RsEvent")],[]);

        def fullExport=new FullExportImportUtility(Logger.getRootLogger());
        def object=loadClass("RsEvent").newInstance();

        //test successfull convert and set
        fullExport.convertAndSetProperty(object,"name","testEvent",String);
        assertEquals("testEvent",object.name);

        fullExport.convertAndSetProperty(object,"severity","3",Long);
        assertEquals((Long)3,object.severity);

        //missing property
        try{
            fullExport.convertAndSetProperty(object,"name_name","testEvent2",String);
            fail("should throw exception");
        }
        catch(e)
        {
            assertTrue(e.getMessage().indexOf("RsEvent.name_name")>=0);
            assertTrue(e.getMessage().indexOf("can not set property")>=0);

        }
        assertEquals("testEvent",object.name);

        //conversion exception  , try to convert RsEvent to Long ( severity )
        try{
            fullExport.convertAndSetProperty(object,"severity",loadClass("RsEvent").newInstance(),Long);
            fail("should throw exception");
        }
        catch(e)
        {
            assertTrue(e.getMessage().indexOf("RsEvent.severity")>=0);
            assertTrue(e.getMessage().indexOf("cannot convert property")>=0);
        }
        assertEquals("testEvent",object.name);

    }
    
    public void testFullImportSuccessfullyImportsXmlResultsOfFullExport()
    {
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);

        def modelClassMap=initializeFullExportModels();
        initializeFullExportInstances(modelClassMap)


        def fullExport=new FullExportImportUtility(Logger.getRootLogger());

        def EXPORT_CONFIG=[:];
        EXPORT_CONFIG.backupDir=directoryPaths.backupDir;
        EXPORT_CONFIG.exportDir=directoryPaths.exportDir;
        EXPORT_CONFIG.objectsPerFile=5;
        EXPORT_CONFIG.MODELS=[];
        EXPORT_CONFIG.MODELS.add([model:"all"]);

        fullExport.fullExport(EXPORT_CONFIG);



        def IMPORT_CONFIG=[:];
        IMPORT_CONFIG.importDir=directoryPaths.importDir;
        IMPORT_CONFIG.exportDir=directoryPaths.exportDir;

        fullExport.fullImport(IMPORT_CONFIG);

        //we save current objects in repo
        def modelsToCheck=["RsTopologyObject","RsGroup","RsTicket","connection.Connection","relation.Relation"];
        def oldRepoResults=[:];
        modelsToCheck.each{ modelName ->
           def modelAlias=fullExport.getModelAlias(modelName);
           oldRepoResults[modelName]=modelClassMap[modelName].searchEvery("alias:${modelAlias.exactQuery()}",[sort:"id",order:"asc"]);
        }
        def oldInderDir=System.getProperty("index.dir");
        assertTrue(System.getProperty("index.dir").compareTo(EXPORT_CONFIG.backupDir)!=0);

        //we remove all instances check that no instance exists

        modelsToCheck.each{ modelName ->
           modelClassMap[modelName].removeAll()
           assertEquals(0,modelClassMap[modelName].countHits("alias:*"));
        }

        def tempBackupDir="../tempbackupdir";
        assertTrue(tempBackupDir.compareTo(EXPORT_CONFIG.backupDir)!=0)
        def ant=new AntBuilder();
        ant.copy(todir: tempBackupDir) {
            ant.fileset(dir: EXPORT_CONFIG.backupDir)
        }

        this.indexDir=tempBackupDir;
        initializeFullExportModels();
        assertTrue(System.getProperty("index.dir").compareTo(tempBackupDir)==0);
                
        oldRepoResults.each{ modelName , oldObjects ->
            def modelAlias=fullExport.getModelAlias(modelName);
            def newObjects=modelClassMap[modelName].searchEvery("alias:${modelAlias.exactQuery()}",[sort:"id",order:"asc"]);
            assertEquals(oldObjects.size(),newObjects.size());
            oldObjects.size().times{ index ->
                def oldObject=oldObjects[0];
                def newObject=newObjects[0];
                assertNotSame(oldObject,newObject);
                assertEquals(oldObject.id,newObject.id);
                def oldMap=oldObject.asMap();
                oldMap.each{ propName , propVal ->
                    assertEquals(propVal,newObject.getProperty(propName))
                }

            }

        }
        //relation model is imported like other models ( without calling addRelation() )
        //check relations of models is accessible and correct
        def parentObjects=modelClassMap.RsTopologyObject.searchEvery("name:parentObject*");
        assertEquals(5,parentObjects.size());
        parentObjects.each{ parentObject ->
            def childObjects=parentObject.childObjects;
            assertEquals(1,childObjects.size());
            assertEquals(parentObject.name.replace("parent","child"),childObjects[0].name);
            assertEquals(0,childObjects[0].childObjects.size());
            assertEquals(1,childObjects[0].parentObjects.size());
        }

        def ticket1=modelClassMap.RsTicket.get(name:"ticket1");
        def ticket2=modelClassMap.RsTicket.get(name:"ticket2");
        assertEquals(null,ticket1.parentTicket);
        assertEquals(ticket2.id,ticket1.subTickets[0].id);
        assertEquals(ticket1.id,ticket2.parentTicket.id);

        def group1=modelClassMap.RsGroup.get(name:"group1");
        assertEquals(ticket1.id,group1.relatedTickets[0].id);

        def group2=modelClassMap.RsGroup.get(name:"group2");
        assertEquals(ticket1.id,group2.relatedTickets[0].id);
        assertEquals(ticket2.id,group2.relatedTickets[1].id);

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
