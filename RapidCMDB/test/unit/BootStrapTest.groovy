import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import auth.RsUser
import auth.RsUserOperations
import auth.Group
import auth.GroupOperations
import auth.Role
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import auth.ChannelUserInformation
import auth.RsUserInformation

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 13, 2009
* Time: 3:42:04 PM
* To change this template use File | Settings | File Templates.
*/
class BootStrapTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }

    public void testRegisterDefaultUsers()
    {
        initialize ([RsUser,Group,Role,RsUserInformation,ChannelUserInformation],[]);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);
        CompassForTests.addOperationSupport (Group,GroupOperations);

        SegmentQueryHelper.getInstance().initialize ([]);

        def bootstrap= new BootStrap();
        bootstrap.registerDefaultUsers();

        assertEquals(2,Role.count());
        assertEquals(2,Group.count());
        assertEquals(2,RsUser.count());

        def userRole=Role.get(name:Role.USER);
        assertNotNull (userRole)

        def adminRole=Role.get(name:Role.ADMINISTRATOR);
        assertNotNull (adminRole)

        def adminGroup=Group.get(name:RsUser.RSADMIN);
        assertNotNull(adminGroup);
        assertEquals(adminRole.id,adminGroup.role.id)

        def userGroup=Group.get(name:RsUser.RSUSER);
        assertNotNull(userGroup);
        assertEquals(userRole.id,userGroup.role.id)

        def adminUser=RsUser.get(username:RsUser.RSADMIN)
        assertNotNull(adminUser);
        assertEquals(1,adminUser.groups.size())
        assertEquals(adminGroup.id,adminUser.groups[0].id)
        assertTrue(adminUser.isPasswordSame("changeme"));
        

        def rsUser=RsUser.get(username:RsUser.RSUSER)
        assertNotNull(rsUser);
        assertEquals(1,rsUser.groups.size())
        assertEquals(userGroup.id,rsUser.groups[0].id)
        assertTrue(rsUser.isPasswordSame("changeme"));
    }

    public void testRegisterDefaultUsersDoesNotUpdateExistingUsers()
    {
        initialize ([RsUser,Group,Role,RsUserInformation,ChannelUserInformation],[]);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);
        CompassForTests.addOperationSupport (Group,GroupOperations);

        SegmentQueryHelper.getInstance().initialize ([]);

        def bootstrap= new BootStrap();
        bootstrap.registerDefaultUsers();

        def adminUser=RsUser.get(username:RsUser.RSADMIN)
        def rsUser=RsUser.get(username:RsUser.RSUSER)

        RsUser.updateUser(adminUser,[password:"123"]);
        RsUser.updateUser(rsUser,[password:"1234"]);

        assertFalse(adminUser.hasErrors())
        assertFalse(rsUser.hasErrors())

        def adminUserFromRepo=RsUser.get(username:RsUser.RSADMIN)
        def rsUserFromRepo=RsUser.get(username:RsUser.RSUSER)

        //we make sure that passwords are not changed , user info is not updated
        assertTrue(adminUserFromRepo.isPasswordSame("123"))
        assertTrue(rsUserFromRepo.isPasswordSame("1234"))



    }

}