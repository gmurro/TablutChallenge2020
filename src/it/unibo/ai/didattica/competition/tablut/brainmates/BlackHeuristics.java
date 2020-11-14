package it.unibo.ai.didattica.competition.tablut.brainmates;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.*;

public class BlackHeuristics extends Heuristics {

    //Number of admissible loss of pawns before changing strategy
    private final int THRESHOLD = 2;
    //Number of tiles on rhombus
    private final int NUM_TILES_ON_RHOMBUS = 16;

    private final Map<String,Double> weights;
    private String[] keys;
    private final int[][] rhombus = {{1,3},{1,4},{2,2},{2,5},{3,1},{3,6},{4,0},{4,7},{5,0},{5,7},{6,1},
            {6,6},{7,2},{7,5},{8,3},{8,4}};
    private int numberOfBlack;
    private int numberOfWhite;

    public BlackHeuristics(State state) {

        super(state);
        //Loading weights
        weights = new HashMap<String, Double>();
        weights.put("Black", 0.5);
        weights.put("White",0.5);
        weights.put("NearKing",0.7);
        weights.put("Rhombus", 0.7);
        weights.put("NextWhiteWins",0.7);

        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }

    @Override
    public double evaluateState() {

        double utilityValue = 0.0;

        //Atomic functions to combine to get utility value
        numberOfBlack = state.getNumberOf(State.Pawn.BLACK);
        System.out.println("Black pawns: " + numberOfBlack);
        numberOfWhite = state.getNumberOf(State.Pawn.WHITE);
        System.out.println("Number of white pawns: " + numberOfWhite);
        int pawnsNearKing = checkNearPawns(state, kingPosition(state),State.Turn.BLACK.toString());
        System.out.println("Number of pawns near to the king:" + pawnsNearKing);
        int numberOfPawnsOnRhombus = getNumberOnRhombus();
        System.out.println("Number of rhombus: " + numberOfPawnsOnRhombus);
        int nextMoveWhiteWins = nextMoveWhiteWon();
        System.out.println("Next move wins: " + nextMoveWhiteWins);


        //Weighted sum of functions to get final utility value
        Map<String,Integer> atomicUtilities = new HashMap<String,Integer>();
        atomicUtilities.put("Black",numberOfBlack);
        atomicUtilities.put("White",numberOfWhite);
        atomicUtilities.put("NearKing",pawnsNearKing);
        atomicUtilities.put("Rhombus",numberOfPawnsOnRhombus);
        atomicUtilities.put("NextWhiteWins",nextMoveWhiteWins);

        for (int i = 0; i < weights.size(); i++){
            utilityValue += weights.get(keys[i]) * atomicUtilities.get(keys[i]);
            System.out.println(keys[i] + ": " + weights.get(keys[i]) + "*" + atomicUtilities.get(keys[i]) + "= " + weights.get(keys[i]) * atomicUtilities.get(keys[i]));
        }

        return utilityValue;

    }

    /**
     * get number of black pawns on rhombus tiles if particular conditions are satisfied
     * @return number of black pawns on tiles if premise is true, 0 otherwise
     */
    private int getNumberOnRhombus(){
        if (checkKingPosition(state) && numberOfBlack >= THRESHOLD) {
            return getValuesOnRhombus();
        }else{
            return 0;
        }
    }

    /**
     *
     * @return number of black pawns on rhombus configuration
     */
    private int getValuesOnRhombus(){

        int count = 0;
        for (int[] position : rhombus) {
            if (state.getPawn(position[0], position[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++;
            }
        }
        return count;

    }

    private int nextMoveWhiteWon(){

        boolean hasWon = kingGoesForWin(state);

        if(hasWon){
            return -5;
        }else{
            return 1;
        }
    }
}
