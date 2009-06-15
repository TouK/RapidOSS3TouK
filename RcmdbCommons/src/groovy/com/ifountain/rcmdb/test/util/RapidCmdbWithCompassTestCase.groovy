/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package com.ifountain.rcmdb.test.util

import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.IdGenerator
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import relation.Relation
import com.ifountain.rcmdb.converter.DateConverter
import com.ifountain.rcmdb.converter.LongConverter
import com.ifountain.rcmdb.converter.DoubleConverter
import com.ifountain.rcmdb.converter.BooleanConverter
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.util.RapidDateUtilities

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 7, 2008
 * Time: 6:28:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCmdbWithCompassTestCase extends RapidCmdbMockTestCase{
    def initialize(List classesToBeLoaded, List pluginsToLoad, boolean isPersistant)
    {
        IdGenerator.destroy();
        IdGenerator.initialize(new MockIdGeneratorStrategy());
        RapidDateUtilities.registerDateUtils();
        registerDefaultConverters();
        pluginsToLoad +=  DomainClassGrailsPlugin;
        pluginsToLoad +=  gcl.loadClass("SearchableGrailsPlugin");
        pluginsToLoad +=  gcl.loadClass("SearchableExtensionGrailsPlugin");
        pluginsToLoad +=  gcl.loadClass("RapidDomainClassGrailsPlugin");
        classesToBeLoaded += Relation;
        super.initialize(classesToBeLoaded, pluginsToLoad, isPersistant);
    }

    def registerDefaultConverters()
    {
        def dateFormat = "yyyy-dd-MM HH:mm:ss.SSS";
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
        RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)
    }

}
