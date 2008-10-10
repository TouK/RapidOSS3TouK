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
import com.smarts.repos.MR_AnyVal;

public class InvokeOperationWithNativeParamsAction implements Action {

    private String className; 
    private String instanceName;
    private String opName;
    private MR_AnyVal[] opParams;
    private MR_AnyVal invokeResult;
    public InvokeOperationWithNativeParamsAction(String className, String instanceName, String opName, MR_AnyVal[] opParams) {
        this.className = className;
        this.instanceName = instanceName;
        this.opName = opName;
        this.opParams = opParams;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        invokeResult = ((SmartsConnectionImpl)ds).getDomainManager().invokeOperation(className, instanceName, opName, opParams);
    }
    public MR_AnyVal getInvokeResult() {
        return invokeResult;
    }

}
