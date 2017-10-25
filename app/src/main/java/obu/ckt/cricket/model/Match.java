package obu.ckt.cricket.model;

/**
 * Created by Administrator on 10/14/2017.
 */

public class Match {
    public String matchId, userId, teamA, teamB, json, result;

    public Match(String matchId, String userId, String teamA, String teamB, String json, String result) {
        this.matchId = matchId;
        this.userId = userId;
        this.teamA = teamA;
        this.teamB = teamB;
        this.json = json;
        this.result = result;
    }
}
