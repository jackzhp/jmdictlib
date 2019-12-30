package sg.danielneutrinos.jmdictlib;

import sg.danielneutrinos.jmdictlib.data.JMEntry;

/**
 * Implement to hook on parser events
 */
public interface ParseEventListener {
    /**
     * @param index index of current parsed entry
     * @param entry the actual entry
     */
    void entryParsed(int index, JMEntry entry);

    /**
     * Called when parser completed all entries
     */
    void completed();
}
