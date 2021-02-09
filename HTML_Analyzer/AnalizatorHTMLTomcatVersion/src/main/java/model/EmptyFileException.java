package model;


/**
 * Class that defines an exception the program
 * should throw when the input document is empty.
 * @author Piotr Gazda
 * @version 4.0
 * @since 1.0
 */
public class EmptyFileException extends Exception{

    /**
     * Field for containing input file name
     * given by the user
     * to be displayed in error message.
     */
    private final String filename;

    /**
     * Public constructor.
     * @param filename Input file name
     */
    public EmptyFileException(String filename) {
        this.filename = filename;
    }

    /**
     * Method that creates error message.
     * with given file name.
     * @return Error messsage.
     */
    public String toString() {
        return "The file " + filename + " is empty.";
    }
}
