package org.example;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Hello world!");
        //Annotation annotation = new Annotation();
        //annotation.addAnnotations();
        //annotation.testPDFHighlight();

        ImageAnnotation imageAnnotation = new ImageAnnotation();
        imageAnnotation.addHighlight();
    }
}