package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

/**
 * Class that is responsible for reading input file.
 * In next versions it should manage writing output
 * to a file,
 * @author Piotr Gazda
 * @version 4.0
 * @since 1.0
 */
public class FileManager {

    /**
     * Public constructor. Initializes field for
     * preserving lines of file with empty array.
     */
    public FileManager(){this.documentLines = new ArrayList<String>();}

    /**
     * Field for path to input file.
     */
    private String filePath;
    /**
     * Field for input file contents.
     */
    private ArrayList<String> documentLines;

    /**
     * Getter for input file path.
     * @return Input file path.
     */
    public String getFilePath(){return this.filePath;}

    /**
     * Setter for input file path.
     * @param newFilePath Input file path.
     */
    public void setFilePath(String newFilePath){this.filePath = newFilePath;}

    /**
     * Getter for lines read from the document.
     * There is no analogous setter because these
     * lines should be set only by readDocument() method.
     * @return Array of strings each being a line that was read from
     * input document.
     */
    public ArrayList<String> getDocumentLines(){
        return this.documentLines;
    }

    /**
     * Method that reads given document line by line
     * and puts these lines into string list.
     * @throws FileNotFoundException
     */
    public void readDocument() throws FileNotFoundException{

        File myObj = new File(filePath);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            documentLines.add(data);
        }
        myReader.close();
    }

    /**
     * Method that uses streams to determine whether the document contains any of basic HTML syntax.
     * @return True if HTML syntax has been found, false otherwise.
     */
    public boolean isHTMLDocument(){

        Optional<String> firstHTMLTagLike = documentLines.stream()
                .filter(i -> i.contains("<") || i.contains(">"))
                .findFirst();
        return firstHTMLTagLike.isPresent();
    }

}

