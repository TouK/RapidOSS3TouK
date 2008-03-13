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

import com.ifountain.core.datasource.BaseListeningAdapter;

public class MockBaseListeningAdapter extends BaseListeningAdapter {

    public MockBaseListeningAdapter(String connectionName, long reconnectInterval) {
        super(connectionName, reconnectInterval, null);
    }

    public MockBaseListeningAdapter(String connectionName){
        this(connectionName, 0);
    }
    
    @Override
    protected void _subscribe() throws Exception {
    }

    @Override
    protected void _unsubscribe() {
    }
    
    @Override
    public Object _update(Observable o, Object arg) {
        return null;
    }
}
