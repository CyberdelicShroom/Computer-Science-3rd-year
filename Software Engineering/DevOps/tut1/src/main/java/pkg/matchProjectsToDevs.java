/** Java program that takes in input of developers and projects,
 *  adds them to ArrayLists of Developer and Project,
 *  evaluates scores for the developers and projects,
 *  computes 2D arrays of project preferences and
 *  developer preferences, and evaluates the stable matching
 *  between projects and developers using the Gale-Shapley
 *  algorithm.
 *
 * @author Keagan Selwyn Gill
 * */
package pkg;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class matchProjectsToDevs {

    /** function swaps the array's first element with last
     *  element, second element with last second element and
     *  so on
     * @param a int array to reverse
     * @param n length of array
     *  */
    static void reverse(int a[], int n) {
        int i, k, t;
        for (i = 0; i < n / 2; i++) {
            t = a[i];
            a[i] = a[n - i - 1];
            a[n - i - 1] = t;
        }
    }

    /** Evaluates a project score for each developer
     * @param developers ArrayList of Developer
     * @param projects ArrayList of Project
     * @param num_projs_and_devs total number of projects/developers, i.e. N
     * @return 2D int array of developers by project scores (Rows are the different devs, columns are the different projects with its score).
     * */
    public static int[][] proj_scores_forEach_dev(ArrayList<Developer> developers, ArrayList<Project> projects, int num_projs_and_devs) {
        int N = num_projs_and_devs;
        int[][] proficiency_scores = new int[N][N];
        // Rows are the different devs, columns are the different projects. (Number of proficiencies that the dev has of each project that is required)

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                proficiency_scores[i][j] = 0;
            }
        }

        int d = 0;
        int p = 0;
        for (Developer dev : developers) {
            for (Project proj : projects) {
                for (int i = 0; i < dev.proficiencies.length; i++){
                    for (int j = 0; j < proj.techStack.length; j++) {
                        if (dev.proficiencies[i].equals(proj.techStack[j])) {
//                            System.out.println("("+d+", "+p+")");
                            proficiency_scores[d][p] += 1;
                        }
                    }

                }
                if (p < N-1){
                    p++;
                } else {
                    p = 0;
                }
            }
            if (d < N-1){
                d++;
            } else {
                d = 0;
            }
        }

//        System.out.println("Proficiency Scores:");
//        for (int i = 0; i < N; i++){
//            for (int j = 0; j < N; j++) {
//                System.out.print(proficiency_scores[i][j] + " ");
//            }
//            System.out.println();
//        }

        int[] time_rate_scores = new int[N];
        int[][] proj_scores_forEach_dev = proficiency_scores;

