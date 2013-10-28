package net.new_liberty.nltweaks.tweak;

import net.new_liberty.tweaks.ChatColorCommands.SetChatColorCommand;
import net.new_liberty.tweaks.ChatColorCommands.SetColorsCommand;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the ChatColorCommands
 */
public class ChatColorCommandsTest {
    @Test
    public void testSetColorsValidate() {
        SetColorsCommand cmd = new SetColorsCommand();

        assertTrue("Restricted color test", cmd.validate("Guardian", "50505050b").contains("prefix colors"));
        assertTrue("Restricted name color test", cmd.validate("Guardian", "dbdbdbdb5").contains("name colors"));
        assertTrue("Solid prefix test", cmd.validate("Guardian", "ddbdbdbdb").contains("solid"));
        assertTrue("Formatting code test", cmd.validate("Guardian", "kbdbdbdbb").contains("formatting"));
        assertEquals("OK test", "OK", cmd.validate("Guardian", "f1f1f1f11"));
    }

    @Test
    public void testSetColorsParse() {
        SetColorsCommand cmd = new SetColorsCommand();

        assertEquals("[&fG&1u&fa&1r&fd&1i&fa&1n&f]&1", cmd.parse("Guardian", "f1f1f1f11"));
    }

    @Test
    public void testSetChatColorValidate() {
        SetChatColorCommand cmd = new SetChatColorCommand();

        assertTrue("Restricted length test", cmd.validate("Guardian", "er").contains("only one"));
        assertTrue("Restricted color test", cmd.validate("Guardian", "e").contains("chat color"));
        assertTrue("Restricted formatting code", cmd.validate("Guardian", "k").contains("formatting"));
        assertTrue("OK test", cmd.validate("Guardian", "b").contains("OK"));
    }
}
