<%
    def eventId = params.id;
    def domainObject = RsHistoricalEvent.get(id: eventId);
    if (domainObject != null) {
        request.getRequestDispatcher("defaultHistoricalEventDetails.gsp").forward(request, response);
    }
    else {
%>
Historical event with id ${eventId} does not exist.
<%
    }
%>
