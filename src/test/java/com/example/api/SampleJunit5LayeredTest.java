package com.example.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
