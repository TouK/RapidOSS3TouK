package com.ifountain.smarts.datasource;

import com.smarts.repos.MR_PropertyNameValue;

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
 * Date: Jul 16, 2008
 * Time: 11:28:56 AM
 */
public class StagedNotification {
    private MR_PropertyNameValue[] nameValuePairs;
    private String eventName;
    private long timeStamp;

    public StagedNotification(MR_PropertyNameValue[] nameValuePairs, String eventName, long timeStamp) {
        this.nameValuePairs = nameValuePairs;
        this.eventName = eventName;
        this.timeStamp = timeStamp;
    }

    public MR_PropertyNameValue[] getNameValuePairs() {
        return nameValuePairs;
    }

    public void setNameValuePairs(MR_PropertyNameValue[] nameValuePairs) {
        this.nameValuePairs = nameValuePairs;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
