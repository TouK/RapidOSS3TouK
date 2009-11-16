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
 * Created on Mar 12, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.core.datasource.mocks;

import java.util.Observable;
import java.util.List;
import java.util.ArrayList;

import com.ifountain.core.datasource.BaseListeningAdapter;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import org.apache.log4j.Logger;

public class MockBaseListeningAdapter extends BaseListeningAdapter {

    public Exception subscribeException;
    public RuntimeException unSubscribeException;
    public Object stateWaitLock;
    public MockBaseListeningAdapter(String connectionName, long reconnectInterval) {
        super(connectionName, reconnectInterval, TestLogUtils.log);
    }

    public MockBaseListeningAdapter(String connectionName){
        this(connectionName, 0);
    }

    @Override
    protected void _subscribe() throws Exception {
        if(stateWaitLock != null){
            synchronized (stateWaitLock){
                stateWaitLock.wait();
            }
        }
        if(subscribeException != null)
        throw subscribeException;
    }

    @Override
    protected void _unsubscribe() {
        if(stateWaitLock != null){
            synchronized (stateWaitLock){
                try{
                    stateWaitLock.wait();    
                }
                catch(Exception e){}
            }
        }
        if(unSubscribeException != null)
        throw unSubscribeException;
    }
    
    @Override
    public Object _update(Observable o, Object arg) {
        return null;
    }
}
