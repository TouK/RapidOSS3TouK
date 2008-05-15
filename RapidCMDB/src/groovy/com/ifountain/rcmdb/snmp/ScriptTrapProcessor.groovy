package com.ifountain.rcmdb.snmp

import com.ifountain.snmp.datasource.SnmpTrapProcessor
import org.apache.log4j.Logger

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
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 */
class ScriptTrapProcessor implements SnmpTrapProcessor{
    def scriptName;
    def scriptingService;
    Logger logger;
    public ScriptTrapProcessor(scriptName, scriptingService, logger){
        this.scriptName = scriptName;
        this.scriptingService = scriptingService;
        this.logger = logger;
    }
    public void processTrap(Map trap) {
         try{
            def bindings = ["rsTrap":trap];
            def result = scriptingService.runScript(scriptName,  bindings);
         }
         catch(t){
            logger.warn("Exception during executing script " + scriptName + " with Snmp Trap " + trap + ". Reason: " + t.toString()) 
         }

    }
}