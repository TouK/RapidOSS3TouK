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
package model
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 26, 2008
 * Time: 5:01:22 PM
 * To change this template use File | Settings | File Templates.
 */
class PropertyAction {
    public static final String CLEAR_RELATION = "clearrelation"
    public static final String SET_DEFAULT_VALUE = "setdefaultvalue"
    static searchable = {
        except = ["propType", "defaultValue", "errors", "__operation_class__"];
    };
    Long id;
    Long version;
    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    String rsOwner = "p"
    String propName;
    String reverseName="";
    String modelName;
    String action;
    String propTypeName = "";
    Class propType;
    Object defaultValue;
    boolean willBeDeleted = false;
    org.springframework.validation.Errors errors ;
    Object __operation_class__;
    static transients = ["propType", "defaultValue", "errors", "__operation_class__"]

    static constraints={
    errors(nullable:true)
    __operation_class__(nullable:true)
    }
}