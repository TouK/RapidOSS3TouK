<% def subject=message.eventType=="create"?"Event Created":"Event Cleared" %>
${subject}
Event Properties
name : ${event.name}
elementName : ${event.elementName}
severity : ${event.severity}