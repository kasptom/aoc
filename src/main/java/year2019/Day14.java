package year2019;

import aoc.IAocTask;

import java.awt.dnd.InvalidDnDOperationException;
import java.util.*;
import java.util.stream.Collectors;

public class Day14 implements IAocTask {
    private HashMap<ChemicalReagent, List<ChemicalReagent>> productToSubstrates;
    private HashMap<ChemicalReagent, Integer> reagentsToInDegree;
    long availableOre = 1000000000000L;
    long oreCount;
    long fuelNeeded;
    private boolean isPrintEnabled;

    @Override
    public String getFileName() {
        return "aoc2019/input_14.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        isPrintEnabled = true;
        fuelNeeded = 1;
        calculateOreNeeded(lines);
        System.out.println(oreCount);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
//        fuelNeeded = 82892753 ; // small_3
//        fuelNeeded = 5586022  ; // small_4
//        fuelNeeded = 460664  ; // small_5
        long oreCountForSingleFuel = oreCount;
        long estimatedFuel = availableOre / oreCountForSingleFuel;
        long lowerFuelBound = 1;
        long upperFuelBound = 2 * estimatedFuel;

        isPrintEnabled = false;
        fuelNeeded = estimatedFuel;
        long prevOreCount;

        while (true) {
            prevOreCount = oreCount;
            calculateOreNeeded(lines);
            System.out.printf("Fuel=%20d, Ore=%d%n", fuelNeeded, oreCount);
            if (prevOreCount == oreCount) {
               break; // found max possible ore
            }
            if (oreCount < availableOre) {
                lowerFuelBound = fuelNeeded;
            } else {
                upperFuelBound = fuelNeeded;
            }
            fuelNeeded = (lowerFuelBound + upperFuelBound) / 2;
        }

        System.out.printf("Max fuel (%d) to buy for 1e12 ORE: %d", fuelNeeded, prevOreCount);
    }

    private void calculateOreNeeded(List<String> lines) {
        productToSubstrates = loadRequirements(lines);
        reagentsToInDegree = getProductsWithInDegrees(productToSubstrates);
        if (isPrintEnabled) {
            System.out.println(productToSubstrates.size());
        }

        List<ChemicalReagent> chemicalsToChange = new ArrayList<>();
        chemicalsToChange.add(new ChemicalReagent("FUEL", fuelNeeded));

        while (reagentsToInDegree.keySet().size() > 0) {
            ChemicalReagent product = reagentsToInDegree.keySet().stream()
                    .filter(prod -> reagentsToInDegree.get(prod) == 0) // condition 1: product is not a substrate of any other chemical
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No product with inDegree == 0"));

            List<ChemicalReagent> substrates = productToSubstrates.get(product);
            if (isPrintEnabled) {
                System.out.printf("%n-----Substituting product %s -> %s%n", product, substrates);
            }

            List<ChemicalReagent> newChemicalsToChange = new ArrayList<>();
            for (ChemicalReagent chemical : chemicalsToChange) {
                List<ChemicalReagent> chemicalRequirements = getRequirements(chemical);
                newChemicalsToChange.addAll(chemicalRequirements);
            }
            newChemicalsToChange = mergeDuplicatedChemicals(newChemicalsToChange);
            chemicalsToChange = newChemicalsToChange;

            // decrement the inDegrees of the substrates
            productToSubstrates.remove(product);
            if (reagentsToInDegree.remove(product) == null)
                throw new RuntimeException(String.format("Could not find %s to remove", product));
            if (substrates != null && !product.name.equals("ORE")) {
                substrates.stream().filter(subs -> !subs.name.equals("ORE"))
                        .forEach(substrate -> reagentsToInDegree.put(substrate, reagentsToInDegree.get(substrate) - 1));
            } else throw new RuntimeException("Only ORE can have no substrates");
        }
        oreCount = chemicalsToChange.get(0).quantity;
    }

    private HashMap<ChemicalReagent, Integer> getProductsWithInDegrees(HashMap<ChemicalReagent, List<ChemicalReagent>> productToSubstrates) {
        HashMap<ChemicalReagent, Integer> productToInDegree = new HashMap<>();
        productToSubstrates.keySet().forEach(product -> productToInDegree.put(product, 0));

        for (ChemicalReagent product : productToSubstrates.keySet()) {
            updateInboundDegree(productToSubstrates, product, productToInDegree);
        }

        productToInDegree.put(new ChemicalReagent("FUEL", fuelNeeded), 0);
        return productToInDegree;
    }

