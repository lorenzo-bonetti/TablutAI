package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

final class MinimaxStrategy {

    //static boolean firstMove = true;

    private MinimaxStrategy() {}


    public static int final_eval(State state, boolean isWhiteTurn) {
        int utility = 0;
        String boardString = state.boardString();
        char aboveKing = boardString.charAt(boardString.indexOf("K") - 10);
        char underKing = boardString.charAt(boardString.indexOf("K") + 10);
        String underKingRow1 = boardString.substring(50,59);
        String underKingRow2 = boardString.substring(60,69);
        String aboveKingRow1 = boardString.substring(30,39);
        String aboveKingRow2 = boardString.substring(20,29);
        StringBuilder sbLeft1 = new StringBuilder();
        StringBuilder sbLeft2 = new StringBuilder();
        StringBuilder sbRight1 = new StringBuilder();
        StringBuilder sbRight2 = new StringBuilder();

        String emptyRowColumn = "OOOOOOOOO";

        for(int i=0;i<90;i=i+10) {
            sbLeft1.append(boardString.charAt(i + 3));
            sbLeft2.append(boardString.charAt(i + 2));
            sbRight1.append(boardString.charAt(i + 5));
            sbRight2.append(boardString.charAt(i + 6));
        }

        String leftKingColumn1 = sbLeft1.toString();
        String leftKingColumn2 = sbLeft2.toString();
        String rightKingColumn1 = sbRight1.toString();
        String rightKingColumn2 = sbRight2.toString();

        if (isWhiteTurn) {
            if (state.getTurn().equalsTurn("WW")) {
                //System.out.println("White can win");
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

            utility += state.getNumberOf(State.Pawn.WHITE) * 50;
            utility += state.getNumberOf(State.Pawn.KING) * 50;

            utility -= state.getNumberOf(State.Pawn.BLACK) * 50;

            if (boardString.contains("KB") ){
                utility -= 5;
            }
            if (boardString.contains("BK") ){
                utility -= 5;
            }
            if (aboveKing == 'B') {
                utility -= 5;
            }
            if (underKing == 'B') {
                utility -= 5;
            }

            /*if (aboveKingRow1.equals(emptyRowColumn)) {
                utility += 3;
            }*/
            if (aboveKingRow2.contains("K") && !aboveKingRow2.contains("B")) {
                utility += 10;
                if (!aboveKingRow2.contains("W")){
                    utility+= 500;
                }
            }
            /*if (underKingRow1.equals(emptyRowColumn)) {
                utility += 3;
            }*/
            if (underKingRow2.contains("K") && !underKingRow2.contains("B")) {
                utility += 10;
                if (!underKingRow2.contains("W")){
                    utility+= 500;
                }
            }
            /*if (leftKingColumn1.equals(emptyRowColumn)) {
                utility += 3;
            }*/
            if (leftKingColumn2.contains("K") && !leftKingColumn2.contains("B")) {
                utility += 10;
                if (!leftKingColumn2.contains("W")){
                    utility+= 500;
                }
            }
            /*if (rightKingColumn1.equals(emptyRowColumn)) {
                utility += 3;
            }*/
            if (rightKingColumn2.contains("K") && !rightKingColumn2.contains("B")) {
                utility += 10;
                if (!rightKingColumn2.contains("W")){
                    utility+= 500;
                }
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
                utility -= 5;
            }
            if (boardString.contains("BK") ){
                utility -= 5;
            }
            if (aboveKing == 'B') {
                utility -= 5;
            }
            if (underKing == 'B') {
                utility -= 5;
            }
            if (aboveKingRow1.equals(emptyRowColumn)) {
                utility += 3;
            }
            if (aboveKingRow2.equals(emptyRowColumn)) {
                utility += 3;
            }
            if (underKingRow1.equals(emptyRowColumn)) {
                utility += 3;
            }
            if (underKingRow2.equals(emptyRowColumn)) {
                utility += 3;
            }
            if (leftKingColumn1.equals(emptyRowColumn)) {
                utility += 3;
            }
            if (leftKingColumn2.equals(emptyRowColumn)) {
                utility += 3;
            }
            if (rightKingColumn1.equals(emptyRowColumn)) {
                utility += 3;
            }
            if (rightKingColumn2.equals(emptyRowColumn)) {
                utility += 3;
            }

            utility += state.getNumberOf(State.Pawn.WHITE) * 50;
            utility += state.getNumberOf(State.Pawn.KING) * 50;

            utility -= state.getNumberOf(State.Pawn.BLACK) * 50;
        }
        return utility;

    }

    public static Action chooseAction(State state, Game rules, boolean isWhiteTurn, int maxDepth) {
        /*if (firstMove) {
            Pair<Action, Integer> bestAction = minimax(state, 3, isWhiteTurn, rules, isWhiteTurn, -999999, 999999);
            firstMove = false;
            return bestAction.getKey();
        }*/
        //ArrayList<Action> possibleActions = getPossibleActions(state, rules, pawns, empty, turn);
        CustomPair winAction = minimax(state, 1, isWhiteTurn, rules, isWhiteTurn, -999999, 999999);
        try {
            if (rules.checkMove(state.clone(), winAction.getKey()).getTurn().equalsTurn("WW") ||
                    rules.checkMove(state.clone(), winAction.getKey()).getTurn().equalsTurn("BW")) {
                System.out.println("can win in this move");
                return winAction.getKey();
            }
        } catch (Exception e) {

        }

        CustomPair bestAction = minimax(state, maxDepth, isWhiteTurn, rules, isWhiteTurn, -999999, 999999);
        System.out.println("best action eval: " + bestAction.getValue());

        return bestAction.getKey();
    }

    private static CustomPair minimax(State state, int currentDepth, boolean isMax, Game rules, boolean isWhiteTurn, int alpha, int beta) {

        if (currentDepth == 0 || state.getTurn().equalsTurn("WW") ||
                state.getTurn().equalsTurn("BW")) {
            CustomPair result = new CustomPair(null, final_eval(state, !isWhiteTurn));
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
                //System.out.println(action.toString());
                CustomPair child = minimax(nextState, currentDepth-1, !isMax, rules, !isWhiteTurn,
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
        return new CustomPair(a, bestValue);

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