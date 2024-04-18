package utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HtmlPageBuilder {

    private static final Logger logger = LoggerFactory.getLogger(HtmlPageBuilder.class);

    private static final String PREFIX_TEMPLATES = "/templates";
    private static final String SUFFIX_HTML = ".html";
    private static final String USER_LIST_PATH = "/user/list";

    private static final Handlebars HANDLEBARS;

    static {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix(PREFIX_TEMPLATES);
        loader.setSuffix(SUFFIX_HTML);
        HANDLEBARS = new Handlebars(loader);
    }

    private HtmlPageBuilder() {
    }

    public static byte[] buildUserListPage() {
        try {
            final Template template = HANDLEBARS.compile(USER_LIST_PATH);
            final Map<String, Object> model = new HashMap<>();
            model.put("users", DataBase.findAll());
            final String pageContent = template.apply(model);
            return pageContent.getBytes(StandardCharsets.UTF_8);
        } catch (final IOException e) {
            logger.error(e.getMessage());
            return new byte[0];
        }
    }
}
