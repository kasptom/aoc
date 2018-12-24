import java.util.*;
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
    private String ARMY_GROUP_REGEX = "([0-9]+) units each with ([0-9]+) hit points [(]?([a-z,;\\s&&[^()]]*)[)]?[ ]?with an attack that does ([0-9]+) ([a-z]+) damage at initiative ([0-9]+)";
    private Pattern pattern = Pattern.compile(ARMY_GROUP_REGEX);

    private Army immuneArmy = new Army("GOD");
    private Army infectionArmy = new Army("BAD");


    private HashMap<String, BattleGroup> targets = new HashMap<>();
    private HashSet<String> occupiedTargets = new HashSet<>();
    private HashMap<String, BattleGroup> preparedToAttack = new HashMap<>();

    @Override
    public String getFileName() {
        return "input_24.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadArmies(lines);
        startBattle();

        printArmiesState(true);
    }

    private void startBattle() {
        System.out.println("starting the battle");

        while (!infectionArmy.isDefeated() && !immuneArmy.isDefeated()) {
            printArmiesState(false);

            setTargets();
            printAttackPlan();

            attack();
            System.out.println("--------------------------------\n");
        }
    }

    private void printArmiesState(boolean showSummary) {
        printGroupState(immuneArmy, showSummary);
        printGroupState(infectionArmy, showSummary);
        System.out.println();
    }

    private void printGroupState(Army army, boolean showSummary) {
        System.out.println(army.name);
        if (army.groups.stream().noneMatch(BattleGroup::isAlive)) {
            System.out.println("No groups remain");
        } else {
            final int[] unitsSum = {0};
            army.groups.forEach(gr -> {
                int unitsCount = (int) gr.units.stream().filter(u -> u.hitPoints != 0).count();
                unitsSum[0] += unitsCount;
                if (unitsCount > 0) {
                    System.out.printf("Group %s contains %d units\n", gr.id, unitsCount);
                }
            });
            if (showSummary) {
                System.out.printf("%d units\n", unitsSum[0]);
            }
        }
    }

    private void printAttackPlan() {
        HashSet<String> printed = new HashSet<>();
        for (int i = 0; i < preparedToAttack.size(); i++) {
            BattleGroup attacking = preparedToAttack.values().stream()
                    .filter(gr -> !printed.contains(gr.id))
                    .filter(BattleGroup::isAlive)
                    .max(Comparator.comparingInt(BattleGroup::getInitiative)).orElse(null);
            BattleGroup beingAttacked = targets.get(attacking.id);

            System.out.printf("%s would deal %s %d damage\n",
                    attacking.id, beingAttacked.id, beingAttacked.calculateDamage(attacking));
            printed.add(attacking.id);
        }
    }

    private void attack() {
        HashSet<String> finished = new HashSet<>();
        for (int i = 0; i < preparedToAttack.size(); i++) {
            BattleGroup battleGroup = preparedToAttack.values().stream()
                    .filter(gr -> !finished.contains(gr.id))
                    .filter(BattleGroup::isAlive)
                    .max(Comparator.comparingInt(BattleGroup::getInitiative))
                    .orElse(null);

            if (battleGroup != null) {
                battleGroup.attack();
                finished.add(battleGroup.id);
            }
        }
    }

    private void setTargets() {
        targets.clear();
        occupiedTargets.clear();
        preparedToAttack.clear();

        infectionArmy.selectTargets();
        immuneArmy.selectTargets();
    }

    private void loadArmies(List<String> lines) {
        String line;
        int i = 0;
        for (; i < lines.size(); ) {
            line = lines.get(i);
            if (line.startsWith("Immune System:")) {
                i++;
                i = loadGroupsToArmy(lines, i, immuneArmy);
            } else if (line.startsWith("Infection:")) {
                i++;
                i = loadGroupsToArmy(lines, i, infectionArmy);
            } else {
                i++;
            }
        }

        immuneArmy.setEnemy(infectionArmy);
        infectionArmy.setEnemy(immuneArmy);
    }

    private int loadGroupsToArmy(List<String> lines, int i, Army army) {
        while (i < lines.size() && !lines.get(i).isEmpty()) {
            BattleGroup group = createGroup(army.name + " #" + (army.groups.size() + 1), lines.get(i));
            army.groups.add(group);
            i++;
        }

        army.groups.sort(Comparator.comparingInt(g -> g.initiative));

        return i;
    }

    private BattleGroup createGroup(String battleGroupId, String groupData) {
        // 989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3
        Matcher matcher = pattern.matcher(groupData);

        if (matcher.find()) {
            int unitsCount = Integer.parseInt(matcher.group(1));
            int hitPoints = Integer.parseInt(matcher.group(2));
            String immuneWeakStr = matcher.group(3);
            int attackPoints = Integer.parseInt(matcher.group(4));
            String attackType = matcher.group(5);
            int initiative = Integer.parseInt(matcher.group(6));

            List<String> immuneTo = getImmuneOrWeakList(immuneWeakStr, "immune to");
            List<String> weakTo = getImmuneOrWeakList(immuneWeakStr, "weak to");


            return new BattleGroup(battleGroupId, unitsCount, hitPoints, immuneTo, weakTo, attackPoints, attackType, initiative);
        }

        throw new RuntimeException(String.format("invalid battle group data %s", groupData));
    }

    private List<String> getImmuneOrWeakList(String immuneWeakStr, String prefix) {
        String[] attacks = immuneWeakStr.split(";");
        String attackStr = Arrays.stream(attacks).filter(str -> str.trim().startsWith(prefix)).findFirst().orElse(null);
        attackStr = attackStr != null ? attackStr.replace(prefix, "") : null;
        return attackStr == null ? new ArrayList<>() :
                Arrays.stream(attackStr.split(",")).map(String::trim).collect(Collectors.toList());
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    class Army {
        String name;
        List<BattleGroup> groups;
        private Army enemy;

        Army(String name) {
            this.name = name;
            this.groups = new ArrayList<>();
        }

        void setEnemy(Army enemy) {
            this.enemy = enemy;
        }

        void selectTargets() {
            if (groups.stream().noneMatch(BattleGroup::isAlive)) {
                return;
            }

            for (int i = 0; i < groups.stream().filter(BattleGroup::isAlive).count(); i++) {
                BattleGroup attackingGroup = getAttackingBattleGroup();
                if (attackingGroup == null) continue;

                BattleGroup enemyGroupToHit = getEnemyGroup(attackingGroup);
                if (enemyGroupToHit == null) continue;

                targets.put(attackingGroup.id, enemyGroupToHit);
                occupiedTargets.add(enemyGroupToHit.id);
                preparedToAttack.put(attackingGroup.id, attackingGroup);
            }
        }

        private BattleGroup getEnemyGroup(BattleGroup attackingGroup) {
            int maxDamageToDeal = enemy.groups.stream()
                    .filter(BattleGroup::isAlive)
                    .filter(gr -> !occupiedTargets.contains(gr.id))
                    .map(gr -> gr.calculateDamage(attackingGroup))
                    .max(Integer::compareTo)
                    .orElse(0);

            List<BattleGroup> enemyGroupsWithMaxDamage = enemy.groups
                    .stream()
                    .filter(gr -> gr.calculateDamage(attackingGroup) == maxDamageToDeal)
                    .collect(Collectors.toList());

            if (enemyGroupsWithMaxDamage.size() > 1) {
                int maxEffectivePowerOfEnemyGroupToAttack = enemyGroupsWithMaxDamage
                        .stream()
                        .map(BattleGroup::getEffectivePower)
                        .max(Integer::compareTo)
                        .orElse(0);

                enemyGroupsWithMaxDamage = enemyGroupsWithMaxDamage
                        .stream()
                        .filter(gr -> gr.getEffectivePower() == maxEffectivePowerOfEnemyGroupToAttack)
                        .collect(Collectors.toList());
            }

            if (enemyGroupsWithMaxDamage.size() > 1) {
                int maxInitiative = enemyGroupsWithMaxDamage
                        .stream()
                        .map(BattleGroup::getInitiative)
                        .max(Integer::compareTo)
                        .orElse(0);

                enemyGroupsWithMaxDamage = enemyGroupsWithMaxDamage
                        .stream()
                        .filter(gr -> gr.initiative == maxInitiative)
                        .collect(Collectors.toList());
            }

            BattleGroup enemyGroupToHit = null;
            if (enemyGroupsWithMaxDamage.size() == 1) {
                enemyGroupToHit = enemyGroupsWithMaxDamage.get(0);
            }
            return enemyGroupToHit;
        }

        private BattleGroup getAttackingBattleGroup() {
            int maxEffectivePower = groups.stream()
                    .filter(gr -> gr.isAlive() && !preparedToAttack.keySet().contains(gr.id))
                    .map(BattleGroup::getEffectivePower)
                    .max(Integer::compareTo)
                    .orElse(0);

            List<BattleGroup> groupsWithMaxEffectivePower = groups
                    .stream()
                    .filter(gr -> gr.getEffectivePower() == maxEffectivePower)
                    .collect(Collectors.toList());

            return groupsWithMaxEffectivePower.stream()
                    .max(Comparator.comparingInt(BattleGroup::getInitiative))
                    .orElse(null);
        }

        boolean isDefeated() {
            return groups.stream().noneMatch(BattleGroup::isAlive);
        }
    }

    class BattleGroup {
        private String id;

        private int unitsCount;
        private int hitPoints;
        private List<String> immuneTo;
        private List<String> weakTo;
        private int attackPoints;
        private String attackType;
        private int initiative;

        List<BattleUnit> units;

        BattleGroup(String id, int unitsCount, int hitPoints, List<String> immuneTo, List<String> weakTo, int attackPoints, String attackType, int initiative) {
            this.id = id;
            this.unitsCount = unitsCount;
            this.hitPoints = hitPoints;
            this.immuneTo = immuneTo;
            this.weakTo = weakTo;
            this.attackPoints = attackPoints;
            this.attackType = attackType;
            this.initiative = initiative;

            this.units = new ArrayList<>();
            for (int i = 0; i < unitsCount; i++) {
                this.units.add(new BattleUnit(hitPoints));
            }
        }

        boolean isAlive() {
            return units.stream().anyMatch(unit -> unit.hitPoints > 0);
        }

        int getEffectivePower() {
            return (int) (units.stream().filter(unit -> unit.hitPoints > 0).count() * this.attackPoints);
        }

        int getInitiative() {
            return initiative;
        }

        int calculateDamage(BattleGroup attackingGroup) {
            int damageMultiplier = immuneTo.contains(attackingGroup.attackType) ? 0 :
                    weakTo.contains(attackingGroup.attackType) ? 2 :
                            1;

            return damageMultiplier * attackingGroup.getEffectivePower();
        }

        void attack() {
            BattleGroup enemyGroupToAttack = targets.get(this.id);
            int unitsKilled = enemyGroupToAttack.receiveDamage(this);
            if (unitsKilled > 0) {
                System.out.printf("%s attacking %s, (I: %d) ", this.id, enemyGroupToAttack.id, this.getInitiative());
                System.out.printf("killing %d\n", unitsKilled);
            }
        }

        private int receiveDamage(BattleGroup attackingGroup) {
            final int[] damage = {calculateDamage(attackingGroup)};

            final int[] unitsKilled = {0};
            this.units.stream()
                    .filter(u -> u.hitPoints != 0)
                    .sorted(Comparator.comparingInt(u2 -> u2.hitPoints))
                    .forEach(unit -> {
                        if (unit.hitPoints <= damage[0]) {
                            damage[0] -= unit.hitPoints;
                            unit.hitPoints = 0;
                            unitsKilled[0]++;
                        }
                    });
            return unitsKilled[0];
        }
    }

    class BattleUnit {
        private int hitPoints;

        public BattleUnit(int hitPoints) {
            this.hitPoints = hitPoints;
        }
    }

    class DamageComparator implements Comparator<BattleGroup> {

        private final BattleGroup attackingGroup;

        public DamageComparator(BattleGroup attackingGroup) {
            this.attackingGroup = attackingGroup;
        }

        @Override
        public int compare(BattleGroup group1, BattleGroup group2) {
            return 0;
        }
    }
}
