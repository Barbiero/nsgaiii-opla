package jmetal5.operator.impl.selection;

import jmetal5.operator.SelectionOperator;
import jmetal5.solution.Solution;
import jmetal5.util.JMetalException;
import jmetal5.util.comparator.CrowdingDistanceComparator;
import jmetal5.util.solutionattribute.Ranking;
import jmetal5.util.solutionattribute.impl.CrowdingDistance;
import jmetal5.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements a selection for selecting a number of solutions from
 * a solution list. The solutions are taken by mean of its ranking and
 * crowding distance values.
 *
 * @author Antonio J. Nebro, Juan J. Durillo
 */
@SuppressWarnings("serial")
public class RankingAndCrowdingSelection<S extends Solution<?>>
        implements SelectionOperator<List<S>, List<S>> {
    private final int solutionsToSelect;

    /**
     * Constructor
     */
    public RankingAndCrowdingSelection(int solutionsToSelect) {
        this.solutionsToSelect = solutionsToSelect;
    }

    /* Getter */
    public int getNumberOfSolutionsToSelect() {
        return solutionsToSelect;
    }

    /**
     * Execute() method
     */
    public List<S> execute(List<S> solutionList) throws JMetalException {
        if (null == solutionList) {
            throw new JMetalException("The solution list is null");
        } else if (solutionList.isEmpty()) {
            throw new JMetalException("The solution list is empty");
        } else if (solutionList.size() < solutionsToSelect) {
            throw new JMetalException("The population size (" + solutionList.size() + ") is smaller than" +
                    "the solutions to selected (" + solutionsToSelect + ")");
        }

        Ranking<S> ranking = new DominanceRanking<S>();
        ranking.computeRanking(solutionList);

        return crowdingDistanceSelection(ranking);
    }

    protected List<S> crowdingDistanceSelection(Ranking<S> ranking) {
        CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
        List<S> population = new ArrayList<>(solutionsToSelect);
        int rankingIndex = 0;
        while (population.size() < solutionsToSelect) {
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

    protected boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
        return ranking.getSubfront(rank).size() < (solutionsToSelect - population.size());
    }

    protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> front;

        front = ranking.getSubfront(rank);

        for (int i = 0; i < front.size(); i++) {
            population.add(front.get(i));
        }
    }

    protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> currentRankedFront = ranking.getSubfront(rank);

        Collections.sort(currentRankedFront, new CrowdingDistanceComparator<S>());

        int i = 0;
        while (population.size() < solutionsToSelect) {
            population.add(currentRankedFront.get(i));
            i++;
        }
    }
}
