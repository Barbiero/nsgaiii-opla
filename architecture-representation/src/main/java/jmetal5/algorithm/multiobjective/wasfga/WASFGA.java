package jmetal5.algorithm.multiobjective.wasfga;

import jmetal5.algorithm.multiobjective.mombi.AbstractMOMBI;
import jmetal5.algorithm.multiobjective.mombi.util.ASFWASFGA;
import jmetal5.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import jmetal5.algorithm.multiobjective.mombi.util.Normalizer;
import jmetal5.algorithm.multiobjective.wasfga.util.WASFGARanking;
import jmetal5.algorithm.multiobjective.wasfga.util.WeightVector;
import jmetal5.operator.CrossoverOperator;
import jmetal5.operator.MutationOperator;
import jmetal5.operator.SelectionOperator;
import jmetal5.problem.Problem;
import jmetal5.solution.Solution;
import jmetal5.util.SolutionListUtils;
import jmetal5.util.evaluator.SolutionListEvaluator;
import jmetal5.util.solutionattribute.Ranking;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the preference based algorithm named WASF-GA on jMetal5.0
 *
 * @author Juanjo Durillo
 * <p>
 * This algorithm is described in the paper: A.B. Ruiz, R. Saborido, M.
 * Luque "A Preference-based Evolutionary Algorithm for Multiobjective
 * Optimization: The Weighting Achievement Scalarizing Function Genetic
 * Algorithm". Journal of Global Optimization. May 2015, Volume 62,
 * Issue 1, pp 101-129
 * DOI = {10.1007/s10898-014-0214-y}
 */
public class WASFGA<S extends Solution<?>> extends AbstractMOMBI<S> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    final AbstractUtilityFunctionsSet<S> achievementScalarizingFunction;
    protected int maxEvaluations;
    protected int evaluations;
    protected Normalizer normalizer;
    List<Double> interestPoint = null;

    /**
     * Constructor
     *
     * @param problem Problem to solve
     */
    public WASFGA(Problem<S> problem,
                  int populationSize,
                  int maxIterations,
                  CrossoverOperator<S> crossoverOperator,
                  MutationOperator<S> mutationOperator,
                  SelectionOperator<List<S>, S> selectionOperator,
                  SolutionListEvaluator<S> evaluator,
                  List<Double> referencePoint) {

        super(problem, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        setMaxPopulationSize(populationSize);
        this.interestPoint = referencePoint;
        this.achievementScalarizingFunction = createUtilityFunction();
    }

    public AbstractUtilityFunctionsSet<S> createUtilityFunction() {
        double[][] weights = WeightVector.initUniformWeights2D(0.005, getMaxPopulationSize());
        weights = WeightVector.invertWeights(weights, true);
        ASFWASFGA<S> aux = new ASFWASFGA<>(weights, interestPoint);

        return aux;
    }

    public void updatePointOfInterest(List<Double> newPointOfInterest) {
        ((ASFWASFGA) this.achievementScalarizingFunction).updatePointOfInterest(newPointOfInterest);
    }


    public int getPopulationSize() {
        return getMaxPopulationSize();
    }

    @Override
    public void specificMOEAComputations() {
        updateNadirPoint(this.getPopulation());
        updateReferencePoint(this.getPopulation());
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);
        Ranking<S> ranking = computeRanking(jointPopulation);
        return selectBest(ranking);
    }

    protected Ranking<S> computeRanking(List<S> solutionList) {
        Ranking<S> ranking = new WASFGARanking<>(this.achievementScalarizingFunction);
        ranking.computeRanking(solutionList);
        return ranking;
    }

    protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int index, List<S> population) {
        population.addAll(ranking.getSubfront(index));
    }

    protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int index, List<S> population) {
        List<S> front = ranking.getSubfront(index);
        int remain = this.getPopulationSize() - population.size();
        population.addAll(front.subList(0, remain));
    }

    protected List<S> selectBest(Ranking<S> ranking) {
        List<S> population = new ArrayList<>(this.getPopulationSize());
        int rankingIndex = 0;

        while (populationIsNotFull(population)) {
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                rankingIndex++;
            } else {
                addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
            }
        }
        return population;
    }

    private boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int index, List<S> population) {
        return (population.size() + ranking.getSubfront(index).size() < this.getPopulationSize());
    }

    protected AbstractUtilityFunctionsSet<S> getUtilityFunctions() {
        return this.achievementScalarizingFunction;
    }

    @Override
    public List<S> getResult() {
        return getNonDominatedSolutions(getPopulation());
    }

    protected List<S> getNonDominatedSolutions(List<S> solutionList) {
        return SolutionListUtils.getNondominatedSolutions(solutionList);
    }

    @Override
    public String getName() {
        return "WASFGA";
    }

    @Override
    public String getDescription() {
        return "Weighting Achievement Scalarizing Function Genetic Algorithm";
    }
}
