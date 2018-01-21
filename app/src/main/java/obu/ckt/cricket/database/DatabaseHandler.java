package obu.ckt.cricket.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import obu.ckt.cricket.comon.DBConstants;
import obu.ckt.cricket.interfaces.CreateMatch;
import obu.ckt.cricket.interfaces.Login;
import obu.ckt.cricket.interfaces.MatchHistory;
import obu.ckt.cricket.model.Match;
import obu.ckt.cricket.model.User;

/**
 * Created by Administrator on 10/14/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cricket";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //create user
        db.execSQL("CREATE TABLE user(userId INTEGER PRIMARY KEY,name TEXT,email TEXT,password TEXT" + ")");
        db.execSQL("CREATE TABLE matches(matchId INTEGER PRIMARY KEY,userId TEXT,teamA TEXT,teamB TEXT,json TEXT,result TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        onCreate(db);
    }

    public long insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);

        // Inserting Row
        long id = db.insert("user", null, values);
        db.close(); // Clo;
        return id;
    }

    public void insertUserWithUDID(User user) {
        FirebaseDatabase.getInstance().getReference().child(DBConstants.USER).child(user.userId).setValue(user);
    }

    public void insertMatch(Match match, String id, CreateMatch create) {
        /*SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userId", match.userId);
        values.put("teamA", match.teamA);
        values.put("teamB", match.teamB);
        values.put("json", match.json);
        values.put("result", match.result);

        // Inserting Row
        if (id == -1)
            id = db.insert("matches", null, values);
        else db.update("matches",
                values, "matchId=?", new String[]{Integer.toString((int) id)});
        db.close(); // Clo;
        if (id > 0) {
            create.success((int) id);
        }*/
        if (match.matchId == null) {
            DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference(DBConstants.MATCHES).child(match.userId);
            match.matchId = matchRef.push().getKey();
            matchRef.child(match.matchId).setValue(match);
        } else {
            DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference(DBConstants.MATCHES).child(match.userId);
            matchRef.child(match.matchId).setValue(match);
        }
        create.success(match.matchId);
    }

    public boolean isEmailExists(String email) {
        boolean isEmailExists = false;
        // Select All Query
        String selectQuery = "SELECT  * FROM user";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(email)) {
                    isEmailExists = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        return isEmailExists;
    }

    public String getUserId(String email) {
        String userId = "";
        // Select All Query
        String selectQuery = "SELECT  * FROM user";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(email)) {
                    userId = cursor.getString(0);
                    break;
                }
            } while (cursor.moveToNext());
        }

        return userId;
    }

    public void validateLogin(String email, String password, Login login) {
        User user = null;
        String selectQuery = "SELECT  * FROM user";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(email) && cursor.getString(3).equals(password)) {
                    user = new User(String.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                    break;
                }
            } while (cursor.moveToNext());
        }
        if (user != null) {
            login.success(user);
            try {
                final DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference(DBConstants.MATCHES).child(user.userId);
                matchRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else login.failure();

    }

    public void getMatches(final String userId, final MatchHistory matchHistory) {
        final List<Match> matchList = new ArrayList<>();
        final DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference(DBConstants.MATCHES).child(userId);

        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                matchList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Match track = postSnapshot.getValue(Match.class);
                    matchList.add(track);
                }
                if (matchList.size() > 0)
                    matchHistory.success(matchList);
                else matchHistory.failure();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                matchHistory.failure();
            }
        });
    }


    public void getMatchInfo(String userId, String matchId, final matchDetails matchInterface) {


        final DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference(DBConstants.MATCHES).child(userId).child(matchId);


        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Match match = dataSnapshot.getValue(Match.class);
                matchInterface.onSuccess(match);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                matchInterface.onFailure();
            }
        });
    }

    /*public Match getMatchInfo(String stringExtra) {
        Match match = null;

        String selectQuery = "SELECT  * FROM matches where matchId=" + stringExtra;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                match = new Match(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5));
            } while (cursor.moveToNext());
        }
        return match;
    }*/

    public interface matchDetails {
        void onSuccess(Match match);

        void onFailure();
    }
}
