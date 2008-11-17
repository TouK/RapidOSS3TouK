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
import com.smarts.repos.MR_Ref;

public class FindInstancesAction implements Action {

    private String classRegExp;
    private String instanceRegExp;
    private long flags;
    private MR_Ref[] instances;
    
    public FindInstancesAction(String classRegExp, String instanceRegExp, long flags) {
        this.classRegExp = classRegExp;
        this.instanceRegExp = instanceRegExp;
        this.flags = flags;
    }

    @Override
    public void execute(IConnection ds) throws Exception {
        instances = ((SmartsConnectionImpl)ds).getDomainManager().findInstances(classRegExp, instanceRegExp, flags);
    }
    
    public MR_Ref[] getInstances(){
        return instances;
    }

}
