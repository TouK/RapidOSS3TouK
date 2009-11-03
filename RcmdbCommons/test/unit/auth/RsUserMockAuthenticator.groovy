package auth
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 2, 2009
 * Time: 5:29:08 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserMockAuthenticator {
    public RsUser authenticateUser(params)
    {
        return auth.RsUser.get(username:"user1");
    }
}