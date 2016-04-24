package com.example.anandundavia.heatbeattracker;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Anand Undavia on 3/28/2016.
 */
public class Database
{
    public static final String MODES[] = {"Normal", "Running", "Sleeping"};

    private static final String DATABASE_NAME = "MasterDB";

    private static final String USER_TABLE = "User";
    private static final String USER_NAME = "Name";

    private static final String CONTACTS_TABLE = "Contacts";
    private static final String CONTACT_NAME = "ContactName";
    private static final String CONTACT_NUMNBER = "ContactNumber";

    private static final String VALUES_TABLE = "ModeValues";
    private static final String MODE = "Mode";
    private static final String SOFT_LIMIT = "SoftLimit";
    private static final String HARD_LIMIT = "HardLimit";
    private static final String MESSAGE = "Message";

    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" + USER_NAME + " TEXT)";
    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + CONTACTS_TABLE + " (" + CONTACT_NAME + " TEXT," + CONTACT_NUMNBER + " TEXT)";
    private static final String CREATE_VALUES_TABLE = "CREATE TABLE IF NOT EXISTS " + VALUES_TABLE + " (" + MODE + " TEXT, " + SOFT_LIMIT + " TEXT, " + HARD_LIMIT + " TEXT ," + MESSAGE + " TEXT)";

    public static Database LOCALDB = null;

    Context localContext;
    SQLiteDatabase db = null;

    public Database(Context ct)
    {
        localContext = ct;
        db = localContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_VALUES_TABLE);
        setInitialLimits();
    }

    void insertName(String name)
    {

        String q = "INSERT INTO " + USER_TABLE + " VALUES ('" + name + "')";
        db.execSQL(q);
    }

    void insertContact(String name, String number)
    {
        String q = "INSERT INTO " + CONTACTS_TABLE + " VALUES ('" + name + "','" + number + "')";
        db.execSQL(q);
    }

    String[] getAllNumbers()
    {
        String returnThis[] = new String[3];
        String q = "SELECT " + CONTACT_NUMNBER + " FROM " + CONTACTS_TABLE;
        Cursor c = db.rawQuery(q, null);

        c.moveToFirst();
        for (int i = 0; i < 3 && c.moveToNext(); i++)
        {
            returnThis[i] = c.getString(0);
        }
        c.close();
        return returnThis;
    }

    boolean isUserRegistered()
    {
        String q = "SELECT " + USER_NAME + " FROM " + USER_TABLE;
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        boolean r = c.getCount() > 0;
        c.close();
        return r;
    }

    int getSoftLimitFor(String mode)
    {
        String q = "SELECT " + SOFT_LIMIT + " FROM " + VALUES_TABLE + " WHERE " + MODE + " = '" + mode + "'";
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        int r = Integer.parseInt(c.getString(0));
        c.close();
        return r;
    }

    int getHardLimitFor(String mode)
    {
        String q = "SELECT " + HARD_LIMIT + " FROM " + VALUES_TABLE + " WHERE " + MODE + " = '" + mode + "'";
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        int r = Integer.parseInt(c.getString(0));
        c.close();
        return r;
    }

    String getMessageFor(String mode)
    {
        String q = "SELECT " + MESSAGE + " FROM " + VALUES_TABLE + " WHERE " + MODE + " = '" + mode + "'";
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        String r = c.getString(0);
        c.close();
        return r;
    }

    void setInitialLimits()
    {
        String q = "INSERT INTO " + VALUES_TABLE + " VALUES ('" + MODES[0] + "','50','70','This is the message')";
        db.execSQL(q);
        q = "INSERT INTO " + VALUES_TABLE + " VALUES ('" + MODES[1] + "','60','90','This is the message')";
        db.execSQL(q);
        q = "INSERT INTO " + VALUES_TABLE + " VALUES ('" + MODES[2] + "','45','60','This is the message')";
        db.execSQL(q);
    }

    String getName()
    {
        String q = "SELECT " + USER_NAME + " FROM " + USER_TABLE;
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        String r = c.getString(0);
        c.close();
        return r;
    }

    void setSoftLimitFor(String mode, String lim)
    {
        String q = "UPDATE " + VALUES_TABLE + " SET " + SOFT_LIMIT + " = '" + lim + "' WHERE " + MODE + " = '" + mode + "'";
        db.execSQL(q);
    }

    void setHardLimitFor(String mode, String lim)
    {
        String q = "UPDATE " + VALUES_TABLE + " SET " + HARD_LIMIT + " = '" + lim + "' WHERE " + MODE + " = '" + mode + "'";
        db.execSQL(q);
    }
    void setMessageFor(String mode, String message){
        String q = "UPDATE " + VALUES_TABLE + " SET " + MESSAGE + " = '" + message + "' WHERE " + MODE + " = '" + mode + "'";
        db.execSQL(q);

    }
}