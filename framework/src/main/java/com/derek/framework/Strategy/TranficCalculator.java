package com.derek.framework.Strategy;

public class TranficCalculator {
    CalculateStrategy mStrategy;

    public void setmStrategy(CalculateStrategy strategy) {
        mStrategy = strategy;
    }

    public int calculatePrice(int km) {
        return mStrategy != null ? mStrategy.calculatePrice(km) : 0;
    }
}
