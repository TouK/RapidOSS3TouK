/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 16, 2008
 * Time: 3:03:32 PM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.commons.io.FileUtils;
class TestDeployer {
   def buildBasePath="http://192.168.1.130:8080/job/ManualTestingBuild/ws/Distribution/";
   def deploymentBasePath="d:"+File.separator+"manualTestingSpace"+File.separator;
   def ant;
   def manualTestingTempPath;
   
   public static void main(String[] args) {

       TestDeployer deployer=new TestDeployer();

       //deployer.run(args);
       deployer.run(["deployModelOperations"]);

    }
    void run(args) {
        ant=new AntBuilder();
        manualTestingTempPath=deploymentBasePath+"manualTesting/";
       
        executeOnServer(" -stop");

        ant.delete(dir:deploymentBasePath)

        if(args.size() >0){
			println "Running target " + args[0];
			invokeMethod(args[0], null);
		}
		else{
			println "No targets found";
		}

        executeOnServer(" -stop");
        executeOnServer(" -uninstall");
        executeOnServer(" -install");
		executeOnServer(" -start");
        println "Done";
				
    }
    void executeOnServer(args)
    {
        println "Call to rs.exe with args ${args}";
        try{
        ant.exec(
             outputproperty:"cmdOut",
             errorproperty: "cmdErr",
             resultproperty:"cmdExit",
             failonerror: "false",
             executable: deploymentBasePath+"RapidServer/RapidSuite/rs.exe")
             {
                 arg(line:args)
             }
        }
        catch(e)
        {
            println "Exception occured while calling rs.exe with args ${args}. Reason:"+e;
        }

//        println "return code:  ${ant.project.properties.cmdExit}"
//        println "stderr:         ${ant.project.properties.cmdErr}"
//        println "stdout:        ${ ant.project.properties.cmdOut}"
    }
    void deployModelOperations()
    {

       File riTarget=new File(deploymentBasePath+"RI_Windows.zip");
       FileUtils.copyURLToFile(new java.net.URL("${buildBasePath}RI_Windows.zip"),riTarget);

       File manualTestingTarget=new File(deploymentBasePath+"ManualTesting.zip");
       FileUtils.copyURLToFile(new java.net.URL("${buildBasePath}ManualTesting.zip"),manualTestingTarget);

       ant.unzip(src:riTarget.getPath(), dest:deploymentBasePath);
       ant.unzip(src:manualTestingTarget.getPath(), dest:manualTestingTempPath);
       ant.copy(todir: deploymentBasePath) {
            ant.fileset(dir: "${manualTestingTempPath}default") {
            }
       }
        ant.copy(todir: deploymentBasePath) {
            ant.fileset(dir: "${manualTestingTempPath}ModelOperations") {
            }
        }
        ant.delete(dir:manualTestingTempPath)

       
  
    }
}