package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static it.unibo.ai.didattica.competition.tablut.client.MinimaxStrategy.final_eval;
import static it.unibo.ai.didattica.competition.tablut.client.MinimaxStrategy.getPossibleActions;

public class MinimaxThread extends RecursiveTask<Pair<Action, Integer>> {

    private State state;
    private Game rules;
    private boolean isWhiteTurn;
    private int currentDepth;
    private boolean isMax;
    private int alpha;
    private int beta;

    MinimaxThread(State state, Game rules, boolean isWhiteTurn, int currentDepth, int alpha, int beta) {
        this.state = state;
        this.rules = rules;
        this.isWhiteTurn = isWhiteTurn;
        this.currentDepth = currentDepth;
        this. isMax = isWhiteTurn;
        this.alpha = alpha;
        this.beta = beta;
        //System.out.println("Creating new thread, depth: " + currentDepth);
    }

    public Pair<Action, Integer> compute() {
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

        ArrayList<MinimaxThread> threadArray = new ArrayList<>();

        for (Action action : possibleActions) {
            try {
                State nextState = rules.checkMove(state.clone(), action);
                //System.out.println(action.toString());
                Pair<Action, Integer> child;
                if (currentDepth < 2) {
                    child = MinimaxStrategy.minimax(nextState, currentDepth-1, !isMax, rules, !isWhiteTurn,
                            alpha, beta);
                } else {
                    MinimaxThread childThread = new MinimaxThread(nextState, rules,!isWhiteTurn, currentDepth-1, alpha, beta);
                    threadArray.add(childThread);
                    childThread.fork();
                    child = childThread.join();

                }


                //System.out.println("Joining thread, child value: " + child.getValue());

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


}
