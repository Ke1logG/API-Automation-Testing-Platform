package com.example.api.demo;

import org.junit.jupiter.api.*;

public class SampleJunit5Test {

    @RepeatedTest(value = 3,name = "{displayName} {currentRepetition} of {totalRepetitions}")
    @DisplayName("第一个用例")
    public void testRepeat(){
        System.out.println("repeat");

    }

}
