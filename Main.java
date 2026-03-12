import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.informed.IterativeDeepeningAStarSearch;
import projecte_ia.src.*;

public class Main {

    public static void main(String[] args) throws Exception {
        // Enunciat problema
        int[] prob;

        // Creacio instancia de Board
        ProbBoard board = new ProbBoard(prob);

        // Creacio instancia de Problem
        Problem p = new Problem(
            board,
            new ProbHeuristicFunction(),
            new ProbGoalTest(),
            new ProbHeuristicFunction()
        );

        // Instancia d'algorisme de cerca
        // Pot ser AStarSearch(new GraphSearch()) or IterativeDeepeningAStarSearch()
        Search alg = new AStarSearch(new GraphSearch());

        // Instanciar Search Agent
        SearchAgent agent = new SearchAgent(p, alg);

        // Resultats de cerca
        // S'han de fer manualment
        // printActions(agent.getActions());
        // printInstrumentation(agent.getInstrumentation());
    }
}
