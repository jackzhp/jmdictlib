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

    static class AnalyzerSentence { //Sentence and below
        static char[] tos = new char[]{'な', 'を', 'が'};
        static Comparator<EKP_a> cByID = (a, b) -> {
            return a.kp.id - b.kp.id;
        };
        static Comparator<EKP_a> cByPriority = (a, b) -> {
            return a.priority - b.priority;
        };
        String s;
        char[] ac;
        //        ArrayList<EKP_a[]> akps = new ArrayList<>();
        EKP_a[][] akps;

        ArrayList<Integer> possibleTopicLocation = new ArrayList<>();
        ArrayList<Integer> possibleObjectLocation = new ArrayList<>();
        ArrayList<Integer> possibleSubjectLocation = new ArrayList<>();

        public AnalyzerSentence(char[] ac) { //String s
//            this.s = s;
            this.ac = ac;// s.toCharArray();
            akps = new EKP_a[ac.length][];
        }

        int nPartitions;
        static Comparator<Partition> cPartitionByLength = (a, b) -> {
            return a.seqNo - b.seqNo;
        };

        class Partition {
            Partition left; //parent.
            int loc; //index of ac
            EKP_a kpa;
            int seqNo; //=left.seqNo+1;

            public Partition(Partition pLeft, int ilocUser, EKP_a kpa) {
                this.left = pLeft;
                this.loc = ilocUser;
                this.kpa = kpa;
                this.seqNo = pLeft.seqNo + 1;
                nPartitions++;
            }

            public Partition() {
                seqNo = loc = -1; //the head
            }

            public int locNext() {
                if (loc == -1)
                    return 0;
                return loc + kpa.kp.getCharArray().length;
            }

            public void present() {
                EKP_a[] aa = new EKP_a[seqNo];
                Partition p = this;
                for (int i = aa.length - 1; i >= 0; i--) {
                    aa[i] = p.kpa;
                    p = p.left;
                }
                System.out.println("length:" + seqNo);
                for (int i = 0; i < aa.length; i++) {
                    EKP_a kpa = aa[i];
                    System.out.println(kpa.kp.s);
                }
            }
        }

        Partition head = new Partition();
        ArrayList<Partition> tails = new ArrayList<>(); //could be sorted by its length, or by the confidence assigned to it.

        ArrayList<Partition> initPartitions(Partition pLeft) {
            ArrayList<Partition> al_kps = new ArrayList<>();
            int ilocUser = pLeft.locNext(); //for (int ilocUser = 0; ilocUser < ac.length; ilocUser++)
            {
                if (ilocUser >= ac.length) {
                    tails.add(pLeft);
                    onTailsChanged();
                    return al_kps;
                }
                char c = ac[ilocUser];
                ArrayList<EKPref> hs = getEKPfromChar(c);
                if (hs == null) {
//                    System.out.println(c + " is not mapped, so this is an unknown");
                    EKP kp = new EKP(new char[]{c});
                    EKP_a kpa = new EKP_a(kp, ilocUser);
//                    akps[ilocUser] = new EKP_a[]{kpa};
                    Partition p = new Partition(pLeft, ilocUser, kpa);
                    al_kps.add(p);
                } else {
//                    EKP_a[] kps = new EKP_a[hs.size()];
//                    int i = 0;
                    for (EKPref kpRef : hs) {
//                        int id = kpRef.kp.id;
                        EKP_a kpa = possibleMatch(ilocUser, kpRef);
                        if (kpa != null) {
//                            al_kps.add(kpa);
                            Partition p = new Partition(pLeft, ilocUser, kpa);
                            al_kps.add(p);
                        }
                    }
//                Arrays.sort(kps, cByID);
//                    akps[ilocUser] = al_kps.toArray(new EKP_a[0]); // kps; //akps.add(kps);
                    //for every known, there might be a new EKP. ignore for now.
                }
            }
            return al_kps;
        }

        //        int minTailLength=Integer.MAX_VALUE;
        Partition min;

        private void onTailsChanged() {
            Collections.sort(tails, cPartitionByLength);
            if (min == null) {
                min = tails.get(0);
                return;
            }
            Partition p = tails.get(0);
            if (p != min) {
                min = p;
                p.present();
            }
//            System.out.println("# of tails:" + tails.size() + " shortest:" + tails.get(0).seqNo);

        }

        ArrayList<Partition> t = new ArrayList<>();

        void initPartitions() {
            t.add(head);
            while (true) {
                int i = t.size();
                if (i == 0)
                    break;
                Partition p = t.remove(i - 1);
                while (true) {
                    ArrayList<Partition> ps = initPartitions(p);
                    i = ps.size();
                    if (i > 0) {
                        i--;
                        for (; i > 0; i--)
                            t.add(ps.get(i));
                        p = ps.get(0); //ps = initPartitions(ps.get(0));
                    } else break;
                }
//                System.out.println("total partition objects:" + nPartitions);
            }
            System.out.println("total partition objects:" + nPartitions);
            System.out.println("# of tails:" + tails.size());
            Partition[] atails = tails.toArray(new Partition[0]);
            Arrays.sort(atails, cPartitionByLength);
            int ilimit = atails.length - 10;
            if (ilimit < 0)
                ilimit = 0;
            for (int i = atails.length - 1; i >= ilimit; i--) {
                Partition p = atails[i];
                p.present();
            }

        }

        private EKP_a possibleMatch(int ilocUser, EKPref kpRef) {
            if (kpRef.iloc != 0) {
                // it is "を"in "~を"
                if (ilocUser == 0)
                    return null; //does not match
                //TODO: if ilocUser is a seperator, then does not match either.
            }
            char[] act = kpRef.kp.getCharArray();
            for (int i = 0; i < act.length; i++) {
                char c = ac[ilocUser + i], ct = act[i];
                if (c != ct)
                    return null;
            }
            EKP_a kpa = new EKP_a(kpRef.kp, ilocUser);
            return kpa;
        }

        static char[][] aaq = new char[][]{"「」".toCharArray()};

        public void replaceLambda() { //not able to deal with nesting yet.
            for (int istart = 0; istart < ac.length; istart++) {
                char c = ac[istart];
                for (int i = 0; i < aaq.length; i++) {
                    char tstart = aaq[i][0];
                    if (c == tstart) { //start found
                        char tend = aaq[i][1];
                        for (int iend = istart + 1; iend < ac.length; iend++) {
                            c = ac[iend];
                            if (c == tend) { //end found
                                lambda(istart, iend);
                            }
                        }
                    }
                }
            }
        }

        private void lambda(int istart, int iend) {
            System.out.println("remove (" + istart + "," + iend + ") from (" + 0 + "," + ac.length + ")");
            int n = iend - istart - 1;
            char[] ac_sub = new char[n]; //from istart+1 to iend-1
            EKP_a[][] akps_sub = null;
            if (akps != null)
                akps_sub = new EKP_a[ac_sub.length][];
            if (n > 0) {
                System.arraycopy(ac, 0, ac_sub, 0, n);
                if (akps != null)
                    System.arraycopy(akps, 0, akps_sub, 0, n);
            }
            AnalyzerSentence as = new AnalyzerSentence(ac_sub);
            //TODO: akps

            char[] ac_new = new char[ac.length - (iend - istart)];
            EKP_a[][] akps_new = null;
            if (akps != null)
                akps_new = new EKP_a[ac_new.length][];
            if (istart > 0) {
                System.arraycopy(ac, 0, ac_new, 0, istart);
                if (akps != null)
                    System.arraycopy(akps, 0, akps_new, 0, istart);
            }
            ac_new[istart] = cLambda;
            EKP_a kpa = new EKP_a(kpLambda, istart);
            kpa.extra = as;
            akps_new[istart] = new EKP_a[]{kpa};
            n = ac.length - iend - 1;
            if (n > 0) {
                System.arraycopy(ac, iend + 1, ac_new, istart + 1, n);
                if (akps != null)
                    System.arraycopy(akps, iend + 1, akps_new, istart + 1, n);
            }
            System.out.println(new String(ac));
            System.out.println(new String(ac_new));
            ac = ac_new;
            if (akps != null)
                akps = akps_new;
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
            EKP_a[] akp_i = akps[loc]; // akps.get(loc); // getEKPfromChar(c).toArray(i0);
            Arrays.sort(akp_i);
//            char cm1 = ac[loc - 1];
            EKP_a[] akp_im1 = akps[loc - 1]; // akps.get(loc - 1); // getEKPfromChar(c).toArray(i0);
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
                    //is it a match? maybe, maybe not. if "abcdefg" is an EKPref, but "de" is not an EKPref, then it is not a match.
                    EKP kp = kp_im1.kp;
                    char[] ac = kp.getCharArray();


                    if (kp_im1.iloc + 1 == kp_i.iloc) {
                        EKP_a kpa = trap(kp_im1, kp_i);
                        akp_im1[im1] = akp_i[i] = kpa;
                        //try to extend backward, and forward
                        int imn = extendBackward(loc - 1, kpa);
//                        int ipn = extendForward(loc, kpa);
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
                EKP_a[] akpa = akps[locm2];// akps.get(locm2); // getEKPfromChar(c).toArray(i0);
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
            EKP_a r = new EKP_a(kp_im1.kp, 0);
            if (kp_im1.priority != kp_i.priority) {
                System.out.println("not same " + kp_im1.priority + ":" + kp_i.priority);
            }
            r.priority = Math.max(kp_im1.priority, kp_i.priority); //both of them should be same.
            r.priority++;
//            kp_im1.priority++;
//            kp_i.priority++;
            return r;
        }

        public void analyze() {
            replaceLambda();
            initPartitions();
//            pass1();

        }
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

    static class EKP {
        int id;
        private char[] ac;
        private String s; //better to use char[]?
        int seqNoJMdict = -1;
        int type; //not used.
        static int IDforEKP = 0;

        EKP() {
            this.id = IDforEKP++;
        }

        EKP(String s) {
            this();
            this.s = s;
            this.ac = s.toCharArray();
        }

        EKP(char[] ac) {
            this();
            this.ac = ac;
            this.s = new String(ac);
        }

        char[] getCharArray() {
            if (ac == null)
                ac = s.toCharArray();
            return ac;
        }

        String getString() {
            if (s == null)
                s = new String(ac);
            return s;
        }
    }

    static class EKPref { //this is an EKPref about the char at s.charAt(iloc)
        EKP kp;
        int iloc; //where is the char in the EKP. it has only 2 value: 0 or infinity. so better use boolean "isStarting"
        //then this could be merged into EKP. but do not do it right away.


        EKPref(EKP kp, int iloc) {
            this.kp = kp;
            this.iloc = iloc;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof EKPref) {
                EKPref o2 = (EKPref) o;
                if (kp.id != o2.kp.id) {
                    return false;
                }
                return iloc == o2.iloc;
            }
            return false;
        }
    }

    static class EKP_a {
        //the sentence refer to the EKP
        //where is it in the sentence? the answer lies where this EKP_a is located.
        EKP kp;
        int iloc; //the location of EKP in a sentence.
        int priority;
        Object extra; //extra data

        public EKP_a(EKP kp, int iloc) {
            this.kp = kp;
            this.iloc = iloc;
        }
    }


}
