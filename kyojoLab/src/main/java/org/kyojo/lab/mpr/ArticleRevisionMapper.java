package org.kyojo.lab.mpr;

import java.util.List;
import org.kyojo.lab.scm.ArticleRevision;

public interface ArticleRevisionMapper {

	List<ArticleRevision> selectOrderBySeqDesc();

	int selectMaxSeq();

	int insert(ArticleRevision articleRevision);

}