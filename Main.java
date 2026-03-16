import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import projecte_ia.src.*;

public class Main {

    public static void main(String[] args) throws Exception {
        // Inicialitzar l'estat (5 centres, 100 grups, 1 helicòpter per centre, llavor 1234)
        // Aquests són els valors del primer experiment que demana l'enunciat de la pràctica
        ProbBoard board = new ProbBoard(5, 100, 1, 1234);

        // COMPROVACIÓ RÀPIDA:
        board.imprimirEstat();
        System.exit(0); // Aturem el programa pk només es per provar de moment

        // Crear el problema d'AIMA
        Problem p = new Problem(
                board,
                new ProbSuccessorFunction(), // Generador de moviments (successors)
                new ProbGoalTest(),          // Test de fita (sempre false en cerca local)
                new ProbHeuristicFunction()  // Càlcul del temps (funció heurística)
        );

        // Instanciar l'algorisme de cerca local (Hill Climbing per començar)
        Search alg = new HillClimbingSearch();

        // Executar l'agent
        System.out.println("Iniciant Cerca Local (Hill Climbing)...");
        SearchAgent agent = new SearchAgent(p, alg);

        // Mostrar resultats
        System.out.println("Accions realitzades: " + agent.getActions().size());
        // printInstrumentation(agent.getInstrumentation());
    }
}
