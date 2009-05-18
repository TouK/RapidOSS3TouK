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
package auth
/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 25, 2008
 * Time: 4:28:04 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserInformation {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "userInformations"];
    };
    RsUser rsUser;
    Long userId;
    Long version;
    Long id;
    String type="";
    String rsOwner = "p"
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    static constraints = {
        userId(key: ["type"], nullable: false)
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
        rsUser(nullable:true)
    };
     static relations = [
            rsUser:[isMany:false, reverseName:"userInformations", type:RsUser]
    ]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "userInformations"];
}