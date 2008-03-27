import test.Author
import test.Book

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
 * Date: Mar 26, 2008
 * Time: 4:48:42 PM
 * To change this template use File | Settings | File Templates.
 */

def insertionCount = 1000;
Author.list()*.delete(flush:true);
Book.list()*.delete(flush:true);

def author = new Author(name:"Oguz Atay").save();
def i = 0;
while(i < insertionCount){
    author.addToBooks(new Book(title:"book" + i , description:"descr"));
    i++;
}

def current = System.currentTimeMillis();
Author.list()*.delete(flush:true);
def elapsed = System.currentTimeMillis() - current;
if(Book.list().size() > 0){
    throw new Exception("could not delete all books");
}
return "deleted " + insertionCount + " records in " + elapsed + " ms.";
