package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

final class MinimaxStrategy {


    private MinimaxStrategy() {

    }


    public static int final_eval(State state, boolean isWhiteturn) {
        int utility = 0;
        if (isWhiteturn) {
            if (state.getTurn().equalsTurn("WW")) {
                utility += 1000;
                return utility;
            } else if (state.getTurn().equalsTurn("BW")) {
                utility -= 1000;
                return utility;
            }

            utility += state.getNumberOf(State.Pawn.WHITE) * 16;

            utility -= state.getNumberOf(State.Pawn.BLACK) * 9;

            System.out.println("Evaluation = " + utility + " W = " +
                    state.getNumberOf(State.Pawn.WHITE) + " B = " +
                    state.getNumberOf(State.Pawn.BLACK));

        } else {
            if (state.getTurn().equalsTurn("BW")) {
                utility += 1000;
                return utility;
            } else if (state.getTurn().equalsTurn("WW")) {
                utility -= 1000;
                return utility;
            }

            utility -= state.getNumberOf(State.Pawn.WHITE) * 16;

            utility += state.getNumberOf(State.Pawn.BLACK) * 9;
        }
        return utility;

    }

    public static Action chooseAction(State state, Game rules, boolean isWhiteTurn, int maxDepth) {
        //ArrayList<Action> possibleActions = getPossibleActions(state, rules, pawns, empty, turn);
        Pair<Action, Integer> bestAction = minimax(state, maxDepth, true, rules, isWhiteTurn, -999999, 999999);

        return bestAction.getKey();
    }

    private static Pair<Action, Integer> minimax(State state, int currentDepth, boolean isMax, Game rules, boolean isWhiteTurn, int alpha, int beta) {

        if (currentDepth == 0 || state.getTurn().equalsTurn("WW") ||
                state.getTurn().equalsTurn("BW")) {

            return new Pair<>(null, final_eval(state, !isWhiteTurn));
        }

        List<int[]> pawns = new ArrayList<>();
        List<int[]> empty = new ArrayList<>();
        ArrayList<Action> possibleActions;

        if (isWhiteTurn) {
            int[] buf;
            for (int i = 0; i < state.getBoard().length; i++) {
                for (int j = 0; j < state.getBoard().length; j++) {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        pawns.add(buf);
                    }
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        pawns.add(buf);
                    } else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        empty.add(buf);
                    }
                }
            }
            possibleActions = getPossibleActions(state, rules, pawns, empty, State.Turn.WHITE);
        } else {
            int[] buf;
            for (int i = 0; i < state.getBoard().length; i++) {
                for (int j = 0; j < state.getBoard().length; j++) {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        pawns.add(buf);
                    } else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        empty.add(buf);
                    }
                }
            }
            possibleActions = getPossibleActions(state, rules, pawns, empty, State.Turn.BLACK);
        }


        int bestValue;
        if (isMax) {
            bestValue = -999999;
        } else {
            bestValue = 999999;
        }
        Action a = null;
        try {
            a = new Action("z0", "z0", State.Turn.WHITE);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        for (Action action : possibleActions) {
            try {
                State nextState = rules.checkMove(state.clone(), action);
                Pair<Action, Integer> child = minimax(nextState, currentDepth-1, !isMax, rules, !isWhiteTurn,
                        alpha, beta);

                if (isMax && bestValue < child.getValue()) {
                    bestValue = child.getValue();
                    a = action;
                    alpha = Math.max(alpha, bestValue);
                    if (beta <= alpha) {
                        System.out.println("PRUNING");
                        break;
                    }
                } else if (!isMax && bestValue > child.getValue()) {
                    bestValue = child.getValue();
                    a = action;
                    beta = Math.min(beta, bestValue);
                    if (beta <= alpha) {
                        System.out.println("PRUNING");
                        break;
                    }
                }
            } catch (Exception e) {

            }

        }
        return new Pair<>(a, bestValue);

    }

    private static ArrayList<Action> getPossibleActions(State state, Game rules, List<int[]> pawns,
                                                       List<int[]> empty, StateTablut.Turn turn) {
        ArrayList<Action> possibleActions = new ArrayList<>();
        int[] selected;
        Action a = null;
        while (pawns.size() > 0) {
            if (pawns.size() > 1) {
                selected = pawns.remove(new Random().nextInt(pawns.size() - 1));
            } else {
                selected = pawns.remove(0);
            }


            String from = state.getBox(selected[0], selected[1]);

            for (int k = 0; k < empty.size(); k++) {
                selected = empty.get(k);
                String to = state.getBox(selected[0], selected[1]);

                try {
                    a = new Action(from, to, turn);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    rules.checkMove(state.clone(), a);
                    possibleActions.add(a);
                } catch (Exception e) {

                }

            }

        }
        return possibleActions;
    }
}
