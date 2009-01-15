package com.ifountain.rcmdb.scripting.methods

import com.ifountain.session.Session
import com.ifountain.session.SessionManager;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 5:45:45 PM
 * To change this template use File | Settings | File Templates.
 */
class WithSessionDefaultMethod implements DefaultScriptMethod{
    String username;
    Closure closureToBeExecuted;
    public WithSessionDefaultMethod(String username, Closure closureToBeExecuted)
    {
        this.username = username;
        this.closureToBeExecuted = closureToBeExecuted;
    }

    public void run()
    {
        Session currentSession = SessionManager.getInstance().getSession();
        def currentSessionUsername = currentSession.username
        try
        {
            SessionManager.getInstance().startSession (username)
            closureToBeExecuted();

        }
        finally {
            SessionManager.getInstance().startSession (currentSessionUsername);
        }
    }
    
}