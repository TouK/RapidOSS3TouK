package connection
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 29, 2008
 * Time: 8:38:43 AM
 * To change this template use File | Settings | File Templates.
 */
class SmartsConnectionTemplate {
    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]
    String name ="";
    String broker ="";
    String username ="";
    String password ="";
    String  brokerUsername="";
    String brokerPassword ="";

    static relations  =[:]
    static constraints={
      name(blank:false,nullable:false,key:[])
      broker(blank:false,nullable:false)
      username(blank:false,nullable:false)
      brokerUsername(blank:false,nullable:false)
      password(blank:true,nullable:true)
      brokerPassword(blank:true,nullable:true)
    }
    static propertyConfiguration= [:]
    static transients = [];

    public String toString()
    {
    	return name;
    }
}