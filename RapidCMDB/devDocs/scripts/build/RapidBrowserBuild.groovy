package build
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 23, 2009
 * Time: 3:49:51 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidBrowserBuild extends Build  {
    def version = "${env.rapid_browser_svn}/RapidBrowserVersion.txt";
    static void main(String[] args) {
        RapidBrowserBuild rapidBrowserBuild = new RapidBrowserBuild();
        rapidBrowserBuild.build();
    }


    def clean(distDir) {
        ant.delete(dir: env.distribution);
        ant.mkdir(dir: env.distribution);
    }

    def build() {
    	build(env.distribution);
    }
    def build(distDir) {
        println "Starting Build RapidBrowser";
        println System.getProperties();
        def versionInBuild = "${env.dist_rapid_browser}/RapidBrowserVersion.txt";
    	clean(distDir);
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);

        def versionDate = getVersionWithDate();
        ant.copy(todir: "${env.dist_rapid_browser}/conf") {
            ant.fileset(dir: "$env.rapid_browser_svn/conf", excludes:".svn");
        }
        ant.copy(todir: "${env.dist_rapid_browser}/lib") {
            ant.fileset(dir: "$env.rapid_browser_svn/lib", excludes:".svn");
        }
        ant.copy(todir: "${env.dist_rapid_browser}") {
            ant.fileset(file: "$env.rapid_browser_svn/rapidbrowser.bat")
            ant.fileset(file: "$env.rapid_browser_svn/rapidbrowser.sh")
            ant.fileset(file: "$env.rapid_browser_svn/readme.txt")
        }
        ant.copy(todir: "${env.dist_rapid_browser}/webapps") {
            ant.fileset(dir: "$env.rapid_browser_svn/webapps", excludes:".svn")
        }
        ant.mkdir(dir:env.rapid_browser_build);
        ant.javac(srcdir:"${env.rapid_browser_svn}/src", destdir:env.rapid_browser_build, executable:"/opt/jdk1.5.0_12/bin/javac", fork:"yes"){
            ant.classpath(refid : "classpath");
        }

        ant.mkdir(dir:"${env.dist_rapid_browser}/webapps/RapidBrowser/WEB-INF/lib/");
        ant.jar(basedir:env.rapid_browser_build, destfile:"${env.dist_rapid_browser}/webapps/RapidBrowser/WEB-INF/lib/RapidBrowser.jar");
        ant.copy(todir:"${env.dist_rapid_browser}/lib"){
            ant.fileset(file:"../ThirdParty/lib/log4j-1.2.13.jar")
            ant.fileset(file:"../ThirdParty/lib/jetty/jetty-6.1.7.jar")
            ant.fileset(file:"../ThirdParty/lib/jetty/jetty-util-6.1.7.jar")
            ant.fileset(file:"../ThirdParty/lib/jetty/servlet-api-2.5-6.1.7.jar")
            ant.fileset(file:"../ThirdParty/lib/jetty/start-6.1.7.jar")
            ant.fileset(dir:"../ThirdParty/lib/jsp")
        }
        ant.zip(zipfile:"${env.distribution}/RapidBrowser${versionDate}.zip"){
            ant.zipfileset(dir:"${env.dist_rapid_browser}", prefix:"RapidBrowser");
        }

        println "RapidBrowser Build Done";
    }
}