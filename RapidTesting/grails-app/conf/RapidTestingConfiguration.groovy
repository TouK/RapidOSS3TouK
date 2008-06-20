/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 20, 2008
 * Time: 3:47:53 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidTestingConfiguration {
    def baseDir = System.getProperty("base.dir");
    def testDirectories = [new File("${baseDir}/test/unit"),
    new File("${baseDir}/test/integration")]
    
}