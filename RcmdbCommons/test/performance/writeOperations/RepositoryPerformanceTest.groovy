package writeOperations

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.method.AddMethodDomainObject1
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import org.apache.commons.io.FileUtils
import com.ifountain.compass.CompositeDirectoryWrapperProvider

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
        _testAddOperationPerformance(CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE, 100, 25);
    }

    private void _testAddOperationPerformance(storageType, numberOfObjectsToBeInserted, expectedNumberOfObjectsToBeInsertedPersecond)
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

    public void testAddWithUpdateOperationPerformanceWithFileStorageType()
    {
        _testUpdateOperationPerformance(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE, 100, 10);
    }

    public void testAddWithUpdateOperationPerformanceWithFileAndMemoryStorageType()
    {
        _testUpdateOperationPerformance(CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE, 100, 30);
    }
}