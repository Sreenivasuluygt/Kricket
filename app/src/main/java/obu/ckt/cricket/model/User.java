package obu.ckt.cricket.model;

/**
 * Created by Administrator on 10/14/2017.
 */

public class User {
    public String name, userId, password, email;

    public User(String userId, String name, String email, String password) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
    }

}
