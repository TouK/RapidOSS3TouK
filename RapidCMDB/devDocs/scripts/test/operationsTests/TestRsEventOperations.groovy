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
event = RsEvent.get(name:"RsEvent1");
assert event == null;

historicalEvent = RsHistoricalEvent.search("name:RsEvent1");
assert historicalEvent.results[0].active == false

journal = RsEventJournal.search("eventId:${oldid}");
assert journal.total == 0;

journal = RsEventJournal.search("eventId:${historicalEvent.results[0].id}")
assert journal.total == 4;

event = RsRiEvent.notify([name:"RsRiEvent1",eventName:"Down", owner:"Karl",createdAt:now,changedAt:now, clearedAt:now]);
oldid = event.id;
event.acknowledge(true,"Pinar");
event.setOwnership(true,"Tugrul");
event.clear();

historicalEvent = RsHistoricalEvent.search("name:RsRiEvent1");
assert historicalEvent.results[0].active == false

journal = RsEventJournal.search("eventId:${oldid}");
assert journal.total == 0;

journal = RsEventJournal.search("eventId:${historicalEvent.results[0].id}")
assert journal.total == 4;

event = RsRiEvent.notify([name:"RsRiEvent1",eventName:"Down", owner:"Karl",createdAt:now,changedAt:now, clearedAt:now]);
event.clear();

historicalEvent = RsHistoricalEvent.search("name:RsRiEvent1");
assert historicalEvent.total == 2

return "Successfully run the tests"