 
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