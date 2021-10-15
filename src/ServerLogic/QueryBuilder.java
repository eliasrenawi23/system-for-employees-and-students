package ServerLogic;


/**
 * This class provides the means to make a dynamic query at run time.
 * By using the Builder pattern, the user can create queries easily with less chance of making type mistakes.
 * */
public class QueryBuilder {

	StringBuilder query;

	public QueryBuilder() {
		query = new StringBuilder();
	}

	private void addStringWithCommaAndClosed(String... strs) {
		for (String s : strs) {
			query.append("`"+s + "`, ");
		}
		query.deleteCharAt(query.length() - 1);
		query.deleteCharAt(query.length() - 1);
	}
	private void addStringsWithComma(String... strs) {
		for (String s : strs) {
			query.append(s + ", ");
		}
		query.deleteCharAt(query.length() - 1);
		query.deleteCharAt(query.length() - 1);
	}

	
	public QueryBuilder select(String... cols) {
		// clear the stringBuilder
		query.setLength(0);
		query.append("SELECT ");
		addStringsWithComma(cols);
		return this;
	}

	public QueryBuilder from(String... cols) {
		query.append(" FROM ");
		addStringsWithComma(cols);

		return this;
	}

	public QueryBuilder from(String col1, String col2) {
		query.append(" FROM " + col1 + ", " + col2);

		return this;
	}

	public QueryBuilder where(String str) {
		query.append(" WHERE " + str);

		return this;
	}

	public QueryBuilder whereNot(String str) {
		query.append(" WHERE NOT " + str);

		return this;
	}

	public QueryBuilder or(String str) {
		query.append(" OR " + str);

		return this;
	}

	public QueryBuilder and(String str) {
		query.append(" AND " + str);

		return this;
	}

	public QueryBuilder less(String str) {
		query.append(" < " + sqlStr(str));

		return this;
	}

	public QueryBuilder lessEq(String str) {
		query.append(" <= " + sqlStr(str));

		return this;
	}

	public QueryBuilder greater(String str) {
		query.append(" > " + sqlStr(str));
		;
		return this;
	}

	public QueryBuilder greaterEq(String str) {
		query.append(" >= " + sqlStr(str));
		return this;
	}

	public QueryBuilder eq(String str) {
		query.append(" = " + sqlStr(str));
		return this;
	}

	public QueryBuilder insertInto(String str) {
		// clear the stringBuilder
		query.setLength(0);

		query.append("INSERT INTO " + str);

		return this;
	}

	public QueryBuilder forColumns(String... cols) {
		query.append(" (");
		addStringWithCommaAndClosed(cols);
		query.append(") ");

		return this;
	}

	public QueryBuilder theValues(String... vals) {
		query.append("VALUES (");
		addStringsWithComma(vals);
		query.append(")");

		return this;
	}

	public QueryBuilder deleteFrom(String table) {
		// clear the stringBuilder
		query.setLength(0);

		query.append("DELETE FROM " + table);

		return this;
	}

	public QueryBuilder update(String tableName) {
		// clear the stringBuilder
		query.setLength(0);

		query.append("UPDATE " + tableName);

		return this;
	}

	public QueryBuilder set(String... colVal) {
		query.append(" SET ");

		if (colVal.length % 2 != 0)
			System.err.println("QuerySetError, for each column there should be a value!");
		else {
			for (int i = 0; i < colVal.length; i += 2) {
				query.append(colVal[i] + " = " + sqlStr(colVal[i + 1]) + ", ");

			}
			query.deleteCharAt(query.length() - 1);
			query.deleteCharAt(query.length() - 1);
		}
		query.append(" ");

		return this;
	}

	public QueryBuilder colWithVal(String col, String val) {

		query.append(col + " = " + sqlStr(val));

		return this;
	}

	// TODO: reduce duplicate with 'theValues' function
	public QueryBuilder in(String... vals) {
		query.append("IN (");
		addStringsWithComma(vals);
		query.append(") ");

		return this;
	}

	public String avg(String col) {
		return "AVG(" + col + ")";
	}

	public String count(String col) {
		return "COUNT(" + col + ")";
	}

	public String sum(String col) {
		return "SUM(" + col + ")";
	}

	public String max(String col) {
		return "MAX(" + col + ")";
	}
	
	private String sqlStr(String str) {
		return "'" + str + "'";
	}

	public String toString() {
		return query.toString() + ";";
	}

	
}
