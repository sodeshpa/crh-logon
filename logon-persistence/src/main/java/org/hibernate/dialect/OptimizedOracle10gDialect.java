package org.hibernate.dialect;

public class OptimizedOracle10gDialect extends Oracle10gDialect {
	public String getLimitString(String sql, int offset, int limit) {
		sql = sql.trim();
		boolean isForUpdate = false;

		StringBuffer pagingSelect = new StringBuffer( sql.length()+128 );
		pagingSelect.append("select * from ( select /*+ FIRST_ROWS(").append(limit).append(") */ row_.*, rownum rownum_ from ( ");
		pagingSelect.append(sql);

		if (pagingSelect.substring( pagingSelect.length()-11 ).equalsIgnoreCase(" for update")){
			pagingSelect.setLength( pagingSelect.length()-11 );
			isForUpdate = true;
		}
		if (offset!=0) {
			pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
		}
		else {
			pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > 0");
		}

		if ( isForUpdate ) {
			pagingSelect.append( " for update" );
		}

		return pagingSelect.toString();
	}
}