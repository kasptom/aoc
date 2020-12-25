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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatsPrinter {

    private static final String TEMPLATES_DIR = "templates/";
    private static final String STYLING_FILE_NAME = "stats.css";
    private static final String OUTPUT_FILE_EXTENSION = ".html";
    private static final String OUTPUT_TEMPLATE_MODE = "HTML5";

    private static final String CONTEXT_VAR_STATS = "stats";
    private static final String CONTEXT_VAR_MEMBERS = "members";
    private static final String CONTEXT_VAR_DAYS = "days";

    static void print(Stats stats, String outputFileName) throws IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(TEMPLATES_DIR);
        templateResolver.setSuffix(OUTPUT_FILE_EXTENSION);
        templateResolver.setTemplateMode(OUTPUT_TEMPLATE_MODE);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Map<String, Object> variables = new HashMap<>();
        variables.put(CONTEXT_VAR_STATS, stats);
        variables.put(CONTEXT_VAR_MEMBERS, stats.getSortedMembers());
        variables.put(CONTEXT_VAR_DAYS, stats.getDays());
        IContext context = new Context(Locale.getDefault(), variables);

        if (outputFileName == null) {
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
//        String timestamp = LocalDateTime.now().format(df);
//        PrintWriter out = new PrintWriter(new FileOutputStream("output_" + timestamp + ".html"));
            outputFileName = getDefaultOutputFileName(stats);
        }
        outputFileName = "./" + outputFileName;

        PrintWriter out = new PrintWriter(new FileOutputStream(outputFileName), true, StandardCharsets.UTF_8);
        Files.copy(FileUtils.getResourcePath(TEMPLATES_DIR + STYLING_FILE_NAME), new FileOutputStream(STYLING_FILE_NAME));
        templateEngine.process("stats", context, out);
        out.flush();
        System.out.format("Stats can be found in: %s alongside %s file%n",
                FileUtils.getAbsolutePath(outputFileName),
                FileUtils.getAbsolutePath(STYLING_FILE_NAME));
    }

    private static String getDefaultOutputFileName(Stats stats) {
        return String.format("output_%s_%s%s", stats.getEvent(), stats.getOwnerId(), OUTPUT_FILE_EXTENSION);
    }
}
