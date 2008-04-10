package build;

class Build extends Parent{
	Build(){
//		if(System.getProperty("os.name").toLowerCase().indexOf("windows") > -1){
//			ant.taskdef(name : "exe4j", classname : "com.exe4j.Exe4JTask", classpath : (String)classpath.getProperty("exe4jlib_jar"));
//			ant.taskdef(name : "nsis", classname : "info.waynegrant.ant.NsisTask", classpath : env.repository_project+"/RapidRepository/lib/build/wat-12.jar");
//		}
		setClasspathForBuild();
	}
	
	void run(args){
		if(args.size() >0){
			println "Running target " + args[0];
			invokeMethod(args[0], null);
		}
		else{
			println "Running default target";
			build();
		}
		println "Done";
	}                                    
	

	
	def cleanDistribution(){
		ant.delete(dir : env.dist_rapid_cmdb);
		ant.delete(){
			ant.fileset(dir : env.distribution, excludes : "*.project*, *.classpath");
		}
	}
	
	def setClasspathForBuild(){
		ant.path(id : "classpath"){
			ant.pathelement(location : env.rapid_comp_jar);
			ant.pathelement(location : env.rapid_core_jar);
			ant.pathelement(location : env.rapid_ext_jar);
			ant.pathelement(location : (String)classpath.getProperty("smack_jar"));
			ant.pathelement(location : (String)classpath.getProperty("smackx_jar"));
			ant.pathelement(location : (String)classpath.getProperty("joscar-0_9_3-patched_jar"));
			ant.pathelement(location : (String)classpath.getProperty("STComm_jar"));
			ant.pathelement(location : (String)classpath.getProperty("RCExtensions_jar"));
			ant.pathelement(location : (String)classpath.getProperty("activation_jar"));
			ant.pathelement(location : (String)classpath.getProperty("blowfishj-2_14_jar"));
			ant.pathelement(location : (String)classpath.getProperty("bsf_jar"));
			ant.pathelement(location : (String)classpath.getProperty("bsh-2_0b2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-beanutils_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-cli-1_0_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-codec-1_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-collections-3_2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-configuration-1_2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-digester-1_7_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-httpclient-3_0_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-lang-2_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-logging_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-pool-1_4_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-io-1_2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("derby_jar"));
			ant.pathelement(location : (String)classpath.getProperty("derbyclient_jar"));
			ant.pathelement(location : (String)classpath.getProperty("derbynet_jar"));
			ant.pathelement(location : (String)classpath.getProperty("javax_servlet_jar"));	
			ant.pathelement(location : (String)classpath.getProperty("jakarta-oro-2_0_8_jar"));
			ant.pathelement(location : (String)classpath.getProperty("jdom_jar"));
			ant.pathelement(location : (String)classpath.getProperty("js_jar"));
			ant.pathelement(location : (String)classpath.getProperty("groovy-all-1_5_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("groovy-xmlrpc-0_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("log4j-1_2_13_jar"));			
			ant.pathelement(location : (String)classpath.getProperty("net_jar"));
			ant.pathelement(location : (String)classpath.getProperty("org_mortbay_jetty_jar"));			
			ant.pathelement(location : (String)classpath.getProperty("regexp_jar"));
			ant.pathelement(location : (String)classpath.getProperty("rife-1_3_1-jdk14_jar"));
			ant.pathelement(location : (String)classpath.getProperty("skclient_jar"));
			ant.pathelement(location : (String)classpath.getProperty("wat-12_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xbean_jar"));
			ant.pathelement(location : (String)classpath.getProperty("prevayler-2_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xerces_jar"));
			ant.pathelement(location : (String)classpath.getProperty("SNMP4J_jar"));
			ant.pathelement(location : (String)classpath.getProperty("mail_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xstream-1_1_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xpp3_min-1_1_3_8_jar"));
			ant.pathelement(location : (String)classpath.getProperty("truelicense_jar"));
			ant.pathelement(location : (String)classpath.getProperty("trueswing_jar"));
			ant.pathelement(location : (String)classpath.getProperty("truexml_jar"));
			ant.pathelement(location : (String)classpath.getProperty("org_mortbay_jetty_jar"));
			ant.pathelement(location : (String)classpath.getProperty("org_mortbay_jmx_jar"));
			ant.pathelement(location : (String)classpath.getProperty("start_jar"));
			ant.pathelement(location : (String)classpath.getProperty("stop_jar"));
			ant.pathelement(location : (String)classpath.getProperty("smppapi-0_3_7_jar"));
			ant.pathelement(location : (String)classpath.getProperty("stax-api-1_0_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("stax-1_2_0_jar"));
			ant.pathelement(location : (String)classpath.getProperty("DdlUtils-1_0_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-betwixt-0_8_jar"));
			
			//Required for compiling test classes
			ant.pathelement(location : (String)classpath.getProperty("junit_jar"));
			ant.pathelement(location : (String)classpath.getProperty("httpunit_jar"));
			ant.pathelement(location : (String)classpath.getProperty("jrequire_eclipse_jar"));
			ant.pathelement(location : (String)classpath.getProperty("mysql-connector-java-3_1_8-bin_jar"));
			ant.pathelement(location : (String)classpath.getProperty("ojdbc14_jar"));
			ant.pathelement(location : (String)classpath.getProperty("poi-2_5_1-final-20040804_jar"));//Excel
			ant.pathelement(location : (String)classpath.getProperty("web-api_jar")); //HP ServiceDesk tests
			ant.pathelement(location : (String)classpath.getProperty("selenium-java-client-driver_jar"));
			ant.pathelement(location : (String)classpath.getProperty("selenium-server_jar"));
			ant.pathelement(location : (String)classpath.getProperty("cobra_jar"));
			
			// GWT
			ant.pathelement(location : (String)classpath.getProperty("gwt-dev-windows_jar"));
			ant.pathelement(location : (String)classpath.getProperty("gwt-servlet_jar"));
			ant.pathelement(location : (String)classpath.getProperty("gwt-user_jar"));
			
		}
	}

}