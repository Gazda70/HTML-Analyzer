package model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing EmptyFileException class.
 * @author Piotr Gazda
 * @version 1.0
 * @since 2.0
 */
class EmptyFileExceptionTest {

    /**
     * Test that aims to determine if the exception class creates proper output message.
     * @param filename Name of supposed file while processing of which exception occured.
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"myfile.txt", "myfile.html", ""})
    void testToString(String filename) {
        try{
            throw new EmptyFileException(filename);
        }catch(EmptyFileException testedException){
            assertTrue(testedException.toString().equals("The file " + filename + " is empty."));
        }
    }
}