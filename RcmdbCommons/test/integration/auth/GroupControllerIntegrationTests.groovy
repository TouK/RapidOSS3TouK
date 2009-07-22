package auth

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 21, 2009
* Time: 3:19:43 PM
* To change this template use File | Settings | File Templates.
*/
class GroupControllerIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;

    def adminUserId;
    def normalUserId;
    def adminRoleId;
    def userRoleId;
    def testGroupname="testuser";

    public void setUp() {
        super.setUp();
        adminUserId=RsUser.get(username:RsUser.RSADMIN).id
        normalUserId=RsUser.get(username:RsUser.RSUSER).id
        adminRoleId=Role.get(name:Role.ADMINISTRATOR).id
        userRoleId=Role.get(name:Role.USER).id

        Group.get(name:testGroupname)?.remove();
        Group.get(name:"${testGroupname}2")?.remove();


    }

    public void tearDown() {
        super.tearDown();
        Group.get(name:testGroupname)?.remove();
        Group.get(name:"${testGroupname}2")?.remove();

    }

    public void testAddGroupDoesNotAddGroupWhenGroupHaveErrors()
    {
        def existingGroup=Group.add(name:testGroupname);
        assertFalse(existingGroup.hasErrors());


        def controller=new GroupController();
        controller.params["name"]=testGroupname;
        controller.params["role.id"]=adminRoleId.toString();
        controller.params["users.id"]=adminUserId.toString();
        controller.params["segmentFilter"]="testfilter";
        controller.params["segmentFilterType"]=Group.CLASS_BASED_FILTER;


        controller.save();

        assertNull(controller.flash.errors);

        assertTrue(controller.modelAndView.model.group.hasErrors());
        assertEquals(testGroupname,controller.modelAndView.model.group.name);

        assertEquals(1,controller.modelAndView.model.groupUsers.size())
        assertEquals(adminUserId,controller.modelAndView.model.groupUsers[0].id)

        assertEquals(1,controller.modelAndView.model.availableUsers.size())
        assertEquals(normalUserId,controller.modelAndView.model.availableUsers[0].id)

        def existingGroupFromRepo=Group.get(name:testGroupname);
        assertEquals(0,existingGroupFromRepo.users.size());

        assertEquals(1,Group.countHits("name:${testGroupname}"));
        assertEquals(3,Group.count());

    }
    public void testAddGroupDoesNotAddGroupWhenExceptionOccurs()
    {
        def controller=new GroupController();
        controller.params["name"]=testGroupname;
        controller.params["segmentFilter"]="testfilter";
        controller.params["segmentFilterType"]=Group.CLASS_BASED_FILTER;
        controller.params["users.id"]=adminUserId.toString();

        controller.save();

        assertEquals(1,controller.flash.errors.getAllErrors().size());
        assertEquals("no.role.specified",controller.flash.errors.getAllErrors()[0].code);

        assertFalse(controller.modelAndView.model.group.hasErrors());
        assertEquals(null,controller.modelAndView.model.group.id);
        assertEquals(testGroupname,controller.modelAndView.model.group.name);

        assertEquals(1,controller.modelAndView.model.groupUsers.size())
        assertEquals(adminUserId,controller.modelAndView.model.groupUsers[0].id)

        assertEquals(1,controller.modelAndView.model.availableUsers.size())
        assertEquals(normalUserId,controller.modelAndView.model.availableUsers[0].id)

        assertEquals(0,Group.countHits("name:${testGroupname}"));
        assertEquals(2,Group.count());
    }

    public void testAddGroupSuccessFully()
    {
        def controller=new GroupController();
        controller.params["name"]=testGroupname;
        controller.params["role.id"]=adminRoleId.toString();
        controller.params["users.id"]=adminUserId.toString();
        controller.params["segmentFilter"]="testfilter";
        controller.params["segmentFilterType"]=Group.CLASS_BASED_FILTER;


        controller.save();

        def group=Group.get(name:testGroupname);
        assertEquals("testfilter",group.segmentFilter);
        assertEquals(Group.CLASS_BASED_FILTER,group.segmentFilterType);
        assertEquals(adminRoleId,group.role.id);
        def groupUsers=group.users;
        assertEquals(1,groupUsers.size())
        assertEquals(adminUserId,groupUsers[0].id);

        assertEquals(1,Group.countHits("name:${testGroupname}"));
        assertEquals(3,Group.count());
    }

    private def addGroupForTest()
    {
        def existingGroup=Group.add(name:testGroupname,segmentFilter:"testfilter",segmentFilterType:Group.GLOBAL_FILTER);
        assertFalse(existingGroup.hasErrors());
        return existingGroup;
    }
    public void testUpdateGroupGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new GroupController();
        //we assign a string to id so controller can not find user
        def groupId="nouser"
        controller.params["id"]=groupId;

        controller.update();

        assertEquals("Group not found with id ${groupId}",controller.flash.message);
        assertEquals("/group/edit/${groupId}", controller.response.redirectedUrl);

    }
    public void testUpdateGroupDoesNotUpdateGroupWhenGroupHaveErrors()
    {
        def existingGroup=addGroupForTest();

        def controller=new GroupController();
        controller.params["id"]=existingGroup.id.toString();
        controller.params["name"]=null;
        controller.params["role.id"]=adminRoleId.toString();
        controller.params["users.id"]=adminUserId.toString();
        controller.params["segmentFilter"]="testfilter2";
        controller.params["segmentFilterType"]=Group.CLASS_BASED_FILTER;


        controller.update();

        assertNull(controller.flash.errors);

        assertTrue(controller.modelAndView.model.group.hasErrors());
        assertEquals("",controller.modelAndView.model.group.name);
        assertEquals("testfilter2",controller.modelAndView.model.group.segmentFilter);
        assertEquals(Group.CLASS_BASED_FILTER,controller.modelAndView.model.group.segmentFilterType);

        assertEquals(1,controller.modelAndView.model.groupUsers.size())
        assertEquals(adminUserId,controller.modelAndView.model.groupUsers[0].id)

        assertEquals(1,controller.modelAndView.model.availableUsers.size())
        assertEquals(normalUserId,controller.modelAndView.model.availableUsers[0].id)

        def existingGroupFromRepo=Group.get(name:testGroupname);
        assertEquals(0,existingGroupFromRepo.users.size());
        assertEquals("testfilter",existingGroupFromRepo.segmentFilter);
        assertEquals(Group.GLOBAL_FILTER,existingGroupFromRepo.segmentFilterType);

        assertEquals(1,Group.countHits("name:${testGroupname}"));        
        assertEquals(3,Group.count());

    }

    public void testUpdateGroupDoesNotUpdateGroupWhenExceptionOccurs()
    {
        def existingGroup=addGroupForTest();

        def controller=new GroupController();
        controller.params["id"]=existingGroup.id.toString();
        controller.params["name"]="${testGroupname}2";
        controller.params["role.id"]="null";
        controller.params["users.id"]=adminUserId.toString();
        controller.params["segmentFilter"]="testfilter2";
        controller.params["segmentFilterType"]=Group.CLASS_BASED_FILTER;


        controller.update();


        assertEquals(1,controller.flash.errors.getAllErrors().size());
        assertEquals("no.role.specified",controller.flash.errors.getAllErrors()[0].code);

        assertFalse(controller.modelAndView.model.group.hasErrors());
        assertEquals("${testGroupname}2",controller.modelAndView.model.group.name);

        assertEquals(1,controller.modelAndView.model.groupUsers.size())
        assertEquals(adminUserId,controller.modelAndView.model.groupUsers[0].id)

        assertEquals(1,controller.modelAndView.model.availableUsers.size())
        assertEquals(normalUserId,controller.modelAndView.model.availableUsers[0].id)

        assertEquals(1,Group.countHits("name:${testGroupname}"));
        assertEquals(0,Group.countHits("name:${testGroupname}2"));
        assertEquals(3,Group.count());
    }
    public void testUpdateGroupSuccessFully()
    {
        def existingGroup=addGroupForTest();

        def controller=new GroupController();
        controller.params["id"]=existingGroup.id.toString();
        controller.params["name"]="${testGroupname}2";
        controller.params["role.id"]=adminRoleId.toString();
        controller.params["users.id"]=adminUserId.toString();
        controller.params["segmentFilter"]="testfilter2";
        controller.params["segmentFilterType"]=Group.CLASS_BASED_FILTER;


        controller.update();


        def groupUpdated=Group.get(id:existingGroup.id);

        assertEquals("Group ${groupUpdated.id} updated",controller.flash.message);

        assertEquals("/group/show/${groupUpdated.id}", controller.response.redirectedUrl);

        assertEquals("${testGroupname}2",groupUpdated.name)
        assertEquals("testfilter2",groupUpdated.segmentFilter)
        assertEquals(Group.CLASS_BASED_FILTER,groupUpdated.segmentFilterType)
        assertEquals(adminRoleId,groupUpdated.role.id)
        def groupUsers=groupUpdated.users;
        assertEquals(1,groupUsers.size());
        assertEquals(adminUserId,groupUsers[0].id);

        assertEquals(0,Group.countHits("name:${testGroupname}"))
        assertEquals(3,Group.count());
    }

    public void testDeleteGroupGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new GroupController();
        //we assign a string to id so controller can not find user
        def groupId="nogroupp"
        controller.params["id"]=groupId;

        controller.delete();

        assertEquals("Group not found with id ${groupId}",controller.flash.message);
        assertEquals("/group/list", controller.response.redirectedUrl);
    }
    public void testDeleteGroupGeneratesErrorWhenRemoveGeneratesError()
    {
        def adminGroup=Group.add(name:RsUser.RSADMIN);
        assertFalse(adminGroup.hasErrors());

        def controller=new GroupController();
        controller.params["id"]=adminGroup.id.toString();
        controller.delete();

        assertEquals(1,controller.flash.errors.getAllErrors().size());
        assertEquals("default.custom.error",controller.flash.errors.getAllErrors()[0].code);

        assertEquals("/group/list", controller.response.redirectedUrl);

        assertEquals(1,Group.countHits("name:${RsUser.RSADMIN}"))
    }
    public void testDeleteGroupSuccessfully()
    {
        def group=addGroupForTest();

        assertEquals(1,Group.countHits("name:${group.name}"));


        def controller=new GroupController();
        controller.params["id"]=group.id.toString();
        controller.delete();

        assertEquals("Group ${group.id} deleted",controller.flash.message);
        assertEquals("/group/list", controller.response.redirectedUrl);

        assertEquals(0,Group.countHits("name:${group.name}"));
    }

}