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

class SmartsModuleTest extends Test{
	
 	static void main(String[] args) {
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
	    ant.copy(file : "$env.dev_docs/Test.properties", toDir : env.rapid_modules);
	}
	
	def setClasspathForTestExecution(){
		addProductionLibJarsToClasspath();
		ant.path(id : "testexecutionclasspath"){
			ant.path(refid : "libJars");
			// test execution only jars, not required for production
			ant.pathelement(location : (String)classpath.getProperty("junit_jar"));
			ant.pathelement(location : (String)classpath.getProperty("skclient_jar"));
			ant.pathelement(location : (String)classpath.getProperty("net_jar"));
		}
	}
	
}
