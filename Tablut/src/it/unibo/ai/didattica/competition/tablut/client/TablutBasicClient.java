package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

/**
 * @author A. Piretti, Andrea Galassi
 */
public class TablutBasicClient extends TablutClient {

    private int game;

    public TablutBasicClient(String player, String name, int gameChosen, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        game = gameChosen;
    }

    public TablutBasicClient(String player) throws UnknownHostException, IOException {
        this(player, "random", 4, 60, "localhost");
    }

    public TablutBasicClient(String player, String name) throws UnknownHostException, IOException {
        this(player, name, 4, 60, "localhost");
    }

    public TablutBasicClient(String player, int gameChosen) throws UnknownHostException, IOException {
        this(player, "random", gameChosen, 60, "localhost");
    }


    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        int gametype = 4;
        String role = "";
        String name = "PickleRicks";
        int timeout = 60;
        String ipAddress = "localhost";
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println(args[0]);
            role = (args[0].toUpperCase());
        }
        if (args.length == 2) {
            timeout = (Integer.parseInt(args[1]));

        }
        if (args.length == 3) {
            timeout = (Integer.parseInt(args[1]));
            ipAddress = (args[2]);
        }
        System.out.println("Selected client: " + args[0]);

        TablutBasicClient client = new TablutBasicClient(role, name, gametype, timeout, ipAddress);
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
                rules = new GameSpeedTablut(99, 0, "fake", "fake");
                System.out.println("Ashton Tablut game");
                break;
            default:
                System.out.println("Error in game selection");
                System.exit(4);
        }


        System.out.println("You are player " + this.getPlayer().toString() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            state = this.getCurrentState();
            //System.out.println(state.toString());
            //System.out.println(state.getPawn(4, 4).toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            if (this.getPlayer().equals(Turn.WHITE)) { // white turn
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {

                    long startTime = System.currentTimeMillis();
                    Action a = MinimaxStrategy.chooseAction(state, rules, true, 4);
                    long estimatedTime = System.currentTimeMillis() - startTime;
                    System.out.println("Mossa scelta: " + a.toString());
                    System.out.println("Time to find move: " + estimatedTime/1000.0 + " seconds");

                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


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
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {

                    long startTime = System.currentTimeMillis();
                    Action a = MinimaxStrategy.chooseAction(state, rules, false, 4);
                    long estimatedTime = System.currentTimeMillis() - startTime;
                    System.out.println("Mossa scelta: " + a.toString());
                    System.out.println("Time to find move: " + estimatedTime/1000.0 + " seconds");


                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

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