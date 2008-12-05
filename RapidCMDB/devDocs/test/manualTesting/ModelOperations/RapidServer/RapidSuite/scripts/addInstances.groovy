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

logger.info("Starting addInstances");

def addlimit=50;

def addTimes=random.nextInt(addlimit)+20;

addTimes.times{
    def name="Fiction${random.nextInt(5000)}";
    Fiction.add(name:name,publishDate:new Date(),description:"fiction book ${Math.random()}",mainCharacterName:"myfictionless");
}
logger.info("Added ${addTimes} Ficton");

addTimes=random.nextInt(addlimit)+20;
addTimes.times{
    def name="ScienceFiction${random.nextInt(5000)}";
    ScienceFiction.add(name:name,publishDate:new Date(),description:"fiction book ${Math.random()}");
}
logger.info("Added ${addTimes} ScienceFicton");

addTimes=random.nextInt(addlimit)+20;
addTimes.times{
    def name="Author${random.nextInt(5000)}";
    Author.add(name:name,birthDate:new Date(),address:"street ${Math.random()}",email:"@d@g@@f@e@s@@a@",numberOfBooks:random.nextInt(5000));
}
logger.info("Added ${addTimes} Author");


addTimes=random.nextInt(addlimit)+20;
addTimes.times{
    def name="Person${random.nextInt(5000)}";
    Person.add(name:name,birthDate:new Date(),address:"street ${Math.random()}",email:"@d@g@@f@e@s@@a@");
}
logger.info("Added ${addTimes} Person");


logger.info("Ended addInstances");
