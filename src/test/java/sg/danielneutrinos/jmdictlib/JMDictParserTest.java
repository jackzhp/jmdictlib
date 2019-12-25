package sg.danielneutrinos.jmdictlib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sg.danielneutrinos.jmdictlib.data.*;

import java.util.Map;

import static org.junit.Assert.*;

public class JMDictParserTest {

    private static Map<Integer, JMEntry> dictionary;
    private final String kireiJSON = "{\"entrySequence\":1591900,\"kanjiElements\":[{\"kanji\":\"綺麗\",\"primaries\":[\"spec1\"],\"info\":[]},{\"kanji\":\"奇麗\",\"primaries\":[\"ichi1\"],\"info\":[]},{\"kanji\":\"暉麗\",\"primaries\":[],\"info\":[\"word containing out-dated kanji\"]}],\"readingElements\":[{\"kana\":\"きれい\",\"notKanjiReading\":false,\"kanjiReadings\":[],\"primaries\":[\"ichi1\",\"spec1\"],\"info\":[]},{\"kana\":\"キレイ\",\"notKanjiReading\":true,\"kanjiReadings\":[],\"primaries\":[\"spec1\"],\"info\":[]}],\"senses\":[{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[\"adjectival nouns or quasi-adjectives (keiyodoshi)\"],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"pretty\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"lovely\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"beautiful\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"fair\",\"primaries\":[]}],\"info\":null},{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"clean\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"clear\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"pure\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"tidy\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"neat\",\"primaries\":[]}],\"info\":null},{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"completely\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"entirely\",\"primaries\":[]}],\"info\":\"as きれいに\"}]}";
    private static int entryParsedCounter = 0;
    private static boolean completed = false;

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        JMDictParser classUnderTest = new JMDictParser(new ParseEventListener() {
            @Override
            public void entryParsed() {
                entryParsedCounter++;
            }

            @Override
            public void completed() {
                completed = true;
            }
        });
        classUnderTest.parse();
        dictionary = classUnderTest.getDictionary();
    }

    @Test public void outputTest() throws Exception {
        //pick an entry to test
        JMEntry testEntry = dictionary.get(1591900);
        testKireiEntry(testEntry);
    }

    @Test public void outputJSONTest() {
        JMEntry toJSONEntry = dictionary.get(1591900);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String actualJSON = gson.toJson(toJSONEntry);
        assertEquals(kireiJSON, actualJSON);

        JMEntry fromJSONEntry = gson.fromJson(actualJSON, JMEntry.class);
        testKireiEntry(fromJSONEntry);
    }

    @Test public void testListener() {
        assertEquals(JMDictParser.getExpectedDictSize(), entryParsedCounter);
        assertTrue(completed);
    }

    private void testKireiEntry(JMEntry testEntry) {
        KanjiElement firstKanjiElement = testEntry.getKanjiElements().get(0);
        ReadingElement firstReadingElement = testEntry.getReadingElements().get(0);
        Sense firstSense = testEntry.getSenses().get(0);
        Gloss firstGloss = firstSense.getGlosses().get(0);

        //kanji test
        assertNotNull(firstKanjiElement);
        assertEquals(3, testEntry.getKanjiElements().size());
        assertEquals("綺麗", firstKanjiElement.getKanji());
        assertEquals(1, firstKanjiElement.getPrimaries().size());
        assertEquals("spec1", firstKanjiElement.getPrimaries().get(0));

        //readings test
        assertNotNull(firstReadingElement);
        assertEquals(2, testEntry.getReadingElements().size());
        assertEquals("きれい", firstReadingElement.getKana());
        assertEquals(2, firstReadingElement.getPrimaries().size());
        assertTrue(firstReadingElement.getPrimaries().contains("ichi1"));
        assertTrue(firstReadingElement.getPrimaries().contains("spec1"));

        //sense test
        assertNotNull(firstSense);
        assertEquals(3, testEntry.getSenses().size());
        assertEquals(1, firstSense.getPartsOfSpeech().size());
        assertEquals("adjectival nouns or quasi-adjectives (keiyodoshi)", firstSense.getPartsOfSpeech().get(0));
        assertEquals(1, firstSense.getMisc().size());
        assertEquals("word usually written using kana alone", firstSense.getMisc().get(0));

        assertNotNull(firstGloss);
        assertEquals(4, firstSense.getGlosses().size());
        assertEquals("pretty", firstGloss.getText());
    }
}
