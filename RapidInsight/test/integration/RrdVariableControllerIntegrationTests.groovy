import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import javax.imageio.ImageIO
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.util.DataStore

/**
* User: ifountain
* Date: Jul 1, 2009
* Time: 3:55:32 PM
*/
class RrdVariableControllerIntegrationTests extends RapidCmdbIntegrationTestCase {

    static transactional = false;

    public void setUp() {
        super.setUp();
    }

    public void tearDown() {
        loadOriginalOperation()
        super.tearDown();
    }

    private void overrideOperation() {

        File opFile=new File("operations/RrdVariableOperations.groovy");
        opFile.setText("""
            import javax.imageio.ImageIO
            import java.awt.image.BufferedImage
            import java.awt.Graphics
            import java.awt.Color
            import org.apache.commons.io.output.ByteArrayOutputStream
            import com.ifountain.rcmdb.domain.util.ControllerUtils
            import com.ifountain.rcmdb.util.DataStore

            public class RrdVariableOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
            {
                def graph(Map config) {
                    DataStore.put("graphTestConfig",config);
                    def bufImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
                    Graphics g = bufImage.getGraphics();
                    g.setColor(new Color(255, 0, 0));
                    g.fillRect(0,0,5,5);
                    g.dispose();

                    def webResponse=DataStore.get("webResponse");
                    ControllerUtils.drawImageToWeb (bufImage,"image/png","png",webResponse);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufImage, 'png', baos);
                    byte[] bytesOut = baos.toByteArray();

                    DataStore.put("imageBytes",bytesOut);

                    return bytesOut;
                }
            }
        """)
        RrdVariable.reloadOperations();
    }

    private void loadOriginalOperation() {

        FileUtils.copyFileToDirectory(new File("../../../RapidModules/RapidInsight/operations/RrdVariableOperations.groovy"),new File("operations"));
        RrdVariable.reloadOperations();

    }

    public void testGraphDrawnSuccessfuly() {
        overrideOperation();


        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        def controller = new RrdVariableController();
        DataStore.put("webResponse",controller.response);
        controller.params["name"] = "variable";
        controller.params["startTime"] = "920804400000"
        controller.params["endTime"] = "920806200000"
        controller.graph();

        def graphMethodConfig = DataStore.get("graphTestConfig");

        assertEquals(920804400000L, graphMethodConfig.startTime)
        assertEquals(920806200000L, graphMethodConfig.endTime)
        assertEquals('web', graphMethodConfig.destination)


        byte[] content = controller.response.getContentAsByteArray()
        byte[] realData = DataStore.get("imageBytes");

        assertEquals(realData.length, content.length)

        for(int i = 0; i < realData.length; i++)
            assertEquals(realData[i], content[i])

    }

    public void testGraphDrawsExceptionGrapIfExceptionOccurs() {
        def controller = new RrdVariableController();
        controller.params["name"] = "NotExistentVariable";
        controller.graph();

        assertEquals("image/png", controller.response.getContentType())
    }

    public void testGraphBuildConfigurationFromParameters() {
        overrideOperation();

        def variable = RrdVariable.add(name:"TestVariable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        def controller = new RrdVariableController();
        DataStore.put("webResponse",controller.response);
        
        controller.params["name"] = "TestVariable"
        controller.params["template"] = "TestTemplate"
        controller.params["title"] = "TestTitle"
        controller.params["color"] = "TestColor"
        controller.params["thickness"] = "TestThickness"
        controller.params["type"] = "TestType"
        controller.params["rpn"] = "TestRPN"
        controller.params["startTime"] = "123456789"
        controller.params["endTime"] = "987654321"
        controller.params["verticalLabel"] = "TestVerticalLabel"
        controller.params["description"] = "TestDescription"
        controller.graph();

        def graphMethodConfig = DataStore.get("graphTestConfig");
        def expectedConfig = [:]

        assertEquals("TestTemplate",graphMethodConfig.template)
        assertEquals("TestTitle",graphMethodConfig.title)
        assertEquals("TestColor", graphMethodConfig.color)
        assertEquals("TestThickness", graphMethodConfig.thickness)
        assertEquals("TestType", graphMethodConfig.type)
        assertEquals("TestRPN", graphMethodConfig.rpn)
        assertEquals(123456789L, graphMethodConfig.startTime)
        assertEquals(987654321L, graphMethodConfig.endTime)
        assertEquals("TestVerticalLabel", graphMethodConfig.vlabel)
        assertEquals("TestDescription", graphMethodConfig.description)
        assertEquals('web',graphMethodConfig.destination)

    }


}
