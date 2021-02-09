package model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that realizes program logic. Its purpose
 * is to provide functionalities of identyfing errors
 * in HTML files, mark them and correct them.
 * @author Piotr Gazda
 * @version 4.0
 * @since 1.0
 */
public class Analyzer {

    /**
     * Public constructor. It initializes
     * appropriate fields with empty string array lists.
     */
    public Analyzer(){
        this.analyzedLines = new ArrayList<String>();
        this.linesWithErrorInfo = new ArrayList<String>();
        this.correctedLines = new ArrayList<String>();
    }

    /**
     * Getter for document contents with error info added.
     * @return List of strings representing said content.
     */
    public ArrayList<String> getLinesWithErrorInfo(){return this.linesWithErrorInfo;}

    /**
     * Getter for document contents with errors corrected.
     * @return List of strings representing said content.
     */
    public ArrayList<String> getCorrectedLines(){return this.correctedLines;}

    /**
     * Setter for original document lines.
     * @param newLines List of strings representing said content.
     */
    public void setAnalyzedLines(ArrayList<String> newLines){
        this.analyzedLines = newLines;
        this.linesWithErrorInfo = new ArrayList<String>(this.analyzedLines);
        this.correctedLines = new ArrayList<String>(this.analyzedLines);
    }

    /**
     * Method that allows to check whether given tag is correct.
     * @param tag Tag to be checked.
     * @return If tag is correct return true else return false.
     */
    public boolean checkSingleOpeningTagIntegrity(String tag){
        boolean toReturn = false;
        if(tag != null) {
            Pattern openingTagPattern = Pattern.compile("<[a-z].+>");
            Matcher match = openingTagPattern.matcher(tag);
            toReturn = match.matches();
        }
        return toReturn;
    }

    /**
     * Method that allows to check whether given ending tag is proper.
     * @param endingTag Given ending tag.
     * @return True if ending tag is correct, false otherwise.
     */
    public boolean checkSingleEndingTagIntegrity(String endingTag){
        boolean toReturn = false;
        if(endingTag != null) {
            Pattern openingTagPattern = Pattern.compile("</[a-z].+>");
            Matcher match = openingTagPattern.matcher(endingTag);
            toReturn = match.matches();
        }return toReturn;
    }
    /**
     * The list for containing original document contents.
     */
    private ArrayList<String> analyzedLines;

    /**
     * Getter for collection containing analyzed document.
     * @return Lines of analyzed document.
     */
    public ArrayList<String> getAnalyzedLines(){return this.analyzedLines;}

    /**
     * The list for containing document contents with error info added.
     */
    private ArrayList<String> linesWithErrorInfo;

    /**
     * The list for containing document contents with errors corrected.
     */
    private ArrayList<String> correctedLines;

    /**
     * Index representing current document line
     * the program is working on.
     */
    private int currentLineIndex = 0;

    /**
     * Getter for index of currently processed line.
     * @return Current line index.
     */
    public int getCurrentLineIndex(){return this.currentLineIndex;}

    /**
     * Index representing current character
     * the program is working on.
     */
    private int currentCharIndex = 0;

    /**
     * String containing HTML tag
     * the program is currently analyzing.
     */
    private String currentOpeningTag = "";

    /**
     * Counter of cases where a tag didn't
     * have an ending bracket.
     */
    private int noEndingBracketErrors = 0;

    /**
     * Getter for noMatchingBracketErrors
     * @return Number of cases when there wasn't a matching bracket for a tag.
     */
    public int getNoMatchingBracketErrors() {
        return noEndingBracketErrors;
    }

    /**
     * Counter of cases where an opening tag
     * didn't have matching ending tag.
     */
    private int noEndingTagErrors = 0;

    /**
     * Getter for noMatchingTagErrors
     * @return Number of cases when there wasn't a matching ending tag for an opening tag.
     */
    public int getNoMatchingTagErrors(){
        return noEndingTagErrors;
    }

    /**
     * Counter of cases where a tag wasn't in lowercase
     */
    private int noLowercaseTagErrors = 0;

    /**
     * Getter for noLowercaseTag
     * @return Number of cases when the tag was not in lowercase.
     */
    public int getNoLowercaseTagErrors(){
        return noLowercaseTagErrors;
    }

