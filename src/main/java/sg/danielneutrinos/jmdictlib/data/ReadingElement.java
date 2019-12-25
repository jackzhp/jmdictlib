package sg.danielneutrinos.jmdictlib.data;

import java.util.ArrayList;
import java.util.List;

/**
 * The reading element typically contains the valid readings
 * of the word(s) in the kanji element using modern kanadzukai.
 * Where there are multiple reading elements, they will typically be
 * alternative readings of the kanji element. In the absence of a
 * kanji element, i.e. in the case of a word or phrase written
 * entirely in kana, these elements will define the entry.
 */
public class ReadingElement {

    /**
     * Restricted to kana and related characters such as
     * chouon and kurikaeshi. Kana usage will be consistent
     * between the kanji and reading elements; e.g. if the kanji
     * contains katakana, so too will the reading.
     */
    private String kana;

    /**
     * Indicates that the reading, while associated with the kanji,
     * cannot be regarded as a true reading of the kanji.
     * It is typically used for words such as foreign place names,
     * gairaigo which can be in kanji or katakana, etc.
     */
    private boolean notKanjiReading;

    /**
     * Reading only applies to a subset of the kanji elements in the entry.
     * In its absence, all readings apply to all kanji elements.
     * The contents of this element must exactly match
     * those of one of the kanji elements.
     */
    private List<String> kanjiReadings = new ArrayList<>();

    /**
     * See @KanjiElement primaries
     */
    private List<String> primaries = new ArrayList<>();

    /**
     * General information pertaining to the specific reading.
     * Typically it will be used to indicate some unusual aspect of
     * the reading.
     */
    private List<String> info = new ArrayList<>();

    public String getKana() {
        return kana;
    }

    public void setKana(String kana) {
        this.kana = kana;
    }

    public boolean isNotKanjiReading() {
        return notKanjiReading;
    }

    public void setNotKanjiReading(boolean notKanjiReading) {
        this.notKanjiReading = notKanjiReading;
    }

    public List<String> getKanjiReadings() {
        return kanjiReadings;
    }

    public void addKanjiReading(String reading) {
        kanjiReadings.add(reading);
    }

    public List<String> getPrimaries() {
        return primaries;
    }

    public void addPrimary(String primary) {
        primaries.add(primary);
    }

    public List<String> getInfo() {
        return info;
    }

    public void addInfo(String info) {
        this.info.add(info);
    }
}
