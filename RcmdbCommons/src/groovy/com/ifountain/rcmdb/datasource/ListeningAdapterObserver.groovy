package com.ifountain.rcmdb.datasource

import com.ifountain.rcmdb.scripting.ScriptManager

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jul 18, 2008
 * Time: 3:39:45 PM
 */
class ListeningAdapterObserver implements Observer{
    def scriptName;
    def logger;
    public ListeningAdapterObserver(scriptName, logger){
        this.scriptName = scriptName;
        this.logger = logger;
    }
    public void update(Observable o, Object arg) {
        try{
             def scriptObject = ScriptManager.getInstance().getScriptObject(scriptName);
             scriptObject.update(arg);
        }
        catch(e){
            logger.warn("Cannot invoke update method of " + scriptName + ". Reason: " + e.getMessage())
        }
    }

}