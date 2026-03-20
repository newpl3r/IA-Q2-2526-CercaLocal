package src;

import IA.Desastres.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ProbBoard {

    // --- DADES ESTÀTIQUES ---
    // Ho posem static perquè l'escenari (on són els grups i els centres) és igual per a tots els estats,
    // així ens estalviem duplicar aquestes dades a la memòria cada cop que provem un moviment nou.
    public static Grupos totsElsGrups;
    public static Centros totsElsCentres;
    public static int numHelicopters;

    // --- DADES DINÀMIQUES ---
    // Aquesta és la nostra estructura de dades principal. És una llista de llistes de llistes.
    // Nivell 1: Tenim cada helicòpter. -> Nivell 2: Els viatges que fa l'helicòpter. -> Nivell 3: Els IDs dels grups que pugen a l'helicòpter en aquell viatge.
    public ArrayList<ArrayList<ArrayList<Integer>>> assignacioHelicopters;

    /**
     * CONSTRUCTOR 1: El fem servir per crear l'escenari per primera vegada al Main.
     */
    public ProbBoard(int numCentres, int numGrups, int nHelicoptersPerCentre, int seed) {
        // Generem el mapa amb la llibreria de la pràctica.
        totsElsCentres = new Centros(numCentres, nHelicoptersPerCentre, seed);
        totsElsGrups = new Grupos(numGrups, seed);
        numHelicopters = numCentres * nHelicoptersPerCentre;

        // Preparem la nostra llista d'helicòpters totalment buida.
        assignacioHelicopters = new ArrayList<>();
        for (int i = 0; i < numHelicopters; i++) {
            assignacioHelicopters.add(new ArrayList<>());
        }

        // Cridem a una de les nostres estratègies inicials per pujar tota aquesta gent als helicòpters.
        generarSolucioAleatoria();
        //generarSolucioAvariciosa(); // Si volem provar l'altra estratègia, comentem la de dalt i descomentem aquesta.
        //generarSolucioAvariciosa2();
    }

    /**
     * CONSTRUCTOR 2: Molt important! El necessitem per clonar l'estat cada cop que simulem moure un grup,
     * així no matxaquem l'estat original quan provem diferents combinacions.
     */
    public ProbBoard(ProbBoard estatAnterior) {
        this.assignacioHelicopters = new ArrayList<>();
        // Fem una còpia exacta (deep copy) de l'assignació de l'estat anterior perquè siguin independents.
        for (ArrayList<ArrayList<Integer>> viatgesHeli : estatAnterior.assignacioHelicopters) {
            ArrayList<ArrayList<Integer>> viatgesClonats = new ArrayList<>();
            for (ArrayList<Integer> viatge : viatgesHeli) {
                viatgesClonats.add(new ArrayList<>(viatge));
            }
            this.assignacioHelicopters.add(viatgesClonats);
        }
    }

    /**
     * ESTRATÈGIA 1: Repartim la gent als helicòpters llançant un dau, completament a l'atzar.
     */
    private void generarSolucioAleatoria() {
        java.util.Random rand = new java.util.Random();

        // Anem mirant un per un tots els grups que tenim esperant ser rescatats.
        for (int idGrup = 0; idGrup < totsElsGrups.size(); idGrup++) {

            int personesDelGrup = totsElsGrups.get(idGrup).getNPersonas();

            // Triem a quin helicòpter l'assignem a l'atzar.
            int idHeli = rand.nextInt(numHelicopters);
            ArrayList<ArrayList<Integer>> viatgesHeli = assignacioHelicopters.get(idHeli);

            // Si el pobre pilot encara no té cap viatge assignat, li creem el primer.
            if (viatgesHeli.isEmpty()) {
                viatgesHeli.add(new ArrayList<>());
            }

            // Mirem què està passant a l'últim viatge d'aquest helicòpter.
            ArrayList<Integer> ultimViatge = viatgesHeli.get(viatgesHeli.size() - 1);

            int personesAlViatge = 0;
            for (int idGrupViatge : ultimViatge) {
                personesAlViatge += totsElsGrups.get(idGrupViatge).getNPersonas();
            }

            // Normes del professor: a l'helicòpter hi caben 15 persones i màxim 3 grups. Comprovem si hi ha lloc.
            if (ultimViatge.size() < 3 && (personesAlViatge + personesDelGrup) <= 15) {
                // Hi cap perfectament! Que pugin en aquest viatge.
                ultimViatge.add(idGrup);
            } else {
                // Ostres, l'helicòpter està ple. L'obliguem a tornar a la base i li assignem un viatge nou per aquest grup.
                ArrayList<Integer> nouViatge = new ArrayList<>();
                nouViatge.add(idGrup);
                viatgesHeli.add(nouViatge);
            }
        }
    }

    /**
     * ESTRATÈGIA 2: Solució Avariciosa (Greedy). Busquem sempre el centre més proper per assignar l'helicòpter.
     */
    private void generarSolucioAvariciosa() {
        int helisPerCentre = numHelicopters / totsElsCentres.size();
        java.util.Random rand = new java.util.Random();

        for (int idGrup = 0; idGrup < totsElsGrups.size(); idGrup++) {

            Grupo grup = totsElsGrups.get(idGrup);
            int personesDelGrup = grup.getNPersonas();

            // PAS 1: Busquem a quin centre de rescat li queda més a prop aquest grup de persones.
            int idCentreMesProper = 0;
            double distanciaMinima = Double.MAX_VALUE;

            for (int idCentre = 0; idCentre < totsElsCentres.size(); idCentre++) {
                Centro centre = totsElsCentres.get(idCentre);

                // Apliquem Pitàgores de tota la vida per trobar la distància en línia recta.
                double dx = grup.getCoordX() - centre.getCoordX();
                double dy = grup.getCoordY() - centre.getCoordY();
                double distancia = Math.sqrt(dx * dx + dy * dy);

                // Ens guardem el centre si està més a prop que els que ja hem mirat.
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    idCentreMesProper = idCentre;
                }
            }

            // PAS 2: Un cop sabem quin és el centre, agafem un dels helicòpters d'aquella base.
            int idHeli = (idCentreMesProper * helisPerCentre) + rand.nextInt(helisPerCentre);

            // PAS 3: Pugem el grup a l'helicòpter d'aquell centre vigilant de no superar el pes màxim,
            // exactament igual que ho fèiem a la solució aleatòria.
            ArrayList<ArrayList<Integer>> viatgesHeli = assignacioHelicopters.get(idHeli);

            if (viatgesHeli.isEmpty()) {
                viatgesHeli.add(new ArrayList<>());
            }

            ArrayList<Integer> ultimViatge = viatgesHeli.get(viatgesHeli.size() - 1);

            int personesAlViatge = 0;
            for (int idGrupViatge : ultimViatge) {
                personesAlViatge += totsElsGrups.get(idGrupViatge).getNPersonas();
            }

            if (ultimViatge.size() < 3 && (personesAlViatge + personesDelGrup) <= 15) {
                ultimViatge.add(idGrup);
            } else {
                ArrayList<Integer> nouViatge = new ArrayList<>();
                nouViatge.add(idGrup);
                viatgesHeli.add(nouViatge);
            }
        }
    }

    /**
     * ESTRATÈGIA 3: Solució Avariciosa 2. Busquem sempre l'helicòpter que minimitzi
     * el valor: "Distància a grup del seu Centre" x "Nombre de viatges"
     * La idea és que no hi hagi un helicòpter que faci 14 viatges quan un altre fa 2,
     * encara que estigui més aprop.
     */
    private void generarSolucioAvariciosa2() {
        int helisPerCentre = numHelicopters / totsElsCentres.size();
        java.util.Random rand = new java.util.Random();

        for (int idGrup = 0; idGrup < totsElsGrups.size(); idGrup++) {

            Grupo grup = totsElsGrups.get(idGrup);
            int personesDelGrup = grup.getNPersonas();

            // PAS 1: Cercarem en tots els centres quin helicopter minimitza
            // el valor -> Distancia x Num. Viatges.

            int idHeliMinim = -1;
            double valorMinim = Double.MAX_VALUE;

            for (int idCentre = 0; idCentre < totsElsCentres.size(); idCentre++) {
                Centro centre = totsElsCentres.get(idCentre);

                // PAS 1.1: Trobem la distància.
                double dx = grup.getCoordX() - centre.getCoordX();
                double dy = grup.getCoordY() - centre.getCoordY();
                double distancia = Math.sqrt(dx * dx + dy * dy);

                // PAS 1.2: Trobem l'helicopter amb el mínim nombre de viatges del centre actual.
                int heliInicial = (idCentre*helisPerCentre);
                int idHeliActual = -1;
                int numViatgesActual = Integer.MAX_VALUE;

                for (int idHeli = heliInicial; idHeli < heliInicial+helisPerCentre; idHeli++) {
                    int numViatgesHeli = assignacioHelicopters.get(idHeli).size();
                    if (numViatgesActual > numViatgesHeli) {
                        numViatgesActual = numViatgesHeli;
                        idHeliActual = idHeli;
                    }
                }

                // PAS 1.3: Multipliquem els dos valors
                double valorActual = distancia * numViatgesActual;

                // PAS 1.4: Comprovem si es menor que el de l'helicopter ja guardat
                // Si ho és, actualitzem els valors
                if (valorMinim > valorActual) {
                    idHeliMinim = idHeliActual;
                    valorMinim = valorActual;
                }
                // En cas d'empat, escollim l'helicòpter amb el menor
                // nombre de persones en l'últim viatge
                else if (valorMinim == valorActual) {

                    // 1. Calcul de persones per l'helicopter minim
                    ArrayList<ArrayList<Integer>> viatgesHeli = assignacioHelicopters.get(idHeliMinim);
                    if (viatgesHeli.isEmpty()) {
                        viatgesHeli.add(new ArrayList<>());
                    }
                    ArrayList<Integer> ultimViatge = viatgesHeli.get(viatgesHeli.size() - 1);

                    int personesMinim = 0;
                    for (int idGrupViatge : ultimViatge) {
                        personesMinim += totsElsGrups.get(idGrupViatge).getNPersonas();
                    }

                    // 2. Calcul de persones per l'helicopter d'aquesta iteracio
                    viatgesHeli = assignacioHelicopters.get(idHeliActual);
                    if (viatgesHeli.isEmpty()) {
                        viatgesHeli.add(new ArrayList<>());
                    }
                    ultimViatge = viatgesHeli.get(viatgesHeli.size() - 1);

                    int personesActual = 0;
                    for (int idGrupViatge : ultimViatge) {
                        personesActual += totsElsGrups.get(idGrupViatge).getNPersonas();
                    }

                    // 3. Escollim l'helicopter amb menys persones en l'ultim viatge
                    if (personesMinim > personesActual) {
                        idHeliMinim = idHeliActual;
                        valorMinim = valorActual;
                    }
                }
            }

            // PAS 2: Pugem el grup a l'helicòpter escollit, vigilant de no superar el pes màxim.

            ArrayList<ArrayList<Integer>> viatgesHeli = assignacioHelicopters.get(idHeliMinim);

            if (viatgesHeli.isEmpty()) {
                viatgesHeli.add(new ArrayList<>());
            }

            ArrayList<Integer> ultimViatge = viatgesHeli.get(viatgesHeli.size() - 1);

            int personesAlViatge = 0;
            for (int idGrupViatge : ultimViatge) {
                personesAlViatge += totsElsGrups.get(idGrupViatge).getNPersonas();
            }

            if (ultimViatge.size() < 3 && (personesAlViatge + personesDelGrup) <= 15) {
                ultimViatge.add(idGrup);
            }
            else {
                ArrayList<Integer> nouViatge = new ArrayList<>();
                nouViatge.add(idGrup);
                viatgesHeli.add(nouViatge);
            }
        }
    }

    // --- MÈTODES OBLIGATORIS PER A LA LLIBRERIA D'AIMA ---

    /**
     * HEURÍSTICA 1: Calcula el temps de vol i rescat base. Com menys trigui, millor serà l'estat.
     */
    public double heuristic() {
        double tempsTotalFlota = 0.0;
        int helisPerCentre = numHelicopters / totsElsCentres.size();

        // Repassem el llibre de ruta de cada helicòpter.
        for (int i = 0; i < assignacioHelicopters.size(); i++) {

            // Així deduïm des de quina base surt l'helicòpter que estem mirant.
            int idCentre = i / helisPerCentre;
            Centro centre = totsElsCentres.get(idCentre);

            double coordX_Centre = centre.getCoordX();
            double coordY_Centre = centre.getCoordY();

            ArrayList<ArrayList<Integer>> viatges = assignacioHelicopters.get(i);

            // Per cada viatge d'aquest helicòpter:
            for (int v = 0; v < viatges.size(); v++) {
                ArrayList<Integer> viatge = viatges.get(v);
                if (viatge.isEmpty()) continue;

                // Penalització: si no és el primer vol del dia, triga 10 minuts a descarregar a la base.
                if (v > 0) tempsTotalFlota += 10.0;

                // El pilot arrenca el vol des de la base (el centre).
                double posActualX = coordX_Centre;
                double posActualY = coordY_Centre;

                // Visitem els grups d'aquest viatge un per un.
                for (int g = 0; g < viatge.size(); g++) {
                    int idGrup = viatge.get(g);
                    Grupo grup = totsElsGrups.get(idGrup);

                    // A) Càlcul del vol: Distància entre on estem ara i el pròxim grup.
                    double dx = grup.getCoordX() - posActualX;
                    double dy = grup.getCoordY() - posActualY;
                    double distancia = Math.sqrt(dx * dx + dy * dy);

                    // L'enunciat diu que vola a 100km/h. Ho passem directament a minuts de vol.
                    tempsTotalFlota += (distancia / 100.0) * 60.0;

                    // B) Càlcul de càrrega: Pugem la gent a bord.
                    int persones = grup.getNPersonas();
                    int prioritat = grup.getPrioridad();

                    if (prioritat == 1) {
                        tempsTotalFlota += (persones * 2.0); // Prioritat 1 vol dir que tenen ferits (2 min per pujar-los)
                    } else {
                        tempsTotalFlota += (persones * 1.0); // Prioritat 2 vol dir que estan sans (1 min ràpid)
                    }

                    // Actualitzem la posició perquè l'helicòpter ja està flotant a sobre del grup.
                    posActualX = grup.getCoordX();
                    posActualY = grup.getCoordY();
                }

                // C) Tornada a casa: Distància des de l'últim grup del viatge fins a la base per descarregar.
                double dxTornada = coordX_Centre - posActualX;
                double dyTornada = coordY_Centre - posActualY;
                double distanciaTornada = Math.sqrt(dxTornada * dxTornada + dyTornada * dyTornada);

                tempsTotalFlota += (distanciaTornada / 100.0) * 60.0;
            }
        }

        return tempsTotalFlota; // Retornem tots els minuts sumats de tota la flota.
    }

    /**
     * HEURÍSTICA 2: El mateix que la primera, però castiguem l'algorisme si fa esperar massa als ferits.
     */
    public double heuristic2() {
        double tempsTotalFlota = 0.0;
        double penalitzacioFerits = 0.0; // Aquesta és la clau de la nova heurística.

        int helisPerCentre = numHelicopters / totsElsCentres.size();

        for (int i = 0; i < assignacioHelicopters.size(); i++) {
            int idCentre = i / helisPerCentre;
            Centro centre = totsElsCentres.get(idCentre);

            double tempsAquestHeli = 0.0; // Ens interessa saber el temps individual perquè si rescata algú al minut 500, el castigarem.
            ArrayList<ArrayList<Integer>> viatges = assignacioHelicopters.get(i);

            for (int v = 0; v < viatges.size(); v++) {
                ArrayList<Integer> viatge = viatges.get(v);
                if (viatge.isEmpty()) continue;

                if (v > 0) tempsAquestHeli += 10.0; // Descans a la base.

                double posActualX = centre.getCoordX();
                double posActualY = centre.getCoordY();

                for (int g = 0; g < viatge.size(); g++) {
                    int idGrup = viatge.get(g);
                    Grupo grup = totsElsGrups.get(idGrup);

                    // Temps de vol
                    double dx = grup.getCoordX() - posActualX;
                    double dy = grup.getCoordY() - posActualY;
                    double distancia = Math.sqrt(dx * dx + dy * dy);
                    tempsAquestHeli += (distancia / 100.0) * 60.0;

                    int persones = grup.getNPersonas();
                    if (grup.getPrioridad() == 1) { // FERITS DETECTATS!
                        tempsAquestHeli += (persones * 2.0);

                        // TRUC DE L'HEURÍSTICA: Multipliquem el minut en el que els hem rescatat per 50.
                        // Si els rescatem aviat (minut 10), sumem 500 de penalització.
                        // Si els rescatem tardíssim (minut 500), sumem 25000! Així la IA aprendrà a no fer-ho.
                        penalitzacioFerits += (tempsAquestHeli * 50.0);
                    } else { // SANS (no els penalitzem)
                        tempsAquestHeli += (persones * 1.0);
                    }

                    posActualX = grup.getCoordX();
                    posActualY = grup.getCoordY();
                }

                // Tornada a la base.
                double dxTornada = centre.getCoordX() - posActualX;
                double dyTornada = centre.getCoordY() - posActualY;
                double distanciaTornada = Math.sqrt(dxTornada * dxTornada + dyTornada * dyTornada);
                tempsAquestHeli += (distanciaTornada / 100.0) * 60.0;
            }
            tempsTotalFlota += tempsAquestHeli;
        }

        // Li tornem a l'AIMA el temps real + el pes gegant de la penalització perquè valori si la combinació és bona.
        return tempsTotalFlota + penalitzacioFerits;
    }

    public boolean is_goal() {
        // En un problema d'optimització (Cerca Local) mai hi ha un final perfecte, sempre busquem una versió "millor".
        return false;
    }

    // --- ELS NOSTRES DOS OPERADORS (Les "eines" de la IA per moure coses) ---

    /**
     * OPERADOR 1: Agafem un grup de gent i els passem a un altre helicòpter.
     */
    public void moureGrup(int idHeliOrigen, int numViatgeOrigen, int posGrup, int idHeliDesti) {

        // 1. Identifiquem a quin grup volem treure i quanta gent hi ha.
        ArrayList<Integer> viatgeOrigen = assignacioHelicopters.get(idHeliOrigen).get(numViatgeOrigen);
        int idGrup = viatgeOrigen.get(posGrup);
        int personesDelGrup = totsElsGrups.get(idGrup).getNPersonas();

        // 2. Els fem baixar de l'helicòpter d'origen.
        viatgeOrigen.remove(posGrup);

        // Si veiem que a l'helicòpter d'origen ja no queda absolutament ningú en aquest vol, cancel·lem el vol.
        if (viatgeOrigen.isEmpty()) {
            assignacioHelicopters.get(idHeliOrigen).remove(numViatgeOrigen);
        }

        // 3. Ens els emportem a l'helicòpter de destí que ens demanin.
        ArrayList<ArrayList<Integer>> viatgesDesti = assignacioHelicopters.get(idHeliDesti);

        if (viatgesDesti.isEmpty()) {
            viatgesDesti.add(new ArrayList<>());
        }

        // Intentem posar-los a l'últim viatge programat de l'helicòpter de destí.
        ArrayList<Integer> ultimViatgeDesti = viatgesDesti.get(viatgesDesti.size() - 1);

        int personesAlViatge = 0;
        for (int grupViatge : ultimViatgeDesti) {
            personesAlViatge += totsElsGrups.get(grupViatge).getNPersonas();
        }

        // Mirem la llibreta: passen de 15 persones? Són més de 3 grups junts?
        if (ultimViatgeDesti.size() < 3 && (personesAlViatge + personesDelGrup) <= 15) {
            ultimViatgeDesti.add(idGrup); // Hi caben! Pugen amb la resta.
        } else {
            // No hi caben, doncs el pilot haurà de fer un viatge extra només per a ells.
            ArrayList<Integer> nouViatge = new ArrayList<>();
            nouViatge.add(idGrup);
            viatgesDesti.add(nouViatge);
        }
    }

    /**
     * OPERADOR 2: Fem un "Swap" (Intercanvi). Agafem un grup de l'Helicòpter A, un de l'Helicòpter B i els canviem de lloc.
     * Retornem true si ens ha sortit bé, i false si amb el canvi l'helicòpter es desestabilitza per excés de pes.
     */
    public boolean intercanviarGrups(int h1, int v1, int p1, int h2, int v2, int p2) {
        ArrayList<Integer> viatge1 = assignacioHelicopters.get(h1).get(v1);
        ArrayList<Integer> viatge2 = assignacioHelicopters.get(h2).get(v2);

        int idGrup1 = viatge1.get(p1);
        int idGrup2 = viatge2.get(p2);

        int persones1 = totsElsGrups.get(idGrup1).getNPersonas();
        int persones2 = totsElsGrups.get(idGrup2).getNPersonas();

        // Calculem el pes actual de cada helicòpter abans de fer l'intercanvi.
        int capacitatActualViatge1 = 0;
        for (int g : viatge1) capacitatActualViatge1 += totsElsGrups.get(g).getNPersonas();

        int capacitatActualViatge2 = 0;
        for (int g : viatge2) capacitatActualViatge2 += totsElsGrups.get(g).getNPersonas();

        // Fem els càlculs mentals: si baixem el Grup 1 i pugem el Grup 2... quanta gent hi haurà?
        int novaCapacitat1 = capacitatActualViatge1 - persones1 + persones2;
        int novaCapacitat2 = capacitatActualViatge2 - persones2 + persones1;

        // Comprovem que cap dels dos helicòpters passi de les 15 persones de límit físic.
        if (novaCapacitat1 <= 15 && novaCapacitat2 <= 15) {
            // El canvi és viable! Fem el Swap a les nostres llistes.
            viatge1.set(p1, idGrup2);
            viatge2.set(p2, idGrup1);
            return true;
        }

        // Si resulta que un dels dos passava de les 15 persones amb l'intercanvi, el deneguem i retornem false.
        return false;
    }

    // Mètode de neteja: ens imprimeix pel terminal on és cadascú per comprovar que estem programant bé.
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
