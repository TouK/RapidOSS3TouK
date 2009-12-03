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

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 13, 2008
* Time: 5:09:50 PM
* To change this template use File | Settings | File Templates.
*/
class IdGenerator {
    private static IdGenerator idGenerator;
    IdGeneratorStrategy strategy;
    private IdGenerator(IdGeneratorStrategy strategy)
    {
        this.strategy = strategy
    }

    public static void initialize(IdGeneratorStrategy strategy)
    {
        if(!idGenerator)
        {
            idGenerator = new IdGenerator(strategy);
        }
    }
    public static void destroy()
    {
        idGenerator = null;
    }
    public static IdGenerator getInstance()
    {
        return idGenerator;
    }
    public IdGeneratorStrategy getStrategy()
    {
        return strategy;    
    }

    public synchronized getNextId()
    {
        return strategy.getNextId();
    }

}

interface IdGeneratorStrategy
{
    public long getNextId();
}

class MockIdGeneratorStrategy implements IdGeneratorStrategy
{
    private long nextId = 0;
    public long getNextId() {
        return nextId++; //To change body of implemented methods use File | Settings | File Templates.
    }

}