package jmetal5.algorithm.multiobjective.gde3;

import jmetal5.algorithm.impl.AbstractDifferentialEvolution;
import jmetal5.operator.impl.crossover.DifferentialEvolutionCrossover;
import jmetal5.operator.impl.selection.DifferentialEvolutionSelection;
import jmetal5.problem.DoubleProblem;
import jmetal5.solution.DoubleSolution;
import jmetal5.util.SolutionListUtils;
import jmetal5.util.comparator.CrowdingDistanceComparator;
import jmetal5.util.comparator.DominanceComparator;
import jmetal5.util.evaluator.SolutionListEvaluator;
import jmetal5.util.solutionattribute.DensityEstimator;
import jmetal5.util.solutionattribute.Ranking;
import jmetal5.util.solutionattribute.impl.CrowdingDistance;
import jmetal5.util.solutionattribute.impl.DominanceRanking;

import java.util.*;

/**
 * This class implements the GDE3 algorithm
 */
@SuppressWarnings("serial")
public class GDE3 extends AbstractDifferentialEvolution<List<DoubleSolution>> {
    protected int maxEvaluations;
    protected int evaluations;
    protected Comparator<DoubleSolution> dominanceComparator;
    protected Ranking<DoubleSolution> ranking;
    protected DensityEstimator<DoubleSolution> crowdingDistance;
    protected SolutionListEvaluator<DoubleSolution> evaluator;
    private int maxPopulationSize;

    /**
     * Constructor
     */
    public GDE3(DoubleProblem problem, int populationSize, int maxEvaluations,
                DifferentialEvolutionSelection selection, DifferentialEvolutionCrossover crossover,
                SolutionListEvaluator<DoubleSolution> evaluator) {
        setProblem(problem);
        setMaxPopulationSize(populationSize);
        this.maxEvaluations = maxEvaluations;
        this.crossoverOperator = crossover;
        this.selectionOperator = selection;

        dominanceComparator = new DominanceComparator<DoubleSolution>();
        ranking = new DominanceRanking<DoubleSolution>();
        crowdingDistance = new CrowdingDistance<DoubleSolution>();

        this.evaluator = evaluator;
    }

    public int getMaxPopulationSize() {
        return maxPopulationSize;
    }

    public void setMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
    }

    @Override
    protected void initProgress() {
        evaluations = getMaxPopulationSize();
    }

    @Override
    protected void updateProgress() {
        evaluations += getMaxPopulationSize();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList<>(getMaxPopulationSize());
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            DoubleSolution newIndividual = getProblem().createSolution();
            population.add(newIndividual);
        }
        return population;
    }

    /**
     * Evaluate population method
     *
     * @param population The list of solutions to be evaluated
     * @return A list of evaluated solutions
     */
    @Override
    protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
        return evaluator.evaluate(population, getProblem());
    }

    @Override
    protected List<DoubleSolution> selection(List<DoubleSolution> population) {
        List<DoubleSolution> matingPopulation = new LinkedList<>();
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            // Obtain parents. Two parameters are required: the population and the
            //                 index of the current individual
            selectionOperator.setIndex(i);
            List<DoubleSolution> parents = selectionOperator.execute(population);

            matingPopulation.addAll(parents);
        }

        return matingPopulation;
    }

    @Override
    protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
        List<DoubleSolution> offspringPopulation = new ArrayList<>();

        for (int i = 0; i < getMaxPopulationSize(); i++) {
            crossoverOperator.setCurrentSolution(getPopulation().get(i));
            List<DoubleSolution> parents = new ArrayList<>(3);
            for (int j = 0; j < 3; j++) {
                parents.add(matingPopulation.get(0));
                matingPopulation.remove(0);
            }

            crossoverOperator.setCurrentSolution(getPopulation().get(i));
            List<DoubleSolution> children = crossoverOperator.execute(parents);

            offspringPopulation.add(children.get(0));
        }

        return offspringPopulation;
    }

    @Override
    protected List<DoubleSolution> replacement(List<DoubleSolution> population,
                                               List<DoubleSolution> offspringPopulation) {
        List<DoubleSolution> tmpList = new ArrayList<>();
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            // Dominance test
            DoubleSolution child = offspringPopulation.get(i);
            int result;
            result = dominanceComparator.compare(population.get(i), child);
            if (result == -1) {
                // Solution i dominates child
                tmpList.add(population.get(i));
            } else if (result == 1) {
                // child dominates
                tmpList.add(child);
            } else {
                // the two solutions are non-dominated
                tmpList.add(child);
                tmpList.add(population.get(i));
            }
        }
        Ranking<DoubleSolution> ranking = computeRanking(tmpList);

        return crowdingDistanceSelection(ranking);
    }

    @Override
    public List<DoubleSolution> getResult() {
        return getNonDominatedSolutions(getPopulation());
    }


    protected Ranking<DoubleSolution> computeRanking(List<DoubleSolution> solutionList) {
        Ranking<DoubleSolution> ranking = new DominanceRanking<DoubleSolution>();
        ranking.computeRanking(solutionList);

        return ranking;
    }

    protected List<DoubleSolution> crowdingDistanceSelection(Ranking<DoubleSolution> ranking) {
        CrowdingDistance<DoubleSolution> crowdingDistance = new CrowdingDistance<DoubleSolution>();
        List<DoubleSolution> population = new ArrayList<>(getMaxPopulationSize());
        int rankingIndex = 0;
        while (populationIsNotFull(population)) {
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                rankingIndex++;
            } else {
                crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
                addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
            }
        }

        return population;
    }

    protected boolean populationIsNotFull(List<DoubleSolution> population) {
        return population.size() < getMaxPopulationSize();
    }

    protected boolean subfrontFillsIntoThePopulation(Ranking<DoubleSolution> ranking, int rank,
                                                     List<DoubleSolution> population) {
        return ranking.getSubfront(rank).size() < (getMaxPopulationSize() - population.size());
    }

    protected void addRankedSolutionsToPopulation(Ranking<DoubleSolution> ranking, int rank,
                                                  List<DoubleSolution> population) {
        List<DoubleSolution> front;

        front = ranking.getSubfront(rank);

        for (DoubleSolution solution : front) {
            population.add(solution);
        }
    }

    protected void addLastRankedSolutionsToPopulation(Ranking<DoubleSolution> ranking, int rank,
                                                      List<DoubleSolution> population) {
        List<DoubleSolution> currentRankedFront = ranking.getSubfront(rank);

        Collections.sort(currentRankedFront, new CrowdingDistanceComparator<DoubleSolution>());

        int i = 0;
        while (population.size() < getMaxPopulationSize()) {
            population.add(currentRankedFront.get(i));
            i++;
        }
    }

    protected List<DoubleSolution> getNonDominatedSolutions(List<DoubleSolution> solutionList) {
        return SolutionListUtils.getNondominatedSolutions(solutionList);
    }

    @Override
    public String getName() {
        return "GDE3";
    }

    @Override
    public String getDescription() {
        return "Generalized Differential Evolution version 3";
    }
} 
