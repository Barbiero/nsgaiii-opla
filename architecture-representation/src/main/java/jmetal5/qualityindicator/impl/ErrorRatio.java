package jmetal5.qualityindicator.impl;

import jmetal5.qualityindicator.QualityIndicator;
import jmetal5.solution.Solution;
import jmetal5.util.JMetalException;
import jmetal5.util.front.Front;
import jmetal5.util.front.imp.ArrayFront;
import jmetal5.util.naming.impl.SimpleDescribedEntity;
import jmetal5.util.point.Point;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * The Error Ratio (ER) quality indicator reports the ratio of solutions in a front of points
 * that are not members of the true Pareto front.
 * <p>
 * NOTE: the indicator merely checks if the solutions in the front are not members of the
 * second front. No assumption is made about the second front is a true Pareto front, i.e,
 * the front could contain solutions that dominate some of those of the supposed Pareto front.
 * It is a responsibility of the caller to ensure that this does not happen.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * TODO: using an epsilon value
 */
@SuppressWarnings("serial")
public class ErrorRatio<Evaluate extends List<? extends Solution<?>>>
        extends SimpleDescribedEntity
        implements QualityIndicator<Evaluate, Double> {
    private Front referenceParetoFront;

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException
     */
    public ErrorRatio(String referenceParetoFrontFile) throws FileNotFoundException {
        super("ER", "Error ratio quality indicator");
        if (referenceParetoFrontFile == null) {
            throw new JMetalException("The pareto front object is null");
        }

        Front front = new ArrayFront(referenceParetoFrontFile);
        referenceParetoFront = front;
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront
     */
    public ErrorRatio(Front referenceParetoFront) {
        super("ER", "Error ratio quality indicator");
        if (referenceParetoFront == null) {
            throw new JMetalException("\"The Pareto front approximation is null");
        }

        this.referenceParetoFront = referenceParetoFront;
    }

    /**
     * Evaluate() method
     *
     * @param solutionList
     * @return
     */
    @Override
    public Double evaluate(Evaluate solutionList) {
        if (solutionList == null) {
            throw new JMetalException("The solution list is null");
        }
        return er(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Returns the value of the error ratio indicator.
     *
     * @param front          Solution front
     * @param referenceFront True Pareto front
     * @return the value of the error ratio indicator
     * @throws JMetalException
     */
    private double er(Front front, Front referenceFront) throws JMetalException {
        int numberOfObjectives = referenceFront.getPointDimensions();
        double sum = 0;

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            Point currentPoint = front.getPoint(i);
            boolean thePointIsInTheParetoFront = false;
            for (int j = 0; j < referenceFront.getNumberOfPoints(); j++) {
                Point currentParetoFrontPoint = referenceFront.getPoint(j);
                boolean found = true;
                for (int k = 0; k < numberOfObjectives; k++) {
                    if (currentPoint.getDimensionValue(k) != currentParetoFrontPoint.getDimensionValue(k)) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    thePointIsInTheParetoFront = true;
                    break;
                }
            }
            if (!thePointIsInTheParetoFront) {
                sum++;
            }
        }

        return sum / front.getNumberOfPoints();
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
