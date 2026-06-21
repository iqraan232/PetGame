package virtualpetsimulator;
 
public class Dog extends Pet { // INHERITANCE
 
    public Dog(String name) {
        super(name);
    }
 
    @Override
    public String[] getFoods() {
        return new String[]{
            "Bone",
            "Chicken",
            "Meat"
        };
    }
 
    @Override
    public String getPetType() {
        return "dog";
    }
}
