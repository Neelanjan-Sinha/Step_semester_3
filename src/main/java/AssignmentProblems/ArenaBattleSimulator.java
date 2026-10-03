// Save as Main.java
interface Attackable {
    String attack();
    String attack(String weaponName);     // overload, resolved at compile time
}

interface Defendable {
    String defend();
}

abstract class GameCharacter {
    private static int counter = 1000;

    private final String characterId;

    protected GameCharacter() {
        counter++;
        this.characterId = "CH-" + counter;
    }

    public abstract String getSpecialMove();

    String getCharacterId() { return characterId; }
}

class Warrior extends GameCharacter implements Attackable, Defendable {
    private final String name;

    public Warrior(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is blank");
        this.name = name;
    }

    @Override
    public String attack() { return name + " strikes with a blade"; }

    @Override
    public String attack(String weaponName) {
        char first = Character.toLowerCase(weaponName.charAt(0));
        boolean vowel = "aeiou".indexOf(first) >= 0;
        return name + " strikes with " + (vowel ? "an " : "a ") + weaponName;
    }

    @Override
    public String defend() { return name + " raises a shield"; }

    @Override
    public String getSpecialMove() { return name + " unleashes Whirlwind Slash"; }
}

// Only Defendable - deliberately NOT a GameCharacter
class Trap implements Defendable {
    private final String trapType;

    public Trap(String trapType) {
        if (trapType == null || trapType.isBlank()) throw new IllegalArgumentException("trapType is blank");
        this.trapType = trapType;
    }

    @Override
    public String defend() { return trapType + " triggers automatically"; }
}

public class Main {
    static void resolveDefense(Defendable[] combatants) {
        for (Defendable d : combatants) {
            System.out.println(d.defend());
        }
    }

    public static void main(String[] args) {
        Warrior w = new Warrior("Kael");
        System.out.println(w.attack());               // Kael strikes with a blade
        System.out.println(w.attack("Iron Sword"));   // Kael strikes with an Iron Sword
        System.out.println(w.defend());               // Kael raises a shield
        System.out.println(w.getSpecialMove());       // Kael unleashes Whirlwind Slash

        Trap t = new Trap("Spike Pit");
        System.out.println(t.defend());               // Spike Pit triggers automatically

        resolveDefense(new Defendable[]{w, t});
    }
}
