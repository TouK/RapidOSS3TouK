<script type="text/javascript">
//Below template have the code to generate username , user groups and roles in javascript

window.currentUserName="${session.username}";
window.currentUserGroups={};
window.currentUserRoles={};

<%
    def userBean = com.ifountain.rcmdb.auth.UserConfigurationSpace.getInstance().getUser(session.username);
    if(userBean)
    {
        userBean.getGroups().each{  groupName, groupBean ->
            println "window.currentUserGroups['${groupName}']=true;";
            def roleName=groupBean.getRole();
            if(roleName)
            {
                println "window.currentUserRoles['${roleName}']=true;";
            }
        }
    }
%>

window.getCurrentUserName=function(){
    return window.currentUserName;
}
window.currentUserHasGroup=function(groupName)
{
    return window.currentUserGroups[groupName]?true:false;
}
window.currentUserHasRole=function(roleName)
{
    return window.currentUserRoles[roleName]?true:false;
}
</script>
