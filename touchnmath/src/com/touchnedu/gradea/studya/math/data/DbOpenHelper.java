package com.touchnedu.gradea.studya.math.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {

	private static final String DATABASE_NAME = "touchnmath.db";
	private static final int DATABASE_VERSION = 1;
	public static SQLiteDatabase mDB;
	private DatabaseHelper mDBHelper;
	private Context context;

	private class DatabaseHelper extends SQLiteOpenHelper{

		// 생성자. DB 이름과 버전을 넘겨받는다.
		public DatabaseHelper(Context context, String name,
																					CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		/* 최초 DB를 만들때 한번만 호출된다.
		 * 생성자에서 넘겨받은 이름과 버전의 데이터베이스가 존재하지 않을 때 한 번만 호출.
		 * 때문에 새로운 데이터베이스를 생성할 때 사용한다. 
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("prjt", "db Create");
			db.execSQL(DataBases.CreateDB._CREATE);

		}

		/* 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
		 * 데이터베이스가 존재하지만 버전이 다르면 호출. 데이터베이스를 변경하고 싶을 때
		 * 버전을 올려주고 새로운 작업을 하면 된다.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DataBases.CreateDB._TABLENAME);
			onCreate(db);
		}
	}

	public DbOpenHelper(Context context) {
		this.context = context;
	}

	public DbOpenHelper open() throws SQLException {
		Log.i("prjt", "db open()");
		mDBHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		mDB = mDBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		Log.i("prjt", "db close");
		mDB.close();
	}

	// Insert DB
	public long insertColumn(String quizNumber, String chapter){
		ContentValues values = new ContentValues();
		values.put(DataBases.CreateDB.QUIZNUMBER, quizNumber);
		values.put(DataBases.CreateDB.CHAPTER, chapter);
		return mDB.insert(DataBases.CreateDB._TABLENAME, null, values);
	}

	// Update DB
	public boolean updateColumn(long id, String quizNumber, String count){
		ContentValues values = new ContentValues();
		values.put(DataBases.CreateDB.QUIZNUMBER, quizNumber);
		return mDB.update(DataBases.CreateDB._TABLENAME, values, "_id="+id, null) > 0;
	}
	
	
	// Delete ID
	public boolean deleteColumn(String quizNumber, String chapter){
		return mDB.delete(DataBases.CreateDB._TABLENAME, 
																									"quizNumber='" + quizNumber + 
																									"' and chapter='" + chapter + 
																									"'", null) > 0;
	}
	
	// isBookmark
	public boolean isBookmark(String quizNumber, String chapter) {
		Cursor cursor = mDB.rawQuery("select * from " 
																+ DataBases.CreateDB._TABLENAME
																+ " where " + DataBases.CreateDB.CHAPTER
																+ "=" + chapter
																+ " and " + DataBases.CreateDB.QUIZNUMBER
																+ "=" + quizNumber, null);
		return cursor.getCount() > 0;
	}
	
//	// Delete Contact
//	public boolean deleteColumn(String number){
//		return mDB.delete(DataBases.CreateDB._TABLENAME, "contact="+number, null) > 0;
//	}
	
	// Select All
	public Cursor getAllColumns(){
		return mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
	}

	// ID 컬럼 얻어 오기
	public Cursor getColumn(long id){
		Cursor c = mDB.query(DataBases.CreateDB._TABLENAME, null, 
				"_id="+id, null, null, null, null);
		if(c != null && c.getCount() != 0)
			c.moveToFirst();
		return c;
	}

	// 챕터로 검색 하기 목차 처음 가져 올때 (rawQuery)
	public Cursor getMatchName(String chapter){
		Cursor c = mDB.rawQuery( "select quizNumber, count(quizNumber) as count from bookmark where chapter=" + "'" + chapter + "'" + " group by quizNumber" , null);
		return c;
	}
	
	// 챕터, 데이터코드 검색 하기 Math 즐겨찾기 (rawQuery)
	public Cursor getMatchNameMath(String chapter, String quizNumber){
		Cursor c = mDB.rawQuery( "select * from bookmark where chapter=" + "'" + chapter + "'" + " and quizNumber=" + "'" + quizNumber + "'" + " order by count asc limit 1 ", null);
		return c;
	}

}
