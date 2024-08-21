/** Java program for stable marriage problem.
 * Generates a stable matching between projects and developers. */
package pkg;
import java.util.*;

public class GaleShapley {

    // Number of Developers or Projects
    static int N;
    public GaleShapley(int N) {
        this.N = N;
    }

    /** This function returns true if project
     * 'p' prefers developer 'd1' over developer 'd'
     * @param prefer 2D int array of project preferences and developer preferences
     * @param p refers to a project
     * @param d refers to a developer
     * @param d1 refers to a different developer
     * @return boolean true/false
     */
    static boolean pPrefers_d1_over_d(int prefer[][], int p, int d, int d1) {

        for (int i = 0; i < N; i++) {

            if (prefer[p][i] == d1) {
                return true;
            }

            if (prefer[p][i] == d) {
                return false;
            }
        }
        return false;
    }

    /** Prints stable matching for N developers and
     * N projects. Projects are numbered as 0 to
     * N-1. Developers are numbered as N to 2N-1.
     * @param prefer 2D int array of project preferences and developer preferences
     * @return N by 2 2D int array of the stable matching project preferences
     */
    int[][] stableMatching(int prefer[][]) {

        int pPartner[] = new int[N];

        boolean dFree[] = new boolean[N];

        Arrays.fill(pPartner, -1);
        int freeCount = N;

        while (freeCount > 0) {

            int d;
            for (d = 0; d < N; d++) {
                if (dFree[d] == false) {
                    break;
                }
            }

            for (int i = 0; i < N && dFree[d] == false; i++) {
                int p = prefer[d][i];

                if (pPartner[p - N] == -1) {
                    pPartner[p - N] = d;
                    dFree[d] = true;
                    freeCount--;
                } else {

                    int d1 = pPartner[p - N];

                    if (pPrefers_d1_over_d(prefer, p, d, d1) == false) {
                        pPartner[p - N] = d;
                        dFree[d] = true;
                        dFree[d1] = false;
                    }
                }
            }
        } // End while loop

        int[][] result = new int[N][2];

        for (int x = 0; x < N; x++) {
            for (int y = 0; y < 2; y++) {
                if (y == 0) {
                    result[x][y] = x + N;
                } else if (y == 1) {
                    result[x][y] = pPartner[x];
                }
            }
        }

        return result;
    }

}
