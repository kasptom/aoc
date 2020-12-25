package stats;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import stats.model.Stats;
import utils.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatsPrinter {
    static void print(Stats stats, String outputFileName) throws IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Map<String, Object> variables = new HashMap<>();
        variables.put("stats", stats);
        variables.put("members", stats.getSortedMembers());
        variables.put("days", stats.getDays());
        IContext context = new Context(Locale.getDefault(), variables);

        if (outputFileName == null) {
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
//        String timestamp = LocalDateTime.now().format(df);
//        PrintWriter out = new PrintWriter(new FileOutputStream("output_" + timestamp + ".html"));
            outputFileName = "output_"+ stats.getEvent() + "_" + stats.getOwnerId() + ".html";
        }
        outputFileName = "./" + outputFileName;

        PrintWriter out = new PrintWriter(new FileOutputStream(outputFileName), true, StandardCharsets.UTF_8);
        Files.copy(FileUtils.getResourcePath("templates/stats.css"), new FileOutputStream("stats.css"));
        templateEngine.process("stats", context, out);
        out.flush();
        System.out.format("Stats can be found in: %s alongside with the stats.css file%n",
                Paths.get(outputFileName).getFileName().toAbsolutePath());
    }
}
