package jmetal5.algorithm.singleobjective.coralreefsoptimization;

import jmetal5.operator.CrossoverOperator;
import jmetal5.operator.MutationOperator;
import jmetal5.operator.SelectionOperator;
import jmetal5.problem.Problem;
import jmetal5.solution.Solution;
import jmetal5.util.AlgorithmBuilder;
import jmetal5.util.JMetalException;

import java.util.Comparator;
import java.util.List;

/**
 * @author Inacio Medeiros <inaciogmedeiros@gmail.com>
 */
public class CoralReefsOptimizationBuilder<S extends Solution<?>> implements
        AlgorithmBuilder<CoralReefsOptimization<S>> {

    /**
     * CoralReefsOptimizationBuilder class
     */
    private Problem<S> problem;

    private SelectionOperator<List<S>, S> selectionOperator;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private Comparator<S> comparator;

    private int maxEvaluations;
    private int N, M; // Grid sizes
    private double rho; // Percentage of occupied reef
    private double Fbs, Fbr; // Percentage of broadcast spawners and brooders
    private double Fa, Fd; // Percentage of budders and depredated corals
    private double Pd; // Probability of depredation
    private int attemptsToSettle;

    /**
     * CoralReefsOptimizationBuilder constructor
     */
    public CoralReefsOptimizationBuilder(Problem<S> problem,
                                         SelectionOperator<List<S>, S> selectionOperator,
                                         CrossoverOperator<S> crossoverOperator,
                                         MutationOperator<S> mutationOperator) {
        this.problem = problem;
        this.selectionOperator = selectionOperator;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
    }

    @Override
    public CoralReefsOptimization<S> build() {
        CoralReefsOptimization<S> algorithm = null;

        algorithm = new CoralReefsOptimization<S>(problem, maxEvaluations,
                comparator, selectionOperator, crossoverOperator,
                mutationOperator, N, M, rho, Fbs, Fa, Pd, attemptsToSettle);

        return algorithm;
    }

    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public CoralReefsOptimizationBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: "
                    + maxEvaluations);
        }
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public Comparator<S> getComparator() {
        return comparator;
    }

    public CoralReefsOptimizationBuilder<S> setComparator(
            Comparator<S> comparator) {
        if (comparator == null) {
            throw new JMetalException("Comparator is null!");
        }

        this.comparator = comparator;

        return this;
    }

    public int getN() {
        return N;
    }

    public CoralReefsOptimizationBuilder<S> setN(int n) {
        if (n < 0) {
            throw new JMetalException("N is negative: " + n);
        }

        N = n;
        return this;
    }

    public int getM() {
        return M;
    }

    public CoralReefsOptimizationBuilder<S> setM(int m) {
        if (m < 0) {
            throw new JMetalException("M is negative: " + m);
        }

        M = m;
        return this;
    }

    public double getRho() {
        return rho;
    }

    public CoralReefsOptimizationBuilder<S> setRho(double rho) {
        if (rho < 0) {
            throw new JMetalException("Rho is negative: " + rho);
        }

        this.rho = rho;
        return this;
    }

    public double getFbs() {
        return Fbs;
    }

    public CoralReefsOptimizationBuilder<S> setFbs(double fbs) {
        if (fbs < 0) {
            throw new JMetalException("Fbs is negative: " + fbs);
        }

        Fbs = fbs;
        return this;
    }

    public double getFbr() {
        return Fbr;
    }

    public CoralReefsOptimizationBuilder<S> setFbr(double fbr) {
        if (fbr < 0) {
            throw new JMetalException("Fbr is negative: " + fbr);
        }

        Fbr = fbr;
        return this;
    }

    public double getFa() {
        return Fa;
    }

    public CoralReefsOptimizationBuilder<S> setFa(double fa) {
        if (fa < 0) {
            throw new JMetalException("Fa is negative: " + fa);
        }

        Fa = fa;
        return this;
    }

    public double getFd() {
        return Fd;
    }

    public CoralReefsOptimizationBuilder<S> setFd(double fd) {
        if (fd < 0) {
            throw new JMetalException("Fd is negative: " + fd);
        }

        Fd = fd;
        return this;
    }

    public double getPd() {
        return Pd;
    }

    public CoralReefsOptimizationBuilder<S> setPd(double pd) {
        if (pd < 0) {
            throw new JMetalException("Pd is negative: " + pd);
        }

        Pd = pd;
        return this;
    }

    public int getAttemptsToSettle() {
        return attemptsToSettle;
    }

    public CoralReefsOptimizationBuilder<S> setAttemptsToSettle(
            int attemptsToSettle) {
        if (attemptsToSettle < 0) {
            throw new JMetalException("attemptsToSettle is negative: "
                    + attemptsToSettle);
        }

        this.attemptsToSettle = attemptsToSettle;
        return this;
    }

}
