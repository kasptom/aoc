import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 implements IAocTask {

/*
Immune System:
17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3

Infection:
801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4
 */
    private String ARMY_GROUP_REGEX = "([0-9]+) units each with ([0-9]+) hit points \\(([a-z,;\\s&&[^()]]+)\\) with an attack that does ([0-9]+) ([a-z]+) damage at initiative ([0-9]+)";
    private Pattern pattern = Pattern.compile(ARMY_GROUP_REGEX);

    private List<BattleGroup> immuneArmy = new ArrayList<>();
    private List<BattleGroup> infectionArmy = new ArrayList<>();

    @Override
    public String getFileName() {
        return "input_24_simple.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadArmies(lines);
        startBattle();

        printBattleResult();
    }

    private void printBattleResult() {

    }

    private void startBattle() {
        System.out.println("startint the battle");
    }

    private void loadArmies(List<String> lines) {
        String line;
        int i = 0;
        for (; i < lines.size(); ) {
            line = lines.get(i);
            if (line.startsWith("Immune System:")) {
                i++;
                i = createArmy(lines, i, this.immuneArmy);
            } else if (line.startsWith("Infection:")) {
                i++;
                i = createArmy(lines, i, this.infectionArmy);
            } else {
                i++;
            }
        }
    }

    private int createArmy(List<String> lines, int i, List<BattleGroup> army) {
        while (i < lines.size() && !lines.get(i).isEmpty()) {
            BattleGroup group = createGroup(lines.get(i));
            army.add(group);
            i++;
        }
        return i;
    }

    private BattleGroup createGroup(String groupData) {
        // 989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3
        Matcher matcher = pattern.matcher(groupData);

        if (matcher.find()) {
            System.out.println(matcher.group(0));
            int unitsCount = Integer.parseInt(matcher.group(1));
            int hitPoints = Integer.parseInt(matcher.group(2));
            String immuneWeakStr = matcher.group(3);
            int attackPoints = Integer.parseInt(matcher.group(4));
            String attackType = matcher.group(5);
            int initiative = Integer.parseInt(matcher.group(6));

            List<String> immuneTo = getImmuneOrWeakList(immuneWeakStr, "immune to");
            List<String> weakTo = getImmuneOrWeakList(immuneWeakStr, "weak to");



            return new BattleGroup(unitsCount, hitPoints, immuneTo, weakTo, attackPoints, attackType, initiative);
        }

        return null;
    }

    private List<String> getImmuneOrWeakList(String immuneWeakStr, String prefix) {
        String[] immuneWeakParts = immuneWeakStr.split(";");
        String immuneString = Arrays.stream(immuneWeakParts).filter(str -> str.startsWith(prefix)).findFirst().orElse(null);
        immuneString = immuneString != null ? immuneString.replace(prefix, "") : null;
        List<String> immuneTo = immuneString == null ? new ArrayList<>() :
                Arrays.stream(immuneString.split(",")).map(String::trim).collect(Collectors.toList());
        return immuneTo;
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    class BattleGroup {

        private int unitsCount;
        private int hitPoints;
        private List<String> immuneTo;
        private List<String> weakTo;
        private int attackPoints;
        private String attackType;
        private int initiative;

        List<BattleUnit> units;

        BattleGroup(int unitsCount, int hitPoints, List<String> immuneTo, List<String> weakTo, int attackPoints, String attackType, int initiative) {
            this.unitsCount = unitsCount;
            this.hitPoints = hitPoints;
            this.immuneTo = immuneTo;
            this.weakTo = weakTo;
            this.attackPoints = attackPoints;
            this.attackType = attackType;
            this.initiative = initiative;

            this.units = new ArrayList<>();
            for (int i = 0; i<unitsCount; i++) {
                this.units.add(new BattleUnit(hitPoints));
            }
        }
    }

    class BattleUnit {
        private int hitPoints;

        public BattleUnit(int hitPoints) {
            this.hitPoints = hitPoints;
        }
    }
}