    /**
     * Enum for describing possible error types.
     */
    private enum ErrorType{NO_ENDING_TAG, NO_ENDING_BRACKET, NO_LOWERCASE_TAG}
    /**
     *
     */
    private void actualizeErrorsCount(ErrorType errorType){
        switch (errorType) {
            case NO_ENDING_BRACKET : noEndingBracketErrors++;
            break;
            case NO_ENDING_TAG : noEndingTagErrors++;
            break;
            case NO_LOWERCASE_TAG : noLowercaseTagErrors++;
            break;
        }
    }
    /**
     * Method that identifies a string as an opening
     * HTML tag.
     * @param suspectedTag The string suspected of being an opening tag.
     * @return True if the string turns out to be an opening tag, false otherwise.
     */
    private boolean identifyOpeningTag(String suspectedTag){
        Pattern openedTag = Pattern.compile("<[^/].+");
        Matcher matcher = openedTag.matcher(suspectedTag);
        return matcher.matches();
    }

    /**
     * Method that identifies a string as an closing
     * HTML tag.
     * @param suspectedTag The string suspected of being an closing tag.
     * @return True if the string turns out to be an closing tag, false otherwise.
     */
    private boolean identifyEndingTag(String suspectedTag){
        Pattern openedTag = Pattern.compile("</.+");
        Matcher matcher = openedTag.matcher(suspectedTag);
        return matcher.matches();
    }

    /**
     * Method to check whether a HTML tag contains
     * only of lowercase letters.
     * @param tag String being the checked tag.
     * @return True if the tag is all lowercase, false otherwise.
     */
    private boolean checkIfTagIsLowercase(String tag){
        Pattern allLowercase = Pattern.compile("<[a-z]+>");
        Matcher matcher = allLowercase.matcher(tag);
        return matcher.matches();
    }

    /**
     * Method to check whether a HTML ending tag contains
     * only of lowercase letters.
     * @param tag String being the checked ending tag.
     * @return True if the ending tag is all lowercase, false otherwise.
     */
    private boolean checkIfEndingTagIsLowercase(String tag){
        Pattern allLowercase = Pattern.compile("</[a-z]+>");
        Matcher matcher = allLowercase.matcher(tag);
        return matcher.matches();
    }


    /**
     * Method that checks if an opening tag has ending bracket.
     * It allows multiline tags, but a tag opening bracket before
     * closing the last tag is considered a mistake. It also
     * calls methods for putting error message in correct places
     * (saving it to array for document lines with error information)
     * and for putting missing tag (saving it to array for document lines with errors corrected).
     * @return True if tag is totally correct, false otherwise.
     */
    private boolean checkOpeningTag(){

        boolean toReturn = false;

        boolean opened = false;

        currentOpeningTag = "";

        int beginTagIndex = currentCharIndex;

        char currentCharacter;

        while(!toReturn){

            //dodaje element nienależący już do opening tag w przypadku niezamkniętego taga
            currentCharacter = checkCurrentCharacter();
            if(currentCharacter == '<'){
                if(opened)
                {
                    manageNoEndingBracketError();
                    return false;
                }else{
                    opened = true;
                }
            }
            if(currentCharacter == '>'){
                toReturn = true;
            }

            currentCharIndex++;
        }

        if(!checkIfTagIsLowercase(currentOpeningTag))
        {
            manageTagNotLowercaseError(beginTagIndex, currentCharIndex + 1);
        }
        return true;
    }

