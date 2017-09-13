package idex.mizuno.daichi.com.testphoto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public MySQLiteOpenHelper(Context context) {
        // 任意のデータベースファイル名と、バージョンを指定する
        super(context, "ranking.db", null, 1);
    }

    /**
     * このデータベースを初めて使用する時に実行される処理
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブルの作成、初期データの投入等を行う。
        db.execSQL(
                "create table ranking_table ("
                        + "_id  integer primary key autoincrement not null, "
                        + "user_name text not null, "
                        + "score integer not null)" );
    }

    /**
     * アプリケーションの更新などによって、データベースのバージョンが上がった場合に実行される処理
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データの退避、テーブルの再構成等を行う。
    }
}