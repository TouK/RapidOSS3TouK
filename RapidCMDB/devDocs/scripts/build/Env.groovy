package build;

class Env {
	
	AntBuilder ant = null;
	def thirdPartyJars = new Properties();
	static def basedir = "..";
// Common environment properties	
	static def jreDir = basedir+"/jre1.6.0_04";
	static def save = basedir+"/save";
	static def distribution = basedir+"/Distribution";
	static def testresults = basedir+"/TestResults";
	static def dist_rapid_server = "$distribution/RapidServer";
	static def dist_rapid_suite = "$dist_rapid_server/RapidSuite";
	static def dist_modules = "$distribution/Modules";
	static def dist_modules_rapid_suite = "$dist_modules/RapidSuite";
	static def dist_modeler = "$dist_rapid_server/Modeler";
	static def dist_rapid_suite_lib = dist_rapid_suite + "/lib";
	static def dist_modeler_lib = dist_modeler + "/lib";
	static def dist_rapid_server_lib = "$distribution/RapidServer/lib";

    static def rapid_modules = basedir+"/RapidModules"
    static def rapid_cmdb_cvs = "$rapid_modules/RapidCMDB"
    static def rapid_cmdb_modeler_cvs = "$rapid_modules/RapidCMDBModeler"
    static def rapid_cmdb_commons_cvs = "$rapid_modules/RcmdbCommons"
    static def dev_docs = "$rapid_cmdb_cvs/devDocs"
    static def version = "$rapid_cmdb_commons_cvs/version.txt"
    static def invalidNames = "$rapid_cmdb_modeler_cvs/invalidNames.txt"
    static def versionInBuild = "$dist_rapid_server/version.txt"
    static def rapid_comp_build = basedir+"/build/comp"
    static def rapid_comp_src = "$rapid_modules/comp/java"
    static def rapid_comp_resources = "$rapid_modules/comp/resources"

    static def rapid_core_build = basedir+"/build/core"
    static def rapid_core_src = "$rapid_modules/core/java"
    static def rapid_core_resources = "$rapid_modules/core/resources"

    static def rapid_ext = "$rapid_modules/ext"
    static def rapid_ext_build = basedir+"/build/ext"
    static def rapid_smarts_build = basedir+"/build/smarts"

    static def rapid_insight = "$rapid_modules/RapidInsight"
    static def rapid_netcool = "$basedir/Netcool"
    static def rapid_smarts = "$basedir/Smarts"
    static def rapid_hyperic = "$basedir/Hyperic"
    static def rapid_opennms = "$basedir/OpenNms"
    static def rapid_apg = "$basedir/Apg"
    static def rapid_ui = "$rapid_modules/RapidUiPlugin"

    static def rapid_cmdb_src = "$rapid_cmdb_cvs/src/java"
    static def rapid_cmdb_build = basedir+"/build/rcmdb"

    static def third_party = basedir+"/ThirdParty";
    static def licenced_jars = basedir+"/LicencedJars";

    //	 Rapid Jars
	static def rapid_core_jar = "$rapid_core_build/core.jar";
	static def rapid_comp_jar = "$rapid_comp_build/comp.jar";
	static def rapid_ext_jar = "$rapid_ext_build/ext.jar";
	static def rapid_rssmarts_jar = "$rapid_ext_build/rssmarts.jar";
	static def rapid_cmdb_jar = "$rapid_cmdb_build/rcmdb.jar";

	static def smarts_testreport = "$testresults/Smarts/test-reports";
	static def smarts_testhtml = "$testresults/Smarts/junit/html";
    static def comp_testreport = "$testresults/Comp/test-reports";
	static def comp_testhtml = "$testresults/Comp/junit/html";
    static def core_testreport = "$testresults/Core/test-reports";
	static def core_testhtml = "$testresults/Core/junit/html";



	Env(AntBuilder antBuilder){
		ant = antBuilder;
		populateThirdPartyJarsMap();
		populateLicencedJarsMap();
	}
	
	def populateThirdPartyJarsMap(){
		File file = new File(third_party+"/lib");
		getLibProperties(thirdPartyJars, file);
	}

	def populateLicencedJarsMap(){
		File file = new File(licenced_jars+"/lib");
		getLibProperties(thirdPartyJars, file);
	}
	
	def getLibProperties(Properties props, File file)
    {
        if(file.isDirectory() && file.getName() != ".svn" && file.getName() != "CVS")
        {
            File[] fileList = file.listFiles();
            for (i in fileList)
            {
                getLibProperties(props, i);
            }
        }
        else
        {
            String path = file.getPath();
            path = path.replaceAll("\\\\", "/");
            String fname = file.getName().replaceAll("\\.", "_");
            props.put(fname,path);
        }
    }
}