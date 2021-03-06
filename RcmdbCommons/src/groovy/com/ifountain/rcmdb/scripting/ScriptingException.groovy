/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.scripting
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 17, 2008
 * Time: 10:51:01 AM
 * To change this template use File | Settings | File Templates.
 */
class ScriptingException extends Exception{

    public ScriptingException(String message) {
        super(message); //To change body of overridden methods use File | Settings | File Templates.
    }

    public ScriptingException(String message, Throwable cause) {
        super(message, cause); //To change body of overridden methods use File | Settings | File Templates.
    }

    public static  ScriptingException scriptDoesnotExist(scriptName)
    {
        return new ScriptingException("Script ${scriptName} does not exist.")
    }

    public static  ScriptingException runScriptException(scriptName, lineNumber, exception)
    {
        return new ScriptingException("Exception occurred while executing script $scriptName at line $lineNumber . Reason :$exception", exception)
    }

    public static  ScriptingException compileScriptException(scriptName, exception)
    {
        return new ScriptingException("Exception occurred while executing script $scriptName . Reason :$exception", exception)
    }
    
}