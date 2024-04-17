package utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HtmlPageBuilder {

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

    public static byte[] buildUserListPage() throws IOException {
        Template template = HANDLEBARS.compile(USER_LIST_PATH);
        Map<String, Object> model = new HashMap<>();
        model.put("users", DataBase.findAll());
        String pageContent = template.apply(model);
        return pageContent.getBytes(StandardCharsets.UTF_8);
    }
}
