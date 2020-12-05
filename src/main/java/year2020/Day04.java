package year2020;

import aoc.IAocTask;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class Day04 implements IAocTask {

    @Override
    public String getFileName() {
        return "aoc2020/input_04.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        List<Passport> passports = loadPassports(lines);
        System.out.println(passports.stream().filter(Passport::valid).count());
    }

    @Override
    public void solvePartTwo(List<String> lines) {

    }

    private List<Passport> loadPassports(List<String> lines) {
        List<Passport> passports = new ArrayList<>();
        StringBuilder data = new StringBuilder();
        for (String line : lines) {
            if (line.isBlank()) {
                var passport = new Passport(data.toString());
                passports.add(passport);
                data = new StringBuilder();
            } else {
                data.append(line);
                data.append(" ");
            }
        }
        return passports;
    }
}

class Passport {
    HashMap<PassportFields, String> fields;

    public Passport(String data) {
        String[] fieldColonValues = data.replaceAll("\n", " ").split(" ");
        fields = new HashMap<>();
        for (String fieldColon : fieldColonValues) {
            String[] split = fieldColon.split(":");
            fields.put(PassportFields.valueOf(split[0]), split[1]);
        }
    }

    public boolean valid() {
        return fields.keySet().containsAll(PassportFields.REQUIRED_FIELDS);
    }

    @Override
    public String toString() {
        return "{ valid: " + valid() + ", fields: " + fields.keySet()  + " }";
    }
}

enum PassportFields {
    byr("Birth Year"),
    iyr("Issue Year"),
    eyr("Expiration Year"),
    hgt("Height"),
    hcl("Hair Color"),
    ecl("Eye Color"),
    pid("Passport ID"),
    cid("Country ID");

    private final String description;

    static final EnumSet<PassportFields> REQUIRED_FIELDS = EnumSet.of(byr, iyr, eyr, hgt, hcl, ecl, pid);
    static final EnumSet<PassportFields> OPTIONAL_FIELDS = EnumSet.of(cid);

    PassportFields(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return name();
    }
}
