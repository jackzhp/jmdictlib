package com.zede.lang.ja;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

//import org.junit.BeforeClass;
//import org.junit.Test;

import sg.danielneutrinos.jmdictlib.JMDictParser;
import sg.danielneutrinos.jmdictlib.ParseEventListener;
import sg.danielneutrinos.jmdictlib.data.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import static org.junit.Assert.*;

public class ABC {

    private static Map<Integer, JMEntry> dictionary;
    private final String kireiJSON = "{\"entrySequence\":1591900,\"kanjiElements\":[{\"kanji\":\"綺麗\",\"primaries\":[\"spec1\"],\"info\":[]},{\"kanji\":\"奇麗\",\"primaries\":[\"ichi1\"],\"info\":[]},{\"kanji\":\"暉麗\",\"primaries\":[],\"info\":[\"word containing out-dated kanji\"]}],\"readingElements\":[{\"kana\":\"きれい\",\"notKanjiReading\":false,\"kanjiReadings\":[],\"primaries\":[\"ichi1\",\"spec1\"],\"info\":[]},{\"kana\":\"キレイ\",\"notKanjiReading\":true,\"kanjiReadings\":[],\"primaries\":[\"spec1\"],\"info\":[]}],\"senses\":[{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[\"adjectival nouns or quasi-adjectives (keiyodoshi)\"],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"pretty\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"lovely\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"beautiful\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"fair\",\"primaries\":[]}],\"info\":null},{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"clean\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"clear\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"pure\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"tidy\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"neat\",\"primaries\":[]}],\"info\":null},{\"invalidSenseKanjis\":[],\"invalidSenseReadings\":[],\"xReferences\":[],\"antonyms\":[],\"partsOfSpeech\":[],\"fields\":[],\"misc\":[\"word usually written using kana alone\"],\"sourceLanguages\":[],\"dialects\":[],\"glosses\":[{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"completely\",\"primaries\":[]},{\"language\":\"eng\",\"gender\":null,\"type\":null,\"text\":\"entirely\",\"primaries\":[]}],\"info\":\"as きれいに\"}]}";
    private static int entryParsedCounter = 0;
    private static boolean completed = false;

    static Character cMax;
    static int max; //how many entries for each key? what is its max?
    static HashMap<Character, HashSet<Integer>> map = new HashMap<>(); //the size of the key set is 6057.
    //if I always search from left to right, then this had better to sorted.
    //   and if EKPref.iloc should always be 0 or infinite(infinity is for the case the "を" in "objectを", the "が" in "subjectが")
    //  for EKPref.iloc not 0 or infinite, they should not be in the set.
    private static HashMap<Character, ArrayList<EKPref>> map_c_kps = new HashMap<>(); //the size of the key set is 6057,5452, max: 64191
    static HashMap<String, HashSet<Integer>> map_s_kps = new HashMap<>(); //size: 382423, max 47. feel that this not language related.
    static HashMap<Integer, EKP> map_id_kp = new HashMap<>(); //size: 409733

    static Comparator<EKPref> cEKPrefByLength = (a, b) -> {
        char[] ac1 = a.kp.getCharArray(), ac2 = b.kp.getCharArray();
        int d = ac1.length - ac2.length;
        if (d == 0) {
            for (int i = 0; i < ac1.length; i++) {
                char c1 = ac1[i], c2 = ac2[i];
                if (c1 < c2) return -1;
                if (c1 > c2) return 1;
            }
            //two string are same Arrays.equals(ac1, ac2)
            if (a.iloc < b.iloc) return -1;
            if (a.iloc > b.iloc) return 1;
            //this should not happen, both are same.
            System.out.println(new String(ac1) + ":" + new String(ac2));
            return 0;
        }
        return d;
    };

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
    static void doMap(EKP kp) { //String s, int seqNo
//        String s = kp.s;
//        int seqNo
        char[] ac = kp.getCharArray();// s.toCharArray();
        HashMap<Character, ArrayList<EKPref>> m = map_c_kps; //map
        int i = 0; //for (int i = 0; i < ac.length; i++)
        {
            Character oc = ac[i];
            ArrayList<EKPref> hs = m.get(oc);
            if (hs == null) {
                hs = new ArrayList<>();
                m.put(oc, hs);
            }
//            EKP kp = new EKP(s);
//            kp.seqNoJMdict = seqNo;
//            map_id_kp.put(kp.id, kp);
            EKPref kpRef = new EKPref(kp, i);
            hs.add(kpRef);
            int n = hs.size();
            if (n > max) {
                cMax = oc;
                max = n;
//                System.out.println("max:" + max + " " + cMax); //max:59586 う
            }
        }
    }

