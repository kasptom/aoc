import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day07 implements IAocTask {

    @Override
    public String getFileName() {
        return "input_07_small.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        HashMap<String, Step> possibleSteps = createAllPossibleSteps();

        // Step C must be finished before step A can begin.
        String STEP_PAIR_REGEX = "Step ([A-Z]) must be finished before step ([A-Z]) can begin";
        Pattern pattern = Pattern.compile(STEP_PAIR_REGEX);

        createDependencyTree(lines, possibleSteps, pattern);

        printStepsInExecutionOrder(possibleSteps);
    }

    private void printStepsInExecutionOrder(HashMap<String, Step> possibleSteps) {
        HashSet<Step> dependentSteps =  new HashSet<>();

        for (Step step : possibleSteps.values()) {
            dependentSteps.addAll(possibleSteps
                    .values()
                    .stream()
                    .filter(st -> step.childStepNames.contains(st.name))
                    .collect(Collectors.toList()));
        }

        for (Step step : possibleSteps.values()) {
            if (!dependentSteps.contains(step)) {
                System.out.printf("Independent step: %s\n", step.name);
            }
        }
    }

    private void createDependencyTree(List<String> lines, HashMap<String, Step> possitbleSteps, Pattern pattern) {
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String prevName = matcher.group(1);
                String nextName = matcher.group(2);

                Step prevStep = possitbleSteps.get(prevName);
                Step nextStep = possitbleSteps.get(nextName);

                prevStep.addChildStep(nextStep);
            }
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    private HashMap<String, Step> createAllPossibleSteps() {
        HashMap<String, Step> steps = new HashMap<>();

        for (char i = 'A'; i <= 'Z'; i++) {
            String stepName = "" + i;
            steps.put(stepName, new Step(stepName));
        }

        return steps;
    }

    class Step {
        String name;
        HashSet<String> childStepNames = new HashSet<>();

        public Step(String name) {
            this.name = name;
        }

        void addChildStep(Step step) {
            childStepNames.add(step.name);
        }
    }
}