//        System.out.println("dev_scores_forEach_proj:");
//        for (int i = 0; i < N; i++){
//            for (int j = 0; j < N; j++) {
//                System.out.print(dev_scores_forEach_proj[i][j] + " ");
//            }
//            System.out.println();
//        }

        int c = 0;
        for (Project proj : projects) {
            time_rate_scores[c] = proj.rate + proj.time;
            c++;
        }

        for (d = 0; d < N; d++) {
            for (p = 0; p < N; p++) {
                proj_scores_forEach_dev[d][p] = time_rate_scores[p] + proj_scores_forEach_dev[d][p];
            }
        }

        return proj_scores_forEach_dev;
    }

    /** Computes a 2D int array of project preferences for each developer (developers by project preferences)
     * @param dev_by_proj 2D int array of project scores for each dev (developers by project scores)
     * @param num_projs_and_devs total number of projects/developers, i.e. N
     * @return 2D int array of project preferences for each developer
     * */
    public static int[][] proj_preferences_forEach_dev(int[][] dev_by_proj, int num_projs_and_devs) {
        int N = num_projs_and_devs;
        int[][] dev_by_proj_beforeSort = new int[N][N];

        for (int j = 0; j < N; j++) {
            for (int x = 0; x < N; x++) {
                dev_by_proj_beforeSort[j][x] = dev_by_proj[j][x];
            }
        }

//        System.out.println();
//        System.out.println("dev_by_proj_beforeSort:");
//        for (int i = 0; i < num_projs_and_devs; i++){
//            int d = i+1;
//            System.out.print("d" + d + " ");
//            for (int j = 0; j < num_projs_and_devs; j++) {
//                System.out.print(dev_by_proj_beforeSort[i][j] + " ");
//            }
//            System.out.println();
//        }

        for (int x = 0; x < N; x++) {
            Arrays.sort(dev_by_proj[x]);
            reverse(dev_by_proj[x], N);
        }

//        System.out.println();
//        System.out.println("dev_by_proj (after sort):");
//        for (int i = 0; i < num_projs_and_devs; i++){
//            int d = i+1;
//            System.out.print("d" + d + " ");
//            for (int j = 0; j < num_projs_and_devs; j++) {
//                System.out.print(dev_by_proj[i][j] + " ");
//            }
//            System.out.println();
//        }

        int[] tmp = new int[N];
        // gives a 2d array of the position that the proj (column) should be
        for (int i = 0; i < N; i++) {
            tmp = dev_by_proj[i];
            for (int j = 0; j < N; j++) {
                for (int x = 0; x < N; x++) {
                    if (tmp[j] == dev_by_proj_beforeSort[i][x]) {
                        dev_by_proj_beforeSort[i][x] = j+1;
                        break;
                    }
                }
            }
        }

        // devs (rows) by proj preferences (columns)
        int[][] proj_preferences_forEach_dev = new int[N][N];
        for (int i = 0; i < N; i++) {
            int row = i;
            for (int j = 0; j < N; j++) {
                int col = dev_by_proj_beforeSort[i][j] - 1;
                proj_preferences_forEach_dev[row][col] = j + 1;
            }
        }
        return proj_preferences_forEach_dev;
    }

    /** Evaluates a developer score for each project
     * @param developers ArrayList of Developer
     * @param projects ArrayList of Project
     * @param num_projs_and_devs total number of projects/developers, i.e. N
     * @return 2D int array of developer scores by projects (Rows are the different devs with its score, columns are the different projects).
     * */
    public static int[][] dev_scores_forEach_proj(ArrayList<Developer> developers, ArrayList<Project> projects, int num_projs_and_devs) {

        int N = num_projs_and_devs;
        int[][] proficiency_scores = new int[N][N];
        // Rows are the different devs, columns are the different projects. (Number of proficiencies that the dev has of each project that is required)

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                proficiency_scores[i][j] = 0;
            }
        }

        int d = 0;
        int p = 0;
        for (Developer dev : developers) {
            for (Project proj : projects) {
                for (int i = 0; i < dev.proficiencies.length; i++){
                    for (int j = 0; j < proj.techStack.length; j++) {
                        if (dev.proficiencies[i].equals(proj.techStack[j])) {
                            proficiency_scores[d][p] += 1;
                        }
                    }

                }
                if (p < N-1){
                    p++;
                } else {
                    p = 0;
                }
            }
            if (d < N-1){
                d++;
            } else {
                d = 0;
            }
        }

