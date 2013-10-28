package net.new_liberty.core;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A logger for a tweak.
 */
public class ModuleLogger extends Logger {
    private final String prefix;

    public ModuleLogger(Module t) {
        super(t.getClass().getCanonicalName(), null);
        prefix = "[" + t.getName() + "] ";
        setParent(t.plugin.getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord l) {
        l.setMessage(prefix + l.getMessage());
        super.log(l);
    }
}
