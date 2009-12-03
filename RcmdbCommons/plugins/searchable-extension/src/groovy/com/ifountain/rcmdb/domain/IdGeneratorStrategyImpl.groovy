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
package com.ifountain.rcmdb.domain

import application.ObjectId
import com.ifountain.rcmdb.domain.method.CompassMethodInvoker


/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 18, 2008
* Time: 11:52:32 AM
* To change this template use File | Settings | File Templates.
*/
class IdGeneratorStrategyImpl implements IdGeneratorStrategy
{
    private long nextId = 0;
    private long numberOfRemainingIds = 0;
    private int INCREMENT_AMOUNT = 1000;

    public IdGeneratorStrategyImpl(String startId)
    {

        try
        {
            this.nextId=Long.parseLong(String.valueOf(startId));        
        }
        catch(Throwable t)
        {
            org.apache.log4j.Logger.getRootLogger().warn("IdGenerator : Invalid startId property ${startId}. it will be assigned to default value ${this.nextId}.");
        }
    }

    public IdGeneratorStrategyImpl(long startId)
    {
        this.nextId=startId;
    }
    
    public long getNextId() {
        if(numberOfRemainingIds == 0)
        {
            def res = CompassMethodInvoker.search (ObjectId.metaClass, [name:"generalObjectId"])
            def objectId = res.results[0];
            if(!objectId)
            {
                objectId = new ObjectId();
                objectId.setProperty("name", "generalObjectId", false);
                objectId.setProperty("nextId", nextId+INCREMENT_AMOUNT, false);
                objectId.setProperty("id", 0, false);
                ObjectId.index(objectId);
                nextId++;
            }
            else
            {
                nextId = objectId.nextId+1;
                objectId.nextId = objectId.nextId+INCREMENT_AMOUNT;
                ObjectId.index(objectId);
            }

            numberOfRemainingIds = INCREMENT_AMOUNT;
        }
        numberOfRemainingIds--;
        return nextId++;
    }
}
