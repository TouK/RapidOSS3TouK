package build;
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
 * Time: 4:45:18 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidCmdbBuild extends Build{
    static void main(args){
		RapidCmdbBuild rapidCmdbBuilder = new RapidCmdbBuild();
		rapidCmdbBuilder.run(args);
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
		ant.unjar(src:classpath.getProperty("groovy-all-1_5_1_jar"), dest : env.rcomp_build+"/groovy");
		ant.copy(todir : env.rcomp_build+"/groovy/org", overwrite:"true"){
			ant.fileset(dir : env.rcomp_build+"/org");
		}

		ant.mkdir(dir : env.dist_ras_lib);
		ant.jar(destfile : env.dist_ras_lib+"/groovy-all-1.5.1.jar", basedir : env.rcomp_build+"/groovy",update:"true");
		ant.delete(dir : env.rcomp_build+"/org");
		ant.delete(dir : env.rcomp_build+"/groovy");
		ant.jar(destfile : env.RapidComponents_jar, basedir : env.rcomp_build,excludes:"org/**");

		if(isObfuscated){
		    obfuscate();
		}
		else{
		    ant.copy(file : env.RapidComponents_jar, toDir : env.dist_ras_lib);
		}
		copyDependentJars();
		copyProdUtils();
	}


	def copyProdUtils(){
	    ant.copy(todir : env.dist_ras+"/models"){
			ant.fileset(dir : env.rcomp_prod_resources+"/models");
		}
		ant.copy(todir : env.dist_ras+"/scripts"){
			ant.fileset(dir : env.rcomp_prod_resources+"/scripts");
		}
	}

	def copyDependentJars(){
	    ant.copy(file : (String)classpath.getProperty("stax-api-1_0_1_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("stax-1_2_0_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("jakarta-oro-2_0_8_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("commons-betwixt-0_8_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("commons-digester-1_7_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("commons-configuration-1_2_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("commons-collections-3_2_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("commons-beanutils_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("commons-lang-2_1_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("DdlUtils-1_0_jar"), toDir : env.dist_ras_lib );
		ant.copy(file : (String)classpath.getProperty("commons-codec-1_3_jar"), toDir : env.dist_ras_lib);
		ant.copy(file : (String)classpath.getProperty("commons-httpclient-3_0_1_jar"), toDir : env.dist_ras_lib);
		ant.copy(file : (String)classpath.getProperty("commons-logging_jar"), toDir : env.dist_ras_lib);
		ant.copy(file : (String)classpath.getProperty("log4j-1_2_13_jar"), toDir : env.dist_ras_lib);
	}

 	def obfuscate(){
		ant.obfuscate(scriptFileName : env.rapidbuild_src + "/rcompbuild/obfusScript.txt", logFileName : env.zkm_logdir+"/obfuscateLogRCOMP.txt", trimLogFileName : env.zkm_logdir+"/obfuscate_trim_log.txt", defaultExcludeFileName : env.zkm_logdir+"/obfuscate_defaultExclude.txt", defaultTrimExcludeFileName : env.zkm_logdir+"/obfuscate_defaultTrimExclude.txt", defaultDirectoryName : "." );
		ant.copy(file : env.zkm_jardir + "/RapidComponents.jar", toDir : env.dist_ras_lib );
	}

	def clean(){
		ant.delete(dir : env.rapid_comp_build);
		ant.mkdir(dir : env.rapid_comp_build);
	}

	def compile(){
		ant.javac(srcdir : env.rcomp_src, destdir : env.rcomp_build, excludes: getExcludedClasses()){
			ant.classpath(refid : "classpath");
		}
	}

	def copyResourcesForJar(){
		ant.copy(file : env.rcomp_src + "/rcomp/gui/PropertyPrompts.properties", toDir : env.rcomp_build + "/rcomp/gui");
		ant.copy(file : env.rcomp_src + "/rcomp/exception/ExceptionMessages.properties", toDir : env.rcomp_build + "/rcomp/exception");
	}
}