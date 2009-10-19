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
package application
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 4:08:09 PM
 * To change this template use File | Settings | File Templates.
 */

class RsApplication {
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]
    String rsOwner = "p"
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    static constraints = {
        __operation_class__(nullable:true)
        __dynamic_property_storage__(nullable:true)
        errors(nullable:true)
    };

    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
}
