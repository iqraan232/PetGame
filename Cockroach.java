package virtualpetsimulator;
 
public class Cockroach extends Pet {
 
    public Cockroach(String name) {
        super(name);
    }
 
    @Override
    public String[] getFoods() {
        return new String[]{
            "Crumbs",
            "Rotting Fruit",
            "Sugar"
        };
    }
 
    @Override
    public String getPetType() {
        return "cockroach";
    }
}
 