//        System.out.println("Proficiency Scores:");
//        for (int i = 0; i < N; i++){
//            for (int j = 0; j < N; j++) {
//                System.out.print(proficiency_scores[i][j] + " ");
//            }
//            System.out.println();
//        }

        int[] time_rate_scores = new int[N];
        int[][] dev_scores_forEach_proj = proficiency_scores;

        int c = 0;
        for (Developer dev : developers) {
             time_rate_scores[c] = dev.rate - dev.time;
             c++;
        }

        for (d = 0; d < N; d++) {
            for (p = 0; p < N; p++) {
                dev_scores_forEach_proj[d][p] = time_rate_scores[p] - dev_scores_forEach_proj[d][p];
            }
        }

        return dev_scores_forEach_proj;
    }

    /** Computes a 2D int array of developer preferences for each project (projects by developer preferences)
     * @param dev_by_proj 2D int array of developer scores for each project (developer scores by projects)
     * @param num_projs_and_devs total number of projects/developers, i.e. N
     * @return 2D int array of developer preferences for each project
     * */
    public static int[][] dev_preferences_forEach_proj(int[][] dev_by_proj, int num_projs_and_devs) {
        int N = num_projs_and_devs;
        int[][] proj_by_dev_beforeSort = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                proj_by_dev_beforeSort[i][j] = 0;
            }
        }

        int[][] proj_by_dev = transposeMatrix(dev_by_proj);

        for (int j = 0; j < N; j++) {
            for (int x = 0; x < N; x++) {
                proj_by_dev_beforeSort[j][x] = proj_by_dev[j][x];
            }
        }

        for (int x = 0; x < N; x++) {
            Arrays.sort(proj_by_dev[x]);
        }

        int[] tmp = new int[N];
        // gives a 2d array of the position that the dev (column) should be
        for (int i = 0; i < N; i++) {
            tmp = proj_by_dev[i];
            for (int j = 0; j < N; j++) {
                for (int x = 0; x < N; x++) {
                    if (tmp[j] == proj_by_dev_beforeSort[i][x]) {
                        proj_by_dev_beforeSort[i][x] = j+1;
                        break;
                    }
                }
            }
        }

        // projects (rows) by dev preferences (columns)
        int[][] dev_preferences_forEach_proj = new int[N][N];
        for (int i = 0; i < N; i++) {
            int row = i;
            for (int j = 0; j < N; j++) {
                int col = proj_by_dev_beforeSort[i][j] - 1;
                dev_preferences_forEach_proj[row][col] = j + 1;
            }
        }

        return dev_preferences_forEach_proj;
    }

    /** Function that transposes a 2D int array
     * @param matrix 2D int array to be transposed
     * @return Transposed array/matrix
     * */
    public static int[][] transposeMatrix(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int[][] transposedMatrix = new int[n][m];

        for(int x = 0; x < n; x++) {
            for(int y = 0; y < m; y++) {
                transposedMatrix[x][y] = matrix[y][x];
            }
        }

        return transposedMatrix;
    }

    public static void main( String[] args ) {
        ArrayList<Developer> developers = new ArrayList<>();
        ArrayList<Project> projects = new ArrayList<>();
        int num_projs_and_devs = 0;

        if (args.length == 2) {
            String pathToProjectsInput = args[0];
            String pathToDevelopersInput = args[1];

            try {
                // src/test/resources/projects.txt
                File projects_file = new File(pathToProjectsInput);
                Scanner in_proj = new Scanner(projects_file);

                // src/test/resources/developers.txt
                File devs_file = new File(pathToDevelopersInput);
                Scanner in_devs = new Scanner(devs_file);

                in_proj.nextLine();
                while (in_proj.hasNextLine()) {
                    projects.add(new Project(in_proj.nextLine()));
                    num_projs_and_devs++;
                }

                in_devs.nextLine();
                while (in_devs.hasNextLine()) {
                    developers.add(new Developer(in_devs.nextLine()));
                }
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } else if (args.length == 0) {
            System.out.println("Enter developers with the following format:");
            System.out.println("id, name, proficiencies, time available (hrs per week), pay rate (Rand per hour)");
            System.out.println("--- Proficiencies must be 4 total with a space in between ---");
            System.out.println("--- Developer id's start from 0 to N-1 and Project id's start from N to 2*N-1 (where N is total number of developers/projects) ---");

            try{
                BufferedReader in_devs = new BufferedReader(new InputStreamReader(System.in));
                String dev_line;
                BufferedReader in_proj = new BufferedReader(new InputStreamReader(System.in));
                String proj_line;
                int p = 0;

                while((dev_line = in_devs.readLine()) != null) {
                    if(dev_line.equals("next")) {
//                        System.out.println("num_projs_and_devs = " + num_projs_and_devs);
                        System.out.println("Enter projects with the following format:");
                        System.out.println("id, name, tech stack, time frame (hrs per week), price per hour (Rand per hour)");
                        System.out.println("--- Tech stack must be 4 total with a space in between ---");
                        break;
                    }
                    developers.add(new Developer(dev_line));
                    num_projs_and_devs++;
                }

                while( ((proj_line = in_proj.readLine()) != null)) {
                    projects.add(new Project(proj_line));
                    p++;
//                    System.out.println("p = " + p);
                    if (p == num_projs_and_devs) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        int[][] dev_by_proj = dev_scores_forEach_proj(developers, projects, num_projs_and_devs);
        int[][] proj_by_dev_preferences = dev_preferences_forEach_proj(dev_by_proj, num_projs_and_devs);

        for (int i = 0; i < num_projs_and_devs; i++) {
            for (int j = 0; j < num_projs_and_devs; j++) {
                proj_by_dev_preferences[i][j] -= 1;
            }
        }

        for (int i = 0; i < num_projs_and_devs; i++) {
            for (int j = 0; j < num_projs_and_devs; j++) {
                proj_by_dev_preferences[i][j] += num_projs_and_devs;
            }
        }

        System.out.println();
        System.out.println("proj_by_dev_preferences:");
        for (int i = 0; i < num_projs_and_devs; i++) {
            int p = i + 1;
            System.out.print("p" + p + " ");
            for (int j = 0; j < num_projs_and_devs; j++) {
                System.out.print(proj_by_dev_preferences[i][j] + " ");
            }
            System.out.println();
        }

        int[][] dev_by_proj_2 = proj_scores_forEach_dev(developers, projects, num_projs_and_devs);
        int[][] dev_by_proj_preferences = proj_preferences_forEach_dev(dev_by_proj_2, num_projs_and_devs);

        for (int i = 0; i < num_projs_and_devs; i++) {
            for (int j = 0; j < num_projs_and_devs; j++) {
                dev_by_proj_preferences[i][j] -= 1;
            }
        }

        System.out.println();
        System.out.println("dev_by_proj_preferences:");
        for (int i = 0; i < num_projs_and_devs; i++) {
            int d = i + 1;
            System.out.print("d" + d + " ");
            for (int j = 0; j < num_projs_and_devs; j++) {
                System.out.print(dev_by_proj_preferences[i][j] + " ");
            }
            System.out.println();
        }

        int[][] result = new int[num_projs_and_devs + num_projs_and_devs][num_projs_and_devs];

        System.arraycopy(proj_by_dev_preferences, 0, result, 0, proj_by_dev_preferences.length);
        System.arraycopy(dev_by_proj_preferences, 0, result, proj_by_dev_preferences.length, dev_by_proj_preferences.length);

        System.out.println();
        System.out.println("result array:");
        for (int i = 0; i < num_projs_and_devs + num_projs_and_devs; i++) {
            for (int j = 0; j < num_projs_and_devs; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }

        GaleShapley gs = new GaleShapley(num_projs_and_devs);
        int[][] output = gs.stableMatching(result);

        System.out.println();
        System.out.println("Stable matches:");
        System.out.println("Proj Dev");
        System.out.println("--------");
        for (int i = 0; i < num_projs_and_devs; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(output[i][j] + "     ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Final output:");
        System.out.println("Proj      Dev");
        System.out.println("-------------");
        for (int i = 0; i < num_projs_and_devs; i++) {
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    for (Project proj : projects) {
                        if (proj.id == output[i][j]) {
                            System.out.print(proj.name + "     ");
                        }
                    }
                } else if (j == 1) {
                    for (Developer dev : developers) {
                        if (dev.id == output[i][j]) {
                            System.out.println(dev.name);
                        }
                    }
                }
            }
        }

    }
}

