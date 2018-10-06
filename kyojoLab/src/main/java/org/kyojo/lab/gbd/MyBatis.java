package org.kyojo.lab.gbd;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.kyojo.core.GlobalData;

public class MyBatis implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(MyBatis.class);

	private static GlobalData gbd = null;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		try {
			gbd = GlobalData.getInstance(servletContext);
		} catch(IOException ioe) {
			logger.fatal(ioe.getMessage(), ioe);
		}

		try(Reader reader = Resources.getResourceAsReader("org/kyojo/lab/gbd/mybatis-config.xml")) {
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			gbd.put(SqlSessionFactory.class, sqlSessionFactory);

			SqlSession sqlSession = sqlSessionFactory.openSession();
			Connection conn = sqlSession.getConnection();
			ScriptRunner runner = new ScriptRunner(conn);
			runner.setLogWriter(null);
			runner.runScript(Resources.getResourceAsReader("org/kyojo/lab/gbd/mybatis-schema.sql"));
		} catch(IOException ioe) {
			logger.fatal(ioe.getMessage(), ioe);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

}
