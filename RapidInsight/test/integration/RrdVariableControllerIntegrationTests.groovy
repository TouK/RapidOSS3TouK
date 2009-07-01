import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import com.ifountain.comp.test.util.file.TestFile
import javax.imageio.ImageIO

/**
* User: ifountain
* Date: Jul 1, 2009
* Time: 3:55:32 PM
*/
class RrdVariableControllerIntegrationTests extends RapidCmdbIntegrationTestCase {
      static transactional = false;

      String fileName = TestFile.TESTOUTPUT_DIR + "/test.rrd";

      public void setUp() {
          super.setUp();
          def rrdFile = new File(fileName);
          rrdFile.mkdirs();
          rrdFile.delete();
      }

      public void tearDown() {
          new File(fileName).delete();
          super.tearDown();
      }

      public void testGraphDrawnSuccessfuly()
      {
          def archive = RrdArchive.add(name:"archive", function:"AVERAGE", xff:0.5, step:1, row:24)
          assertFalse(archive.errors.toString(), archive.hasErrors())

          def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                         file: fileName, startTime:920804400000L, step:300, archives: [archive])
          assertFalse(variable.errors.toString(), variable.hasErrors())

          variable.createDB()

          variable.updateDB(time:920804700000L, value:12345)
          variable.updateDB(time:920805000000L, value:12357)
          variable.updateDB(time:920805300000L, value:12363)
          variable.updateDB(time:920805600000L, value:12363)
          variable.updateDB(time:920805900000L, value:12363)

          def controller = new RrdVariableController();
          controller.params["name"] = "variable";
          controller.params["startTime"] = "920804400000"
          controller.params["endTime"] = "920806200000"
          controller.graph();

          //org.springframework.mock.web.MockHttpServletResponse

          byte[] content = controller.response.getContentAsByteArray()

          byte[] realData = variable.graph([startTime:920804400000L, endTime:920806200000L]);

          println "Content:" + content
          println "realData   :" + realData


//          DataOutputStream dos = new DataOutputStream( new FileOutputStream("testContImage.png"))
//          dos.write(content)

          ByteArrayOutputStream bos=new ByteArrayOutputStream(realData.size());

          InputStream inn = new ByteArrayInputStream(realData);
          def image =  ImageIO.read(inn);
          ImageIO.write(image, "png", bos);
          //ImageIO.write(image, "png", new File("test2.png"));

          byte[] compressedData = bos.toByteArray();
          println "dataimage :"+compressedData
          


          assertEquals(compressedData.length, content.length)

          for(int i = 0; i < compressedData.length; i++)
                assertEquals(compressedData[i], content[i])

      }
//      public void testGraphDrawsExceptionGrapIfExceptionOccurs()
//      {
//
//      }
//      public void testGraphBuildConfigurationFromParameters()
//      {
//
//      }


}
