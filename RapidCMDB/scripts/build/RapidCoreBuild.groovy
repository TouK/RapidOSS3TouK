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
		rapidCoreBuilder.run(args);
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
		ant.jar(destfile : env.rapid_core_jar, basedir : env.rapid_core_build);
        ant.copy(file : env.rapid_core_jar, toDir : env.distribution_lib);
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