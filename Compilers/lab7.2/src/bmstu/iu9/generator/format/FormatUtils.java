package bmstu.iu9.generator.format;

import java.text.MessageFormat;

public final class FormatUtils {
    public static String format(String str, String... args) {
        MessageFormat format = new MessageFormat(str);
        return format.format(args);
    }
}
