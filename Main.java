import java.util.Properties;
import java.util.Iterator;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import projecte_ia.src.*;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1. Inicialitzem l'estat base amb els valors del primer experiment de l'enunciat:
        // 5 centres de comandament, 100 grups de persones a rescatar, 1 helicòpter per centre i la llavor 1234.
        ProbBoard board = new ProbBoard(5, 100, 1, 1234);

        // COMPROVACIÓ: Imprimim per pantalla com hem repartit inicialment la gent als helicòpters.
        board.imprimirEstat();
        //System.exit(0); // Podem descomentar això si només volem veure l'estat inicial i aturar el programa.

        // 2. Creem el problema per a la llibreria d'AIMA. Li passem el nostre tauler,
        // la funció que genera els moviments (ara mateix SA), el test de meta i com calculem el temps.
        Problem p = new Problem(
                board,
                new ProbSuccessorFunctionSA(), // Funció que triarà un moviment a l'atzar
                new ProbGoalTest(),
                new ProbHeuristicFunction()
        );

        // 3. Preparem l'algorisme de Simulated Annealing (Recuita Simulada).
        // Aquests números són els paràmetres que anirem canviant per fer els experiments de la pràctica.
        Search alg = new SimulatedAnnealingSearch(10000, 100, 5, 0.001);

        // 4. Posem l'agent a treballar perquè busqui la millor combinació de rescats.
        System.out.println("Iniciant Cerca Local (Simulated Annealing)...");
        SearchAgent agent = new SearchAgent(p, alg);

        // 5. Un cop ha acabat, mostrem quantes iteracions i passes ha fet per trobar la solució.
        System.out.println("--- ESTADÍSTIQUES DE LA CERCA ---");
        printInstrumentation(agent.getInstrumentation());
    }

    // Aquesta petita funció l'hem afegit per poder veure les dades ocultes (com els nodes expandits) de l'AIMA.
    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
    }
}