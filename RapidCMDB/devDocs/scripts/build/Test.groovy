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
//		ant.echo(message : "Running all tests for test class " + testClass + " and will output xml results to " + outputXmlDir + "/" + outputXmlFile);
//		ant.mkdir(dir :  outputXmlDir);
//		ant.junit(printsummary : "yes", haltonfailure : "no", fork : "yes", showoutput : "true"){
//			ant.classpath(refid : "testexecutionclasspath");
//			ant.jvmarg(value : "-Xmx512m");
//			ant.jvmarg(value : "-DWORKSPACE=.");
//			ant.formatter(type :  "xml");
//			ant.test(name : testClass, haltonfailure : "no",  outfile : outputXmlDir + "/" + outputXmlFile){
//				ant.formatter(type : "xml");
//			}
//		}
//        def testSuiteString = "</testsuite>";
//        def xmlFiles = new File(outputXmlDir).listFiles();
//        xmlFiles.each{File f->
//            if(f.getName().endsWith(".xml"))
//            {
//                def lines = f.readLines();
//                def strBuf = new StringBuffer();
//                lines.each{String line->
//                    if(line.indexOf("</testsuite>") < 0)
//                    {
//                        strBuf.append (line).append("\n");
//                    }
//                    else
//                    {
//                        strBuf.append (line.substring(0, line.indexOf(testSuiteString)+testSuiteString.length())).append("\n");
//                        return;
//                    }
//                }
//                println "new content for ${f.getCanonicalPath()} is ${strBuf.toString()}"
//                f.setText(strBuf.toString());
//            }
//        }
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
		File file1 = new File(env.dist_rapid_suite_lib);
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