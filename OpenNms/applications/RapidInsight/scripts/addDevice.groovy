def targetHost = "localhost"
def port = 5817
def writer = new StringWriter();

def s = new Socket(targetHost,port);
def xml = new groovy.xml.MarkupBuilder()
xml.log {
    events {
        event {
            uei("uei.opennms.org/internal/discovery/newSuspect")
            source("RapidInsight")
            host("OpenNmsHost")
            "interface"("192.168.1.100")
        }
    }
}


s << xml
s.close()

/*
<log>
 <events>
  <event >
   <uei>uei.opennms.org/internal/discovery/newSuspect</uei>
   <source>RapidInsight</source>
   <time>Monday, November 3, 2008 4:02:15 PM GMT</time>
   <host>OpenNmsHost</host>
   <interface>192.168.1.1</interface>
  </event>
 </events>
</log>
*/