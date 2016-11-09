package com.company;

import com.company.type.NoCoverage;




/**
 * Created by arabbani on 5/23/16.
 */
public class JavaReadable implements IJavaReadable{


    public JavaReadable(){
        System.out.println("Java readable constructed...");
    }

     public void method1() {
        System.out.println("method1");
        int counter = 0;
        for(int i = 0 ;  i < 10; i++) {
            counter++;
        }
        int i = 9;
        System.out.println("method1 counter "+counter);
        System.out.println("method1 " + i);
    }

    @Deprecated
    public  void method2() {
        System.out.println("method2");
        int counter = 0;
        for(int i = 0 ;  i < 10; i++) {
            counter++;
        }
        int i = 9;
        System.out.println("method2 counter "+counter);
        System.out.println("method2 " + i);
    }

    @Override
    public  String toString(){
        return "String representation";
    }
}
