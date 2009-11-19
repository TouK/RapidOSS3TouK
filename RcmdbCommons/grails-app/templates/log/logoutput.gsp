<%
    def isCompleteLine = line.endsWith("\n");
    def isException = line.indexOf("Exception") >= 0 || line.startsWith("\tat ");
    line = line.replaceAll("\n", "")
    line = line.replaceAll("\r", "")
    line = line.replaceAll("\t", "&#160;&#160;&#160;&#160;&#160;")
    line = line.replaceAll(" ", "&#160;")
    if(line == "") line = "&#160;";

    if(!isException)
    {
        def replacements = ["WARN":"""<span class="warnLogLevel">WARN</span>""",
                "DEBUG":"""<span class="debugLogLevel">DEBUG</span>""",
                "FATAL":"""<span class="fatalLogLevel">FATAL</span>""",
                "INFO":"""<span class="infoLogLevel">INFO</span>""",
                "ERROR":"""<span class="infoLogLevel">INFO</span>""",
                "Exception":"""<span class="exceptionLogLevel">INFO</span>"""]
        def entries = replacements.entrySet()
        for(it = entries.iterator(); it.hasNext();)
        {
            def replacement = it.next();
            if(line.indexOf(replacement.key) >= 0)
            {
                line = line.replaceAll(replacement.key, replacement.value)
                break;
            }
        }
    }
%>
<span class="${isCompleteLine?"logLine":"incompleteLogLine"} ${isException?"exceptionLogLine":""}">${line}</span>${isCompleteLine?"<br>":""}