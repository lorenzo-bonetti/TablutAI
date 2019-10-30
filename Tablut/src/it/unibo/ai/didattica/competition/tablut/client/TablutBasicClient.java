package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import static java.util.stream.Collectors.toMap;

/**
 * @author A. Piretti, Andrea Galassi
 */
public class TablutBasicClient extends TablutClient {

    private int game;

    public TablutBasicClient(String player, String name, int gameChosen) throws UnknownHostException, IOException {
        super(player, name);
        game = gameChosen;
    }

    public TablutBasicClient(String player) throws UnknownHostException, IOException {
        this(player, "random", 4);
    }

    public TablutBasicClient(String player, String name) throws UnknownHostException, IOException {
        this(player, name, 4);
    }

    public TablutBasicClient(String player, int gameChosen) throws UnknownHostException, IOException {
        this(player, "random", gameChosen);
    }

    public int evaluation(State stateA, State stateB) {
        int eval = 0;


        //controlla numero pedine avversario
        if (stateA.getNumberOf(State.Pawn.BLACK) > stateB.getNumberOf(State.Pawn.BLACK)) {
            eval += 10;

        }


        return eval;


    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        int gametype = 4;
        String role = "";
        String name = "basic";
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println(args[0]);
            role = (args[0]);
        }
        if (args.length == 2) {
            System.out.println(args[1]);
            gametype = Integer.parseInt(args[1]);
        }
        if (args.length == 3) {
            name = args[2];
        }
        System.out.println("Selected client: " + args[0]);

        TablutBasicClient client = new TablutBasicClient(role, name, gametype);
        client.run();
    }

    @Override
    public void run() {

        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state;

        Game rules = null;
        switch (this.game) {
            case 1:
                state = new StateTablut();
                rules = new GameTablut();
                break;
            case 2:
                state = new StateTablut();
                rules = new GameModernTablut();
                break;
            case 3:
                state = new StateBrandub();
                rules = new GameTablut();
                break;
            case 4:
                state = new StateTablut();
                state.setTurn(State.Turn.WHITE);
                rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
                System.out.println("Ashton Tablut game");
                break;
            default:
                System.out.println("Error in game selection");
                System.exit(4);
        }

        int[] king = new int[2];
        List<int[]> pawns = new ArrayList<>();
        List<int[]> empty = new ArrayList<>();

        System.out.println("You are player " + this.getPlayer().toString() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("Current state:");
            System.out.println("Current state:");
            state = this.getCurrentState();
            System.out.println(state.toString());
            System.out.println(state.getPawn(4, 4).toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            if (this.getPlayer().equals(Turn.WHITE)) {
                // il mio turno
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
                    int[] buf;
                    for (int i = 0; i < state.getBoard().length; i++) {
                        for (int j = 0; j < state.getBoard().length; j++) {
                            if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                                System.out.println("KING");
                                king[0] = i;
                                king[1] = j;
                                buf = new int[2];
                                buf[0] = i;
                                buf[1] = j;
                                pawns.add(buf);
                            }
                            if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {
                                System.out.println("WHITE");
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

                    //BasicWhiteStrategy strategy = new BasicWhiteStrategy(state, king, pawns, empty, rules);

                    boolean found = false;
                    Action a = null;
                    try {
                        a = new Action("z0", "z0", State.Turn.WHITE);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }



                    int i = 0;
                    while (i < empty.size() && !found) {
                        String from = this.getCurrentState().getBox(king[0], king[1]);
                        System.out.println("trying to move king from " + king[0] + ", " + king[1] + " type: " + state.getPawn(king[0], king[1]).toString());
                        String to = this.getCurrentState().getBox(empty.get(i)[0], empty.get(i)[1]);
                        try {
                            a = new Action(from, to, State.Turn.WHITE);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        try {
                            State possibleState = rules.checkMove(state.clone(), a);
                            if (possibleState.getTurn().equalsTurn("WW")) {
                                System.out.println("King can win!");
                                rules.checkMove(state, a);
                                found = true;

                            }
                        } catch (Exception e) {

                        }
                        System.out.println(i);
                        i++;
                    }



                    int[] selected = null;
                    Map<Action, Integer> evalMoves = new HashMap<Action, Integer>();

                    if (!found) {
                        while (pawns.size() > 0) {
                            if (pawns.size() > 1) {
                                selected = pawns.remove(new Random().nextInt(pawns.size() - 1));
                            } else {
                                selected = pawns.remove(0);
                            }


                            String from = this.getCurrentState().getBox(selected[0], selected[1]);

                            for (int k = 0; k < empty.size(); k++) {
                                selected = empty.get(k);
                                String to = this.getCurrentState().getBox(selected[0], selected[1]);

                                try {
                                    a = new Action(from, to, State.Turn.WHITE);
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                int score = 0;
                                try {
                                    State possibleState = rules.checkMove(state.clone(), a);
                                    score = evaluation(state, possibleState);
                                    evalMoves.put(a, score);
                                } catch (Exception e) {

                                }


                            }


                        }

                        Map<Action, Integer> sorted = evalMoves
                                .entrySet()
                                .stream()
                                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                                .collect(
                                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                                LinkedHashMap::new));

                        a = sorted.entrySet().iterator().next().getKey();

                        System.out.println("My move has evaluation = " + sorted.get(a));
                    }


                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    pawns.clear();
                    empty.clear();

                }
                // � il turno dell'avversario
                else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
                    System.out.println("Waiting for your opponent move... ");
                }
                // ho vinto
                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                // ho perso
                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }
                // pareggio
                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            } else {

                // il mio turno
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
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

                    int[] selected = null;

                    boolean found = false;
                    Action a = null;

                    while (!found) {
                        selected = pawns.get(new Random().nextInt(pawns.size() - 1));
                        String from = this.getCurrentState().getBox(selected[0], selected[1]);

                        selected = empty.get(new Random().nextInt(empty.size() - 1));
                        String to = this.getCurrentState().getBox(selected[0], selected[1]);

                        try {
                            a = new Action(from, to, State.Turn.BLACK);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        System.out.println("try: " + a.toString());
                        try {
                            rules.checkMove(state, a);
                            found = true;
                        } catch (Exception e) {

                        }

                    }

                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    pawns.clear();
                    empty.clear();

                } else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            }
        }

    }
}

