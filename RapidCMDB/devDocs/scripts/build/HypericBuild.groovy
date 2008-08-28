package build
/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 27, 2008
 * Time: 1:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
class HypericBuild extends Build{
    static void main(String []args){
    	HypericBuild hypericBuild = new HypericBuild();
    	hypericBuild.run(args);
	}

    def build(){
        createPlugin(env.rapid_hyperic,["generatedModels/**"]);
    }

}