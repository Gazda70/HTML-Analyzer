package model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing core logic.
 * @author Piotr Gazda
 * @version 1.0
 * @since 2.0
 */
@RunWith(Parameterized.class)
class AnalyzerTest {

    /**
     * Enum to determine what kind of document will be put to HTML analyzing method;
     */
    enum documentTagState{HAS_ENDING_TAGS, NO_ENDING_TAG, IMPROPER_TAG};

    /**
     * Method for checking if an opening HTML tag is correct.
     * @param tag Tag to be checked
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"<tag>", "<1234>", "<>"})
    void checkSingleOpeningTagIntegrity(String tag) {
        Analyzer testAnalyzer = new Analyzer();
        assertTrue(testAnalyzer.checkSingleOpeningTagIntegrity(tag));
    }

    /**
     * Method for checking if an ending HTML tag is correct.
     * @param endingTag Tested ending tag.
     */
    @ParameterizedTest
    @NullAndEmptySource
    @CsvSource({"</tag>" ,"<1234>"})
    void checkSingleEndingTagIntegrity(String endingTag) {
        Analyzer testAnalyzer = new Analyzer();
        assertTrue(testAnalyzer.checkSingleEndingTagIntegrity( endingTag));
    }

    /**
     * Method that checks if function to determine HTML
     * correctness is working properly for different kind of HTML documents.
     * @param documentType Enum argument to determine what kind of document will be tested.
     */
    @ParameterizedTest
    @EnumSource(documentTagState.class)
    void checkTagPair(documentTagState documentType) {
        Analyzer testAnalyzer = new Analyzer();
        ArrayList<String> testInputDocument = new ArrayList<>();
        ArrayList<String>  testDocumentWithNotifiedErrors = new ArrayList<>();
        ArrayList<String> testDocumentWithCorrectedErrors = new ArrayList<>();

        if(documentType == documentTagState.HAS_ENDING_TAGS){
            testInputDocument.add("<testtag>");
            testInputDocument.add("<testinnertag>");
            testInputDocument.add("</testinnertag>");
            testInputDocument.add("</testtag>");
            testDocumentWithNotifiedErrors = new ArrayList<>(Arrays.asList(
                    "<testtag>","<testinnertag>","</testinnertag>","</testtag>"
            ));
            testDocumentWithCorrectedErrors = new ArrayList<>(Arrays.asList(
                    "<testtag>","<testinnertag>","</testinnertag>","</testtag>"
            ));
        }else if(documentType == documentTagState.NO_ENDING_TAG){
            testInputDocument.add("<testtag>");
            testInputDocument.add("<testinnertag>");
            testInputDocument.add("</testinnertag>");
            testDocumentWithNotifiedErrors = new ArrayList<>(Arrays.asList(
                    "<testtag>","<testinnertag>","</testinnertag> *"
            ));
            testDocumentWithCorrectedErrors = new ArrayList<>(Arrays.asList(
                    "<testtag>","<testinnertag>","</testinnertag>","</testtag>"
            ));
        }else if(documentType == documentTagState.IMPROPER_TAG){
            testInputDocument.add("<znacznik>");
            testInputDocument.add("<drugi");
            testInputDocument.add("</drugi>");
            testInputDocument.add("</znacznik>");
            testDocumentWithNotifiedErrors = new ArrayList<>(Arrays.asList(
                    "<znacznik>","<drugi","</drugi> * *", "</znacznik>"
            ));
            testDocumentWithCorrectedErrors = new ArrayList<>(Arrays.asList(
                    "<znacznik>","<drugi","></drugi>","</znacznik>"
            ));
        }

        testAnalyzer.setAnalyzedLines(testInputDocument);

        while(testAnalyzer.getCurrentLineIndex() < testAnalyzer.getAnalyzedLines().size()) {
            testAnalyzer.checkTagPair("", false);
        }

        ArrayList<String> correctedDocument = testAnalyzer.getCorrectedLines();
        ArrayList<String> notifiedErrorsDocument = testAnalyzer.getLinesWithErrorInfo();

        for(int i = 0; i < testInputDocument.size(); i++){
            assertEquals(notifiedErrorsDocument.get(i), testDocumentWithNotifiedErrors.get(i));
            assertEquals(correctedDocument.get(i), testDocumentWithCorrectedErrors.get(i));
        }

    }
}