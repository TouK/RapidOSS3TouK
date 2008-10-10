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

public class InsertAction implements Action {

    private String className;
    private String instanceName;
    private String propertyName;
    private MR_AnyVal propertyValue;
    public InsertAction(String className, String instanceName, String propertyName, MR_AnyVal propertyValue) {
        this.className = className;
        this.instanceName = instanceName;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        ((SmartsConnectionImpl)ds).getDomainManager().insert(className, instanceName, propertyName, propertyValue);
    }

}
