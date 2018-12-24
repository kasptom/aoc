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
    private String ARMY_GROUP_REGEX = "([0-9]+) units each with ([0-9]+) hit points \\(([a-z,;\\s&&[^()]]+)\\) with an attack that does ([0-9]+) ([a-z]+) damage at initiative ([0-9]+)";
    private Pattern pattern = Pattern.compile(ARMY_GROUP_REGEX);

    private Army immuneArmy = new Army("immune");
    private Army infectionArmy = new Army("infection");


    private HashMap<Integer, BattleGroup> targets = new HashMap<>();
    private HashSet<Integer> occupiedTargers = new HashSet<>();
    private HashMap<Integer, BattleGroup> preparedToAttack = new HashMap<>();

    private boolean battleIsOver = false;

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
        System.out.println("starting the battle");

        while (!infectionArmy.isDefeated() || !immuneArmy.isDefeated()) {
            setTargets();
            printAttackPlan();
            attack();
//            printAttackResult();
        }
    }

    private void printAttackPlan() {
        for (Integer attackingId : targets.keySet()) {
            BattleGroup beingAttacked = targets.get(attackingId);
            System.out.printf("%d would deal %d %d damage\n",
                    attackingId, beingAttacked.id, beingAttacked.calculateDamage(preparedToAttack.get(attackingId)));
        }
    }

    private void attack() {
        for (int i=0; i < preparedToAttack.size(); i++) {
            BattleGroup attacking = preparedToAttack.values().stream()
                    .filter(BattleGroup::isAlive)
                    .max(Comparator.comparingInt(BattleGroup::getInitiative))
                    .orElse(null);
            if (attacking != null) {
                attacking.attack();
            }
        }
    }

    private void setTargets() {
        targets.clear();
        occupiedTargers.clear();
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
                i = loadGroupsToArmy(lines, i, immuneArmy.groups);
            } else if (line.startsWith("Infection:")) {
                i++;
                i = loadGroupsToArmy(lines, i, infectionArmy.groups);
            } else {
                i++;
            }
        }

        immuneArmy.setEnemy(infectionArmy);
        infectionArmy.setEnemy(immuneArmy);
    }

    private int loadGroupsToArmy(List<String> lines, int i, List<BattleGroup> battleGroups) {
        while (i < lines.size() && !lines.get(i).isEmpty()) {
            BattleGroup group = createGroup(i, lines.get(i));
            battleGroups.add(group);
            i++;
        }

        battleGroups.sort(Comparator.comparingInt(g -> g.initiative));

        return i;
    }

    private BattleGroup createGroup(int battleGroupId, String groupData) {
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


            return new BattleGroup(battleGroupId, unitsCount, hitPoints, immuneTo, weakTo, attackPoints, attackType, initiative);
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

    class Army {
        String name;
        List<BattleGroup> groups;
        private Army enemy;

        public Army(String name) {
            this.name = name;
            this.groups = new ArrayList<>();
        }

        void setEnemy(Army enemy) {
            this.enemy = enemy;
        }

        void selectTargets() {
            if (groups.stream().noneMatch(BattleGroup::isAlive)) {
                battleIsOver = true;
                return;
            }

            for (int i = 0; i < groups.stream().filter(BattleGroup::isAlive).count(); i++) {
                BattleGroup attackingGroup = getAttackingBattleGroup();
                if (attackingGroup == null) continue;

                BattleGroup enemyGroupToHit = getEnemyGroup(attackingGroup);
                if (enemyGroupToHit == null) continue;

                targets.put(attackingGroup.id, enemyGroupToHit);
                occupiedTargers.add(enemyGroupToHit.id);
                preparedToAttack.put(attackingGroup.id, attackingGroup);
            }
        }

        private BattleGroup getEnemyGroup(BattleGroup attackingGroup) {
            int maxDamageToDeal = enemy.groups.stream()
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
        private int id;

        private int unitsCount;
        private int hitPoints;
        private List<String> immuneTo;
        private List<String> weakTo;
        private int attackPoints;
        private String attackType;
        private int initiative;

        List<BattleUnit> units;

        BattleGroup(int id, int unitsCount, int hitPoints, List<String> immuneTo, List<String> weakTo, int attackPoints, String attackType, int initiative) {
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
            enemyGroupToAttack.receiveDamage(this);
        }

        private void receiveDamage(BattleGroup battleGroup) {
            final int[] damage = {calculateDamage(battleGroup)};

            this.units.stream().sorted((u1, u2) -> Integer.compare(u2.hitPoints, u1.hitPoints))
                    .forEach(unit -> {
                        unit.hitPoints -= Math.min(unit.hitPoints, damage[0]);
                        damage[0] -= Math.min(unit.hitPoints, damage[0]);
                    });
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
