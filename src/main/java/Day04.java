import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day04 implements IAocTask {

    private TreeMap<Date, GuardEvent> guardLog;

    private int laziestGuardId = -1;
    private int mostTimesAsleepMinute = -1;

    @Override
    public String getFileName() {
        return "input_04_small.txt";
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
                    addLog(year, month, day, hour, minute, eventDetails);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        fillGuideIdsAndSetSleepingStatus();

        findTheLaziestGuard();
    }


    private void findTheLaziestGuard() {
//        Map<Integer, Integer> guardSleepTime = new HashMap<>();
//        guardLog.values()
//                .forEach(guardEvent -> {
//                    if (!guardSleepTime.containsKey(guardEvent.guardId)) {
//                        guardSleepTime.put(guardEvent.guardId, 0);    // BEGINS
//                    } else if (guardEvent.isSleeping) {
//                        guardSleepTime.put(guardEvent.guardId, guardSleepTime.get(guardEvent.guardId) + 1);
//                    }
//                });
//
//        int maxAsleepTime = guardSleepTime.values().stream().max(Integer::compareTo).orElse(-1);
//
//        for (Integer key : guardSleepTime.keySet()) {
//            if (guardSleepTime.get(key) == maxAsleepTime) {
//                laziestGuardId = key;
//            }
//        }
//
//        Map<Integer, Integer> sleepMinutes = new HashMap<>();
//        mostTimesAsleepMinute = guardLog.values()
//                    .forEach(guardEvent -> {
//                        int eventMinute = !sleepMinutes.containsKey(guardEvent.date.getMinutes();
//                      if ()) {
//                          sleepMinutes.put()
//                      }
//                    });
    }

    private void fillGuideIdsAndSetSleepingStatus() {
        TreeMap<Date, GuardEvent> guardLogNoGaps = new TreeMap<>();

        int currentGuideId = 0;
        for (GuardEvent event : guardLog.values()) {
            if (event.eventType == GuardEventType.BEGINS) {
                currentGuideId = event.guardId;
                event.isSleeping = false;
            } else {
                event.guardId = currentGuideId;
            }

            if (event.eventType == GuardEventType.FALLS_ASLEEP) {
                event.isSleeping = true;
            } else if (event.eventType == GuardEventType.WAKES_UP) {
                event.isSleeping = false;
            }

            event = normalizeEvent(event);

            if (!guardLogNoGaps.isEmpty()) {
                Date previousDate = guardLogNoGaps.lastKey();
                GuardEvent previousEvent = guardLogNoGaps.get(previousDate);

                for (int i = 1; i < getNumberOfMinutesToFill(event, previousDate); i++) {
                    Date date = new GregorianCalendar(2018, previousDate.getMonth(), previousDate.getDate(), previousDate.getHours(), previousDate.getMinutes() + i).getTime();
                    guardLogNoGaps.put(date, new GuardEvent(date, previousEvent.guardId, previousEvent.eventType != GuardEventType.BEGINS ? previousEvent.eventType : GuardEventType.WAKES_UP));
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

    private void addLog(String year, String month, String day, String hour, String minute, String eventDetails) {
        Date date = new GregorianCalendar(2018, Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute)).getTime();

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

        guardLog.put(date, new GuardEvent(date, guardId, guardEventType));
    }

    private int getGuardIdFromLog(String eventDetails) {
        String numberSubstring = eventDetails.substring(eventDetails.indexOf('#'));
        numberSubstring = numberSubstring.substring(1, numberSubstring.indexOf(' '));

        return Integer.parseInt(numberSubstring);
    }

    class GuardEvent {

        public boolean isSleeping;

        public GuardEvent(Date date, int guardId, GuardEventType eventType) {
            this.date = date;
            this.guardId = guardId;
            this.eventType = eventType;
        }

        Date date;
        int guardId;
        GuardEventType eventType;

    }

    enum GuardEventType {
        BEGINS,
        WAKES_UP,
        FALLS_ASLEEP;
    }
}
