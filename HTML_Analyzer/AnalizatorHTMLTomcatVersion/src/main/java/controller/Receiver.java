package controller;

import model.Analyzer;
import model.EmptyFileException;
import model.FileManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Class that realizes communication between user interface
 * and program logic.
 * @author Piotr Gazda
 * @version 1.0
 * @since 4.0
 */
public class Receiver {

    /**
     * Public constructor.
     * @param myAnalyzer Analyzer object - program logic.
     * @param myFileManager FileManger object - file management system.
     */
    public Receiver(Analyzer myAnalyzer, FileManager myFileManager){
        this.analyzer = myAnalyzer;
        this.fileManager = myFileManager;
    }

    /**
     * Field containing object of class Analyzer which realizes main logic -
     * - HTML processing.
     */
    private final Analyzer analyzer;

    /**
     * Field containing object of class FileManager which realizes
     * file management, file reading.
     */
    private final FileManager fileManager;

    /**
     * Getter for noMatchingBracketErrors from Analyzer
     * @return Number of cases when there wasn't a matching bracket for a tag.
     */
    public int getNoMatchingBracketErrors() {
        return analyzer.getNoMatchingBracketErrors();
    }

    /**
     * Getter for noMatchingTagErrors from Analyzer
     * @return Number of cases when there wasn't a matching ending tag for an opening tag.
     */
    public int getNoMatchingTagErrors(){
        return analyzer.getNoMatchingTagErrors();
    }

    /**
     * Getter for noLowercaseTag from Analyzer
     * @return Number of cases when the tag was not in lowercase.
     */
    public int getNoLowercaseTagErrors(){
        return analyzer.getNoLowercaseTagErrors();
    }

    /**
     * Setter for input file path.
     * @param path Path to input file.
     */
    public void setFilePath(String path){
        this.fileManager.setFilePath(path);
    };

    /**
     * Method that launches methods responsible for file
     * reading as well as for analyzing it.
     * @throws EmptyFileException Thrown when input file is empty.
     * @throws FileNotFoundException Thrown when input file not found.
     */
    public void proceedDocument() throws EmptyFileException, FileNotFoundException {
        fileManager.readDocument();
        if(!fileManager.getDocumentLines().isEmpty()
                && fileManager.isHTMLDocument()) {

            this.analyzer.setAnalyzedLines(fileManager.getDocumentLines());

            while(this.analyzer.getCurrentLineIndex() < this.analyzer.getAnalyzedLines().size()) {
                this.analyzer.checkTagPair("", false);
            }

        }else{
            throw new EmptyFileException(this.fileManager.getFilePath());
        }
    }

    /**
     * Method for launching document processing given an list of strings.
     * @param myDocument List of strings representing document contents.
     */
    public void proceedDocumentGivenAsList(ArrayList<String> myDocument, String filename) throws EmptyFileException{

        if(myDocument.isEmpty())
        {
            throw new EmptyFileException(filename);
        }
        else {
            this.analyzer.setAnalyzedLines(myDocument);

            while (this.analyzer.getCurrentLineIndex() < this.analyzer.getAnalyzedLines().size()) {
                this.analyzer.checkTagPair("", false);
            }
        }

    }
    /**
     * Getter for document lines containing error notifications.
     * @return List of strings containing error notifications.
     */
    public ArrayList<String> getDocumentWithErrorsNotification(){
        return analyzer.getLinesWithErrorInfo();
    }

    /**
     * Getter for document lines with corrected errors.
     * @return List of strings containing corrected errors.
     */
    public ArrayList<String> getDocumentWithErrorsCorrected(){
        return analyzer.getCorrectedLines();
    }

    /**
     * Getter for document lines without any modification.
     * @return List of strings containing raw document lines.
     */
    public ArrayList<String> getRawDocument(){return analyzer.getAnalyzedLines();}
}
