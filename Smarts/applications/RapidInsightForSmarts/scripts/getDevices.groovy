
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 22, 2008
 * Time: 5:20:20 PM
 * To change this template use File | Settings | File Templates.
 */
import groovy.xml.MarkupBuilder;

def writer = new StringWriter();
def builder = new MarkupBuilder(writer);

def devices = Device.list();

builder.Devices
{
    devices.each {Device device ->
        builder.Object(domainClass:"Device", id:device.id, creationClassName:device.creationClassName, name:device.name){
             builder.Object(id:"${device.id}_composedOf", name:"composedOf"){
                 device.composedOf.each{DeviceComponent dComponent ->
                     builder.Object(domainClass:dComponent.class.name, id:dComponent.id, creationClassName:dComponent.creationClassName, name:dComponent.name)
                 }
             }
             builder.Object(id:"${device.id}_connectedVia", name:"connectedVia"){
                 device.connectedVia.each{Link link ->
                     builder.Object(domainClass:link.class.name, id:link.id, creationClassName:link.creationClassName, name:link.name)
                 }
             }
             builder.Object(id:"${device.id}_hostsAccessPoints", name:"hostsAccessPoints"){
                 device.hostsAccessPoints.each{Ip ip ->
                     builder.Object(domainClass:ip.class.name, id:ip.id, creationClassName:ip.creationClassName, name:ip.name)
                 }
             }
        }
    }
}
return writer.toString();