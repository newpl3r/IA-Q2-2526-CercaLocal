package src;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProbSuccessorFunctionSA implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> retval = new ArrayList<>();
        ProbBoard board = (ProbBoard) state;
        Random rand = new Random();

        ProbBoard nouEstat = new ProbBoard(board);
        String accio = "";

        // A diferència del Hill Climbing on provàvem TOTS els escenaris, aquí estem en SA.
        // La clau del SA és trobar UN moviment aleatori ràpidament. Tirem un dau: cara o creu (0 o 1).
        int tipusOperador = rand.nextInt(2);

        if (tipusOperador == 0) {
            // --- HEM TRET CARA: Fem un intent de moure un grup normal. ---
            int heliOrigen, viatgeOrigen, posGrup, heliDesti;

            // Busquem un helicòpter d'origen, però vigilem que l'helicòpter que hem triat tingui feina assignada.
            do {
                heliOrigen = rand.nextInt(board.assignacioHelicopters.size());
            } while (board.assignacioHelicopters.get(heliOrigen).isEmpty());

            // Seleccionem un viatge i un grup al vol completament a cegues.
            viatgeOrigen = rand.nextInt(board.assignacioHelicopters.get(heliOrigen).size());
            posGrup = rand.nextInt(board.assignacioHelicopters.get(heliOrigen).get(viatgeOrigen).size());

            // Triem cap a on volem llançar el grup i ens assegurem que no l'estiguem enviant al seu propi helicòpter.
            do {
                heliDesti = rand.nextInt(board.numHelicopters);
            } while (heliDesti == heliOrigen);

            // Ho executem contra la nostra còpia de l'estat (nouEstat)
            nouEstat.moureGrup(heliOrigen, viatgeOrigen, posGrup, heliDesti);
            accio = "SA: Mou grup de l'Heli " + heliOrigen + " a l'Heli " + heliDesti;

        } else {
            // --- HEM TRET CREU: Intentem intercanviar dos grups a cegues. ---
            int h1 = 0, v1 = 0, p1 = 0, h2 = 0, v2 = 0, p2 = 0;
            boolean valid = false;

            // A vegades triem dos grups que no els podem intercanviar perquè, per exemple, pesen massa
            // i ens enfonsen l'helicòpter. Per si de cas, ens donem 10 intents (intents < 10) per trobar-ne un de vàlid.
            int intents = 0;
            while (!valid && intents < 10) {
                // Selecció cega del Grup 1
                do { h1 = rand.nextInt(board.assignacioHelicopters.size()); } while (board.assignacioHelicopters.get(h1).isEmpty());
                v1 = rand.nextInt(board.assignacioHelicopters.get(h1).size());
                p1 = rand.nextInt(board.assignacioHelicopters.get(h1).get(v1).size());

                // Selecció cega del Grup 2
                do { h2 = rand.nextInt(board.assignacioHelicopters.size()); } while (board.assignacioHelicopters.get(h2).isEmpty());
                v2 = rand.nextInt(board.assignacioHelicopters.get(h2).size());
                p2 = rand.nextInt(board.assignacioHelicopters.get(h2).get(v2).size());

                // Si per atzar hem escollit intercanviar la mateixa gent... passem, ho tornem a provar.
                if (h1 == h2 && v1 == v2) continue;

                // Provem l'intercanvi de grups contra la còpia.
                valid = nouEstat.intercanviarGrups(h1, v1, p1, h2, v2, p2);
                intents++;
            }

            // Si hem trobat un bon intercanvi ho apuntem; si als 10 intents seguíem rebentant l'helicòpter, ho declarem fallit.
            if (valid) {
                accio = "SA: Intercanvi Heli " + h1 + " amb Heli " + h2;
            } else {
                accio = "SA: Intercanvi fallit";
            }
        }

        // Lliurem l'ÚNIC estat derivat que hem fabricat a la llibreria d'AIMA perquè avanci una sola casella.
        retval.add(new Successor(accio, nouEstat));
        return retval;
    }
}