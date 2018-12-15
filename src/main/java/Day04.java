import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day04 implements IAocTask {

    private TreeMap<Date, GuardEvent> guardLog;

    private int laziestGuardId = -1;
    private int mostTimesAsleepMinute = -1;

    @Override
    public String getFileName() {
        return "input_04.txt";
    }

    @Override
    public void solvePartOne(List<String> lines) {
        guardLog = new TreeMap<>();

        String EVENT_REGEX = "\\[([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2})] ([\\p{Print}\\s]+)";
        Pattern pattern = Pattern.compile(EVENT_REGEX);

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String year = matcher.group(1);
                String month = matcher.group(2);
                String day = matcher.group(3);
                String hour = matcher.group(4);
                String minute = matcher.group(5);

                String eventDetails = matcher.group(6);

                try {
                    addLog(year, month, day, hour, minute, eventDetails, line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.printf("Could not find log in line: %s\n", line);
            }
        }

        System.out.printf("log size (before filling up): %d\n", guardLog.values().size());

        fillGuideIdsAndSetSleepingStatus();

        printGuideLogs();

        findTheLaziestGuard();
    }

    private void printGuideLogs() {
        for (GuardEvent guardEvent : guardLog.values()) {
            if (guardEvent.isGenerated == false) {
                System.out.println(guardEvent.print());
            }
        }
    }


    private void findTheLaziestGuard() {
        Map<Integer, Integer> guardSleepTime = new HashMap<>();
        guardLog.values()
                .forEach(guardEvent -> {
                    if (!guardSleepTime.containsKey(guardEvent.guardId)) {
                        guardSleepTime.put(guardEvent.guardId, 0);    // BEGINS
                    } else if (guardEvent.isSleeping) {
                        guardSleepTime.put(guardEvent.guardId, guardSleepTime.get(guardEvent.guardId) + 1);
                    }
                });

        int maxAsleepTime;
        maxAsleepTime = guardSleepTime.values().stream().max(Integer::compareTo).orElse(-1);

        for (Integer key : guardSleepTime.keySet()) {
            if (guardSleepTime.get(key) == maxAsleepTime) {
                laziestGuardId = key;
            }
        }

        Map<Integer, Integer> sleepMinutes = new HashMap<>();

        List<GuardEvent> laziestGuideAllEvents = guardLog
                .values()
                .stream()
                .filter(guardEvent -> guardEvent.guardId == laziestGuardId)
                .collect(Collectors.toList());

        printGuideEvents(laziestGuideAllEvents);

        List<GuardEvent> laziestGuideSleepingEvents = laziestGuideAllEvents
                .stream()
                .filter(guardEvent -> guardEvent.isSleeping)
                .collect(Collectors.toList());

        laziestGuideSleepingEvents
                .forEach(guardEvent -> {
                    int minute = guardEvent.date.getMinutes();

                    if (!sleepMinutes.containsKey(minute)) {
                        sleepMinutes.put(minute, 1);
                    } else {
                        sleepMinutes.put(minute, sleepMinutes.get(minute) + 1);
                    }
                });

        int mostMinuteRepeats = sleepMinutes.values().stream().max(Integer::compareTo).orElse(-1);

        for (Integer minute : sleepMinutes.keySet()) {
            if (sleepMinutes.get(minute) == mostMinuteRepeats) {
                mostTimesAsleepMinute = minute;
                break;
            }
        }

        System.out.println(String.format("laziest guard #%d was sleeping most often during the %d minute (%d)",
                laziestGuardId, mostTimesAsleepMinute, laziestGuardId * mostTimesAsleepMinute));
    }

    private void printGuideEvents(List<GuardEvent> laziestGuideAllEvents) {

    }

    private void fillGuideIdsAndSetSleepingStatus() {
        TreeMap<Date, GuardEvent> guardLogNoGaps = new TreeMap<>();

        int currentGuideId = 0;
        for (GuardEvent event : guardLog.values()) {
            if (event.eventType == GuardEventType.BEGINS) {
                currentGuideId = event.guardId;
            } else {
                event.guardId = currentGuideId;
            }

            event = normalizeEvent(event);

            if (!guardLogNoGaps.isEmpty()) {
                Date previousDate = guardLogNoGaps.lastKey();
                GuardEvent previousEvent = guardLogNoGaps.get(previousDate);

                for (int i = 1; i < getNumberOfMinutesToFill(event, previousDate); i++) {
                    Date date = new GregorianCalendar(2018, previousDate.getMonth(), previousDate.getDate(), previousDate.getHours(), previousDate.getMinutes() + i).getTime();
                    GuardEvent toFill = new GuardEvent(date, previousEvent.guardId, previousEvent.eventType != GuardEventType.BEGINS ? previousEvent.eventType : GuardEventType.WAKES_UP);
                    toFill.isGenerated = true;
                    guardLogNoGaps.put(date, toFill);
                }

                guardLogNoGaps.put(event.date, event);
            } else {
                guardLogNoGaps.put(event.date, event);
            }
        }

        guardLog = guardLogNoGaps;
    }

    private GuardEvent normalizeEvent(GuardEvent event) {
        Date date = new Date(event.date.getTime());
        if (date.getHours() != 0) {
            date.setHours(0);
            date.setMinutes(0);
            date = new Date(date.getTime() + 24 * 60 * 60 * 1000);
        }
        return new GuardEvent(date, event.guardId, event.eventType);
    }

    private long getNumberOfMinutesToFill(GuardEvent event, Date previousDate) {
        Date currentDate = event.date;

        if (currentDate.getDate() != previousDate.getDate()) {
            return 60 - previousDate.getMinutes();
        } else {
            return (currentDate.getTime() - previousDate.getTime()) / (1000 * 60);
        }
    }

    @Override
    public void solvePartTwo(List<String> lines) {
        System.out.println("part 2 to do");
    }

    private void addLog(String year, String month, String day, String hour, String minute, String eventDetails, String rawLine) {
        Date date = new GregorianCalendar(2018, Integer.parseInt(month) - 1, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute)).getTime();

        int guardId = 0;
        GuardEventType guardEventType = null;
        if (eventDetails.contains("#")) {
            guardId = getGuardIdFromLog(eventDetails);
            guardEventType = GuardEventType.BEGINS;
        } else if (eventDetails.contains("falls asleep")) {
            guardEventType = GuardEventType.FALLS_ASLEEP;
        } else if (eventDetails.contains("wakes up")) {
            guardEventType = GuardEventType.WAKES_UP;
        }

        if (guardLog.containsKey(date)) {
            throw new RuntimeException(String.format("duplicate log - two guards at the same time! %s\n%s\n%s\n", date, rawLine, guardLog.get(date).rawLine));
        }

        guardLog.put(date, new GuardEvent(date, guardId, guardEventType, rawLine));
    }

    private int getGuardIdFromLog(String eventDetails) {
        String numberSubstring = eventDetails.substring(eventDetails.indexOf('#'));
        numberSubstring = numberSubstring.substring(1, numberSubstring.indexOf(' '));

        return Integer.parseInt(numberSubstring);
    }

    class GuardEvent {

        private final String rawLine;
        boolean isGenerated;
        boolean isSleeping;

        GuardEvent(Date date, int guardId, GuardEventType eventType) {
            this(date, guardId, eventType, null);
        }

        GuardEvent(Date date, int guardId, GuardEventType eventType, String rawLine) {
            this.date = date;
            this.guardId = guardId;
            this.eventType = eventType;
            this.isSleeping = eventType == GuardEventType.FALLS_ASLEEP;
            this.isGenerated = false;
            this.rawLine = rawLine;
        }

        Date date;
        int guardId;
        GuardEventType eventType;


        String print() {
            String dateLog = String.format("[%d-%02d-%02d %02d:%02d] ", 1518, date.getMonth(), date.getDate(), date.getHours(), date.getMinutes());
            String description = eventType == GuardEventType.FALLS_ASLEEP
                    ? "falls asleep" : eventType == GuardEventType.WAKES_UP
                    ? "wakes up" : String.format("Guard #%d begins shift", guardId);
            return isGenerated ?
                    "----> " + dateLog + description : dateLog + description;
        }
    }

    enum GuardEventType {
        BEGINS,
        WAKES_UP,
        FALLS_ASLEEP;
    }
}
