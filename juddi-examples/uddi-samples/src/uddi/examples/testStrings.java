/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.examples;

import java.util.concurrent.atomic.AtomicReference;
import javax.xml.ws.Holder;

/**
 * A simple program to illistrate how to pass by "reference" vs by "value" in Java. Or more accurately, how to persist changes on 
 * method parameters to the caller. Written mostly because I forget it frequently and use this as reference material.
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class testStrings {
        
    public static void main(String[] args) {
        String str = "hi";                                                      
        System.out.println(str);                                                //hi
        System.out.println(Test1(str));                                         //hir
        System.out.println(Test2(str));                                         //hix
        Test3(str);
        System.out.println(str);                                                //hi no change
        Holder<String> holder = new Holder<String>();   
        holder.value = str;
        Test4(holder);
        System.out.println(str);                                                //hi no change
        System.out.println(holder.value);                                       //hiw changed persists

        AtomicReference<String> astr = new AtomicReference<String>();
        astr.set(str);
        Test5(astr);        
        System.out.println(str);                                                //hi no change
        System.out.println(astr.get());                                         //hit change persists

    }

    static String Test1(String s) {
        return s + "r";
    }

    static String Test2(String s) {
        s += "x";
        return s;
    }

    static void Test3(String s) {
        s += "z";
    }

    static void Test4(Holder<String> s) {
        s.value += "w";
    }

    static void Test5(AtomicReference<String> s) {
        s.set(s.get() + "t");
    }
}
