package org.kyojo.plugin.miscLab

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.ibatis.exceptions.PersistenceException
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
import org.kyojo.lab.mpr.ArticleRevisionMapper
import org.kyojo.lab.scm.ArticleRevision

class DatabaseLab {

	private static final Log logger = LogFactory.getLog(DatabaseLab.class)

	List<ArticleRevision> articleRevisionList
	ArticleRevision editRevision
	String editMessage

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		editRevision = new ArticleRevision()

		return null
	}

	List<ArticleRevision> search(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		SqlSession sqlSession = null
		try {
			sqlSession = gbd.get(SqlSessionFactory.class).openSession()
			ArticleRevisionMapper mpr = sqlSession.getMapper(ArticleRevisionMapper.class)
			return mpr.selectOrderBySeqDesc()
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

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		articleRevisionList = search(args, gbd, ssd, rqd, rpd)
		if (articleRevisionList != null && articleRevisionList.size() > 0) {
			editRevision.seq = articleRevisionList[0].seq
			if(StringUtils.isEmpty(editMessage)) {
				editRevision.text = articleRevisionList[0].text
			}
		}

		return null
	}

	Object doSubmit(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		SqlSession sqlSession = null
		editMessage = ""
		try {
			sqlSession = gbd.get(SqlSessionFactory.class).openSession()
			ArticleRevisionMapper mpr = sqlSession.getMapper(ArticleRevisionMapper.class)
			int maxSeq = mpr.selectMaxSeq()
			if(editRevision != null && editRevision.seq == maxSeq) {
				editRevision.seq = null
				editRevision.createdAt = null
				mpr.insert(editRevision)
				sqlSession.commit()
			} else {
				editMessage = "Not changed. Someone accomplished a revision in advance of your submission."
			}
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
