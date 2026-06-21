package virtualpetsimulator;
 
public class Food {
 
    private final String name;
    private final int    nutrition;
 
    public Food(String name, int nutrition) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Food name cannot be empty.");
        }
        if (nutrition < 0 || nutrition > 100) {
            throw new IllegalArgumentException("Nutrition value must be between 0 and 100.");
        }
        this.name      = name;
        this.nutrition = nutrition;
    }
 
    public String getName()     { return name; }
    public int    getNutrition(){ return nutrition; }
}
