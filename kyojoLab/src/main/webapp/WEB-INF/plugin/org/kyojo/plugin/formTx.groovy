package org.kyojo.plugin

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

class FormTx {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		SqlSession sqlSession = null
		try {
			// openSession()でトランザクション開始
			sqlSession = gbd.get(SqlSessionFactory.class).openSession()
			te.appendPiecedTemplate(cache, "formTx", args, indent, isForced)
		} finally {
			if(sqlSession != null) {
				// トランザクション終了
				sqlSession.close()
			}
		}

		return null
	}

}
