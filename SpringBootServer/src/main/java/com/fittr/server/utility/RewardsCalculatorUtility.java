package com.fittr.server.utility;

public  class RewardsCalculatorUtility {

    private RewardsCalculatorUtility()
    {

    }
    public static int computeCoins(String mode, Double distance) {
        double computedCoins = 0;
        switch (mode) {
            case "CYCLING":
                computedCoins = distance * 1;
                break;
            case "RUNNING":
                computedCoins = distance * 2;
                break;
            case "WALKING":
                computedCoins = distance * 3;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
        return (int)computedCoins;
    }

}
