package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BasicWhiteStrategy implements Strategy{

    private State state;
    private int[] king;
    private List<int[]> pawns;
    private List<int[]> empty;
    private Game rules;

    BasicWhiteStrategy(State state, int[] king, List<int[]> pawns, List<int[]> empty, Game rules) {
        this.state = state;
        this.king = king;
        this.pawns = pawns;
        this.empty = empty;
        this.rules = rules;
    }

    @Override
    public Action getAction() {
        Action a = null;
        try {
            a = new Action("z0", "z0", State.Turn.WHITE);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return a;
    }

    boolean kingCanWin() {
        String from = state.getBox(king[0], king[1]);
        List<int[]> wins = new ArrayList<>();
        wins.add(new int[]{king[0], 0});
        wins.add(new int[]{king[0], 8});
        wins.add(new int[]{0, king[1]});
        wins.add(new int[]{8, king[1]});
        for (int[] win : wins) {
            String to = state.getBox(win[0], win[1]);
            Action test = null;
            try {
                test = new Action(from, to, StateTablut.Turn.WHITE);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                rules.checkMove(state, test);
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }
}
