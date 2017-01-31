package com.touchnedu.gradea.studya.math.data;

import android.provider.BaseColumns;

// DataBase Table
public final class DataBases {
	
	public static final class CreateDB implements BaseColumns {
		public static final String QUIZNUMBER = "quiznumber";
		public static final String COUNT = "count";
		public static final String CHAPTER = "chapter";
		public static final String _TABLENAME = "BOOKMARK";
		public static final String _CREATE = "create table " + _TABLENAME + "(" 
																	+ _ID + " integer primary key autoincrement, "
																	+ CHAPTER + " text not null , "
																	+ QUIZNUMBER + " INTEGER not null );"; 
	}
}