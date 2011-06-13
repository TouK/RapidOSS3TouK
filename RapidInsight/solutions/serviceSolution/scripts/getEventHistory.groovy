/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 25, 2008
 * Time: 3:04:11 PM
 */
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat
import groovy.xml.MarkupBuilder;

def timeStampFormat = "MMM dd yyyy HH:mm:ss";
def formatter = new SimpleDateFormat(timeStampFormat);

def eventQuery=params.eventQuery;

//getPropertyValues is faster than search for limited number of properties
def searchParams = [max: "1000", sort: "clearedAt", order: "desc"];
def searchProps  = ["name", "elementName", "createdAt", "clearedAt"];
def historicalEvents = RsHistoricalEvent.getPropertyValues(eventQuery, searchProps, searchParams);
def sw = new StringWriter();
def builder = new MarkupBuilder(sw);
builder.data() {
    historicalEvents.each {historicalEvent ->
        def start = formatter.format(new Timestamp(historicalEvent.createdAt)) + " GMT";
        def end = formatter.format(new Timestamp(historicalEvent.clearedAt)) + " GMT";
        //if end < start then graph gives error
        if(historicalEvent.clearedAt<historicalEvent.createdAt)
        {
            end=start;
        }
        def title = historicalEvent.elementName + " " + historicalEvent.name;
        builder.event(title: title, start: start, end: end, isDuration: "true", historicalEvent.name)
    }
}
return sw.toString();
