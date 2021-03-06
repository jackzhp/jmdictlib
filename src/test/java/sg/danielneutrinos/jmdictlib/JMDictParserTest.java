package sg.danielneutrinos.jmdictlib;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

import sg.danielneutrinos.jmdictlib.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private static HashMap<Character, HashSet<Integer>> map_c_kps = new HashMap<>(); //the size of the key set is 6057, max: 64191
    static HashMap<String, HashSet<Integer>> map_s_kps = new HashMap<>(); //size: 382423, max 47
    static HashMap<Integer, EKP> map_id_kp = new HashMap<>(); //size: 409733
    static int EKPfromChar_ID = 0;


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
        HashMap<Character, HashSet<Integer>> m = map_c_kps; //map
        for (int i = 0; i < ac.length; i++) {
            Character oc = ac[i];
            HashSet<Integer> al = m.get(oc);
            if (al == null) {
                al = new HashSet<>();
                m.put(oc, al);
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

    static <T extends Object> void sortByNumber(HashMap<T, HashSet<Integer>> m, String tag) {
        System.out.println(tag);
        class MapCount {
            T c;//Character c;
            HashSet<Integer> set;

            public MapCount(T key, HashSet<Integer> value) {
                c = key;
                set = value;
            }
        }
        MapCount[] amc = new MapCount[m.size()];
        Set<Map.Entry<T, HashSet<Integer>>> s = m.entrySet();
        int i = 0;
        for (Map.Entry<T, HashSet<Integer>> me : s) {
            amc[i++] = new MapCount(me.getKey(), me.getValue());
        }
        Arrays.sort(amc, (a, b) -> {
            return a.set.size() - b.set.size();
        });
        i = amc.length - 10;
        for (; i < amc.length; i++) {
            MapCount mc = amc[i];
            System.out.println(mc.c + ":" + mc.set.size());
        }
        System.out.println(tag + " size:" + amc.length);
//        print(amc[amc.length - 2].set);

    }

    static void sortByNumber() {
        sortByNumber(map_s_kps, "from string to EKPs");
        sortByNumber(map_c_kps, "from char to EKPs");//                 = map_c_kp; //map

    }


    static void outputMax() {

        System.out.println("size of map_s_kps:" + map_s_kps.size()); //382423
        System.out.println("size of map_id_kp:" + map_id_kp.size()); //409733
        System.out.println("size of map_c_kp:" + map_c_kps.size()); //6057
        if (cMax != null) {
            HashSet<Integer> a = map_c_kps.get(cMax);// map.get(cMax);
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
            if (false) {
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
            } else {
                EKP kp = map_id_kp.get(seqNo);
                System.out.println(kp.s);
            }
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
                doMap(entry);
            }

            @Override
            public void completed() {
                completed = true;
                System.out.println("all completed");
                outputMax();
                sortByNumber();
                parse1();
            }
        });
    }

    static class AnalyzerSentence {
        static char[] tos = new char[]{'な', 'を', 'が'};
        static Comparator<EKP_a> cByID = (a, b) -> {
            return a.kp.id - b.kp.id;
        };
        static Comparator<EKP_a> cByPriority = (a, b) -> {
            return a.priority - b.priority;
        };
        String s;
        char[] ac;
        ArrayList<EKP_a[]> akps = new ArrayList<>();

        ArrayList<Integer> possibleTopicLocation = new ArrayList<>();
        ArrayList<Integer> possibleObjectLocation = new ArrayList<>();
        ArrayList<Integer> possibleSubjectLocation = new ArrayList<>();

        public AnalyzerSentence(String s) {
            this.s = s;
            ac = s.toCharArray();
            for (char c : ac) {
                HashSet<Integer> hs = getEKPfromChar(c);
                EKP_a[] kps = new EKP_a[hs.size()];
                int i = 0;
                for (Integer id : hs) {
                    EKP_a kp = new EKP_a(map_id_kp.get(id));
                    kps[i] = kp;
                }
//                Arrays.sort(kps, cByID);
                akps.add(kps);
            }
        }

        public void pass1() {
            for (int i = 0; i < ac.length; i++) {
                char c = ac[i];
                check(i, c);

            }


        }

        private void check(int loc, char c) {
            if (loc == 0)
                return;
//            Integer[] i0 = new Integer[0];
            EKP_a[] akp_i = akps.get(loc); // getEKPfromChar(c).toArray(i0);
            Arrays.sort(akp_i);
//            char cm1 = ac[loc - 1];
            EKP_a[] akp_im1 = akps.get(loc - 1); // getEKPfromChar(c).toArray(i0);
            Arrays.sort(akp_im1);
            //for any ID in kp_i, is it also in im1? if yes, its priority can be doubled.
            int im1 = 0, i = 0;
            boolean adjusted = false; //priorities adjusted?
            for (; im1 < akp_im1.length && i < akp_i.length; ) { //i++,im1++
                EKP_a kp_im1 = akp_im1[im1], kp_i = akp_i[i];
                int id_im1 = kp_im1.kp.id, id_i = kp_i.kp.id;
                if (id_im1 < id_i) {
                    im1++;
                    continue;
                }
                if (id_im1 == id_i) {
                    //is it a match? maybe, maybe not. if "abcdefg" is an EKP, but "de" is not an EKP, then it is not a match.
                    EKP kp = kp_im1.kp;
                    char[] ac=kp.getCharArray();


                    if (kp_im1.kp.iloc + 1 == kp_i.kp.iloc) {
                        EKP_a kpa = trap(kp_im1, kp_i);
                        akp_im1[im1] = akp_i[i] = kpa;
                        //try to extend backward, and forward
                        int imn = extendBackward(loc - 1, kpa);
                        int ipn = extendForward(loc, kpa);
                    }
                    im1++;
                    i++;
                    continue;
                }
            }
        }

        private int extendBackward(int locm1, EKP_a kpa) {
            int locm2 = locm1;
            while (locm2 > 0) {
                locm1 = locm2;
                locm2 = locm1 - 1;
                EKP_a[] akpa = akps.get(locm2); // getEKPfromChar(c).toArray(i0);
                for (EKP_a t : akpa) {
                    if (t.kp == kpa.kp) {

                    }
                }


            }
            return 0;
        }

        private EKP_a trap(EKP_a kp_im1, EKP_a kp_i) {
            if (kp_im1.kp != kp_i.kp)
                throw new IllegalArgumentException(kp_im1.kp.id + ":" + kp_im1.kp.s + " vs " + kp_i.kp.id + ":" + kp_i.kp.s);
            EKP_a r = new EKP_a(kp_im1.kp);
            if (kp_im1.priority != kp_i.priority) {
                System.out.println("not same " + kp_im1.priority + ":" + kp_i.priority);
            }
            r.priority = Math.max(kp_im1.priority, kp_i.priority); //both of them should be same.
            r.priority++;
//            kp_im1.priority++;
//            kp_i.priority++;
            return r;
        }
    }

    static HashSet<Integer> getEKPfromChar(char c) {
        return map_c_kps.get(c);
    }

    private static void parse1() {
        String s = "「な」は、名前を意味する言葉として使用されることがある。";
//        String s="Aは、名前を意味する言葉として使用されることがある。";
        AnalyzerSentence as = new AnalyzerSentence(s);
        as.pass1();


    }

    private static void doMap(JMEntry entry) {
        int seqNo = entry.getEntrySequence();
        if (seqNo < 0) {
            System.out.println("#<0:" + seqNo);
        } else {
//                    System.out.println(seqNo);
        }
        if (false) {
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
        } else {
            HashSet<String> hss = new HashSet<>();
            List<KanjiElement> a = entry.getKanjiElements();
            for (KanjiElement k : a) {
                String s = k.getKanji();
                hss.add(removePunctuation_dot(s));
            }
            List<ReadingElement> b = entry.getReadingElements();
            for (ReadingElement e : b) {
                String s = e.getKana();
                hss.add(removePunctuation_dot(s));
            }
            int size = hss.size();
            for (String s : hss) {
                if (size > 1) {
//                    System.out.println(s);
                }
                HashSet<Integer> kps = map_s_kps.get(s);
                if (kps == null) {
                    kps = new HashSet<>();
                    map_s_kps.put(s, kps);
                } else {
                    //this happened when the same string has different meanings.
//                    System.out.println(s + ":" + seqNo + " already mapped from " + kps.size());
                }
                EKP kp = new EKP();
                kp.s = s;
                kp.seqNoJMdict = seqNo;
                kps.add(kp.id);
                map_id_kp.put(kp.id, kp);
                doMap(s, kp.id);
            }
//            System.out.println();
        }
    }

    private static String removePunctuation_dot(String s) {
        StringBuilder sb = new StringBuilder();
        char[] ac = s.toCharArray();
        for (char c : ac) {
            if (c == '.') {
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    static class EKP { //this is an EKP about the char at s.charAt(iloc)
        int id;
        String s; //better to use char[]?
        int iloc; //where is the char
        int seqNoJMdict = -1;
        int type; //not used.

        EKP() {
            id = EKPfromChar_ID++;
        }

        char[] getCharArray() {
            return s.toCharArray();
        }
    }

    static class EKP_a {
        EKP kp;
        int priority;

        public EKP_a(EKP ekp) {
            this.kp = kp;
        }
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
