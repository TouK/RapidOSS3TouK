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
package script

import com.ifountain.rcmdb.scripting.ScriptManager
import datasource.BaseListeningDatasource
import org.quartz.CronTrigger
import org.apache.log4j.Level

class CmdbScript {
    def messageService;

    public static String CRON = "Cron";
    public static String PERIODIC = "Periodic";
    public static String ONDEMAND = "OnDemand";
    public static String SCHEDULED = "Scheduled";
    public static String LISTENING = "Listening";
    static searchable = {
        except = ["listeningDatasource", "errors", "__operation_class__", "__is_federated_properties_loaded__","messageService"];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["name": ["nameInDs": "name"]]]]
    Long startDelay = 0;
    String name = "";
    String scriptFile = "";
    String rsOwner = "p"
    String type = ONDEMAND;
    boolean enabled = false;
    String scheduleType = PERIODIC;
    String cronExpression = "* * * * * ?";
    Long period = 1;
    String staticParam = "";
    String logFile="cmdbscript";
    String logLevel = Level.WARN.toString();
    boolean logFileOwn=false;
    
    BaseListeningDatasource listeningDatasource;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static relations = [
            listeningDatasource:[type:BaseListeningDatasource, reverseName:"listeningScript", isMany:false]
    ]

    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "messageService"];

    static constraints = {
        name(blank: false, key: []);
        scriptFile(blank: false, validator: {val, obj ->
            try
            {
                ScriptManager.getInstance().checkScript(val);
            }
            catch (Throwable t)
            {
                return ['script.compilation.error', t.toString()];
            }
        });
        type(inList:[ONDEMAND, SCHEDULED, LISTENING]);
        scheduleType(inList: [PERIODIC, CRON])
        listeningDatasource(nullable:true)
        cronExpression(validator: {val, obj ->
            if(obj.type == SCHEDULED)
            {
                try {
                    def trigger = new CronTrigger(obj.name, null, val);
                    trigger.getFireTimeAfter(new Date());
                }
                catch (Throwable t) {
                    return ['script.cron.doesnt.match', t.toString()];
                }
            }
        })
        staticParam(blank:true, nullable:true)
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
        logLevel(inList:[Level.ALL.toString(),Level.DEBUG.toString(),Level.INFO.toString(),
              Level.WARN.toString(), Level.ERROR.toString(), Level.FATAL.toString(), Level.OFF.toString()])
    }

    String toString()
    {
        return "$name";
    }

    
}