    /**
     * Auxilliary method to check currentChar and currentCharacter value in context of analyzed lines value.
     */
    private char checkCurrentCharacter(){
        char  currentCharacter = ' ';
        if(currentCharIndex >= analyzedLines.get(currentLineIndex).length()){
            currentCharIndex = 0;
            currentLineIndex++;
        }
        if(currentCharIndex < analyzedLines.get(currentLineIndex).length())
        {
            currentCharacter = analyzedLines.get(currentLineIndex).charAt(currentCharIndex);
            currentOpeningTag = currentOpeningTag + currentCharacter;
        }
        return currentCharacter;
    }
    /**
     * Method to manage error of tag not in lowercase.
     */
    private void manageTagNotLowercaseError(int beginIdx, int endIdx){
        this.linesWithErrorInfo.set(currentLineIndex, addErrorInfo(this.linesWithErrorInfo.get(currentLineIndex)));
        toLowercase(currentLineIndex, currentCharIndex - currentOpeningTag.length(), currentCharIndex - 1);
        actualizeErrorsCount(ErrorType.NO_LOWERCASE_TAG);
    }
    /**
     * Method to annotate and correct error of lack of tag ending bracket.
     */
    private void manageNoEndingBracketError(){
        this.linesWithErrorInfo.set(currentLineIndex, addErrorInfo(this.linesWithErrorInfo.get(currentLineIndex)));
        insertEndBracket(currentLineIndex, currentCharIndex);
        actualizeErrorsCount(ErrorType.NO_ENDING_BRACKET);
    }

