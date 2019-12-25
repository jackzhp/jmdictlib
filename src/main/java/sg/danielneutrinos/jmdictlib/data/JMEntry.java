package sg.danielneutrinos.jmdictlib.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Entries consist of kanji elements, reading elements,
 * general information and sense elements. Each entry must have at
 * least one reading element and one sense element. Others are optional.
 */
public class JMEntry {

    /**
     * A unique numeric sequence number for each entry
     */
    private int entrySequence;

    /**
     * The kanji element, or in its absence, the reading element,
     * is the defining component of each entry.
     */
    private List<KanjiElement> kanjiElements = new ArrayList<>();

    /**
     * The reading element typically contains the valid readings
     * of the word(s) in the kanji element using modern kanadzukai.
     * Where there are multiple reading elements, they will typically be
     * alternative readings of the kanji element.
     */
    private List<ReadingElement> readingElements = new ArrayList<>();

    /**
     * The sense element will record the translational equivalent
     * of the Japanese word, plus other related information. Where there
     * are several distinctly different meanings of the word, multiple
     * sense elements will be employed.
     */
    private List<Sense> senses = new ArrayList<>();

    public int getEntrySequence() {
        return entrySequence;
    }

    public void setEntrySequence(int entrySequence) {
        this.entrySequence = entrySequence;
    }

    public List<KanjiElement> getKanjiElements() {
        return kanjiElements;
    }

    public void addKanjiElement(KanjiElement kanjiElement) {
        kanjiElements.add(kanjiElement);
    }

    public List<ReadingElement> getReadingElements() {
        return readingElements;
    }

    public void addReadingElement(ReadingElement readingElement) {
        readingElements.add(readingElement);
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public void addSense(Sense sense) {
        senses.add(sense);
    }
}
