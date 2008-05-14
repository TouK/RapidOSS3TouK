package build
/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Mar 19, 2008
 * Time: 1:37:14 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidExtBuild extends Build{
    public static void main(String[] args){
		RapidExtBuild rapidExtBuilder = new RapidExtBuild();
		rapidExtBuilder.run(args);
	}
    def String getExcludedClasses(){
		if (!TEST){
			return "**/*Test*, **/*Mock*, **/test/**";
		}
		return "";
	}

	def build(){
        clean();
        compile();
		copyResourcesForJar();
		ant.jar(destfile : env.rapid_ext_jar, basedir : env.rapid_ext_build);
        ant.copy(file : env.rapid_ext_jar, toDir : env.dist_rapid_cmdb_lib);

		copyDependentJars();
	}

	def copyDependentJars(){
	}

    def clean(){
		ant.delete(dir : env.rapid_ext_build);
		ant.mkdir(dir : env.rapid_ext_build);
	}
	def compile(){
		ant.javac(srcdir : "$env.rapid_ext/database/java", destdir : env.rapid_ext_build, excludes: getExcludedClasses()){
			ant.classpath(refid : "classpath");
		}
        ant.javac(srcdir : "$env.rapid_ext/snmp/java", destdir : env.rapid_ext_build, excludes: getExcludedClasses()){
			ant.classpath(refid : "classpath");
		}
//		ant.javac(srcdir : "$env.rapid_ext/smarts/java", destdir : env.rapid_ext_build, excludes: getExcludedClasses()){
//			ant.classpath(refid : "classpath");
//		}
	}


	def copyResourcesForJar(){
        ant.copy(todir : "$env.dist_rapid_cmdb/grails-app/ext"){
			ant.fileset(dir : "$env.rapid_ext/database/groovy"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")    
                }
            };
//			ant.fileset(dir : "$env.rapid_ext/smarts/groovy"){
//                ant.exclude(name:"**/test/**")
//                ant.exclude(name:"**/*Test*")
//            };
            ant.fileset(dir : "$env.rapid_ext/rapidinsight/groovy"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")    
                }
            };
            ant.fileset(dir : "$env.rapid_ext/http/groovy"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")
                }
            };
		}
    }
}