package com.example.api;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class SampleJunit5Test {

   /*@BeforeAll
   public static void test1(){
       System.out.println("*******");
   }
   @BeforeEach
   public void test2(){
       System.out.println("Before");
   }
   @AfterEach
   public void test3(){
       System.out.println("After");
   }
   @AfterAll
   public static void test4(){
       System.out.println("-----");
   }
   @Test
   @DisplayName("第一个用例")
   public void API1(){
       System.out.println("test1");
       assertEquals(2,1+1);
   }
   @Test
   @DisplayName("第二个用例")
   public void API2(){
       System.out.println("test2");
       assertEquals(2,1+1);
   }*/


   /*@Test
   public void assertTest1(){
       System.out.println("assertTest");
       assertEquals(2,1+1);
       assertTrue(true);
       assertTrue(3>1);
       assertNotNull(1);
   }*/
   /*@Test
   public void assertTest2(){
       System.out.println("assertTest");
       assertAll("test",
               ()-> assertEquals(2,1+1),
               ()-> assertTrue(3>1),
               ()-> assertEquals(3,1+1),
               ()-> assertEquals(4,1+1));
   }*/

   /*@ParameterizedTest
   @ValueSource(strings = {"hello", "world", "junit5"})
   public void testWithStringParam(String word) {
       assertNotNull(word);
       assertTrue(word.length()>0);
   }

   @ParameterizedTest
   @MethodSource("testMethod1")
   public void testWithStringParam2(Integer id){
       System.out.println(id);
   }
   public static Stream<Integer> testMethod1(){
       return Stream.of(1,2);
   }

   @ParameterizedTest
   @MethodSource("testMethod2")
   public void testWithStringParam3(String name,Integer id){
       System.out.println(name+","+id);
   }
   public static Stream<Arguments> testMethod2(){
       return Stream.of(
              Arguments.arguments("kl",21),
              Arguments.arguments("jy",20)
              );
   }*/

    @RepeatedTest(value = 3,name = "{displayName} {currentRepetition} of {totalRepetitions}")
    @DisplayName("第一个用例")
    public void testRepeat(){
        System.out.println("repeat");

    }

}
