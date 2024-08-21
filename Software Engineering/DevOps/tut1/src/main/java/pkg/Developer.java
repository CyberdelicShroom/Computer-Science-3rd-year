/** Data structure for storing each developer's details */
package pkg;
public class Developer {
    int id;
    String name;
    String[] proficiencies = new String[4];
    int time;
    int rate;
    public Developer(String dev) {
        this.id = Integer.parseInt(dev.split(", ")[0]);
        this.name = dev.split(", ")[1];
        this.proficiencies = dev.split(", ")[2].split(" ");
        this.time = Integer.parseInt(dev.split(", ")[3]);
        this.rate = Integer.parseInt(dev.split(", ")[4]);
    }
}
