package model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test file management.
 * @author Piotr Gazda
 * @version 1.0
 * @since 2.0
 */
class FileManagerTest {

    /**
     * Test that checks whether the HTML documents are correctly identified.
     * @param documentHTMLState Enum that determines if a document featuring HTML tag brackets
     *                          will be processed by the tested method.
     * @throws IOException Exception that indicates error during writing to the test file.
     */
    @ParameterizedTest
    @EnumSource(documentToBeReadHTMLFeatures.class)
    void isHTMLDocument(documentToBeReadHTMLFeatures documentHTMLState) throws IOException {
        String filename = "myfile.html";
        FileManager myFileManager = new FileManager();
        myFileManager.setFilePath(filename);
        File myObj = new File("myfile.html");
        if(myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter("myfile.html");


            if (documentHTMLState == documentToBeReadHTMLFeatures.HTML) {
                myWriter.write("<htmltag>");
                myWriter.write("</htmltag>");
                myWriter.close();
                assertTrue(myFileManager.isHTMLDocument());
            } else if (documentHTMLState == documentToBeReadHTMLFeatures.NOT_HTML) {
                myWriter.write("htmltag");
                myWriter.write("/htmltag");
                myWriter.close();
                assertFalse(myFileManager.isHTMLDocument());
            }
        }
    }

    /**
     * Enum to describe situations when a file is empty and when a file is filled.
     */
    enum documentToBeReadBasicFeatures{EMPTY, PROPER};
    /**
     * Enum to describe situations when a file has HTML traits and when it doesn't have.
     */
    enum documentToBeReadHTMLFeatures{HTML, NOT_HTML};


    /**
     * Test that aims to check whether proper exceptions (or if any) are thrown
     * by readDocument method depending on input file state.
     * @param documentState Value of an enum determining document state.
     * @throws IOException Exception that may occure while testing - not of interest.
     */
    @ParameterizedTest
    @EnumSource(documentToBeReadBasicFeatures.class)
    void readDocument(documentToBeReadBasicFeatures documentState) throws IOException {
        String filename = "myfile.html";
        FileManager myFileManager = new FileManager();
        myFileManager.setFilePath(filename);
         if (documentState == documentToBeReadBasicFeatures.EMPTY) {
            File myObj = new File("myfile.html");
            if (myObj.createNewFile()) {
                Exception exception = assertThrows(FileNotFoundException.class, myFileManager::readDocument);
                String expectedMessage = "The file" + filename + " is empty.";
                String actualMessage = exception.getMessage();
                assertTrue(actualMessage.contains(expectedMessage));
            }
        } else if (documentState == documentToBeReadBasicFeatures.PROPER) {
            File myObj = new File("myfile.html");
            if (myObj.createNewFile()) {
                FileWriter myWriter = new FileWriter("myfile.html");
                myWriter.write("<open>");
                myWriter.write("</open>");
                myWriter.close();
                Exception exception = assertThrows(FileNotFoundException.class, myFileManager::readDocument);
                assertNull(exception);
            }
        }
    }
}