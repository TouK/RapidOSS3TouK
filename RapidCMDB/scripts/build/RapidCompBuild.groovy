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
 * User: Administrator
 * Date: Mar 18, 2008
 * Time: 4:50:07 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidCompBuild extends Build{
    public static void main(String[] args){
		RapidCompBuild rapidCompBuilder = new RapidCompBuild();
		rapidCompBuilder.run(args);
	}

	def String getExcludedClasses(){
		if (!TEST){
			return "**/*Test*, **/*Mock*, rcomp/test/**";
		}
		return "";
	}

	def build(){
		clean();
		compile();
		copyResourcesForJar();
		ant.jar(destfile : env.rapid_comp_jar, basedir : env.rapid_comp_build);
        ant.copy(file : env.rapid_comp_jar, toDir : env.distribution_lib);
		copyDependentJars();
	}

	def copyDependentJars(){
	}


	def clean(){
		ant.delete(dir : env.rapid_comp_build);
		ant.mkdir(dir : env.rapid_comp_build);
	}

	def compile(){
		ant.javac(srcdir : env.rapid_comp_src, destdir : env.rapid_comp_build, excludes: getExcludedClasses()){
			ant.classpath(refid : "classpath");
		}
	}

	def copyResourcesForJar(){
        ant.copy(todir : env.rapid_comp_build){
			ant.fileset(dir : "$env.rapid_comp_resources/");
		}
	}
}