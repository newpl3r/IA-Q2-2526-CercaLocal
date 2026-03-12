package projecte_ia.src;

import aima.search.framework.HeuristicFunction;

public class ProbHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n) {
        return ((ProbBoard) n).heuristic();
    }
}
