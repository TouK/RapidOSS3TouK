import java.text.SimpleDateFormat
import com.ifountain.rcmdb.util.CollectionUtils

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


public class RsHistoricalEventOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static final formatters = [
                      year:new SimpleDateFormat("yyyy"),
                      month:new SimpleDateFormat("yyyy-MM"),
                      day:new SimpleDateFormat("yyyy-MM-dd"),
                      hour:new SimpleDateFormat("yyyy-MM-dd-HH"),
                      minute:new SimpleDateFormat("yyyy-MM-dd-HH-mm"),
    ]
    def beforeInsert()
    {
        duration = calculateDuration();
        def createdAtDate = new Date(createdAt)
        formatters.each{String propName, formatter->
            setProperty(propName, formatter.format(createdAtDate));
        }
        super.beforeInsert();
    }
    
    def calculateDuration()
    {
        def notificationDuration = clearedAt - createdAt;
        return notificationDuration;
    }
    //only addToHistoricalEventCache & saveHistoricalEventCache are Thread safe
    public static def addToHistoricalEventCache(historicalEventModel,props)
    {
        synchronized(RsHistoricalEvent){
            props.historicalEventModel=historicalEventModel;
            RsHistoricalEvent.retrieveHistoricalEventCache().add(props);
        }
    }
    //only addToHistoricalEventCache & saveHistoricalEventCache are Thread safe
    public static void saveHistoricalEventCache()
    {
        def tempHistoricalEvents = [];
        synchronized(RsHistoricalEvent){
            def historicalEvents = retrieveHistoricalEventCache();
            getLogger().info("Will remove and save ${historicalEvents.size()} historical events in cache");
            tempHistoricalEvents.addAll(historicalEvents);
            clearHistoricalEventCache();
            getLogger().info("Cleared HistoricalEventCache");
        }
        getLogger().info("Will add ${tempHistoricalEvents.size()} historical events");
        CollectionUtils.executeForEachBatch(tempHistoricalEvents, 100) {List archivedNotifications ->
            application.RapidApplication.executeBatch{
                archivedNotifications.each{props ->
                    getLogger().debug("Adding historical event with props: ${props}")
                    def histEvent = props.historicalEventModel.add(props);
                    if(!histEvent.hasErrors()){
                        getLogger().info("Successfully added historical event ${props.historicalEventModel} ${props.name}.");
                    }
                    else{
                        getLogger().warn("Couldn't add historical event. Reason: ${histEvent.errors}")
                    }
                }
            }
        }
    }

    //this method is not thread safe , only  addToHistoricalEventCache & saveHistoricalEventCache are Thread safe
    public static def retrieveHistoricalEventCache()
    {
       def historicalEvents=com.ifountain.rcmdb.util.DataStore.get("HistoricalEventCache");
       if(historicalEvents == null){
            historicalEvents=clearHistoricalEventCache();
        }
       return historicalEvents;
    }
    //this method is not thread safe , only  addToHistoricalEventCache & saveHistoricalEventCache are Thread safe
    public static def clearHistoricalEventCache()
    {
       def  historicalEvents=[];
       com.ifountain.rcmdb.util.DataStore.put("HistoricalEventCache", historicalEvents);
       return historicalEvents;
    }
}
    