package stats;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import stats.model.Stats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class StatsApp {
    public static void main(String[] args) throws IOException {
        ClassLoader classLoader = StatsApp.class.getClassLoader();
        byte[] jsonData = getData(classLoader);
        ObjectMapper objectMapper = getObjectMapper();
        Stats stats = objectMapper.readValue(jsonData, Stats.class);
        System.out.println(stats);
    }

    private static byte[] getData(ClassLoader classLoader) throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("stats/private_leaderboard.json")).getFile());
        return Files.readAllBytes(inputFile.toPath());
    }

    private static ObjectMapper getObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
