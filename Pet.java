package virtualpetsimulator;
 
public abstract class Pet { // ENCAPSULATION
 
    private String name;
    private int hunger;
    private int happiness;
    private int energy;
    private int age;
    private int ageTick;      // internal counter so age increments slowly
    private String stage;
 
    // How many timer ticks (seconds) before each stat drops by 1
    private static final int DECAY_INTERVAL = 4;   // stats drop every 4 s
    private static final int AGE_INTERVAL   = 6;   // age increments every 6 s
 
    private int decayTick = 0;
    private int ageTickCounter = 0;
 
    public Pet(String name) {
        this.name      = name;
        this.hunger    = 80;
        this.happiness = 80;
        this.energy    = 80;
        this.age       = 0;
        this.stage     = "Baby";
    }
 
    /**
     * Called every timer tick (1 second).
     * Stats and age are updated at their own slower cadences.
     */
    public void updateStats() {
        decayTick++;
        ageTickCounter++;
 
        if (decayTick >= DECAY_INTERVAL) {
            decayTick = 0;
            hunger    = Math.max(0, hunger    - 1);
            happiness = Math.max(0, happiness - 1);
            energy    = Math.max(0, energy    - 1);
        }
 
        if (ageTickCounter >= AGE_INTERVAL) {
            ageTickCounter = 0;
            age++;
            evolve();
        }
    }
 
    private void evolve() {
        if      (age >= 60) stage = "Adult";
        else if (age >= 30) stage = "Teen";
        else                stage = "Baby";
    }
 
    public boolean isAlive() {
        return hunger > 0 && happiness > 0 && energy > 0;
    }
 
    // ── Actions ──────────────────────────────────────────
 
    /**
     * Feed the pet with a specific food item.
     * Throws IllegalArgumentException if the food is not valid for this pet type.
     */
    public void feed(String foodName, int nutritionValue) {
        if (!isValidFood(foodName)) {
            throw new IllegalArgumentException(
                "\"" + foodName + "\" is not suitable food for a " + getPetType() + "!");
        }
        hunger = Math.min(100, hunger + nutritionValue);
    }
 
    /** Checks whether the given food name is in this pet's food list. */
    public boolean isValidFood(String foodName) {
        for (String f : getFoods()) {
            if (f.equalsIgnoreCase(foodName)) return true;
        }
        return false;
    }
 
    public void play() {
        happiness = Math.min(100, happiness + 15);
        energy    = Math.max(0,  energy    - 10);
    }
 
    public void walk() {
        happiness = Math.min(100, happiness + 10);
        energy    = Math.max(0,  energy    - 15);
        hunger    = Math.max(0,  hunger    - 5);
    }
 
    public void sleep() {
        energy = Math.min(100, energy + 25);
    }
 
    // ── Abstract ─────────────────────────────────────────
 
    public abstract String[] getFoods(); // ABSTRACTION
    public abstract String   getPetType();
 
    // ── Getters ──────────────────────────────────────────
 
    public String getName()      { return name; }
    public int    getHunger()    { return hunger; }
    public int    getHappiness() { return happiness; }
    public int    getEnergy()    { return energy; }
    public int    getAge()       { return age; }
    public String getStage()     { return stage; }
}