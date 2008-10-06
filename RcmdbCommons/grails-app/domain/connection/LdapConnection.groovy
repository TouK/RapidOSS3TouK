package connection
/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 25, 2008
 * Time: 11:03:50 AM
 * To change this template use File | Settings | File Templates.
 */
class LdapConnection  extends Connection{
    String url="";
    String userPassword ="";
    String username ="";
    String contextFactory ="com.sun.jndi.ldap.LdapCtxFactory";
    String dummy="";
    
    static constraints={
        url(blank:false)
        userPassword(blank:true,  nullable:true)
        username(blank:true,  nullable:true)
        contextFactory(blank:false)
    }
    
}