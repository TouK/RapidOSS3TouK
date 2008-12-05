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


logger.info("Starting searchInstances");

def output=" "

def random=new Random(System.currentTimeMillis());
def limit=random.nextInt(20)+5

limit.times{
    Book.search("alias:*",[max:150]);
    Fiction.search("alias:*",[max:150]);
    ScienceFiction.search("alias:*",[max:150]);
    Person.search("alias:*",[max:150]);
    Author.search("alias:*",[max:150]);
}

logger.info("Search all models ${limit} times");

logger.info("Ended searchInstances");


