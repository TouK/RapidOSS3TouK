package auth
import connection.LdapConnection
/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 25, 2008
 * Time: 4:28:16 PM
 * To change this template use File | Settings | File Templates.
 */
class LdapUserInformation extends RsUserInformation{
    String userdn;


    LdapConnection ldapConnection;

    static relations = [
            ldapConnection:[type:LdapConnection,isMany:false]
    ]

    static constraints = {
        userdn(nullable: false, blank: false)
        ldapConnection(nullable:false)
    }

}