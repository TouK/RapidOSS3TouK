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

logger.info("Starting addRelations");

def addLimit=30;
def addCount=0;


def fictionList=[]
addCount=0;
Fiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && addCount<addLimit)
    {
       fictionList.add(it);
       addCount++;
    }
}

addCount=0;
Person.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && addCount<addLimit)
    {
       if(!fictionList.isEmpty())
       {
//            long t = System.nanoTime();
	        it.addRelation(referringBooks:fictionList.remove(0));
//            logger.info((System.nanoTime()-t)/Math.pow(10,9));
            addCount++;
       }
    }
}
logger.info("Added ${addCount} Ficton - Person Relations");


def scienceFictionList=[]
addCount=0;
ScienceFiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(5)==0 && addCount<addLimit)
    {
       scienceFictionList.add(it);
       addCount++;
    }
}

addCount=0;
Author.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(5)==0 && addCount<addLimit)
    {
       if(!scienceFictionList.isEmpty())
       {
//	        long t = System.nanoTime();
            it.addRelation(books:scienceFictionList.remove(0));
//            logger.info((System.nanoTime()-t)/Math.pow(10,9));
            addCount++;
       }
    }
}


logger.info("Added ${addCount} ScienceFicton - Author Relations");

logger.info("Ended addRelations");