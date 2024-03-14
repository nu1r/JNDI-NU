package com.qi4l.jndi.template;

public class isSuccess {
    static {
        System.out.println("static run success");
        isSuccess x = new isSuccess();
        System.out.println(x.getTest());
    }

    public String test = "impl run success";

    public String getTest() {
        return test;
    }

    public String toString() {
        System.out.println("toString run success");
        return "";
    }
}
