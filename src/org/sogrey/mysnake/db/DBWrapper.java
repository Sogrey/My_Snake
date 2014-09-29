/**
 * 
 */
package org.sogrey.mysnake.db;

import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Sogrey
 * 
 */
public class DBWrapper {

	/** SQL数据库对象 */
	private SQLiteDatabase mDb;
	public DBHelper helper;
	/** 单例模式 对象 */
	private static DBWrapper sInstance;

	/**
	 * 单例模式 <br>
	 * 一个类最多只能有一个实例 <br>
	 * 1、有一个私有静态成员 <br>
	 * 2、有一个公开静态方法getInstance得到这个私有静态成员 <br>
	 * 3、有一个私有的构造方法（不允许被实例化） <br>
	 */

	public static DBWrapper getInstance(Context context) {
		if (sInstance == null) {
			synchronized (DBWrapper.class) {
				if (sInstance == null) {
					sInstance = new DBWrapper(context);
				}
			}
		}
		return sInstance;
	}

	private DBWrapper(Context context) {
		helper = new DBHelper(context);
		mDb = helper.getWritableDatabase();
	}

	public void insertDB(String name, int scores, String date) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_NAME, name);
		values.put(DBHelper.COLUMN_SCORES, scores);
		values.put(DBHelper.COLUMN_DATE, date);
		mDb.insert(DBHelper.TABLE＿HIGHSCORES, "", values);
	}

	public void daleteDB_ALL() {
		mDb.execSQL("DROP TABLE IS " + DBHelper.TABLE＿HIGHSCORES);
		helper.onCreate(mDb);
	}

	public LinkedList<DBWrapper.data> rawQueryDB() {
		LinkedList<DBWrapper.data> list = new LinkedList<DBWrapper.data>();
		Cursor cursor = null;
		try {

			cursor = mDb.query(DBHelper.TABLE＿HIGHSCORES,// 表名
					new String[] { DBHelper.COLUMN_NAME,
							DBHelper.COLUMN_SCORES, DBHelper.COLUMN_DATE }, // 要查询的列名
					null, // 查询条件
					null,// 查询条件值
					null, null,
					DBHelper.COLUMN_SCORES + " DESC", "50");
			int nameIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
			int scoresIndex = cursor.getColumnIndex(DBHelper.COLUMN_SCORES);
			int dateIndex = cursor.getColumnIndex(DBHelper.COLUMN_DATE);
			DBWrapper.data data;
			while (cursor.moveToNext()) {
				data = new DBWrapper.data();
				data.name = cursor.getString(nameIndex);
				data.scores = cursor.getInt(scoresIndex);
				data.date = cursor.getString(dateIndex);
				list.add(data);
			}

		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	public class data {
		public String name;
		public int scores;
		public String date;
	}
}
