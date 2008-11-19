package com.ifountain.smarts.connection.mocks;

import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.core.connection.ConnectionParam;
import com.smarts.remote.SmRemoteDomainManager;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Nov 18, 2008
 * Time: 5:41:04 PM
 * To change this template use File | Settings | File Templates.
 */
 public class SmartsConnectionImplMock extends SmartsConnectionImpl {
        public static boolean isConnected=true;
        public static boolean detachCalled=false;
        public boolean checkConnection() {
            return isConnected;
        }

        public void init(ConnectionParam param) throws Exception {
            super.init(param);
            this.domainManager=new SmRemoteDomainManager(){
               public void detach() {
                    detachCalled=true;
               }

            };
	    }

    }
