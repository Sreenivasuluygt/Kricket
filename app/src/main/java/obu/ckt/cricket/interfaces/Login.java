package obu.ckt.cricket.interfaces;

import obu.ckt.cricket.model.User;

/**
 * Created by Administrator on 10/14/2017.
 */

public interface Login {
    void success(User user);

    void failure();
}
