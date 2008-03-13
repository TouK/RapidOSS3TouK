import models.Script;
Script.add("RegisterScripts");
Script.execute("RegisterScripts",[:]);
Script.execute("CreateConnections",[:]);
Script.execute("CreateDatasources",[:]);
Script.execute("CreateDatabase",[:]);
Script.execute("Connector",[:]);
