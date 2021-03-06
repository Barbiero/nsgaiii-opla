package jmetal5.util.pseudorandom.impl;

import jmetal5.util.pseudorandom.PseudoRandomGenerator;
import org.apache.commons.math3.random.Well44497b;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class Well44497bGenerator implements PseudoRandomGenerator {
    private static final String name = "Well44497b";
    private Well44497b rnd;
    private long seed;

    /**
     * Constructor
     */
    public Well44497bGenerator() {
        this(System.currentTimeMillis());
    }

    /**
     * Constructor
     */
    public Well44497bGenerator(long seed) {
        this.seed = seed;
        rnd = new Well44497b(seed);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
        rnd.setSeed(seed);
    }

    @Override
    public int nextInt(int lowerBound, int upperBound) {
        return lowerBound + rnd.nextInt((upperBound - lowerBound) + 1);
    }

    @Override
    public double nextDouble(double lowerBound, double upperBound) {
        return lowerBound + rnd.nextDouble() * (upperBound - lowerBound);
    }

    @Override
    public double nextDouble() {
        return nextDouble(0.0, 1.0);
    }

    @Override
    public String getName() {
        return name;
    }
}
