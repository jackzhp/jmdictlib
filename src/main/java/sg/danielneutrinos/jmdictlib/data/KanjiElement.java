package sg.danielneutrinos.jmdictlib.data;

import java.util.ArrayList;
import java.util.List;

/**
 * The defining component of each entry.
 * The overwhelming majority of entries will have a single kanji
 * element associated with a word in Japanese. Where there are
 * multiple kanji elements within an entry, they will be orthographical
 * variants of the same word, either using variations in okurigana, or
 * alternative and equivalent kanji. Common "mis-spellings" may be
 * included, provided they are associated with appropriate information
 * fields. Synonyms are not included; they may be indicated in the
 * cross-reference field associated with the sense element.
 */
public class KanjiElement {

    /**
     * A word or short phrase in Japanese which is written
     * using at least one non-kana character (usually kanji,
     * but can be other characters). The valid characters are
     * kanji, kana, related characters such as chouon and kurikaeshi,
     * and in exceptional cases, letters from other alphabets.
     */
    private String kanji;

    /**
     * Information about the relative priority of the entry,  and consist
     * of codes indicating the word appears in various references which
     * can be taken as an indication of the frequency with which the word
     * is used. This field is intended for use either by applications which
     * want to concentrate on entries of  a particular priority, or to
     * generate subset files.
     */
    private List<String> primaries = new ArrayList<>();

    /**
     * Information field related specifically to the orthography of the keb,
     * and will typically indicate some unusual aspect, such as
     * okurigana irregularity.
     */
    private List<String> info = new ArrayList<>();

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
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
