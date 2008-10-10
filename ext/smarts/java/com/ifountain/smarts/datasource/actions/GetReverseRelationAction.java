/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 21, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource.actions;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import com.ifountain.smarts.connection.SmartsConnectionImpl;

public class GetReverseRelationAction implements Action {

    private String relationName;
    private String className;
    private String reverseRelation;
    public GetReverseRelationAction(String className, String relationName) {
        this.className = className;
        this.relationName = relationName;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        reverseRelation = ((SmartsConnectionImpl)ds).getDomainManager().getReverseRelation(className, relationName);
    }
    public String getReverseRelation() {
        return reverseRelation;
    }

}
