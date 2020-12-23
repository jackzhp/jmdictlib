package sg.danielneutrinos.jmdictlib;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

import sg.danielneutrinos.jmdictlib.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class JMDictParserTest {

    private static Map<Integer, JMEntry> dictionary;
    private final String kireiJSON = "{\"entrySequence\":1591900,\"kanjiElements\":[{\"kanji\":\"綺麗\",\"primaries\":[\"spec1\"],\"info\":[]},{\"kanji\":\"奇麗\",\"primaries\":[\"ichi1\"],\"info\":[]},{\"kanji\":\"暉麗\",\"primaries\":[],\"info\":[\"word containing out-dated kanji\"]}],\"readingElements\":[{\"kana\":\"きれい\",\"notKanjiReading\":false,\"kanjiReadings\":[],\"primaries\":[\"ichi1\",\"spec1\"],\"info\":[]},{\"kana\":\"キレイ\",\"notKanjiReading\":true,\"kanjiReadings\":[],\"primaries\":[\"spec1\"],\"info\":[]}],\"senses\":[{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[\"adjectival nouns or quasi-adjectives (keiyodoshi)\"],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"pretty\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"lovely\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"beautiful\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"fair\",\"primaries\":[]}],\"info\":null},{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"clean\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"clear\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"pure\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"tidy\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"neat\",\"primaries\":[]}],\"info\":null},{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"completely\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"entirely\",\"primaries\":[]}],\"info\":\"as きれいに\"}]}";
    private static int entryParsedCounter = 0;
    private static boolean completed = false;

    static Character cMax;
    static int max; //how many entries for each key? what is its max?
    static HashMap<Character, HashSet<Integer>> map = new HashMap<>(); //the size of the key set is 6057.

    //I saw many question marks since they can not be printed!
    //but I do see many punctuation marks.
    /* for example:
    は:8605
    ッ:8860
    っ:9034
    て:9299
    る:9628
    ル:9833
    ト:10137
    イ:10530
    が:11095
    さ:11500
    と:11547
    け:11765
    ス:11886
    ・:11942
    た:13448
    ち:13758
    ゅ:14355
    せ:15743
    り:16425
    つ:17226
    ン:17817
    こ:18261
    じ:18325
    ー:22500
    ょ:26529
    き:27054
    か:28539
    く:29207
    し:37852
    い:50062
    ん:54936
    う:59586  the larger the number, the better the result.
     */
    static void doMap(String s, int seqNo) {
        char[] ac = s.toCharArray();
        for (int i = 0; i < ac.length; i++) {
            Character oc = ac[i];
            HashSet<Integer> al = map.get(oc);
            if (al == null) {
                al = new HashSet<>();
                map.put(oc, al);
            }
            al.add(seqNo);
            int n = al.size();
            if (n > max) {
                cMax = oc;
                max = n;
//                System.out.println("max:" + max + " " + cMax); //max:59586 う
            }
        }
    }

    static void sortByNumber() {
        class MapCount {
            Character c;
            HashSet<Integer> set;

            public MapCount(Character key, HashSet<Integer> value) {
                c = key;
                set = value;
            }
        }
        MapCount[] amc = new MapCount[map.size()];
        Set<Map.Entry<Character, HashSet<Integer>>> s = map.entrySet();
        int i = 0;
        for (Map.Entry<Character, HashSet<Integer>> me : s) {
            amc[i++] = new MapCount(me.getKey(), me.getValue());
        }
        Arrays.sort(amc, (a, b) -> {
            return a.set.size() - b.set.size();
        });
        for (MapCount mc : amc) {
            System.out.println(mc.c + ":" + mc.set.size());
        }
        System.out.println("size:" + amc.length);
//        print(amc[amc.length - 2].set);

    }

    static void outputMax() {
        if (cMax != null) {
            HashSet<Integer> a = map.get(cMax);
            print(a);
        } else {
            System.out.println("which one with max mapped entries, don't know");
        }
    }

    static void print(HashSet<Integer> a) {
        int n = 0, nMax = 10;
        for (int seqNo : a) {
            if (n >= nMax)
                break;
            n++;
            JMEntry e = dictionary.get(seqNo);
            List<KanjiElement> al = e.getKanjiElements();
            for (KanjiElement k : al) {
                System.out.print(",");
                System.out.print(k.getKanji());
            }
            List<ReadingElement> bl = e.getReadingElements();
            for (ReadingElement k : bl) {
                System.out.print(",");
                System.out.print(k.getKana());
            }
            System.out.println();
        }
    }

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        JMDictParser classUnderTest = new JMDictParser();
        dictionary = classUnderTest.getDictionary();
        classUnderTest.parse(new ParseEventListener() {
            @Override
            public void entryParsed(int index, JMEntry entry) {
                entryParsedCounter++;
                int seqNo = entry.getEntrySequence();
                if (seqNo < 0) {
                    System.out.println("#<0:" + seqNo);
                } else {
//                    System.out.println(seqNo);
                }
                List<KanjiElement> a = entry.getKanjiElements();
                int size = a.size();
                for (KanjiElement k : a) {
                    String s = k.getKanji();
                    doMap(s, seqNo);
                    if (size > 1) {
                        System.out.println(seqNo + ":" + s);
                    }
                }
                List<ReadingElement> b = entry.getReadingElements();
                size = b.size();
                ReadingElement[] c;
                if (size > 1) {
                    HashSet<ReadingElement> d = new HashSet<>();
                    for (ReadingElement e : d) {
                        String s = e.getKana();
                        d.add(e);
                    }
                    c = d.toArray(new ReadingElement[0]);
                } else {
                    c = b.toArray(new ReadingElement[0]);
                }
                for (ReadingElement k : c) {
                    String s = k.getKana();
                    doMap(s, seqNo);
                    if (size > 1) {
                        System.out.println(seqNo + "|" + s);
                    }
                }
            }

            @Override
            public void completed() {
                completed = true;
                outputMax();
                sortByNumber();
            }
        });
    }

    @Test
    public void outputTest() throws Exception {
        //pick an entry to test
        JMEntry testEntry = dictionary.get(1591900);
        testKireiEntry(testEntry);
    }

    @Test
    public void outputJSONTest() {
//        JMEntry toJSONEntry = dictionary.get(1591900);
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        String actualJSON = gson.toJson(toJSONEntry);
//        assertEquals(kireiJSON, actualJSON);
//
//        JMEntry fromJSONEntry = gson.fromJson(actualJSON, JMEntry.class);
//        testKireiEntry(fromJSONEntry);
    }

    @Test
    public void testListener() {
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
