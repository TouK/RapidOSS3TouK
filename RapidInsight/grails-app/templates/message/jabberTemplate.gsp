<% def subject=message.eventType=="create"?"Event Created":"Event Cleared" %>
----------------------------------
${subject}
Event Properties
name : ${event.name}
elementName : ${event.elementName}
owner : ${event.owner}
severity : ${event.severity}
acknowledged : ${event.acknowledged}
createdAt : ${new Date(event.createdAt)}
changedAt : ${new Date(event.changedAt)}
clearedAt : ${new Date(event.clearedAt)}