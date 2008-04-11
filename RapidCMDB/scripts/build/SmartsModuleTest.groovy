package build;

class SmartsModuleTest extends Test{
	
 	public static void main(args){
		SmartsModuleTest smartsTest = new SmartsModuleTest();
		smartsTest.run(args);
	}
	
	def runAllTests(){
		setupForTestExecution();
		runTests("com.ifountain.smarts.test.AllTests", env.smarts_testreport, "Smarts_AllTests", env.smarts_testhtml);		
	}
	
	def setupForTestExecution(){
		setClasspathForTestExecution();
		ant.delete(dir : env.smarts_testreport);
		ant.delete(dir : env.smarts_testhtml);		
	}
	
	def setClasspathForTestExecution(){
		addProductionLibJarsToClasspath();
		ant.path(id : "testexecutionclasspath"){
			ant.path(refid : "libJars");
			// test execution only jars, not required for production
			ant.pathelement(location : (String)classpath.getProperty("junit_jar"));
			ant.pathelement(location : (String)classpath.getProperty("ant-1_6_5_jar"));
			ant.pathelement(location : (String)classpath.getProperty("skclient_jar"));
			ant.pathelement(location : (String)classpath.getProperty("net_jar"));
			ant.pathelement(location : (String)classpath.getProperty("mysql-connector-java-3_1_8-bin_jar"));
		}
	}
	
}
