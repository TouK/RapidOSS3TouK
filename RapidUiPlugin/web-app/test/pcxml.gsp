<%@ page contentType="text/xml;charset=UTF-8" %>
<ResultSet >

<% random = new Random()
   //nodecount=random.nextInt(10)
   5.times{
   count=random.nextInt(5)+8
%>
    <Item name="foo${it}" count="${count}" />
<% } %>
</ResultSet>
