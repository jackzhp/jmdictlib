package com.zede.lang.ja;

public class EKP_a {
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