    static <T extends Object, T2 extends Object> void sortByNumber(HashMap<T, HashSet<T2>> m, String tag) {
        System.out.println(tag);
        class MapCount {
            T c;//Character c;
            HashSet<T2> set;

            public MapCount(T key, HashSet<T2> value) {
                c = key;
                set = value;
            }
        }
        MapCount[] amc = new MapCount[m.size()];
        Set<Map.Entry<T, HashSet<T2>>> s = m.entrySet();
        int i = 0;
        for (Map.Entry<T, HashSet<T2>> me : s) {
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
//        sortByNumber(map_c_kps, "from char to EKPs");//                 = map_c_kp; //map

    }


    static void outputMax() {

        System.out.println("size of map_s_kps:" + map_s_kps.size()); //382423
        System.out.println("size of map_id_kp:" + map_id_kp.size()); //409733
        System.out.println("size of map_c_kp:" + map_c_kps.size()); //6057
        if (cMax != null) {
            ArrayList<EKPref> a = map_c_kps.get(cMax);// map.get(cMax);
            print(a);
        } else {
            System.out.println("which one with max mapped entries, don't know");
        }
    }

    static void print(ArrayList<EKPref> a) {
        int n = 0, nMax = 10;
        for (EKPref kpRef : a) {
            int seqNo = kpRef.kp.id; //kpRef.kp.seqNoJMdict
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

    public static void main(String[] args) {
        try {
            oneTimeSetUp();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void oneTimeSetUp() throws Exception {
        addLambda();
        JMDictParser parser = new JMDictParser();
        dictionary = parser.getDictionary();
        parser.parse(new ParseEventListener() {
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
                analyzeSentence_1();
            }
        });
    }

    static char cLambda = '\u039B'; //greek capital letter lamda      '\u1D726'; //mathematical bold italic capital lamda
    static EKP kpLambda;

    private static void addLambda() {
        kpLambda = new EKP(new char[]{cLambda});
        kpLambda.type = 1; //for temp. to indicate this is not a ordinary character, but a replacement.
        map_id_kp.put(kpLambda.id, kpLambda);
        EKPref kpRef = new EKPref(kpLambda, 0);
        ArrayList<EKPref> hs = new ArrayList<>();
        hs.add(kpRef);
        map_c_kps.put(cLambda, hs);
    }


    static ArrayList<EKPref> getEKPfromChar(char c) {
        return map_c_kps.get(c);
    }

    private static void analyzeSentence_1() {
        String s;
        s = "「な」は、名前を意味する言葉として使用されることがある。";
//        s = "名前は、名前を意味する言葉として使用されることがある。";
        AnalyzerSentence as = new AnalyzerSentence(s.toCharArray());
        as.analyze();
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
                EKP kp = new EKP(s);
                kp.seqNoJMdict = seqNo;
                map_id_kp.put(kp.id, kp);
                doMap(kp);
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
                EKP kp = new EKP(s);
                kp.seqNoJMdict = seqNo;
                map_id_kp.put(kp.id, kp);
                doMap(kp);
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
                EKP kp = new EKP(s);
                kp.seqNoJMdict = seqNo;
                kps.add(kp.id);
                map_id_kp.put(kp.id, kp);
                doMap(kp);
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





}
