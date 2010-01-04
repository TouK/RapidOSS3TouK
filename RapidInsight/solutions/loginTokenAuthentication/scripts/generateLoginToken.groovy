try{


    def expireAfter=params.expireAfter;
    if(!expireAfter)
    {
        expireAfter="60";
    }

    expireAfter=Long.parseLong(expireAfter);

    def tokenUser=params.tokenUser;
    def currentUserName=web.session.username;
    //if tokenUser is not specified then token will be generated for current user
    //if tokenUser is specified then token will be generated for that user , this is only avaliable for admins

    if(!tokenUser) //every user can request a token for himself if already authenticated
    {
        tokenUser=currentUserName;
    }
    else  //only admin users are able to request a token for other users
    {
        def user=auth.RsUser.get(username:currentUserName);
        if(user==null)
        {
            throw new Exception("User ${currentUserName} does not exist")
        }
        if(!auth.RsUser.hasRole(currentUserName,auth.Role.ADMINISTRATOR))
        {
            throw new Exception("Administrator Role is needed to generate Token for user ${tokenUser}");
        }
    }

    def tokenAuthenticator=application.RapidApplication.getUtility("auth.RsUserTokenAuthenticator");

    def loginToken=tokenAuthenticator.generateTokenForUser(tokenUser,expireAfter);
    logger.info("LoginToken generated for user : ${tokenUser}");
    return loginToken;

}
catch(e)
{
    logger.warn("Exception occured while generating loginToken. Reason : ${e}")
    return "Error : LoginToken Request Failed. Reason : ${e.getMessage()}";
}