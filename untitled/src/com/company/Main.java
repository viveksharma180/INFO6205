package com.company;

import java.util.*;

public class Main {
    public static void sort(Comparable[] a){
        for(int i =0; i < a.length; i++) insert(i,a);
    }

    public static void insert(int i, Comparable[] a){
        for(int j = i+1; j < a.length; j++){
            if(a[j].compareTo(a[j+1]) > 0) swap(a,j,j+1);
            else break;
        }
    }

    public static void swap(Object[] a, int j, int i){
        Object temp = a[j];
        a[j] = a[i];
        a[i] = temp;
    }

    public static void main(String[] args) {
        Comparable[] a = {"z","f","q","g","a"};
        sort(a);
        for(int i=0; i < a.length;i++){
            System.out.println(a[i]);
        }
    }
}
