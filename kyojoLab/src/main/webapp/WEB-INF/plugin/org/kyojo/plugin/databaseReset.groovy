package org.kyojo.plugin

import java.sql.Connection
import org.apache.ibatis.exceptions.PersistenceException
import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory;
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.schemaorg.SimpleJsonBuilder

class DatabaseReset {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		SqlSession sqlSession = null
		try {
			sqlSession = gbd.get(SqlSessionFactory.class).openSession()
			Connection conn = sqlSession.getConnection();
			ScriptRunner runner = new ScriptRunner(conn);
			runner.setLogWriter(null);
			runner.runScript(Resources.getResourceAsReader("org/kyojo/lab/gbd/mybatis-schema.sql"));
		} catch(PersistenceException pe) {
			throw new PluginException(this, getClass(), args,
					gbd, ssd, rqd, rpd, pe.getMessage(), pe, PluginException.Level.WARN)
		} finally {
			if(sqlSession != null) {
				sqlSession.close()
			}
		}

		return null
	}

}
