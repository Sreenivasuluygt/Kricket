package obu.ckt.cricket.interfaces;

import java.util.List;

import obu.ckt.cricket.model.Match;

/**
 * Created by Administrator on 10/14/2017.
 */

public interface MatchHistory {
    void success(List<Match> matchList);
    void failure();
}
