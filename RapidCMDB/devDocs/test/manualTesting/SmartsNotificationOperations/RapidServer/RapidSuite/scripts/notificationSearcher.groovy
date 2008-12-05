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
import connector.*;
import org.apache.log4j.Logger



logger.warn("*gonna do notsearcher");


def output=" "

def random=new Random(System.currentTimeMillis());
def limit=random.nextInt(100)+1000

output+=" gonna search ${limit} RsSmartsNotification items <br> "
SmartsNotification.search("alias:*",[max:limit]).results.each{

}
output+=" searched ${limit} items <br> "

limit=random.nextInt(100)+1000

output+=" gonna search ${limit} RsEvent items <br> "
RsEvent.search("alias:*",[max:limit]).results.each{

}
output+=" searched ${limit} items <br> "

limit=random.nextInt(100)+1000

output+=" gonna search ${limit} RsRiEvent items <br> "
RsRiEvent.search("alias:*",[max:limit]).results.each{

}
output+=" searched ${limit} items <br> "

logger.warn("done notsearcher");
