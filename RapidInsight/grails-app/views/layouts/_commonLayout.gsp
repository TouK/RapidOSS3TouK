<script type="text/javascript">
//Below template have the code to generate username , user groups and roles in javascript

window.currentUserName="${session.username}";
window.currentUserGroups={};
window.currentUserRoles={};

<%
    def rsUser=auth.RsUser.get(username:session.username);
    if(rsUser)
    {
        rsUser.groups.each{ group ->
            println "window.currentUserGroups['${group.name}']=true;";
            def role=group.role;
            if(role)
            {
                println "window.currentUserRoles['${role.name}']=true;";
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
