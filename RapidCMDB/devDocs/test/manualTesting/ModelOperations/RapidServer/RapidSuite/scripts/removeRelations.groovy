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

logger.info("Starting removeRelations");

def deleteLimit=20;
def deleteCount=0;

//Note that no books are added to repo but Fictons and ScienceFictions are added
Book.search("alias:*",[max:3000]).results.each{    
    if(it.mainCharacter!=null  && deleteCount<deleteLimit)
    {
	   //long t = System.nanoTime();
       it.removeRelation(mainCharacter:it.mainCharacter);
       //logger.info((System.nanoTime()-t)/Math.pow(10,9));
       deleteCount++;
    }
}
logger.info("Deleted ${deleteCount} Books' all relations ");

deleteCount=0;
Author.search("alias:*",[max:3000]).results.each{
    if(it.books.size()>0  && deleteCount<deleteLimit)
    {
	   //long t = System.nanoTime();
       it.removeRelation(books:it.books);
       //logger.info((System.nanoTime()-t)/Math.pow(10,9));
       deleteCount++;
    }
}
logger.info("Deleted ${deleteCount} Authors' all relations ");

logger.info("Ended removeRelations");
