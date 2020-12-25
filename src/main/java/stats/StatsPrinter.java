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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatsPrinter {
    static void print(Stats stats) throws IOException {
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

//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
//        String timestamp = LocalDateTime.now().format(df);
//        PrintWriter out = new PrintWriter(new FileOutputStream("output_" + timestamp + ".html"));
        PrintWriter out = new PrintWriter(new FileOutputStream("output.html"));
        Files.copy(FileUtils.getResourcePath("templates/stats.css"), new FileOutputStream("stats.css"));
        templateEngine.process("stats", context, out);
        out.flush();
    }
}
