package sg.danielneutrinos.jmdictlib.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Target-language words or phrases which are equivalents to the
 * Japanese word
 */
public class Gloss {

    /**
     * Target language of the gloss
     */
    private Locale language;

    /**
     * Gender of the gloss (typically a noun in the target language.
     * When absent, the gender is either not relevant or has yet to be provided.
     */
    private String gender;

    /**
     * Specifies that the gloss is of a particular type
     * e.g. "lit" (literal), "fig" (figurative), "expl" (explanation).
     */
    private String type;

    /**
     * Target language translation
     */
    private String text;

    /**
     * Highlight particular target-language words which are strongly
     * associated with the Japanese word. The purpose is to establish
     * a set of target-language words which can effectively be used as
     * head-words in a reverse target-language/Japanese relationship.
     */
    private List<String> primaries = new ArrayList<>();

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getPrimaries() {
        return primaries;
    }

    public void addPrimary(String primary) {
        primaries.add(primary);
    }
}
