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
 * User: Administrator
 * Date: Jun 11, 2008
 * Time: 2:26:15 PM
 */
class MessageService {
    boolean transactional = false
    def messageSource;
    public void afterPropertiesSet(){}
    public String getMessage(code, args, defaultMessage) {
        return messageSource.getMessage(code, args as Object[], defaultMessage, Locale.ENGLISH);        
    }

    public String getMessage(error) {
        return messageSource.getMessage(error, Locale.ENGLISH);
    }

    public String getMessage(code, args) {
        return messageSource.getMessage(code, args as Object[], Locale.ENGLISH); 
    }
}