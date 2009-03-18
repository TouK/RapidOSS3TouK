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

logger.warn("*gonna do notsender");

def smcon=SmartsConnector.get(name:"smnot");
def ds=smcon.ds;

def addlimit=50;
def addcount=0;

def errorcount
def random=new Random(System.currentTimeMillis());
addlimit.times{

	def elid=random.nextInt(150)+1000;
	def evid=random.nextInt(150)+1000;
	ds.addNotification(ClassName:"Router",InstanceName:"trouter${elid}",EventName:"tevent${evid}");
	addcount++;

}

logger.warn("done notsender, added ${addcount} events");