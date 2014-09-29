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

	/** SQL���ݿ���� */
	private SQLiteDatabase mDb;
	public DBHelper helper;
	/** ����ģʽ ���� */
	private static DBWrapper sInstance;

	/**
	 * ����ģʽ <br>
	 * һ�������ֻ����һ��ʵ�� <br>
	 * 1����һ��˽�о�̬��Ա <br>
	 * 2����һ��������̬����getInstance�õ����˽�о�̬��Ա <br>
	 * 3����һ��˽�еĹ��췽����������ʵ������ <br>
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
		mDb.insert(DBHelper.TABLE��HIGHSCORES, "", values);
	}

	public void daleteDB_ALL() {
		mDb.execSQL("DROP TABLE IS " + DBHelper.TABLE��HIGHSCORES);
		helper.onCreate(mDb);
	}

	public LinkedList<DBWrapper.data> rawQueryDB() {
		LinkedList<DBWrapper.data> list = new LinkedList<DBWrapper.data>();
		Cursor cursor = null;
		try {

			cursor = mDb.query(DBHelper.TABLE��HIGHSCORES,// ����
					new String[] { DBHelper.COLUMN_NAME,
							DBHelper.COLUMN_SCORES, DBHelper.COLUMN_DATE }, // Ҫ��ѯ������
					null, // ��ѯ����
					null,// ��ѯ����ֵ
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
