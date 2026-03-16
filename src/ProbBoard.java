package projecte_ia.src;

import IA.Desastres.*;
import java.util.ArrayList;

public class ProbBoard {

    public static Grupos totsElsGrups;
    public static Centros totsElsCentres;
    public static int numHelicopters;

    public ArrayList<ArrayList<ArrayList<Integer>>> assignacioHelicopters;

    public ProbBoard(int numCentres, int numGrups, int nHelicoptersPerCentre, int seed) {
        // Inicialitzar l'escenari simulat amb les classes proporcionades (Desastres.jar)
        totsElsCentres = new Centros(numCentres, nHelicoptersPerCentre, seed);
        totsElsGrups = new Grupos(numGrups, seed);
        numHelicopters = numCentres * nHelicoptersPerCentre;

        // Inicialitzar l'estructura dels helicòpters buida
        assignacioHelicopters = new ArrayList<>();
        for (int i = 0; i < numHelicopters; i++) {
            assignacioHelicopters.add(new ArrayList<>());
        }

        // Cridem al mètode per repartir els grups
        //generarSolucioAleatoria();
        generarSolucioAvariciosa();
    }

    public ProbBoard(ProbBoard estatAnterior) {
        this.assignacioHelicopters = new ArrayList<>();
        // Fem una còpia profunda (deep copy) de l'assignació
        for (ArrayList<ArrayList<Integer>> viatgesHeli : estatAnterior.assignacioHelicopters) {
            ArrayList<ArrayList<Integer>> viatgesClonats = new ArrayList<>();
            for (ArrayList<Integer> viatge : viatgesHeli) {
                viatgesClonats.add(new ArrayList<>(viatge));
            }
            this.assignacioHelicopters.add(viatgesClonats);
        }
    }

    private void generarSolucioAleatoria() {
        java.util.Random rand = new java.util.Random();

        // Recorrem tots els grups d'1 en 1
        for (int idGrup = 0; idGrup < totsElsGrups.size(); idGrup++) {

            // Obtenim quantes persones té aquest grup
            int personesDelGrup = totsElsGrups.get(idGrup).getNPersonas();

            // Triem un helicòpter a l'atzar (del 0 al numHelicopters - 1)
            int idHeli = rand.nextInt(numHelicopters);

            // Agafem la llista de viatges d'aquest helicòpter
            ArrayList<ArrayList<Integer>> viatgesHeli = assignacioHelicopters.get(idHeli);

            // Si l'helicòpter encara no té cap viatge assignat, en creem un de buit
            if (viatgesHeli.isEmpty()) {
                viatgesHeli.add(new ArrayList<>());
            }

            // Agafem l'últim viatge per veure si hi ha lloc
            ArrayList<Integer> ultimViatge = viatgesHeli.get(viatgesHeli.size() - 1);

            // Calculem quantes persones hi ha actualment en aquest últim viatge
            int personesAlViatge = 0;
            for (int idGrupViatge : ultimViatge) {
                personesAlViatge += totsElsGrups.get(idGrupViatge).getNPersonas();
            }

            // Comprovem les dues restriccions: max 3 grups i max 15 persones en total
            if (ultimViatge.size() < 3 && (personesAlViatge + personesDelGrup) <= 15) {
                // Hi cap! L'afegim a l'últim viatge
                ultimViatge.add(idGrup);
            } else {
                // No hi cap. Creem un viatge nou, hi afegim el grup, i guardem el viatge a l'helicòpter
                ArrayList<Integer> nouViatge = new ArrayList<>();
                nouViatge.add(idGrup);
                viatgesHeli.add(nouViatge);
            }
        }
    }

    private void generarSolucioAvariciosa() {
        // Calculem quants helicòpters hi ha a cada centre per saber quin li toca
        int helisPerCentre = numHelicopters / totsElsCentres.size();
        java.util.Random rand = new java.util.Random();

        // Recorrem tots els grups d'1 en 1
        for (int idGrup = 0; idGrup < totsElsGrups.size(); idGrup++) {

            // Obtenim el grup i les seves persones
            Grupo grup = totsElsGrups.get(idGrup);
            int personesDelGrup = grup.getNPersonas();

            // BUSCAQUEM EL CENTRE MÉS PROPER
            int idCentreMesProper = 0;
            double distanciaMinima = Double.MAX_VALUE;

            for (int idCentre = 0; idCentre < totsElsCentres.size(); idCentre++) {
                Centro centre = totsElsCentres.get(idCentre);

                // Càlcul de la distància euclidiana clàssica: arrel((x2-x1)^2 + (y2-y1)^2)
                // (Nota per a tu: comprova a l'IDE si els mètodes són getCoordX() o getX())
                double dx = grup.getCoordX() - centre.getCoordX();
                double dy = grup.getCoordY() - centre.getCoordY();
                double distancia = Math.sqrt(dx * dx + dy * dy);

                // Si aquest centre està més a prop, ens el guardem
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    idCentreMesProper = idCentre;
                }
            }

            // TRIEM UN HELICÒPTER D'AQUEST CENTRE
            // Els helicòpters estan indexats per ordre. Si hi ha 2 helis per centre:
            // Centre 0 té l'heli 0 i 1. Centre 1 té el 2 i 3...
            int idHeli = (idCentreMesProper * helisPerCentre) + rand.nextInt(helisPerCentre);

            // AFEGIM EL GRUP A L'HELICÒPTER
            ArrayList<ArrayList<Integer>> viatgesHeli = assignacioHelicopters.get(idHeli);

            if (viatgesHeli.isEmpty()) {
                viatgesHeli.add(new ArrayList<>());
            }

            ArrayList<Integer> ultimViatge = viatgesHeli.get(viatgesHeli.size() - 1);

            int personesAlViatge = 0;
            for (int idGrupViatge : ultimViatge) {
                personesAlViatge += totsElsGrups.get(idGrupViatge).getNPersonas();
            }

            // Comprovem les dues restriccions: max 3 grups i max 15 persones en total
            if (ultimViatge.size() < 3 && (personesAlViatge + personesDelGrup) <= 15) {
                ultimViatge.add(idGrup);
            } else {
                ArrayList<Integer> nouViatge = new ArrayList<>();
                nouViatge.add(idGrup);
                viatgesHeli.add(nouViatge);
            }
        }
    }

    // --- MÈTODES OBLIGATORIS PER A AIMA ---

    public double heuristic() {
        // De moment retornem 0 perquè no doni error de compilació.
        return 0.0;
    }

    public boolean is_goal() {
        return false;
    }

    // Mètode per comprovar si s'ha generat bé l'estat inicial
    public void imprimirEstat() {
        System.out.println("--- ESTAT ACTUAL DE LA FLOTA ---");
        for (int i = 0; i < assignacioHelicopters.size(); i++) {
            System.out.println("Helicòpter " + i + ":");
            ArrayList<ArrayList<Integer>> viatges = assignacioHelicopters.get(i);
            if (viatges.isEmpty()) {
                System.out.println("  Sense viatges assignats.");
            } else {
                for (int v = 0; v < viatges.size(); v++) {
                    System.out.println("  Viatge " + (v+1) + ": Grups " + viatges.get(v));
                }
            }
        }
        System.out.println("--------------------------------");
    }
}
