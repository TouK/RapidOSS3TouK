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
now = Date.now();
RsEvent.removeAll();
RsRiEvent.removeAll();
RsEventJournal.removeAll();
RsHistoricalEvent.removeAll();


event1 = RsEvent.add(name:"RsEvent1",owner:"Pinar",createdAt:now,changedAt:now, clearedAt:now);
event2 = RsEvent.add(name:"RsEvent2",owner:"Berkay",createdAt:now,changedAt:now, clearedAt:now);
event3 = RsEvent.add(name:"RsEvent3",owner:"Karl",createdAt:now,severity:2,changedAt:now, clearedAt:now);

assert (event1.owner == "Pinar" && event1.acknowledged == false)
event1.acknowledge(true,"Berkay");
event1 = RsEvent.get(name:"RsEvent1");
assert (event1.owner == "Pinar" && event1.acknowledged == true)

event1 = RsEvent.get(name:"RsEvent1");
event1.acknowledge(false,"Pinar");
event1 = RsEvent.get(name:"RsEvent1");
assert (event1.owner == "Pinar" && event1.acknowledged == false)

event1 = RsEvent.get(name:"RsEvent1");
event1.acknowledge(true,"Pinar");
event1 = RsEvent.get(name:"RsEvent1");
assert (event1.owner == "Pinar" && event1.acknowledged == true)


event = RsEvent.get(name:"RsEvent2");
event.setOwnership(true,"Pinar");
event = RsEvent.get(name:"RsEvent2");
assert (event.owner == "Pinar")

event.setOwnership(false,"Admin");
event = RsEvent.get(name:"RsEvent2");
assert (event.owner == "")


event = RsEvent.get(name:"RsEvent3");
event.addToJournal("Something")
event.addToJournal("Something", "Did smt")
event.addToJournal([eventName:"Anything", details:"did anything",rsTime:new Date()]);

event = RsEvent.get(name:"RsEvent1");
oldid = event.id;
journal = RsEventJournal.search("eventId:${oldid}");
assert journal.total == 3;
event.clear();
RsHistoricalEvent.saveHistoricalEventCache();

event = RsEvent.get(name:"RsEvent1");
assert event == null;



historicalEvent = RsHistoricalEvent.search("name:RsEvent1");
assert historicalEvent.results[0].activeId == oldid

journal = RsEventJournal.search("eventId:${oldid}");
assert journal.total == 4;



event = RsRiEvent.notify([name:"RsRiEvent1",eventName:"Down", owner:"Karl",createdAt:now,changedAt:now, clearedAt:now]);
oldid = event.id;
event.acknowledge(true,"Pinar");
event.setOwnership(true,"Tugrul");
event.clear();
RsHistoricalEvent.saveHistoricalEventCache();

historicalEvent = RsHistoricalEvent.search("name:RsRiEvent1");
assert historicalEvent.results[0].activeId == oldid

journal = RsEventJournal.search("eventId:${oldid}");
assert journal.total == 4;


event = RsRiEvent.notify([name:"RsRiEvent1",eventName:"Down", owner:"Karl",createdAt:now,changedAt:now, clearedAt:now]);
event.clear();
RsHistoricalEvent.saveHistoricalEventCache();

historicalEvent = RsHistoricalEvent.search("name:RsRiEvent1");
assert historicalEvent.total == 2

return "Successfully run the tests"