    /**
     * Method to insert a character at a specific index in string.
     * @param str String to which the new character will be inserted.
     * @param ch Character to be inserted.
     * @param position Position at which the new character will be inserted.
     * @return String with new character at correct position.
     */
    private String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }
    /**
     * Method for checking if the ending tag is correct.
     * It is less complicated than the method checking the opening tag,
     * because it assumes the begging tag is already known.
     * @param openingTag Opening tag to know how ending tag should look like.
     * @return True if a correct ending tag has been identified, false otherwise.
     */
    private boolean checkEndingTag(String openingTag){
        boolean toReturn = false;
        String endingTag = "</" + openingTag.substring(1);
        if(currentCharIndex + endingTag.length() <= analyzedLines.get(currentLineIndex).length()){
            if(analyzedLines.get(currentLineIndex)
                    .startsWith(endingTag, currentCharIndex)){
                //how to get ending tag?
                if(checkIfEndingTagIsLowercase(analyzedLines.get(currentLineIndex)
                        .substring(currentCharIndex, currentCharIndex + endingTag.length()))) {
                    currentCharIndex += endingTag.length();
                    toReturn = true;
                }
            }
        }
        currentCharIndex ++;
        return toReturn;
    }


    /**
     * Recursive method which aims to determine if every opening tag is correctly
     * paired with an ending tag. It iterates over text lines looking for
     * closing tag for its given opening tag. If it encounters another opening
     * tag it calls itself recursively.
     * It also calls methods for putting error message in correct places
     * (saving it to array for document lines with error information)
     * and for putting missing tag (saving it to array for document lines with errors corrected).
     * @param openingTag Opening tag that needs to be found a pair. If not given,
     *                   method iterates over document lines to find one.
     * @param isOpened Flag to annotate if method works after an opening tag
     *                 has been found (true) or it needs to find an opening tag (false).
     */
    public void checkTagPair(String openingTag, boolean isOpened){

        boolean isOpeningTag = isOpened;
        boolean iterate = true;
        String myOpeningTag = openingTag;

        while(iterate) {
            if (currentCharIndex >= analyzedLines.get(currentLineIndex).length()) {
                currentCharIndex = 0;
                currentLineIndex++;
            }
            if (currentLineIndex < analyzedLines.size()) {
                String currentPart = analyzedLines.get(currentLineIndex)
                        .substring(currentCharIndex);

                if (identifyOpeningTag(currentPart)) {
                    if (checkOpeningTag()) {
                        if (isOpeningTag) {
                            checkTagPair(this.currentOpeningTag,true);
                            //powrót do starej wartości currentOpeningTag
                            this.currentOpeningTag = myOpeningTag;
                        } else {
                            isOpeningTag = true;
                            myOpeningTag = this.currentOpeningTag;
                        }
                    } else {
                        currentOpeningTag = "";
                        this.linesWithErrorInfo.set(currentLineIndex, addErrorInfo(this.linesWithErrorInfo.get(currentLineIndex)));
                        return;
                    }
                } else if (identifyEndingTag(currentPart)) {
                    if(!currentOpeningTag.equals(""))
                    {
                        if (!checkEndingTag(myOpeningTag)) {
                            manageNoEndingTagError(myOpeningTag);
                        }
                    }
                    currentCharIndex++;
                    return;
                } else {
                    currentCharIndex++;
                }
            }else{
                iterate = false;
            }
        }
        if(isOpeningTag)
        {
            manageNoEndingTagError(myOpeningTag);
        }
    }

    /**
     * Method to annotate and correct error of lack of ending tag.
     * @param myOpeningTag Opening tag that lacks ending tag.
     */
    private void manageNoEndingTagError(String myOpeningTag){
        if(currentLineIndex == this.analyzedLines.size())
        {
            this.linesWithErrorInfo.set(currentLineIndex - 1,
                    addErrorInfo(this.linesWithErrorInfo.get(currentLineIndex - 1)));
        }else{
            this.linesWithErrorInfo.set(currentLineIndex,
                    addErrorInfo(this.linesWithErrorInfo.get(currentLineIndex)));
        }
        insertEndingTag(currentLineIndex, currentCharIndex, myOpeningTag.substring(1, myOpeningTag.length() - 1));
        actualizeErrorsCount(ErrorType.NO_ENDING_TAG);
    }

    /**
     * Method adds asterisk to a line containing an error.
     * @param erroneous Line containing an error.
     * @return Line with added asterisk.
     */

    private String addErrorInfo (String erroneous){
        return erroneous + " *";
    }

    /**
     * Method converts string to lowercase.
     * @param lineNumber Line number - index in string list.
     * @param beginChar  Index of beginning of the string to be converted
     * @param endChar   Index of ending of the string to be converted
     */
    private void toLowercase(int lineNumber, int beginChar, int endChar){
        String beforeTag = this.analyzedLines.get(lineNumber).substring(0, beginChar);
        String invalidTag = this.analyzedLines.get(lineNumber).substring(beginChar, endChar).toLowerCase();
        String afterTag = this.analyzedLines.get(lineNumber).substring(endChar);
        this.correctedLines.set(lineNumber, beforeTag + invalidTag + afterTag);
    }

    /**
     * Method for inserting ending bracket.
     * @param lineNumber Line number - index in string list.
     * @param indexAfterEndBracket Index of character before which the ending bracket should be placed.
     */
    private void insertEndBracket(int lineNumber,int indexAfterEndBracket) {
        String beforeBracket = "";
        String afterBracket = "";

        if (indexAfterEndBracket < 1 )
        {
            afterBracket = this.analyzedLines.get(lineNumber);
        }
        else if (indexAfterEndBracket > this.analyzedLines.get(currentLineIndex).length())
        {
            beforeBracket = this.analyzedLines.get(lineNumber).substring(0, indexAfterEndBracket - 1);
        }
        else
        {
            beforeBracket = this.analyzedLines.get(lineNumber).substring(0, indexAfterEndBracket - 1);
            afterBracket = this.analyzedLines.get(lineNumber).substring(indexAfterEndBracket,
                    this.analyzedLines.get(currentLineIndex).length());
        }
        this.correctedLines.set(lineNumber, beforeBracket + '>' + afterBracket);
    }

    /**
     * Method for inserting ending tag.
     * @param lineNumber Line number - index in string list.
     * @param indexAfterEndTag Index of character before which the ending tag should be placed.
     * @param tagName Opening tag name
     */
    private void insertEndingTag(int lineNumber,  int indexAfterEndTag, String tagName) {
        String beforeTag = "";
        String afterTag = "";
        String newTag = "</" + tagName.toLowerCase() + ">";
        if (currentLineIndex < analyzedLines.size()) {
            if (indexAfterEndTag < 1) {
                afterTag = this.analyzedLines.get(lineNumber);
            } else if (indexAfterEndTag > this.analyzedLines.get(currentLineIndex).length()) {
                beforeTag = this.analyzedLines.get(lineNumber).substring(0, indexAfterEndTag - 1);
            } else {
                beforeTag = this.analyzedLines.get(lineNumber).substring(0, indexAfterEndTag - 1);
                afterTag = this.analyzedLines.get(lineNumber).substring(indexAfterEndTag,
                        this.analyzedLines.get(currentLineIndex).length());
            }
            this.correctedLines.set(lineNumber, beforeTag + newTag + afterTag);
        }
        this.correctedLines.add(beforeTag + newTag + afterTag);
    }
}

