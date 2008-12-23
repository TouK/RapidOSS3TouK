/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package performance.writeOperations

import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 3, 2008
 * Time: 11:30:55 PM
 * To change this template use File | Settings | File Templates.
 */
class RepositoryPerformanceTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp();
    }

    public void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testAddOperationPerformanceWithFileStorageType()
    {
        _testAddOperationPerformance(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE, 100, 10);
    }

    public void testAddOperationPerformanceWithFileAndMemoryStorageType()
    {
        _testAddOperationPerformance(CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE, 100, 20);
    }

    private void _testAddOperationPerformance(storageType, numberOfObjectsToBeInserted, expectedNumberOfObjectsToBeInsertedPersecond)
    {
        Class modelClass = intializeCompassWithSimpleObjects(storageType);
        long t = System.nanoTime();
        for(int i=0; i < numberOfObjectsToBeInserted; i++)
        {
            modelClass.'add'(keyProp:"keyPropValue"+i);
        }
        long totalTime = (System.nanoTime() - t)/Math.pow(10,9);
        assertEquals(numberOfObjectsToBeInserted, modelClass.'search'("alias:*").total);
        def numberOfObjectsInsertedPerSecond = numberOfObjectsToBeInserted/totalTime;
        println "Number of objects inserted per second:"+numberOfObjectsInsertedPerSecond
        assertTrue ("Number of inserted objects ${numberOfObjectsInsertedPerSecond} should be greater than ${expectedNumberOfObjectsToBeInsertedPersecond}", numberOfObjectsInsertedPerSecond > expectedNumberOfObjectsToBeInsertedPersecond);
    }

    private void _testUpdateOperationPerformance(storageType, numberOfObjectsToBeInserted, expectedNumberOfObjectsToBeInsertedPersecond)
    {
        Class modelClass = intializeCompassWithSimpleObjects(storageType);

        for(int i=0; i < numberOfObjectsToBeInserted; i++)
        {
            modelClass.'add'(keyProp:"keyPropValue"+i);
        }
        assertEquals(numberOfObjectsToBeInserted, modelClass.'search'("alias:*").total);

        long t = System.nanoTime();
        for(int i=0; i < numberOfObjectsToBeInserted; i++)
        {
            modelClass.'add'(keyProp:"keyPropValue"+i);
        }
        long totalTime = (System.nanoTime() - t)/Math.pow(10,9);
        def numberOfObjectsInsertedPerSecond = numberOfObjectsToBeInserted/totalTime;
        println "Number of objects update per second:"+numberOfObjectsInsertedPerSecond
        assertTrue ("Number of updated objects ${numberOfObjectsInsertedPerSecond} should be greater than ${expectedNumberOfObjectsToBeInsertedPersecond}", numberOfObjectsInsertedPerSecond > expectedNumberOfObjectsToBeInsertedPersecond);
    }

    private void _testRemoveOperationPerformance(storageType, numberOfObjectsToBeInserted, expectedNumberOfObjectsToBeInsertedPersecond)
    {

        Class modelClass = intializeCompassWithSimpleObjects(storageType);
        def modelIntances = [];
        for(int i=0; i < numberOfObjectsToBeInserted; i++)
        {
            modelIntances.add(modelClass.'add'(keyProp:"keyPropValue"+i));
        }
        assertEquals(numberOfObjectsToBeInserted, modelClass.'search'("alias:*").total);

        long t = System.nanoTime();
        modelIntances*.remove();
        long totalTime = (System.nanoTime() - t)/Math.pow(10,9);

        def numberOfObjectsInsertedPerSecond = numberOfObjectsToBeInserted;
        if(totalTime > 0)
        {
            numberOfObjectsInsertedPerSecond = numberOfObjectsToBeInserted/totalTime;
        }
        println "Number of objects deleted per second:"+numberOfObjectsInsertedPerSecond
        assertEquals(0, modelClass.'search'("alias:*").total);
        assertTrue ("Number of deleted objects ${numberOfObjectsInsertedPerSecond} should be greater than ${expectedNumberOfObjectsToBeInsertedPersecond}", numberOfObjectsInsertedPerSecond > expectedNumberOfObjectsToBeInsertedPersecond);
    }



    def intializeCompassWithSimpleObjects(String storageType)
    {
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        String propValue = "ThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValue"
        def modelMetaProps = [name:"Model1", storageType:storageType]
        def modelProps = [keyProp];
        for(int i=0; i < 50; i++)
        {
            def prop = [name:"prop"+i, type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:propValue]
            modelProps.add(prop);

        }
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        Class modelClass = this.gcl.parseClass(modelString);
        initialize([modelClass],[], true);
        return modelClass;
    }

    def intializeCompassWithObjectsHavingRelations(String storageType)
    {
        def modelName1 = "Model1";
        def modelName2 = "Model2";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName1, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        String propValue = "ThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValue"
        def model1MetaProps = [name:modelName1, storageType:storageType]
        def model2MetaProps = [name:modelName2, storageType:storageType]
        def modelProps = [keyProp];

        def keyPropList = [keyProp];
        String model1String = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, [rel1])
        String model2String = ModelGenerationTestUtils.getModelText(model2MetaProps, modelProps, keyPropList, [revrel1])
        this.gcl.parseClass(model1String+model2String);
        Class model1Class = this.gcl.loadClass(modelName1);
        Class model2Class = this.gcl.loadClass(modelName2);
        initialize([model1Class, model2Class],[], true);
        return [model1Class, model2Class];
    }

    public void testAddWithUpdateOperationPerformanceWithFileStorageType()
    {
        _testUpdateOperationPerformance(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE, 100, 10);
    }

    public void testAddWithUpdateOperationPerformanceWithFileAndMemoryStorageType()
    {
        _testUpdateOperationPerformance(CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE, 100, 20);
    }

    public void testRemoveOperationPerformanceWithFileStorageType()
    {
        _testRemoveOperationPerformance(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE, 100, 90);
    }

    public void testRemoveOperationPerformanceWithFileAndMemoryStorageType()
    {
        _testRemoveOperationPerformance(CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE, 100, 90);
    }

    public void testAddRemoveRelationOperationPerformanceWithAddingRelationToSingleInstance()
    {
        def numberOfRelationsToBeInserted = 100;
        def expectedNumberOfRelationsToBeInsertedPersecond = 95;
        def expectedNumberOfRelationsToBeRemovedPersecond = 95;
        def modelClasses = intializeCompassWithObjectsHavingRelations(CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE);
        def relatedModelInstances = [];
        def singleModelInstance = modelClasses[0].'add'(keyProp:"keyPropValue0")
        for(int i=0; i < numberOfRelationsToBeInserted; i++)
        {
            relatedModelInstances.add(modelClasses[1].'add'(keyProp:"keyPropValue"+i));
        }
        assertEquals(1, modelClasses[0].'search'("alias:*").total);
        assertEquals(numberOfRelationsToBeInserted, modelClasses[1].'search'("alias:*").total);

        long t = System.nanoTime();
        singleModelInstance.addRelation(rel1:relatedModelInstances);
        long totalTime = (System.nanoTime() - t)/Math.pow(10,9);

        def numberOfRelationsInsertedPerSecond = numberOfRelationsToBeInserted;
        if(totalTime > 0)
        {
            numberOfRelationsInsertedPerSecond = numberOfRelationsToBeInserted/totalTime;
        }
        println "Number of relation inserted to one object per second:"+numberOfRelationsInsertedPerSecond
        assertEquals(numberOfRelationsToBeInserted, singleModelInstance.rel1.size());
        assertTrue ("Number of added relations ${numberOfRelationsInsertedPerSecond} should be greater than ${expectedNumberOfRelationsToBeInsertedPersecond}", numberOfRelationsInsertedPerSecond > expectedNumberOfRelationsToBeInsertedPersecond);


        t = System.nanoTime();
        singleModelInstance.removeRelation(rel1:relatedModelInstances);
        totalTime = (System.nanoTime() - t)/Math.pow(10,9);

        def numberOfDeletedRelationsPerSecond = numberOfRelationsToBeInserted;
        if(totalTime > 0)
        {
            numberOfDeletedRelationsPerSecond = numberOfRelationsToBeInserted/totalTime;
        }
        println "Number of deleted relations from one object per second:"+numberOfDeletedRelationsPerSecond
        assertEquals(0, singleModelInstance.rel1.size());
        assertTrue ("Number of deleted relations ${numberOfRelationsInsertedPerSecond} should be greater than ${expectedNumberOfRelationsToBeRemovedPersecond}", numberOfDeletedRelationsPerSecond > expectedNumberOfRelationsToBeRemovedPersecond);

    }

    public void testAddRemoveRelationOperationPerformanceWithAddingRelationToMultipleInstance()
    {
        def numberOfRelationsToBeInserted = 100;
        def expectedNumberOfRelationsToBeInsertedPersecond = 10;
        def expectedNumberOfRelationsToBeRemovedPersecond = 10;
        def modelClasses = intializeCompassWithObjectsHavingRelations(CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE);
        def relatedModelInstancesGroup1 = [];
        def relatedModelInstancesGroup2 = [];
        for(int i=0; i < numberOfRelationsToBeInserted; i++)
        {
            relatedModelInstancesGroup1.add(modelClasses[0].'add'(keyProp:"keyPropValue"+i));
            relatedModelInstancesGroup2.add(modelClasses[1].'add'(keyProp:"keyPropValue"+i));
        }
        assertEquals(numberOfRelationsToBeInserted, modelClasses[0].'search'("alias:*").total);
        assertEquals(numberOfRelationsToBeInserted, modelClasses[1].'search'("alias:*").total);

        long t = System.nanoTime();
        for(int i=0; i < numberOfRelationsToBeInserted; i++)
        {
            relatedModelInstancesGroup1[i].addRelation(rel1:relatedModelInstancesGroup2[i]);
        }
        long totalTime = (System.nanoTime() - t)/Math.pow(10,9);

        def numberOfRelationsInsertedPerSecond = numberOfRelationsToBeInserted;
        if(totalTime > 0)
        {
            numberOfRelationsInsertedPerSecond = numberOfRelationsToBeInserted/totalTime;
        }
        println "Number of inserted relations to one object per second:"+numberOfRelationsInsertedPerSecond
        for(int i=0; i < numberOfRelationsToBeInserted; i++)
        {
            def group1Obj = modelClasses[0].'get'(keyProp:"keyPropValue"+i)
            assertEquals(relatedModelInstancesGroup2[i].id, group1Obj.rel1[0].id);
        }
        assertTrue ("Number of inserted relations ${numberOfRelationsInsertedPerSecond} should be greater than ${expectedNumberOfRelationsToBeInsertedPersecond}", numberOfRelationsInsertedPerSecond > expectedNumberOfRelationsToBeInsertedPersecond);


        t = System.nanoTime();
        for(int i=0; i < numberOfRelationsToBeInserted; i++)
        {
            relatedModelInstancesGroup1[i].removeRelation(rel1:relatedModelInstancesGroup2[i]);
        }
        totalTime = (System.nanoTime() - t)/Math.pow(10,9);

        def numberOfRelationsDeletedPerSecond = numberOfRelationsToBeInserted;
        if(totalTime > 0)
        {
            numberOfRelationsDeletedPerSecond = numberOfRelationsToBeInserted/totalTime;
        }
        println "Number of deleted relations to one object per second:"+numberOfRelationsDeletedPerSecond
        for(int i=0; i < numberOfRelationsToBeInserted; i++)
        {
            def group1Obj = modelClasses[0].'get'(keyProp:"keyPropValue"+i)
            assertEquals(0, group1Obj.rel1.size());
        }
        assertTrue ("Number of deleted relations ${numberOfRelationsDeletedPerSecond} should be greater than ${expectedNumberOfRelationsToBeRemovedPersecond}", numberOfRelationsDeletedPerSecond > expectedNumberOfRelationsToBeRemovedPersecond);
    }
}