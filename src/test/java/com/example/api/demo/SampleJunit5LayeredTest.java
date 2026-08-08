package com.example.api.demo;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SampleJunit5LayeredTest {

    @Nested
    public class Auther{
        @Nested
        public class Manager{
            @Test
            public void addGoods(){
                System.out.println("add");
            }
            @Test
            public void removeGoods(){
                System.out.println("remove");
            }
        }
        @Test
        public void changeAuther(){
            System.out.println("changeAuther");
        }
    }

    @Nested
    public class User{
        @Test
        public void changeUser(){
            System.out.println("changeUser");
        }

    }

}
