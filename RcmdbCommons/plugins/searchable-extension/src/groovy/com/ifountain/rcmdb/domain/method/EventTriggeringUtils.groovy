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
package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jun 14, 2008
 * Time: 4:37:27 AM
 * To change this template use File | Settings | File Templates.
 */
class EventTriggeringUtils {
    static final String ONLOAD_EVENT = 'onLoad'
    static final String BEFORE_INSERT_EVENT = 'beforeInsert'
    static final String BEFORE_UPDATE_EVENT = 'beforeUpdate'
    static final String BEFORE_DELETE_EVENT = 'beforeDelete'
    static final String AFTER_INSERT_EVENT = 'afterInsert'
    static final String AFTER_UPDATE_EVENT = 'afterUpdate'
    static final String AFTER_DELETE_EVENT = 'afterDelete'
    public static Object triggerEvent(entity, event) {
        return triggerEvent (entity, event, null);
    }
    public static Object triggerEvent(entity, event, Map params) {
        try
        {
            if(params != null)
            {
                return entity."${event}Wrapper"(params);
            }
            else
            {
                return entity."${event}Wrapper"();
            }
        }
        catch(MissingMethodException exception)
        {
            if(exception.getMethod() != event && exception.getMethod() != "${event}Wrapper"|| exception.getType().name != entity.class.name)
            {
                throw exception;                
            }
        }
    }
}