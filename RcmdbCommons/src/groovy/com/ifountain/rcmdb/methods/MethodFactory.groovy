package com.ifountain.rcmdb.methods

import com.ifountain.rcmdb.methods.exception.UndefinedMethodException

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 16, 2009
* Time: 8:50:23 AM
* To change this template use File | Settings | File Templates.
*/
public class MethodFactory {
    public static final String WITH_SESSION_METHOD = "withSession";
    public static Closure createMethod(String method)
    {
        switch (method)
        {
            case WITH_SESSION_METHOD:
                return {String username, Closure codeToBeExecuted->
                    def mt = new WithSessionDefaultMethod(username, codeToBeExecuted);
                    mt.run();
                }
            break;
            default:
                throw new UndefinedMethodException(method);
        }
    }
}