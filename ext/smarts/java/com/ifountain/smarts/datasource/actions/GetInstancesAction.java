/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource.actions;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.smarts.remote.SmRemoteDomainManager;

public class GetInstancesAction implements Action {

    private String className;
    private boolean classNameSpecified;
    private String[] instances;
    
    public GetInstancesAction() {
        classNameSpecified = false;
    }
    public GetInstancesAction(String className) {
        this.className = className;
        classNameSpecified = true;
    }

    @Override
    public void execute(IConnection ds) throws Exception {
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl)ds).getDomainManager();
        if(classNameSpecified){
            instances = domainManager.getInstances(className);
        }
        else{
            instances = domainManager.getInstances();
        }
    }
    public String[] getInstances() {
        return instances;
    }

}
