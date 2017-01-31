package com.touchnedu.gradea.studya.math.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
* sql을 사용하기 위한 제반 클래스
* SQLiteOpenHelper는 사용에 도움을 주는 클래스이다.
*/
public class SqliteDbcrud {
 
	private static final String[] columns = null;
	
	SQLiteDatabase db;
	SqliteDbOpenHelper helper ;
	
	// insert
    public void insert(String datacode, int number) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
 
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        // 데이터의 삽입은 put을 이용한다.
        values.put("datacode", datacode);
        values.put("number", number);
        db.insert("bookmark", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
    }
 
    // update
    public void update (String datacode, String number) {
        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능
        
        ContentValues values = new ContentValues();
        values.put("number", number);    //number 값을 수정
        db.update("bookmark", values, "datacode=?", new String[]{datacode});
        /*
         * new String[] {datacode} 이런 간략화 형태가 자바에서 가능하다
         * 당연하지만, 별도로 String[] asdf = {datacode} 후 사용하는 것도 동일한 결과가 나온다.
         */
        
        /*
         * public int update (String table,
         * ContentValues values, String whereClause, String[] whereArgs)
         */
    }
    
    // delete
    public void delete (String datacode, String number) {
        db = helper.getWritableDatabase();
        db.delete("bookmark", "datacode=? and number=?", new String[]{datacode, number});
        Log.i("db", " datacode == " + datacode +  "  number == " + number  + "정상적으로 삭제 되었습니다.");
    }
    
    // select
    public void select(String datacodeStr) {
 
        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용
 
        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.query("bookmark", columns, "datacode=\'"+datacodeStr+"\'",  null, null, null, null);
 
        /*
         * 위 결과는 select * from bookmark 가 된다. Cursor는 DB결과를 저장한다. public Cursor
         * query (String table, String[] columns, String selection, String[]
         * selectionArgs, String groupBy, String having, String orderBy)
         */
 
        while (c.moveToNext()) {
            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
            int _id = c.getInt(c.getColumnIndex("_id"));
            String datacode = c.getString(c.getColumnIndex("datacode"));
            int number = c.getInt(c.getColumnIndex("number"));
            Log.i("db bookmark ", "id: " + _id + ", datacode : " + datacode + ", number : " + number);
        }
    }
}  