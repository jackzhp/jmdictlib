package com.zede.lang.ja;

public class EKP {
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

