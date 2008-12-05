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
def random=new Random(System.currentTimeMillis());

logger.info("Starting removeInstances");

def deletelimit=40;
def deletecount=0;

def deleteList=[]

logger.info("loop with delete");
deletecount=0;
Fiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deletecount<deletelimit)
    {
       it.remove();
       deletecount++;
    }
}
logger.info("loops ended");



logger.info("Deleted ${deletecount} Ficton");

deletecount=0;
ScienceFiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deletecount<deletelimit)
    {
       it.remove();
       deletecount++;
    }
}

logger.info("Deleted ${deletecount} ScienceFicton");


deletecount=0;
Person.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deletecount<(deletelimit*2))
    {
       it.remove();
       deletecount++;
    }
}

logger.info("Deleted ${deletecount} Person");

logger.info("ended removeInstances");