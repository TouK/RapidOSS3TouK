package build
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 18, 2008
 * Time: 2:39:20 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidUiPluginBuild extends Build{
    static void main(String []args){
    	RapidUiPluginBuild rapidUiPluginBuild = new RapidUiPluginBuild();
    	rapidUiPluginBuild.run(args);
	}

    def build(){
        createPlugin(env.rapid_ui,[]);
    }
}