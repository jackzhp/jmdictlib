package com.zede.lang.ja;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class AnalyzerSentence { //Sentence and below
    static char[] tos = new char[]{'な', 'を', 'が'};
    static Comparator<ABC.EKP_a> cByID = (a, b) -> {
        return a.kp.id - b.kp.id;
    };
    static Comparator<ABC.EKP_a> cByPriority = (a, b) -> {
        return a.priority - b.priority;
    };
    String s;
    char[] ac;
    //        ArrayList<EKP_a[]> akps = new ArrayList<>();
    ABC.EKP_a[][] akps;

    ArrayList<Integer> possibleTopicLocation = new ArrayList<>();
    ArrayList<Integer> possibleObjectLocation = new ArrayList<>();
    ArrayList<Integer> possibleSubjectLocation = new ArrayList<>();

    public AnalyzerSentence(char[] ac) { //String s
//            this.s = s;
        this.ac = ac;// s.toCharArray();
        akps = new ABC.EKP_a[ac.length][];
    }

    int nPartitions;
    static Comparator<Partition> cPartitionByLength = (a, b) -> {
        return a.seqNo - b.seqNo;
    };

    class Partition {
        Partition left; //parent.
        int loc; //index of ac
        ABC.EKP_a kpa;
        int seqNo; //=left.seqNo+1;

        public Partition(Partition pLeft, int ilocUser, ABC.EKP_a kpa) {
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
            ABC.EKP_a[] aa = new ABC.EKP_a[seqNo];
            Partition p = this;
            for (int i = aa.length - 1; i >= 0; i--) {
                aa[i] = p.kpa;
                p = p.left;
            }
            System.out.println("length:" + seqNo);
            for (int i = 0; i < aa.length; i++) {
                ABC.EKP_a kpa = aa[i];
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
            ArrayList<ABC.EKPref> hs = getEKPfromChar(c);
            if (hs == null) {
//                    System.out.println(c + " is not mapped, so this is an unknown");
                ABC.EKP kp = new ABC.EKP(new char[]{c});
                ABC.EKP_a kpa = new ABC.EKP_a(kp, ilocUser);
//                    akps[ilocUser] = new EKP_a[]{kpa};
                Partition p = new Partition(pLeft, ilocUser, kpa);
                al_kps.add(p);
            } else {
//                    EKP_a[] kps = new EKP_a[hs.size()];
//                    int i = 0;
                for (ABC.EKPref kpRef : hs) {
//                        int id = kpRef.kp.id;
                    ABC.EKP_a kpa = possibleMatch(ilocUser, kpRef);
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

    private ABC.EKP_a possibleMatch(int ilocUser, ABC.EKPref kpRef) {
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
        ABC.EKP_a kpa = new ABC.EKP_a(kpRef.kp, ilocUser);
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
        ABC.EKP_a[][] akps_sub = null;
        if (akps != null)
            akps_sub = new ABC.EKP_a[ac_sub.length][];
        if (n > 0) {
            System.arraycopy(ac, 0, ac_sub, 0, n);
            if (akps != null)
                System.arraycopy(akps, 0, akps_sub, 0, n);
        }
        AnalyzerSentence as = new AnalyzerSentence(ac_sub);
        //TODO: akps

        char[] ac_new = new char[ac.length - (iend - istart)];
        ABC.EKP_a[][] akps_new = null;
        if (akps != null)
            akps_new = new ABC.EKP_a[ac_new.length][];
        if (istart > 0) {
            System.arraycopy(ac, 0, ac_new, 0, istart);
            if (akps != null)
                System.arraycopy(akps, 0, akps_new, 0, istart);
        }
        ac_new[istart] = cLambda;
        ABC.EKP_a kpa = new ABC.EKP_a(kpLambda, istart);
        kpa.extra = as;
        akps_new[istart] = new ABC.EKP_a[]{kpa};
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
        ABC.EKP_a[] akp_i = akps[loc]; // akps.get(loc); // getEKPfromChar(c).toArray(i0);
        Arrays.sort(akp_i);
//            char cm1 = ac[loc - 1];
        ABC.EKP_a[] akp_im1 = akps[loc - 1]; // akps.get(loc - 1); // getEKPfromChar(c).toArray(i0);
        Arrays.sort(akp_im1);
        //for any ID in kp_i, is it also in im1? if yes, its priority can be doubled.
        int im1 = 0, i = 0;
        boolean adjusted = false; //priorities adjusted?
        for (; im1 < akp_im1.length && i < akp_i.length; ) { //i++,im1++
            ABC.EKP_a kp_im1 = akp_im1[im1], kp_i = akp_i[i];
            int id_im1 = kp_im1.kp.id, id_i = kp_i.kp.id;
            if (id_im1 < id_i) {
                im1++;
                continue;
            }
            if (id_im1 == id_i) {
                //is it a match? maybe, maybe not. if "abcdefg" is an EKPref, but "de" is not an EKPref, then it is not a match.
                ABC.EKP kp = kp_im1.kp;
                char[] ac = kp.getCharArray();


                if (kp_im1.iloc + 1 == kp_i.iloc) {
                    ABC.EKP_a kpa = trap(kp_im1, kp_i);
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

    private int extendBackward(int locm1, ABC.EKP_a kpa) {
        int locm2 = locm1;
        while (locm2 > 0) {
            locm1 = locm2;
            locm2 = locm1 - 1;
            ABC.EKP_a[] akpa = akps[locm2];// akps.get(locm2); // getEKPfromChar(c).toArray(i0);
            for (ABC.EKP_a t : akpa) {
                if (t.kp == kpa.kp) {

                }
            }


        }
        return 0;
    }

    private ABC.EKP_a trap(ABC.EKP_a kp_im1, ABC.EKP_a kp_i) {
        if (kp_im1.kp != kp_i.kp)
            throw new IllegalArgumentException(kp_im1.kp.id + ":" + kp_im1.kp.s + " vs " + kp_i.kp.id + ":" + kp_i.kp.s);
        ABC.EKP_a r = new ABC.EKP_a(kp_im1.kp, 0);
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
