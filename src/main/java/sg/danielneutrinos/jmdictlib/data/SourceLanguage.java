package sg.danielneutrinos.jmdictlib.data;

import java.util.Locale;

/**
 * Source language(s) of a loan-word/gairaigo.
 */
public class SourceLanguage {

    /**
     * Defines the language(s) from which a loanword is drawn.
     */
    Locale language = Locale.forLanguageTag("en");

    /**
     * Indicates whether language fully or partially
     * describes the source word or phrase of the
     * loanword. Is either "full" or "part".
     */
    String type = "full";

    /**
     * Indicates that the Japanese word has been constructed
     * from words in the source language, and not from an
     * actual phrase in that language. Most commonly used to
     * indicate "waseieigo".
     */
    boolean isWaseiGo = false;

    /**
     * Source word or phrase.
     */
    private String text;

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWaseiGo() {
        return isWaseiGo;
    }

    public void setWaseiGo(boolean waseiGo) {
        isWaseiGo = waseiGo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
