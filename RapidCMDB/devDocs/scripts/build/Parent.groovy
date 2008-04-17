package build;

class Parent {
	AntBuilder ant = new AntBuilder();
	Env env = new Env(ant);
	Properties classpath = env.thirdPartyJars;	
	public static boolean TEST = false;

}