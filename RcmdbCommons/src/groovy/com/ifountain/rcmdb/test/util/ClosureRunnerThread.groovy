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
package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 27, 2008
 * Time: 4:11:56 PM
 * To change this template use File | Settings | File Templates.
 */
class ClosureRunnerThread extends Thread{
    Closure closure;
    boolean isFinished;
    boolean isStarted;
    Throwable exception;
    Object result;

    public void run()
    {
        isStarted = true;
        try
        {
            result = closure();
        }
        catch(Throwable t)
        {
            this.exception = t;
        }
        isFinished = true;

    }
}