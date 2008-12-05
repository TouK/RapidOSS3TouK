/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
 
dataSource {
	pooled = false
	driverClassName = "org.hsqldb.jdbcDriver"
	username = "sa"
	password = ""
}
hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    jdbc.batch_size=0
    cache.provider_class='org.hibernate.cache.EhCacheProvider'
}
// environment specific settings
environments {
	production {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:data/prodDb;shutdown=true"
		}
	}
    development {
		dataSource {
            configClass = GrailsAnnotationConfiguration.class
            dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:file:data/devDB;shutdown=true"
		}
	}
	reset {
		dataSource {
            configClass = GrailsAnnotationConfiguration.class
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:file:data/devDB;shutdown=true"
		}
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:mem:testDb"
		}
	}
	performance{
        dataSource{
            dbCreate = "update"
            driverClassName = "com.mysql.jdbc.Driver"
            url = "jdbc:mysql://localhost/students"
	        username = "root"
	        password = "root"
        }
    }

}