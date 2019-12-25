package sg.danielneutrinos.jmdictlib.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Translational equivalent	of the Japanese word,
 * plus other related information. Where there
 * are several distinctly different meanings of the word,
 * multiple sense objects will be employed
 */

public class Sense {

    /**
     * If present, indicate that the sense is restricted to Kanji
     */
    private List<String> invalidSenseKanjis = new ArrayList<>();

    /**
     * If present, indicate that the sense is restricted to Reading
     */
    private List<String> invalidSenseReadings = new ArrayList<>();

    /**
     * Cross-reference to another entry with a similar or related
     * meaning or sense.
     * In some cases a keb will be followed by a reb and/or a sense number to provide
     * a precise target for the cross-reference. Where this happens, a JIS
     * "centre-dot" (0x2126) is placed between the components of the
     * cross-reference. The target keb or reb must not contain a centre-dot.
     */
    private List<String> xReferences = new ArrayList<>();

    /**
     * Indicates another entry which is an antonym of the current entry/sense.
     * The content of this element must exactly match that of a keb or reb
     * element in another entry.
     */
    private List<String> antonyms = new ArrayList<>();

    /**
     * In general where there are multiple senses in an entry, the
     * part-of-speech of an earlier sense will apply to later senses
     * unless there is a new part-of-speech indicated.
     */
    private List<String> partsOfSpeech = new ArrayList<>();

    /**
     * Information about the field of application of the entry/sense.
     * When absent, general application is implied.
     */
    private List<String> fields = new ArrayList<>();

    /**
     * Used for other relevant information about the entry/sense.
     * As with part-of-speech, information will usually apply to
     * several senses.
     */
    private List<String> misc = new ArrayList<>();

    /**
     * Information about the source language(s) of a loan-word/gairaigo.
     */
    private List<SourceLanguage> sourceLanguages = new ArrayList<>();

    /**
     * For words specifically associated with regional dialects in Japanese
     */
    private List<String> dialects = new ArrayList<>();

    /**
     * Target-language words or phrases which are equivalents to the
     * Japanese word. Would normally be present, however it
     * may be omitted in entries which are purely for a cross-reference.
     */
    private List<Gloss> glosses = new ArrayList<>();

    /**
     * Additional information to be recorded about a sense. Typical usage would
     * be to indicate such things as level of currency of a sense, the
     * regional variations, etc.
     */
    private String info;

    public List<String> getInvalidSenseKanjis() {
        return invalidSenseKanjis;
    }

    public void addInvalidSenseKanji(String kanji) {
        invalidSenseKanjis.add(kanji);
    }

    public List<String> getInvalidSenseReadings() {
        return invalidSenseReadings;
    }

    public void addInvalidSenseReading(String reading) {
        invalidSenseReadings.add(reading);
    }

    public List<String> getXReferences() {
        return xReferences;
    }

    public void addXReference(String xref) {
        xReferences.add(xref);
    }

    public List<String> getAntonyms() {
        return antonyms;
    }

    public void addAntonym(String antonym) {
        antonyms.add(antonym);
    }

    public List<String> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void addPartOfSpeech(String pos) {
        partsOfSpeech.add(pos);
    }

    public List<String> getFields() {
        return fields;
    }

    public void addField(String field) {
        fields.add(field);
    }

    public List<String> getMisc() {
        return misc;
    }

    public void addMisc(String misc) {
        this.misc.add(misc);
    }

    public List<SourceLanguage> getSourceLanguages() {
        return sourceLanguages;
    }

    public void addSourceLanguage(SourceLanguage sourceLanguage) {
        sourceLanguages.add(sourceLanguage);
    }

    public List<String> getDialects() {
        return dialects;
    }

    public void addDialect(String dialect) {
        dialects.add(dialect);
    }

    public List<Gloss> getGlosses() {
        return glosses;
    }

    public void addGloss(Gloss gloss) {
        glosses.add(gloss);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
