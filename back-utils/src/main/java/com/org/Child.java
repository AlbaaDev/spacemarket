package com.org;

public class Child extends Parent {
    @Override
    public void func(int a) {
        System.out.println("Child.func(int):" + a);
    }
}
