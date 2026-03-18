package src;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;

public class ProbSuccessorFunction implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> retval = new ArrayList<>();
        ProbBoard board = (ProbBoard) state;

        // --- EXECUTANT L'OPERADOR 1 (MOURE) ---
        // Aquí li diem a la IA: "ullera tots els racons". Fem bucles per explorar absolutament
        // cada helicòpter, cada viatge i cada grup que tenim al mapa.
        for (int i = 0; i < board.assignacioHelicopters.size(); i++) {

            for (int j = 0; j < board.assignacioHelicopters.get(i).size(); j++) {

                for (int k = 0; k < board.assignacioHelicopters.get(i).get(j).size(); k++) {

                    // Ara que tenim un grup concret fixat, provem de moure'l a tooots els altres helicòpters del món.
                    for (int dest = 0; dest < board.numHelicopters; dest++) {

                        // Comprovem que no l'intentem moure a l'helicòpter on ja està (seria fer el tonto).
                        if (i != dest) {
                            // PAS CLAU: Copiem l'estat perquè el nostre mapa original quedi intacte de fons!
                            ProbBoard nouEstat = new ProbBoard(board);

                            // Posem en marxa l'operador al clon que acabem de crear.
                            nouEstat.moureGrup(i, j, k, dest);

                            // Etiquetem la caixa per saber què hem fet a l'AIMA.
                            String accio = "Mou grup de l'Heli " + i + " a l'Heli " + dest;

                            // Guardem aquesta combinació de futur possible.
                            retval.add(new Successor(accio, nouEstat));
                        }
                    }
                }
            }
        }

        // --- EXECUTANT L'OPERADOR 2 (INTERCANVIAR) ---
        // Fem el mateix rastreig per trobar el Grup 1...
        for (int h1 = 0; h1 < board.assignacioHelicopters.size(); h1++) {
            for (int v1 = 0; v1 < board.assignacioHelicopters.get(h1).size(); v1++) {
                for (int p1 = 0; p1 < board.assignacioHelicopters.get(h1).get(v1).size(); p1++) {

                    // ... i rastregem un segon cop buscant amb quin Grup 2 el podem intercanviar!
                    for (int h2 = h1; h2 < board.assignacioHelicopters.size(); h2++) {
                        for (int v2 = 0; v2 < board.assignacioHelicopters.get(h2).size(); v2++) {
                            for (int p2 = 0; p2 < board.assignacioHelicopters.get(h2).get(v2).size(); p2++) {

                                // Evitem intentar intercanviar un grup de gent per algú que viatja amb ells al mateix seient.
                                if (h1 == h2 && v1 == v2) continue;

                                ProbBoard nouEstat = new ProbBoard(board);
                                // Llancem la funció, i ens dirà true si els pesos quadren i no cau cap helicòpter.
                                boolean valid = nouEstat.intercanviarGrups(h1, v1, p1, h2, v2, p2);

                                if (valid) {
                                    // Si ha funcionat, ens guardem l'estat futur.
                                    String accio = "Intercanvia grup de l'Heli " + h1 + " amb Heli " + h2;
                                    retval.add(new Successor(accio, nouEstat));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Retornem la llista on hem anotat els centenars de combinacions que hem trobat.
        // L'AIMA buscarà quina és la millor i seguirà des d'allà.
        return retval;
    }
}
