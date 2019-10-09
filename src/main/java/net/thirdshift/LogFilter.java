package net.thirdshift;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {
    private boolean debug;

    public LogFilter(Tokens plugin) {
        debug = plugin.getConfig().getBoolean("General.Verbose_Logging");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return !(record.getMessage().contains("[Debug]") && !debug);
    }
}