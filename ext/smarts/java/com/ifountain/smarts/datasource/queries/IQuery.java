/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.datasource.queries;

import java.util.Iterator;
import java.util.Map;

public interface IQuery {
    public Iterator<Map<String,Object>> execute() throws Exception;
}
