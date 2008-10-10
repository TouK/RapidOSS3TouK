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

public class DeleteInstanceAction implements Action {

    private String className;
    private String instanceName;
    public DeleteInstanceAction(String className, String instanceName) {
        this.className = className;
        this.instanceName = instanceName;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        ((SmartsConnectionImpl)ds).getDomainManager().deleteInstance(className, instanceName);
    }

}
