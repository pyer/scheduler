package ab;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Postgresql {
    private static final Logger LOGGER = LoggerFactory.getLogger(Job1.class);

    Postgresql() {
    	try {
    		Class.forName("org.postgresql.Driver");
    	} catch (ClassNotFoundException e) {
    		LOGGER.error("Postgres driver not found",e);
    	}

    	try {
    		String url = "jdbc:postgresql://localhost/postgres?user=postgres&ssl=false";
    		Connection conn = DriverManager.getConnection(url);
    	} catch (SQLException e) {
    		LOGGER.error("Database connexion failed",e);
    	}
    }
    
	
}