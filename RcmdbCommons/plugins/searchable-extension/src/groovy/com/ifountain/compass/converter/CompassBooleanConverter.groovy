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
package com.ifountain.compass.converter

import org.compass.core.converter.Converter


/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 5:01:07 PM
 * To change this template use File | Settings | File Templates.
 */
class CompassBooleanConverter extends AbstractCompassConverterWrapper{
    org.compass.core.converter.basic.BooleanConverter booleanConverter;
    public CompassBooleanConverter()
    {
        booleanConverter = new org.compass.core.converter.basic.BooleanConverter()
    }
    protected Converter getConverter() {
        return booleanConverter; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object getDefaultValue() {
        return new Boolean(false);
    }
}