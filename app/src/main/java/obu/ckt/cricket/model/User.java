package obu.ckt.cricket.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Administrator on 10/14/2017.
 */
@IgnoreExtraProperties
public class User {
    public String name, userId, password, email;

    public User(String userId, String name, String email, String password) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
    }
    public User(){

    }

}
