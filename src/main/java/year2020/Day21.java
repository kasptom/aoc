package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 implements IAocTask {
    List<String> ingredientsWithAllergens;
    TreeMap<String, TreeSet<String>> allergenToPossible;

    @Override
    public String getFileName() {
        return "aoc2020/input_21.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Food> foods = lines.stream().map(Food::parse).collect(Collectors.toList());
        Set<String> allIngredients = foods.stream()
                .flatMap(food -> food.ingredients.stream())
                .collect(Collectors.toCollection(TreeSet::new));
        Set<String> allAllergens = foods.stream()
                .flatMap(food -> food.allergens.stream())
                .collect(Collectors.toCollection(TreeSet::new));

        TreeMap<String, Integer> ingredientToCount = foods
                .stream()
                .flatMap(food -> food.ingredients.stream())
                .collect(Collectors.toMap(in -> in, in -> 1, Integer::sum, TreeMap::new));

        List<String> allergensSorted = new ArrayList<>(allAllergens).stream().sorted(String::compareTo).collect(Collectors.toList());

        allergenToPossible = allAllergens.stream()
                .collect(Collectors.toMap(al -> al, al -> new TreeSet<>(), (x, y) -> y, TreeMap::new));

        while (!allergenToPossible.values().stream().allMatch(v -> v.size() == 1)) {
            for (var allergen : allergensSorted) {
                TreeSet<String> possible = allergenToPossible.get(allergen);
//                System.out.println("\n--- allergen: " + allergen);
                for (Food food : foods) {
                    if (food.allergens.contains(allergen) && possible.isEmpty()) {
                        possible.addAll(food.ingredients);
                    }
                    if (food.allergens.contains(allergen)) {
                        var toRemove = possible.stream().filter(in -> !food.ingredients.contains(in)).collect(Collectors.toList());
                        possible.removeAll(toRemove); // intersect
                    }
//                    System.out.println("possible: " + possible);

                }
                if (possible.size() == 1) {
                    var toRemove = possible.iterator().next();
                    allergenToPossible.keySet().stream()
                            .filter(k -> !k.equals(allergen))
                            .forEach(k -> allergenToPossible.get(k).remove(toRemove));
                }
//                System.out.format("ingredients for allergen %s: %s%n", allergen, possible.toString());
            }
        }
        ingredientsWithAllergens = allergenToPossible
                .values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<String> ingredientsWithoutAllergens = allIngredients.stream().filter(o -> !ingredientsWithAllergens.contains(o))
                .collect(Collectors.toList());
//        System.out.println("ingredients without allergens: " + ingredientsWithoutAllergens);
        long sum = ingredientsWithoutAllergens.stream().mapToInt(ingredientToCount::get).sum();
        System.out.println(sum);
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        String code = allergenToPossible.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.joining(","));
        System.out.println(code);
    }

    static class Food {
        private final Set<String> ingredients;
        private final Set<String> allergens;

        public Food(Set<String> ingredients, Set<String> allergens) {
            this.ingredients = ingredients;
            this.allergens = allergens;
        }

        static Food parse(String line) {
            line = line.replaceAll("\\(", "").replaceAll("\\)", "")
                    .replaceAll(",", "");
            List<String> words = Arrays.stream(line.split(" ")).collect(Collectors.toList());
            int containsIdx = words.indexOf("contains");
            Set<String> ingredients = new TreeSet<>(words.subList(0, containsIdx));
            Set<String> allergens = new TreeSet<>(words.subList(containsIdx + 1, words.size()));
            return new Food(ingredients, allergens);
        }

        @Override
        public String toString() {
            return ingredients + " --> " + allergens;
        }
    }
}
