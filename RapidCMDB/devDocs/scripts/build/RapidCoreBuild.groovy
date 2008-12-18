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
package build
/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Mar 19, 2008
 * Time: 1:34:35 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidCoreBuild extends Build{
    public static void main(String[] args){
		RapidCoreBuild rapidCoreBuilder = new RapidCoreBuild();
		rapidCoreBuilder.build();
	}

	def String getExcludedClasses(){
		if (!TEST){
			return "**/*Test*, **/*Mock*";
		}
		return "";
	}

	def build(){
		clean();
		compile();
		copyResourcesForJar();
		ant.jar(destfile : env.rapid_core_jar, basedir : env.rapid_core_build, manifest : env.versionInBuild);
//        ant.copy(file : env.rapid_core_jar, toDir : env.dist_rapid_suite_lib);
//        ant.copy(file : env.rapid_core_jar, toDir : env.dist_modeler_lib);
		copyDependentJars();
	}

	def copyDependentJars(){
	}


	def clean(){
		ant.delete(dir : env.rapid_core_build);
		ant.mkdir(dir : env.rapid_core_build);
	}

	def compile(){
		ant.javac(srcdir : env.rapid_core_src, destdir : env.rapid_core_build, excludes: getExcludedClasses()){
			ant.classpath(refid : "classpath");
		}
	}

	def copyResourcesForJar(){
	}
}