package build;

class Test extends Parent{
	
	void run(args){
		if(args.size() >0){
			println "Running target " + args[0];
			invokeMethod(args[0], null);
		}
		else{
			println "Running default target";
			runAllTests();
		}
		println "Done";
	}
	
	def runTests(String testClass, String outputXmlDir, String outputXmlFile, String htmlDir){
		ant.echo(message : "Running all tests for test class " + testClass + " and will output xml results to " + outputXmlDir + "/" + outputXmlFile);
		ant.mkdir(dir :  outputXmlDir);
		ant.junit(printsummary : "yes", haltonfailure : "no", fork : "yes", showoutput : "true"){
			ant.classpath(refid : "testexecutionclasspath");
			ant.jvmarg(value : "-Xmx512m");
			ant.jvmarg(value : "-DWORKSPACE=.");
			ant.formatter(type :  "xml");
			ant.test(name : testClass, haltonfailure : "no",  outfile : outputXmlDir + "/" + outputXmlFile){
				ant.formatter(type : "xml");
			}
		}
		
//		ant.mkdir(dir :  htmlDir);
//		ant.junitreport(todir : htmlDir ){
//			ant.fileset(dir : outputXmlDir){
//				ant.include(name : "*.xml");
//			}
//			ant.report(format : "frames", todir : htmlDir);
//		}
	}
	
	def Properties getLibJars(){
		Properties libJars = new Properties();
		File file1 = new File(env.dist_rapid_cmdb_lib);
		File file2 = new File(env.dist_rapid_server_lib);
		env.getLibProperties(libJars, file1);
		env.getLibProperties(libJars, file2);
		return libJars;
	}
	
	def addProductionLibJarsToClasspath(){
		Properties libJars = getLibJars();
		Enumeration jarKeys = libJars.keys();
		ant.path(id : "libJars"){
			while(jarKeys.hasMoreElements()){
				String jarName = (String) jarKeys.nextElement();
				ant.pathelement(location : libJars.getProperty(jarName));
			}
		}
	}	
}