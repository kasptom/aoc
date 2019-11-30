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

    int BOOST = 30;

    private HashMap<String, BattleGroup> attackerToDefending = new HashMap<>();
    private HashSet<String> occupiedTargets = new HashSet<>();
    private TreeMap<String, BattleGroup> preparedToAttack = new TreeMap<>();
    private HashSet<String> notAttacking = new HashSet<>();
    private HashSet<String> attackNames = new HashSet<>();

    @Override
    public String getFileName() {
//        return "input_24_simple.txt";
        return "aoc2018/input_24.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        loadArmies(lines);
        startBattle();
        printArmiesState(true);
    }

    private void clearArmies() {
        immuneArmy = new Army("GOD");
        infectionArmy = new Army("BAD");
    }

    boolean isDamageGiven = true;

    private void startBattle() {
        System.out.println("starting the battle");
        while (infectionArmy.isFighting() && immuneArmy.isFighting() && isDamageGiven) {
            isDamageGiven = false;
//            printArmiesState(false);

            setTargets();
//            printAttackPlan();

            attack();
//            System.out.println("--------------------------------\n");
        }
    }

    private void printArmiesState(boolean showSummary) {
        printGroupState(immuneArmy, showSummary);
        printGroupState(infectionArmy, showSummary);
        System.out.printf("BOOST: %d\n", BOOST);
    }

    private void printGroupState(Army army, boolean showSummary) {
        System.out.println(army.name);
        if (army.groups.isEmpty()) {
            System.out.println("No groups remain");
            return;
        }

        int unitsSum = 0;
        for (BattleGroup group : army.groups) {
            int unitsCount = group.units.size();
            unitsSum += unitsCount;
            if (unitsCount > 0) {
                System.out.printf("Group %s contains %d units\n", group.id, unitsCount);
            }
        }

        if (showSummary) {
            System.out.printf("%d units\n", unitsSum);
        }
    }

    private void printAttackPlan() {
        preparedToAttack
                .values()
                .stream()
                .sorted(Comparator.comparing(gr -> gr.id))
                .forEach(gr -> {
                    BattleGroup defending = attackerToDefending.get(gr.id);
                    System.out.printf("%s would deal %s %d damage\n",
                            gr.id, defending.id, defending.calculateDamageFrom(gr));
                });
    }

    private void attack() {
        while (!preparedToAttack.isEmpty()) {
            BattleGroup attackingGroup = preparedToAttack.values().stream()
                    .max(Comparator.comparingInt(BattleGroup::getInitiative))
                    .orElse(null);

            if (attackingGroup == null) throw new RuntimeException("Attacking group cannot be null");

            if (!attackingGroup.isAlive()) throw new RuntimeException("Attacking group cannot be dead");

            attackingGroup.dealDamage();

            preparedToAttack.remove(attackingGroup.id);
            preparedToAttack.values().stream().filter(gr -> !gr.isAlive()).findFirst().ifPresent(possiblyDead -> preparedToAttack.remove(possiblyDead.id));
        }
    }

    private void setTargets() {
        attackerToDefending.clear();
        occupiedTargets.clear();
        preparedToAttack.clear();
        notAttacking.clear();

        infectionArmy.selectTargets();
        immuneArmy.selectTargets();

        for (BattleGroup group : immuneArmy.groups) {
            if (preparedToAttack.containsKey(group.id) && notAttacking.contains(group.id)) {
                throw new RuntimeException("Group cannot be prepared to deal damage and not attacking at once");
            }
        }
    }

    private void loadArmies(List<String> lines) {
        String line;
        int i = 0;
        while (i < lines.size()) {
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

        printAttackNames();

        immuneArmy.setEnemy(infectionArmy);
        infectionArmy.setEnemy(immuneArmy);
    }

    private void printAttackNames() {
        System.out.println("atack names: ");
        attackNames.stream().sorted().forEach(System.out::println);
        System.out.println("-----------------------------\n");
    }

    private int loadGroupsToArmy(List<String> lines, int i, Army army) {
        int boost = army.name.equals("GOD") ? BOOST : 0;
        while (i < lines.size() && !lines.get(i).isEmpty()) {

            BattleGroup group = createGroup(army.name + " #" + (army.groups.size() + 1), lines.get(i), boost);
            army.groups.add(group);
            i++;
        }

        return i;
    }

    private BattleGroup createGroup(String battleGroupId, String groupData, int boost) {
        // 989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an dealDamage that does 25 slashing damage at initiative 3
        Matcher matcher = pattern.matcher(groupData);

        if (matcher.find()) {
            int unitsCount = Integer.parseInt(matcher.group(1));
            int hitPoints = Integer.parseInt(matcher.group(2));
            String immuneWeakStr = matcher.group(3);
            int attackPoints = Integer.parseInt(matcher.group(4)) + boost;
            String attackType = matcher.group(5);

            attackNames.add(attackType);

            int initiative = Integer.parseInt(matcher.group(6));

            List<String> immuneTo = getImmuneOrWeakList(immuneWeakStr, "immune to");
            List<String> weakTo = getImmuneOrWeakList(immuneWeakStr, "weak to");

            attackNames.addAll(immuneTo);
            attackNames.addAll(weakTo);

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
        boolean immuneWon = false;
        while(!immuneWon) {
            clearArmies();
            solvePartOne(lines);
            if (immuneArmy.isFighting() || !infectionArmy.isFighting()) {
                immuneWon = true;
            } else {
                BOOST++;
            }
        }

        System.out.println(String.format("min boost: %d", BOOST));
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
            groups.removeAll(groups.stream().filter(gr -> !gr.isAlive()).collect(Collectors.toList()));

            if (groups.isEmpty()) {
                return;
            }

            for (int i = 0; i < groups.size(); i++) {
                BattleGroup attackingGroup = getNextAttackingGroup();

                if (attackingGroup == null) {
                    continue;
                }

                BattleGroup enemyGroupToHit = getBestEnemyGroup(attackingGroup);
                if (enemyGroupToHit == null) {
                    notAttacking.add(attackingGroup.id);
                    continue;
                }

                attackerToDefending.put(attackingGroup.id, enemyGroupToHit);

                if (occupiedTargets.contains(enemyGroupToHit.id)) {
                    String alreadyAttackingId = attackerToDefending.keySet().stream().filter(attackerId -> attackerToDefending.get(attackerId).id.equals(enemyGroupToHit.id)).findFirst().orElse(null);
                    alreadyAttackingId = alreadyAttackingId != null ? alreadyAttackingId : "NONE";
                    throw new RuntimeException(String.format("[%s] Enemy %s is already attacked by %s", attackingGroup.id, enemyGroupToHit.id, alreadyAttackingId));
                }

                occupiedTargets.add(enemyGroupToHit.id);
                preparedToAttack.put(attackingGroup.id, attackingGroup);
            }
        }

        private BattleGroup getBestEnemyGroup(BattleGroup attackingGroup) {
            if (enemy.groups.stream().anyMatch(gr -> !gr.isAlive())) {
                throw new RuntimeException("Enemy still has dead groups");
            }

            long maxDamageToDeal = enemy.groups.stream()
                    .filter(gr -> !occupiedTargets.contains(gr.id))
                    .map(gr -> gr.calculateDamageFrom(attackingGroup))
                    .max(Long::compareTo)
                    .orElse(0L);

            if (maxDamageToDeal == 0) return null;

            List<BattleGroup> enemyGroupsWithMaxDamage = enemy.groups
                    .stream()
                    .filter(gr -> gr.calculateDamageFrom(attackingGroup) == maxDamageToDeal
                            && !occupiedTargets.contains(gr.id))
                    .collect(Collectors.toList());

            if (enemyGroupsWithMaxDamage.size() > 1) {
                long maxEffectivePowerOfEnemyGroupToAttack = enemyGroupsWithMaxDamage
                        .stream()
                        .map(BattleGroup::getEffectivePower)
                        .max(Long::compareTo)
                        .orElse(0L);

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

        private BattleGroup getNextAttackingGroup() {
            if (groups.stream().anyMatch(gr -> !gr.isAlive()))
                throw new RuntimeException("Found dead group");

            long maxEffectivePower = groups.stream()
                    .filter(gr -> !notAttacking.contains(gr.id)
                            && !preparedToAttack.keySet().contains(gr.id))
                    .map(BattleGroup::getEffectivePower)
                    .max(Long::compareTo)
                    .orElse(0L);

            if (maxEffectivePower == 0) {
                return null;
//                throw new RuntimeException("No max effective power found");
            }

            List<BattleGroup> groupsWithMaxEffectivePower = groups
                    .stream()
                    .filter(gr -> gr.isAlive()
                            && !preparedToAttack.keySet().contains(gr.id)
                            && !notAttacking.contains(gr.id)
                            && gr.getEffectivePower() == maxEffectivePower)
                    .collect(Collectors.toList());

            BattleGroup attacking = groupsWithMaxEffectivePower.stream()
                    .max(Comparator.comparingInt(BattleGroup::getInitiative))
                    .orElse(null);

            if (attacking == null) throw new RuntimeException("No attacking group found");
            return attacking;
        }

        boolean isFighting() {
            return groups.stream().anyMatch(BattleGroup::isAlive);
        }
    }

    class BattleGroup {
        private String id;
        private List<String> immuneTo;
        private List<String> weakTo;
        private long attackPoints;
        private String attackType;
        private int initiative;

        List<BattleUnit> units;

        BattleGroup(String id, int unitsCount, int hitPoints, List<String> immuneTo, List<String> weakTo, int attackPoints, String attackType, int initiative) {
            this.id = id;
            this.immuneTo = immuneTo;
            this.weakTo = weakTo;
            this.attackPoints = attackPoints;
            this.attackType = attackType;
            this.initiative = initiative;

            if (initiative <= 0) {
                throw new RuntimeException("Initiative must be grater than zero");
            }

            if (attackPoints <= 0) {
                throw new RuntimeException("Attack point must be greater than zero");
            }

            this.units = new ArrayList<>();
            for (int i = 0; i < unitsCount; i++) {
                this.units.add(new BattleUnit(hitPoints));
            }
        }

        boolean isAlive() {
            return units.stream().anyMatch(unit -> unit.hitPoints > 0);
        }

        long getEffectivePower() {
            long power = units.stream().filter(unit -> unit.hitPoints > 0).count() * this.attackPoints;
            if (power == 0) throw new RuntimeException("effective power is 0");
            return power;
        }

        int getInitiative() {
            return initiative;
        }

        long calculateDamageFrom(BattleGroup attackingGroup) {
            long damageMultiplier = 1;
            if (immuneTo.contains(attackingGroup.attackType)) {
                damageMultiplier = 0;
            } else if (weakTo.contains(attackingGroup.attackType)) {
                damageMultiplier = 2;
            }

            return damageMultiplier * attackingGroup.getEffectivePower();
        }

        void dealDamage() {
            BattleGroup enemyGroupToAttack = attackerToDefending.remove(this.id);
            int unitsKilled = enemyGroupToAttack.receiveDamageFrom(this);
            if (unitsKilled > 0) {
                isDamageGiven = true;
//                System.out.printf("%s attacking %s, (I: %d) ", this.id, enemyGroupToAttack.id, this.getInitiative());
//                System.out.printf("killing %d", unitsKilled);

//                if (!enemyGroupToAttack.isAlive()) {
//                    System.out.print(" (none remaining)");
//                }

//                System.out.println();
            }
        }

        private int receiveDamageFrom(BattleGroup attackingGroup) {
            long damage = calculateDamageFrom(attackingGroup);

            if (damage == 0) throw new RuntimeException(
                    String.format("Group %s deals no positive damage to %s", attackingGroup.id, this.id));

            int unitsKilled = 0;

            for (BattleUnit unit : units) {
                if (unit.hitPoints <= damage) {
                    damage -= unit.hitPoints;
                    unit.hitPoints = 0;
                    unitsKilled++;
                }
            }

            units.removeAll(units.stream().filter(u -> u.hitPoints == 0).collect(Collectors.toList()));
            if (!isAlive()) {
                if (id.startsWith("BAD")) {
                    infectionArmy.groups.remove(this);
                } else {
                    immuneArmy.groups.remove(this);
                }
            }

            return unitsKilled;
        }
    }

    class BattleUnit {
        private int hitPoints;

        BattleUnit(int hitPoints) {
            this.hitPoints = hitPoints;
        }
    }
}
