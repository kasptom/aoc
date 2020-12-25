package stats;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import stats.dto.StatsDto;
import stats.factory.StatsFactory;
import stats.model.Stats;
import utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StatsApp {
    public static void main(String[] args) throws IOException {
        byte[] jsonData = getData();
        ObjectMapper objectMapper = getObjectMapper();
        StatsDto dto = objectMapper.readValue(jsonData, StatsDto.class);
        Stats stats = StatsFactory.create(dto);
        StatsPrinter.print(stats);
    }

    private static byte[] getData() throws IOException {
        Path fullPath = FileUtils.getResourcePath("stats/private_leaderboard.json");
        return Files.readAllBytes(fullPath);
    }

    private static ObjectMapper getObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
