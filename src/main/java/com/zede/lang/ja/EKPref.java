package com.zede.lang.ja;

public class EKPref { //this is an EKPref about the char at s.charAt(iloc)
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
