<% def subject=message.eventType=="create"?"Event Created":"Event Cleared" %>
<html>
<body>
<b>${subject}</b>
<br><br>Event Properties
<ul>
<li>name : ${event.name}</li>
<li>elementName : ${event.elementName}</li>
<li>owner : ${event.owner}</li>
<li>severity : ${event.severity}</li>
<li>acknowledged : ${event.acknowledged}</li>
<li>createdAt : ${new Date(event.createdAt)}</li>
<li>changedAt : ${new Date(event.changedAt)}</li>
<li>clearedAt : ${new Date(event.clearedAt)}</li>
</ul>
</body>
</html>