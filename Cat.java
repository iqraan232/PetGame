package virtualpetsimulator;

public class Cat extends Pet {

    public Cat(String name) {
        super(name);
    }

    @Override
    public String[] getFoods() {

        return new String[]{
            "Fish",
            "Milk",
            "Chicken"
        };
    }

    @Override
    public String getPetType() {
        return "cat";
    }
}