package com.mg.community;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaTest {

    public static void main(String[] args) {
        StrT str1 = a -> System.out.println("landba str"+a);
        str1.printStr("lan");
        StrT str2 = b -> b.trim();

        List<String> list = new ArrayList<>();
        list.add("str1");
        list.add("str2  ");
        list.add("str3");
        List<String> stringList = list.stream().map(s -> s.trim()).collect(Collectors.toList());
        for (String s : stringList) {
            s = s + ':';
            System.out.println(s);
        }
        stringList.stream().forEach(s -> {
            s = s + ':';
            System.out.println(s);
        });
    }

    interface StrT {
        void printStr(String message);
    }
}
