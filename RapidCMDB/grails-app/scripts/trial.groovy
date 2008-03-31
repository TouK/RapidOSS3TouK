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
 * User: mustafa
 * Date: Mar 19, 2008
 * Time: 10:35:04 PM
 * To change this template use File | Settings | File Templates.
 */

//Author.list()*.delete();
//Book.list()*.delete();
//
//
//
//def author = new Author(name:"author", book:new Book(title:"book", description:"descr")).save(flush:true);
//
//
//Book.list()*.delete();
//def book = new Book(title:"book", description:"descr").save();
//author.book = book;
//book.author = author;
//author.addToBooks(new Book(title:"book", description:"descr"));

//author.save();
//
//author.delete();



//
//def event =  Event.get([name:"event2"]);
//return event.lastOccured;
def model1 = Model1.findByProp1("prop1value");
def model2 = Model2.findByProp2("prop2value");
def model3 = Model1.findByProp1("prop1value1")
if(!model1)
{
    model1 = Model1.add(prop1:"prop1value")
    model2 = Model2.add(prop2:"prop2value")
    model3 = Model1.add(prop1:"prop1value1")
    model2.rel1 = model1;
    model2.save();
}
//model2.rel1 = model3;
//    model2.save();
//model2.rel1 = null;
//    model2.save();

return ""+model2.rel1 + "==" + model3.revrel1

