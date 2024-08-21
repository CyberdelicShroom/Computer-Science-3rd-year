package pkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class matchProjectsToDevsTest {
    
    @Test
    public void evaluateProjectsAndDevsMatches() throws FileNotFoundException {
        int[][] expectedMatches = {
            {6,0},
            {7,1},
            {8,3},
            {9,4},
            {10,5},
            {11,2}};
        
            ArrayList<Developer> developers = new ArrayList<>();
            ArrayList<Project> projects = new ArrayList<>();

            File projects_file = new File("src/test/resources/projects.txt");
            Scanner in_proj = new Scanner(projects_file);

            File devs_file = new File("src/test/resources/developers.txt");
            Scanner in_devs = new Scanner(devs_file);

            int num_projs_and_devs = 0;

            in_proj.nextLine();
            while (in_proj.hasNextLine()) {
                projects.add(new Project(in_proj.nextLine()));
                num_projs_and_devs++;
            }

            in_devs.nextLine();
            while (in_devs.hasNextLine()) {
                developers.add(new Developer(in_devs.nextLine()));
            }

            int[][] dev_by_proj = matchProjectsToDevs.dev_scores_forEach_proj(developers, projects, num_projs_and_devs);
            int[][] proj_by_dev_preferences = matchProjectsToDevs.dev_preferences_forEach_proj(dev_by_proj, num_projs_and_devs);

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

            int[][] dev_by_proj_2 = matchProjectsToDevs.proj_scores_forEach_dev(developers, projects, num_projs_and_devs);
            int[][] dev_by_proj_preferences = matchProjectsToDevs.proj_preferences_forEach_dev(dev_by_proj_2, num_projs_and_devs);

            for (int i = 0; i < num_projs_and_devs; i++) {
                for (int j = 0; j < num_projs_and_devs; j++) {
                    dev_by_proj_preferences[i][j] -= 1;
                }
            }

            int[][] result = new int[num_projs_and_devs + num_projs_and_devs][num_projs_and_devs];

            System.arraycopy(proj_by_dev_preferences, 0, result, 0, proj_by_dev_preferences.length);
            System.arraycopy(dev_by_proj_preferences, 0, result, proj_by_dev_preferences.length, dev_by_proj_preferences.length);

            GaleShapley gs = new GaleShapley(num_projs_and_devs);
            int[][] output = gs.stableMatching(result);

            assertArrayEquals(expectedMatches, output);
    }

    @Test
    public void evaluateProjectsAndDevsMatchesWithDifferentInput() throws FileNotFoundException {
        int[][] expectedMatches = {
                {6,0},
                {7,5},
                {8,1},
                {9,2},
                {10,4},
                {11,3}};

        ArrayList<Developer> developers = new ArrayList<>();
        ArrayList<Project> projects = new ArrayList<>();

        File projects_file = new File("src/test/resources/projects2.txt");
        Scanner in_proj = new Scanner(projects_file);

        File devs_file = new File("src/test/resources/developers2.txt");
        Scanner in_devs = new Scanner(devs_file);

        int num_projs_and_devs = 0;

        in_proj.nextLine();
        while (in_proj.hasNextLine()) {
            projects.add(new Project(in_proj.nextLine()));
            num_projs_and_devs++;
        }

        in_devs.nextLine();
        while (in_devs.hasNextLine()) {
            developers.add(new Developer(in_devs.nextLine()));
        }

        int[][] dev_by_proj = matchProjectsToDevs.dev_scores_forEach_proj(developers, projects, num_projs_and_devs);
        int[][] proj_by_dev_preferences = matchProjectsToDevs.dev_preferences_forEach_proj(dev_by_proj, num_projs_and_devs);

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

        int[][] dev_by_proj_2 = matchProjectsToDevs.proj_scores_forEach_dev(developers, projects, num_projs_and_devs);
        int[][] dev_by_proj_preferences = matchProjectsToDevs.proj_preferences_forEach_dev(dev_by_proj_2, num_projs_and_devs);

        for (int i = 0; i < num_projs_and_devs; i++) {
            for (int j = 0; j < num_projs_and_devs; j++) {
                dev_by_proj_preferences[i][j] -= 1;
            }
        }

        int[][] result = new int[num_projs_and_devs + num_projs_and_devs][num_projs_and_devs];

        System.arraycopy(proj_by_dev_preferences, 0, result, 0, proj_by_dev_preferences.length);
        System.arraycopy(dev_by_proj_preferences, 0, result, proj_by_dev_preferences.length, dev_by_proj_preferences.length);

        GaleShapley gs = new GaleShapley(num_projs_and_devs);
        int[][] output = gs.stableMatching(result);

        assertArrayEquals(expectedMatches, output);
    }

    @Test
    public void evaluateProjectPreferences() throws FileNotFoundException {
        int[][] expectedProjectPreferences = {
                {6,8,9,10,11,7},
                {8,9,10,11,6,7},
                {8,11,9,10,6,7},
                {10,11,6,7,8,9},
                {10,11,6,7,8,9},
                {7,6,10,8,9,11}};

        ArrayList<Developer> developers = new ArrayList<>();
        ArrayList<Project> projects = new ArrayList<>();

        File projects_file = new File("src/test/resources/projects2.txt");
        Scanner in_proj = new Scanner(projects_file);

        File devs_file = new File("src/test/resources/developers2.txt");
        Scanner in_devs = new Scanner(devs_file);

        int num_projs_and_devs = 0;

        in_proj.nextLine();
        while (in_proj.hasNextLine()) {
            projects.add(new Project(in_proj.nextLine()));
            num_projs_and_devs++;
        }

        in_devs.nextLine();
        while (in_devs.hasNextLine()) {
            developers.add(new Developer(in_devs.nextLine()));
        }

        int[][] dev_by_proj = matchProjectsToDevs.dev_scores_forEach_proj(developers, projects, num_projs_and_devs);
        int[][] proj_by_dev_preferences = matchProjectsToDevs.dev_preferences_forEach_proj(dev_by_proj, num_projs_and_devs);

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

        assertArrayEquals(expectedProjectPreferences, proj_by_dev_preferences);
    }

    @Test
    public void evaluateDeveloperPreferences() throws FileNotFoundException{
        int[][] expectedDeveloperPreferences = {
                {4,5,0,1,3,2},
                {4,5,0,1,3,2},
                {4,5,0,1,3,2},
                {4,5,0,1,3,2},
                {4,5,0,1,3,2},
                {4,5,0,1,3,2}};

        ArrayList<Developer> developers = new ArrayList<>();
        ArrayList<Project> projects = new ArrayList<>();

        File projects_file = new File("src/test/resources/projects2.txt");
        Scanner in_proj = new Scanner(projects_file);

        File devs_file = new File("src/test/resources/developers2.txt");
        Scanner in_devs = new Scanner(devs_file);

        int num_projs_and_devs = 0;

        in_proj.nextLine();
        while (in_proj.hasNextLine()) {
            projects.add(new Project(in_proj.nextLine()));
            num_projs_and_devs++;
        }

        in_devs.nextLine();
        while (in_devs.hasNextLine()) {
            developers.add(new Developer(in_devs.nextLine()));
        }

        int[][] dev_by_proj_2 = matchProjectsToDevs.proj_scores_forEach_dev(developers, projects, num_projs_and_devs);
        int[][] dev_by_proj_preferences = matchProjectsToDevs.proj_preferences_forEach_dev(dev_by_proj_2, num_projs_and_devs);

        for (int i = 0; i < num_projs_and_devs; i++) {
            for (int j = 0; j < num_projs_and_devs; j++) {
                dev_by_proj_preferences[i][j] -= 1;
            }
        }
        assertArrayEquals(expectedDeveloperPreferences, dev_by_proj_preferences);
    }

    @Test
    public void transposeMatrixTest() {
        int[][] randomMatrix = {
                {17, 13, 19, 24, 11, 18},
                {19, 28,  2, 11,  6, 16},
                {11,  1,  3,  0,  6, 24},
                {2, 12, 24,  3, 10, 17},
                {11, 24, 21,  5,  4, 16},
                {1, 11,  8,  7, 23, 20}};

        int[][] expectedMatrix = {
                {17, 19, 11,  2, 11,  1},
                {13, 28,  1, 12, 24, 11},
                {19,  2,  3, 24, 21,  8},
                {24, 11,  0,  3,  5,  7},
                {11,  6,  6, 10,  4, 23},
                {18, 16, 24, 17, 16, 20}};

        int[][] transposedRandomMatrix = matchProjectsToDevs.transposeMatrix(randomMatrix);

        assertArrayEquals(expectedMatrix, transposedRandomMatrix);
    }
}
