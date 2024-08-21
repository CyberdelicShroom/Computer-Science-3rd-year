/** Data structure for storing each project's details */
package pkg;
public class Project {
    int id;
    String name;
    String[] techStack = new String[4];
    int time;
    int rate;
    public Project(String proj) {
        this.id = Integer.parseInt(proj.split(", ")[0]);
        this.name = proj.split(", ")[1];
        this.techStack = proj.split(", ")[2].split(" ");
        this.time = Integer.parseInt(proj.split(", ")[3]);
        this.rate = Integer.parseInt(proj.split(", ")[4]);
    }
}