    private void updateInboundDegree(HashMap<ChemicalReagent, List<ChemicalReagent>> productToSubstrates, ChemicalReagent product, HashMap<ChemicalReagent, Integer> productToInDegree) {
        List<ChemicalReagent> substrates = productToSubstrates.get(product);
        for (ChemicalReagent substrate : substrates) {
            productToSubstrates.keySet()
                    .stream()
                    .filter(v -> v.name.equals(substrate.name))
                    .forEach(v -> productToInDegree.put(v, productToInDegree.get(v) + 1));
        }
    }

    private List<ChemicalReagent> mergeDuplicatedChemicals(List<ChemicalReagent> newChemicalsToChange) {
        if (isPrintEnabled) {
            System.out.print("merging: " + newChemicalsToChange);
        }
        ArrayList<ChemicalReagent> merged = new ArrayList<>();
        HashSet<String> names = newChemicalsToChange.stream().map(chemical -> chemical.name).collect(Collectors.toCollection(HashSet::new));
        for (String name : names) {
            long sameChemicalsOverallQuantity = newChemicalsToChange.stream().filter(chem -> chem.name.equals(name))
                    .map(chem -> chem.quantity).reduce(this::checkAndSum).orElse(-1L);
            if (sameChemicalsOverallQuantity == -1L) throw new RuntimeException();
            merged.add(new ChemicalReagent(name, sameChemicalsOverallQuantity));
        }
        if (isPrintEnabled) {
            System.out.println("\n      -> " + merged);
        }
        return merged;
    }

    private Long checkAndSum(Long a, Long b) {
        if (Long.MAX_VALUE - a < b || Long.MAX_VALUE - b < a) {
            throw new RuntimeException("TOO BIG VALUES TO MERGE");
        }
        return a + b;
    }

    private List<ChemicalReagent> getRequirements(ChemicalReagent chemical) {
        List<ChemicalReagent> chemicalRequirements = new ArrayList<>();
        if (reagentsToInDegree.containsKey(chemical) && reagentsToInDegree.get(chemical) != 0) {
            chemicalRequirements.add(chemical);
            return chemicalRequirements;
        }

        List<ChemicalReagent> materials = productToSubstrates.get(chemical);

        ChemicalReagent product = productToSubstrates.keySet().stream().filter(prod -> prod.name.equals(chemical.name))
                .findFirst().orElse(null);

        if (product == null && chemical.name.equals("ORE")) {
            chemicalRequirements.add(chemical);
            return chemicalRequirements;
        } else if (product == null) {
            throw new InvalidDnDOperationException("NULL producs for ORE");
        }

        double multiplier = product.quantity > chemical.quantity
                ? 1.0
                : Math.ceil((double) chemical.quantity / (double) product.quantity);

        materials.forEach(material -> chemicalRequirements.add(new ChemicalReagent(material.name, (long) (material.quantity * multiplier))));
        return chemicalRequirements;
    }

    private HashMap<ChemicalReagent, List<ChemicalReagent>> loadRequirements(List<String> lines) {
        HashMap<ChemicalReagent, List<ChemicalReagent>> requirements = new HashMap<>();
        lines.forEach(line -> updateRequirements(requirements, line));
        return requirements;
    }

    private void updateRequirements(HashMap<ChemicalReagent, List<ChemicalReagent>> requirements, String line) {
        String[] productsToSubstrates = line.split("=>");
        String[] substrates = productsToSubstrates[0].split(",");

        List<ChemicalReagent> product = convertToChemicals(productsToSubstrates[1]);
        List<ChemicalReagent> chemicalIngredients = convertToChemicals(substrates);
        requirements.put(product.get(0), chemicalIngredients);
    }

    private List<ChemicalReagent> convertToChemicals(String... ingredients) {
        List<ChemicalReagent> chemicals = new ArrayList<>();
        for (String ingredientStr : ingredients) {
            String[] countAndName = ingredientStr.trim().split(" ");
            int count = Integer.parseInt(countAndName[0]);
            String name = countAndName[1];
            chemicals.add(new ChemicalReagent(name, count));
        }
        return chemicals;
    }

    static class ChemicalReagent {
        String name;
        long quantity;

        public ChemicalReagent(String name, long quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChemicalReagent that = (ChemicalReagent) o;

            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }


        @Override
        public String toString() {
            return "{" +
                    "name='" + name + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}
