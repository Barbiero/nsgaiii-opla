package jmetal5.util.solutionattribute;

import java.util.List;

/**
 * Interface representing implementations to compute the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DensityEstimator<S> extends SolutionAttribute<S, Double> {
    void computeDensityEstimator(List<S> solutionSet);
}

