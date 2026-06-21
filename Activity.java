package virtualpetsimulator;
 
public class Activity {
 
    private final String name;
 
    public Activity(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Activity name cannot be empty.");
        }
        this.name = name;
    }
 
    public String getName() { return name; }
}