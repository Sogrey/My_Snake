/**
 * 
 */
package org.sogrey.mysnake.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Sogrey
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	/** 数据库名 */
	public static final String SCORES = "Scores";
	/** 数据库版本 */
	public static final int SCORES_VERSION =1;
	/** 高分榜表名 */
	public static final String TABLE＿HIGHSCORES = "HighScores";
	/** 列-ID */
	public static final String COLUMN_ID = "_id";
	/** 列-NAME */
	public static final String COLUMN_NAME = "name";
	/** 列-SCORES */
	public static final String COLUMN_SCORES = "scores";
	/** 列-DATE */
	public static final String COLUMN_DATE = "date";

	public DBHelper(Context context) {
		super(context, SCORES, null, SCORES_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE＿HIGHSCORES+"("
		+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
		+COLUMN_NAME+" TEXT,"
		+COLUMN_SCORES+" INTEGER,"
		+COLUMN_DATE+" TEXT"
		+")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IS "+TABLE＿HIGHSCORES);
		onCreate(db);
	}

}
