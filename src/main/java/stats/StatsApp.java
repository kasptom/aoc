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
        String jsonFilePath = "stats/private_leaderboard.json";
        String outputFilePath = null;
        if (args.length >= 2) {
            outputFilePath = args[1];
        }
        if (args.length >= 1) {
            jsonFilePath = args[0];
        }
        System.out.format("Using %s as the input%n", jsonFilePath);
        System.out.format("Using %s for the output%n", outputFilePath == null
                ? "the default name \"output_{event}_{ownerId}.html\""
                : "\"" + outputFilePath + "\" name");

        byte[] jsonData = getData(jsonFilePath);
        ObjectMapper objectMapper = getObjectMapper();
        StatsDto dto = objectMapper.readValue(jsonData, StatsDto.class);
        Stats stats = StatsFactory.create(dto);
        StatsPrinter.print(stats, outputFilePath);
    }

    private static byte[] getData(String jsonFilePath) throws IOException {
        Path fullPath = FileUtils.getResourcePath(jsonFilePath);
        return Files.readAllBytes(fullPath);
    }

    private static ObjectMapper getObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
