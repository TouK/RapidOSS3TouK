package build
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:49:14 AM
 * To change this template use File | Settings | File Templates.
 */
class CompModuleTest extends Test{
    public static void main(args){
		CompModuleTest compTest = new CompModuleTest();
		compTest.run(args);
	}

	def runAllTests(){
		setupForTestExecution();
		runTests("com.ifountain.comp.test.AllTests", env.comp_testreport, "Comp_AllTests", env.comp_testhtml);
	}

	def setupForTestExecution(){
		setClasspathForTestExecution();
		ant.delete(dir : env.comp_testreport);
		ant.delete(dir : env.comp_testhtml);
	    ant.copy(file : "$env.dev_docs/Test.properties", toDir : env.rapid_modules);
	}

	def setClasspathForTestExecution(){
		addProductionLibJarsToClasspath();
		ant.path(id : "testexecutionclasspath"){
			ant.path(refid : "libJars");
			// test execution only jars, not required for production
			ant.pathelement(location : (String)classpath.getProperty("junit_jar"));
			ant.pathelement(location : (String)classpath.getProperty("mysql-connector-java-3_1_8-bin_jar"));
		}
	}
}