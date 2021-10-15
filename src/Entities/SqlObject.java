package Entities;

import java.lang.reflect.Field;
import java.sql.Timestamp;

import Utility.DateUtil;


/**
 * This abstract class is used for objects that can be inserted, updated and deleted generally without the need to make a different query
 * for every new entite, the class uses java reflection in order to set the fields of the class for the different queries.
 * 
 * @author Bshara
 * */
public abstract class SqlObject {

	/**
	 * Configure the data base fields types
	 */
	public static class Config {
		public static final String sqlLong = "BIGINT(8)";
		public static final String sqlInt = "INT";
		public static final String sqlDate = "TIMESTAMP";
		public static final String sqlBlob = "LONGBLOB";
		public static final String sqlBoolean = "BOOLEAN";
		public static final String sqlString = "VARCHAR(256)";
		public static final String sqlLongText = "LONGTEXT";
		public static final String sqlDefault = "VARCHAR(45)";
		public static String schemaName = "icm";

		// Use this suffix in the field name of a class where the field should be a long
		// text
		public static String longTextSuffix = "LT";

	}

	private int fieldsCount;

	private String tableName;

	private Field[] fields;
	private String[] fieldsNames;

	public SqlObject() {
		fields = this.getClass().getFields();

		fieldsCount = fieldsLastIndex() > 0 ? fieldsLastIndex() + (hasForeignKey() ? 1 : 0) : fields.length;
		tableName = this.getClass().getName();

		// Table name should not contain dots.
		if (tableName.contains(".")) {
			// To remove the packages names and keep the class name only,
			// Set the tableName to the substring after the last dot.
			tableName = tableName.substring(tableName.lastIndexOf('.') + 1);

		}

		fieldsNames = calFieldsNames();
	}

	public Field[] getFields() {
		return fields;
	}

	public String getTableName() {
		return "`" + Config.schemaName + "`.`" + tableName + "`";
	}

	/**
	 * Returns the info in SQL format of this object. Note: this is not executed per
	 * object since this function is mainly used for creating a table for this
	 * object, which should happen only once.
	 */
	public String tableInfo() {

		StringBuilder sb = new StringBuilder(50);

		// Adding the table name
		sb.append(getTableName() + " ( ");

		// Adding the fields names with types

		Field[] fields = getClass().getFields();

		for (int i = 0; i < fieldsCount; i++) {

			String fieldName = fields[i].getName();
			sb.append("`" + fieldName + "` ");

			String fieldType = fields[i].getType().toString();

			switch (fieldType) {

			case "long":
				sb.append(Config.sqlLong);

				break;
			case "int":
				sb.append(Config.sqlInt);

				break;

			case "boolean":
				sb.append(Config.sqlBoolean);
				break;

			case "class java.sql.Timestamp":
				sb.append(Config.sqlDate);

				break;

			case "interface java.sql.Blob":
				sb.append(Config.sqlBlob);

				break;

			case "class java.lang.String":

				// If the field name ends with this suffix then configure it as a long text
				// in the database.
				if (fieldName.endsWith(Config.longTextSuffix)) {
					sb.append(Config.sqlLongText);

				} else {
					sb.append(Config.sqlString);

				}

				break;

			default:
				sb.append(Config.sqlDefault);
				System.err.println(
						"Error: type [" + fieldType + "] not recognized! in class " + this.getClass().getName());

				break;
			}

			sb.append(" NOT NULL, ");

		}

		// Adding the primary key

		sb.append("PRIMARY KEY (`" + getPrimaryKeyName() + "`)");

		if (hasForeignKey()) {
			sb.append(", CONSTRAINT `" + getReferenceTableForeignKeyName() + tableName + "FK` FOREIGN KEY (`"
					+ getForeignKeyName() + "`) ");
			sb.append("REFERENCES `" + Config.schemaName + "`.`" + getReferenceTableName() + "` (`"
					+ getReferenceTableForeignKeyName() + "`) ");
			sb.append("ON DELETE CASCADE ON UPDATE CASCADE )");
		} else {
			sb.append(")");

		}

		return sb.toString();
	}

	/**
	 * By default, the key is the first public field in the class. Override this
	 * method to change the key.
	 */

	public abstract int getPrimaryKeyIndex();

	public abstract int getForeignKeyIndex();

	public abstract String getReferenceTableName();

	public abstract boolean hasForeignKey();

	public abstract String getReferenceTableForeignKeyName();

	public abstract int fieldsLastIndex();

	/**
	 * returns the value of the primary key as an String object.
	 */
	public String getForeignKeyName() {
		return getFieldName(getForeignKeyIndex());
	}

	/**
	 * returns the value of the primary key as an String object.
	 */
	public String getPrimaryKeyName() {
		return getFieldName(getPrimaryKeyIndex());
	}

	/**
	 * returns the value of the primary key as an String object.
	 */
	public String getPrimaryKeyValue() {
		return getFieldValue(getPrimaryKeyIndex());
	}

	private String getFieldValue(int index) {
		try {
			return getClass().getFields()[index].get(this).toString();
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getFieldName(int index) {
		try {
			return getClass().getFields()[index].getName();
		} catch (IllegalArgumentException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String[] getFieldsAndValues() {

		Field[] fields = getClass().getFields();
		String[] results = new String[fieldsCount * 2];

		for (int i = 0, j = 0; i < fieldsCount; i++, j += 2) {
			results[j] = "`" + fields[i].getName() + "`";
			try {
				results[j + 1] = fieldValueDecoder(fields[i]);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return results;
	}

	private String[] calFieldsNames() {

		Field[] fields = getClass().getFields();
		String[] results = new String[fieldsCount];

		for (int i = 0; i < fieldsCount; i++) {
			results[i] = fields[i].getName();
		}
		return results;
	}

	public String[] getFieldsNames() {
		return fieldsNames;
	}

	public String[] getFieldsValues() {

		Field[] fields = getClass().getFields();
		String[] results = new String[fieldsCount];

		for (int i = 0; i < fieldsCount; i++) {
			results[i] = "'" + fieldValueDecoder(fields[i]) + "'";
		}
		return results;
	}

	private String fieldValueDecoder(Field f) {

		String type = f.getType().toString();
		if (type.compareTo("boolean") == 0) {
			try {
				return f.getBoolean(this) ? "1" : "0";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (type.compareTo("class java.sql.Timestamp") == 0) {
			try {

				Timestamp timeStamp = (Timestamp) f.get(this);

				timeStamp = DateUtil.add(timeStamp, 0, 3, 30);

				String ts = timeStamp.toString();

				return ts.substring(0, ts.lastIndexOf('.'));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			return f.get(this).toString();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("Error at field decoder");
		return "";

	}
}
