<%
    def isCompleteLine = line.endsWith("\n");
    line = line.replaceAll("\n", "")
    line = line.replaceAll("\r", "")
    line = line.replaceAll(" ", "&#160;")
    if(line == "") line = "&#160;";
%>
<span class="${isCompleteLine?"logLine":"incompleteLogLine"}">${line}</span>${isCompleteLine?"<br>":""}