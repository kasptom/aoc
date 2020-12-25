package year2020;

import aoc.IAocTask;

import java.util.*;
import java.util.regex.Pattern;

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
        List<Passport> passports = loadPassports(lines);
        System.out.println(passports.stream().filter(Passport::valid2).count());
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
        return "{ valid: " + valid() + ", fields: " + fields.keySet() + " }";
    }

    private static final Pattern HAIR_COLOR_PATTERN = Pattern.compile("#[a-f0-9]{6}");
    private static final Set<String> VALID_EYE_COLORS = Set.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth");
    private static final Pattern PASSPORT_ID_PATTERN = Pattern.compile("[0-9]{9}");

    /**
     * byr (Birth Year) - four digits; at least 1920 and at most 2002.
     * iyr (Issue Year) - four digits; at least 2010 and at most 2020.
     * eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
     * hgt (Height) - a number followed by either cm or in:
     * If cm, the number must be at least 150 and at most 193.
     * If in, the number must be at least 59 and at most 76.
     * hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
     * ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
     * pid (Passport ID) - a nine-digit number, including leading zeroes.
     * cid (Country ID) - ignored, missing or not.
     *
     * @return true if valid
     */
    public boolean valid2() {
        if (!valid()) {
            return false;
        }
        for (var entry : fields.entrySet()) {
            if (!validateEntry(entry)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateEntry(Map.Entry<PassportFields, String> entry) {
        switch (entry.getKey()) {
            case byr:
                int birthDate = Integer.parseInt(entry.getValue());
                return birthDate >= 1920 && birthDate <= 2002;
            case iyr:
                int issueYear = Integer.parseInt(entry.getValue());
                return issueYear >= 2010 && issueYear <= 2020;
            case eyr:
                int expirationYear = Integer.parseInt(entry.getValue());
                return expirationYear >= 2020 && expirationYear <= 2030;
            case hgt:
                String heightPlusUnit = entry.getValue();
                if (heightPlusUnit.endsWith("cm")) {
                    int height = Integer.parseInt(heightPlusUnit.substring(0, heightPlusUnit.length() - 2));
                    return height >= 150 && height <= 193;
                } else if (heightPlusUnit.endsWith("in")) {
                    int height = Integer.parseInt(heightPlusUnit.substring(0, heightPlusUnit.length() - 2));
                    return height >= 59 && height <= 76;
                } else return false;
            case hcl:
                return HAIR_COLOR_PATTERN.matcher(entry.getValue()).matches();
            case ecl:
                return VALID_EYE_COLORS.contains(entry.getValue());
            case pid:
                return PASSPORT_ID_PATTERN.matcher(entry.getValue()).matches();
            case cid:
                return true;
            default:
                return false;
        }
    }
}

enum PassportFields {
    byr, // Birth Year
    iyr, // Issue Year
    eyr, // Expiration Year
    hgt, // Height
    hcl, // Hair Color
    ecl, // Eye Color
    pid, // Passport ID
    cid; // Country ID

    static final EnumSet<PassportFields> REQUIRED_FIELDS = EnumSet.of(byr, iyr, eyr, hgt, hcl, ecl, pid);

    @Override
    public String toString() {
        return name();
    }
}
