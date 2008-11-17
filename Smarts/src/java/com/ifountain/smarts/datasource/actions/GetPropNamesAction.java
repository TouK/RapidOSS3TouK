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

public class GetPropNamesAction implements Action {

    private String className;
    private String[] propNames;
    public GetPropNamesAction(String className) {
        this.className = className;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        propNames = ((SmartsConnectionImpl)ds).getDomainManager().getPropNames(className);
    }
    public String[] getPropNames() {
        return propNames;
    }
    

}
