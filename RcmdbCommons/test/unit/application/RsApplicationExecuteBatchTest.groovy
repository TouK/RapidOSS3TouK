package application

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 11, 2009
* Time: 10:50:08 PM
* To change this template use File | Settings | File Templates.
*/
class RsApplicationExecuteBatchTest extends RapidCmdbWithCompassTestCase{
    def models;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        initialize ();
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testExecuteBatchWithModelWhichHasNoKeys()
    {
        def executed = false
        long t = System.currentTimeMillis();
        2.times{
            println it;
            RsApplication.executeBatch{
                300.times{
                    models.Model2.add(prop1:"prop1val"+it);
                }
                assertEquals (300, models.Model2.count());
                assertNotNull (models.Model2.search("prop1:prop1val0"));
                models.Model2.list()*.remove()
                assertEquals (0, models.Model2.count());
                executed = true;
            }
        }
        assertTrue (executed)
        println "${System.currentTimeMillis()-t}"
    }

    public void testExecuteBatchWithModelWhichHasKeys()
    {
        def executed = false
        long t = System.currentTimeMillis();
        2.times{
            println it;
            RsApplication.executeBatch{
                300.times{
                    models.Model1.add(prop1:"prop1val"+it);
                }
                assertEquals (300, models.Model1.count());
                assertNotNull (models.Model1.search("prop1:prop1val0"));
                models.Model1.list()*.remove()
                assertEquals (0, models.Model1.count());
                executed = true;
            }
        }
        assertTrue (executed)
        println "${System.currentTimeMillis()-t}"
    }

    def initialize()
    {
        models = [:];
        def model1Name = "Model1";
        def model2Name = "Model2";
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE];
        def prop2 = [name: "prop2", type: ModelGenerator.STRING_TYPE];
        def model1MetaProps = [name: model1Name]
        def model2MetaProps = [name: model2Name]

        def modelProps = [prop1, prop2];
        def keyPropList = [prop1];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, []);
        def model2Text = ModelGenerationTestUtils.getModelText(model2MetaProps, modelProps, [], []);
        gcl.parseClass(model1Text)
        gcl.parseClass(model2Text)
        def model1Class = gcl.loadClass(model1Name)
        def model2Class = gcl.loadClass(model2Name)
        models.Model1 = model1Class
        models.Model2 = model2Class
        initialize ([RsApplication, model1Class, model2Class], []);
        CompassForTests.addOperationSupport (RsApplication, RsApplicationOperations);
    }
}