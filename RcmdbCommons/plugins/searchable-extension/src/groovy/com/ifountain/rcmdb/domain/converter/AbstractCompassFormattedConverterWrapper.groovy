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
package com.ifountain.rcmdb.domain.converter

import org.compass.core.converter.basic.FormatConverter
import org.compass.core.converter.Converter
import org.compass.core.config.CompassConfigurable

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 5:33:29 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractCompassFormattedConverterWrapper extends AbstractCompassConverterWrapper implements FormatConverter, CompassConfigurable{
    public void setFormat(String s) {
        ((FormatConverter)getConverter()).setFormat (s);
    }

    public FormatConverter copy() {
        return ((FormatConverter)getConverter()).copy();
    }

}