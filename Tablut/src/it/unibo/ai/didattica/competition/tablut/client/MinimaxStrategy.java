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
import java.util.concurrent.ForkJoinPool;

final class MinimaxStrategy {


    private MinimaxStrategy() {

    }


    public static int final_eval(State state, boolean isWhiteTurn) {
        int utility = 0;
        String boardString = state.boardString();
        //System.out.println(boardString);
        char aboveKing = boardString.charAt(boardString.indexOf("K") - 10);
        char underKing = boardString.charAt(boardString.indexOf("K") + 10);

        if (isWhiteTurn) {
            if (state.getTurn().equalsTurn("WW")) {
                System.out.println("White can win");
                utility += 1000;
                return utility;
            } else if (state.getTurn().equalsTurn("BW")) {
                utility -= 1000;
                return utility;
            } else if (state.getTurn().equalsTurn("D")) {
                if (state.getNumberOf(State.Pawn.WHITE) < 5 &&
                    state.getNumberOf(State.Pawn.BLACK) > 10) {
                    utility += 500;
                } else {
                    utility -= 500;
                }
                return utility;
            }

            utility += state.getNumberOf(State.Pawn.WHITE) * 16;
            utility += state.getNumberOf(State.Pawn.KING) * 16;

            utility -= state.getNumberOf(State.Pawn.BLACK) * 9;

            if (boardString.contains("KB") ){
                utility -= 1;
            }
            if (boardString.contains("BK") ){
                utility -= 1;
            }
            if (aboveKing == 'B') {
                utility -= 1;
            }
            if (underKing == 'B') {
                utility -= 1;
            }

            //System.out.println("Evaluation = " + utility + " W = " +
            //        state.getNumberOf(State.Pawn.WHITE) + " B = " +
              //      state.getNumberOf(State.Pawn.BLACK));

        } else {
            if (state.getTurn().equalsTurn("BW")) {
                utility -= 1000;
                return utility;
            } else if (state.getTurn().equalsTurn("WW")) {
                utility += 1000;
                return utility;
            } else if (state.getTurn().equalsTurn("D")) {
                if (state.getNumberOf(State.Pawn.BLACK) < 6 &&
                        state.getNumberOf(State.Pawn.WHITE) > 4) {
                    utility -= 500;
                } else {
                    utility += 500;
                }
                return utility;
            }

            if (boardString.contains("KB") ){
                utility -= 1;
            }
            if (boardString.contains("BK") ){
                utility -= 1;
            }
            if (aboveKing == 'B') {
                utility -= 1;
            }
            if (underKing == 'B') {
                utility -= 1;
            }

            utility += state.getNumberOf(State.Pawn.WHITE) * 16;
            utility += state.getNumberOf(State.Pawn.KING) * 16;

            utility -= state.getNumberOf(State.Pawn.BLACK) * 9;
        }
        return utility;

    }

    private Action parallelMinimax(State state, Game rules, boolean isWhiteTurn, int maxDepth) {
        Pair<Action, Integer> winAction = minimax(state, 1, isWhiteTurn, rules, isWhiteTurn, -999999, 999999);
        if (winAction.getValue() == 1000 && state.getTurn().equalsTurn("W") ||
                winAction.getValue() == -1000 && state.getTurn().equalsTurn("B")) {
            System.out.println("can win in this move");
            return winAction.getKey();
        }
        return ForkJoinPool.commonPool().invoke(new MinimaxThread(state, rules, isWhiteTurn, maxDepth)).getKey();
    }

    public static Action chooseAction(State state, Game rules, boolean isWhiteTurn, int maxDepth) {

        //ArrayList<Action> possibleActions = getPossibleActions(state, rules, pawns, empty, turn);
        Pair<Action, Integer> winAction = minimax(state, 1, isWhiteTurn, rules, isWhiteTurn, -999999, 999999);
        if (winAction.getValue() == 1000 && state.getTurn().equalsTurn("W") ||
            winAction.getValue() == -1000 && state.getTurn().equalsTurn("B")) {
            System.out.println("can win in this move");
            return winAction.getKey();
        }
        Pair<Action, Integer> bestAction = minimax(state, maxDepth, isWhiteTurn, rules, isWhiteTurn, -999999, 999999);
        System.out.println("best action eval: " + bestAction.getValue());

        return bestAction.getKey();
    }

    private static Pair<Action, Integer> minimax(State state, int currentDepth, boolean isMax, Game rules, boolean isWhiteTurn, int alpha, int beta) {

        if (currentDepth == 0 || state.getTurn().equalsTurn("WW") ||
                state.getTurn().equalsTurn("BW")) {
            Pair<Action, Integer> result = new Pair<>(null, final_eval(state, !isWhiteTurn));
            /*if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
                //System.out.println("WHITE played - Leaf: " + result.getValue());
            } else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
                //System.out.println("BLACK played - Leaf: " + result.getValue());
            }*/

            return result;
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
        //System.out.println("Possible actions: " + possibleActions.size());

        for (Action action : possibleActions) {
            try {
                State nextState = rules.checkMove(state.clone(), action);
                System.out.println(action.toString());
                Pair<Action, Integer> child = minimax(nextState, currentDepth-1, !isMax, rules, !isWhiteTurn,
                        alpha, beta);

                if (isMax && bestValue < child.getValue()) {
                    bestValue = child.getValue();
                    a = action;
                    //System.out.println("    " + a.toString() + " Value: " + bestValue + "   MAX");
                    alpha = Math.max(alpha, bestValue);
                    if (beta <= alpha) {
                      //  System.out.println("PRUNING");
                        break;
                    }
                } else if (!isMax && bestValue > child.getValue()) {
                    bestValue = child.getValue();
                    a = action;
                    //System.out.println("    " + a.toString() + " Value: " + bestValue + "   min");
                    beta = Math.min(beta, bestValue);
                    if (beta <= alpha) {
                      //  System.out.println("PRUNING");
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
        int[] selFrom;
        int[] selTo;
        Action a = null;
        while (pawns.size() > 0) {
            if (pawns.size() > 1) {
                selFrom = pawns.remove(new Random().nextInt(pawns.size() - 1));
            } else {
                selFrom = pawns.remove(0);
            }


            String from = state.getBox(selFrom[0], selFrom[1]);

            for (int k = 0; k < empty.size(); k++) {
                selTo = empty.get(k);

                if (selFrom[0] == selTo[0] || selFrom[1] == selTo[1]) {
                    String to = state.getBox(selTo[0], selTo[1]);


                    try {
                        a = new Action(from, to, turn);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    try {
                        //System.out.println("checking move: " + a.toString());
                        rules.checkMove(state.clone(), a);
                        possibleActions.add(a);
                    } catch (Exception e) {

                    }
                }

            }

        }
        return possibleActions;
    }
}
