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
 * Date: Jul 28, 2008
 * Time: 5:41:45 PM
 */
class CacheFilters {
    def filters = {
        all(controller: '*', action: '*') {
            before = {
                response.addHeader("Cache-Control", "no-cache"); // HTTP/1.1
                response.addHeader("Cache-Control", "no-store"); // HTTP/1.1
                response.addHeader("Cache-Control", "must-revalidate"); // HTTP/1.1
                response.addHeader("Pragma", "no-cache"); // HTTP 1.0
                response.addHeader("Expires", "1");
            }
        }
    }
}