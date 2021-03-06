package jmetal5.util.neighborhood.impl;

import jmetal5.util.neighborhood.util.TwoDimensionalMesh;

/**
 * Class defining a L5 neighborhood of a solution belonging to a list of solutions which is
 * structured as a bi-dimensional square mesh. The neighbors are those solutions that are in the positions
 * North, South, East and West
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class L5<S> extends TwoDimensionalMesh<S> {
    private static final int[] north = {-1, 0};
    private static final int[] south = {1, 0};
    private static final int[] east = {0, 1};
    private static final int[] west = {0, -1};

    private static final int[][] neighborhood = {north, south, west, east};

    /**
     * Constructor.
     * Defines a neighborhood for solutionSetSize (it has to have an exact squared root)
     */
    public L5(int rows, int columns) {
        super(rows, columns, neighborhood);
    }
}

