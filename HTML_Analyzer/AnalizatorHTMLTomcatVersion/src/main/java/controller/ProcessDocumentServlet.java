package controller;

import database.DatabaseManager;
import model.EmptyFileException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Main class of the servlet displaying current time, date and image. Site
 * generated by servlet is refreshed every 10 seconds.
 *
 * @author Piotr Gazda
 * @version 1.0
 * @since 4.0
 */
@WebServlet("/myProcessDocumentServlet")
@MultipartConfig
public class ProcessDocumentServlet extends HttpServlet {

    /**
     * Field for Receiver object - communication between servlets and logic.
     */
    private controller.Receiver myReceiver;
    /**
     * Field for Analyzer object - main processing logic.
     */
    private model.Analyzer myAnalyzer;
    /**
     * Field for FileManager object - file reading.
     */
    private model.FileManager myFileManager;

    /**
     * Object for managing database connection
     * and CRUD operations.
     */
    private DatabaseManager databaseManager;

    /**
     * Constant for operation name - showing raw document.
     */
    private final String showRawDocument = "Show raw document";

    /**
     * Constant for operation name - showing document with errors notified.
     */
    private final String showWithErrorsNotified = "Show document with notified errors";

    /**
     * Constant for operation name - showing document with errors corrected.
     */
    private final String showWithErrorsCorrected = "Show document with corrected errors";

    /**
     * Initializer for private fields, crucial for correct program working.
     */
    @Override
    public void init() {
        myAnalyzer = new model.Analyzer();
        myFileManager = new model.FileManager();
        myReceiver = new controller.Receiver(myAnalyzer, myFileManager);
        databaseManager = new DatabaseManager();
        databaseManager.setupDriver();
        databaseManager.setupConnection();
        databaseManager.createDatabase();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    /**
     * Method for processing request for analyzing HTML document.
     * It chooses the right way of obtaining input data depending on
     * method used to send request.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void processRequest( HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException{

        String documentOutput = "";
        PrintWriter out = response.getWriter();
        try {
            if (request.getMethod().equals("POST")) {
                documentOutput = processInputWhenStream(request, response);
            } else if (request.getMethod().equals("GET")) {
                String pressedButton = request.getParameter("databaseButton");
                if(pressedButton != null){
                    ArrayList<String> files = databaseManager.executeSelectStatement("SELECT * FROM Files");
                    documentOutput = "Performed operations: " + "\n" + "\n" +
                            getDocumentOperationsHistory(files);
                }else {
                    documentOutput = processInputWhenFilename(request, response);
                }
            }

        }catch(Exception exception){
            documentOutput = exception.getMessage();
        }
        out.print("<html>\n<body>\n<pre>\n<code>\n");
        out.println(documentOutput);
        out.println("</pre>\n</code>\n");
        out.println("</body>\n</html>");
    }

    /**
     * Method for creating string with file names connected with
     * names of operations performed on them.
     * @param documents List of filenames.
     * @return Formatted string.
     */
    private String getDocumentOperationsHistory(ArrayList<String> documents){
        String toReturn = "";
        for(String doc : documents){
            toReturn += doc + " -> " + "\n"
            + createDocumentOutputWithNewline(databaseManager.executeSelectStatement("SELECT Name FROM Operations "+
                    "WHERE FileID IN (SELECT ID FROM FILES WHERE Name = '" + doc + "' )")) + "\n" + "\n";
        }
        return toReturn;
    }
    /**
     * Method for reading document contents from stream provided by
     * POST method. It enables uploading files to server. It checks
     * which decision button has been pressed and performs appropriate
     * processing.
     * @param request servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @return String containing processed HTML document.
     */
    private String processInputWhenStream(HttpServletRequest request, HttpServletResponse resp) throws IOException, ServletException, EmptyFileException {

        Object[] myParts = request.getParts().toArray();
        InputStream fileStream = ((Part) myParts[0]).getInputStream();
        String filename = ((Part) myParts[0]).getSubmittedFileName();
        myReceiver.proceedDocumentGivenAsList(readFromRequest(fileStream), filename);
        String documentOutput = "";
        String pressedButton = request.getParameter("processButton_1");
        if(pressedButton.equals("showRawDocumentButton")){
            documentOutput = createDocumentOutput(myReceiver.getRawDocument());
            insertFileAndOperation(filename,showRawDocument);
        }
        else if(pressedButton.equals("showWithErrorsNotifiedButton")){
            documentOutput = createDocumentOutput(myReceiver.getDocumentWithErrorsNotification());
            insertFileAndOperation(filename,showWithErrorsNotified);
        }
        else if(pressedButton.equals("showWithErrorsCorrectedButton")){
            documentOutput = createDocumentOutput(myReceiver.getDocumentWithErrorsCorrected());
            insertFileAndOperation(filename,showWithErrorsCorrected);
        }

        manageCookies(request, resp, filename);

        return documentOutput;
    }

    /**
     * Method responsible for inserting file name
     * and connected operation into database.
     * @param filename Name of file to be inserted.
     * @param operation Name of operation connected with file.
     */
    private void insertFileAndOperation(String filename, String operation){
        ArrayList<String> filenames = databaseManager.executeSelectStatement("SELECT Name FROM Files");
        long foreignKey = 0L;
        boolean found = false;
        for(String existingFilename : filenames){
            if(existingFilename.equals(filename)){
                found = true;
                foreignKey =
        Long.parseLong(databaseManager.executeSelectStatement("SELECT ID FROM Files WHERE Name = "+existingFilename).get(0));
            }
        }
        if(!found){
            foreignKey =
                databaseManager.executeInsertStatement("INSERT INTO Files (Name) VALUES ('"+filename+"')");
        }

        databaseManager.executeInsertStatement("INSERT INTO Operations (Name, FileID) VALUES ('"+operation+"',"+foreignKey+")");
    }

    /**
     * Method for reading document contents from stream provided by
     * GET method. It enables processing file from client computer
     * to which absolute path has been given. It checks
     * which decision button has been pressed and performs appropriate
     * processing.
     * @param request servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @return String containing processed HTML document.
     */
    private String processInputWhenFilename(HttpServletRequest request, HttpServletResponse resp) throws FileNotFoundException, EmptyFileException {

        HttpSession session = request.getSession(true);
        Object obj = session.getAttribute("absoluteValue");
        String filename = "";
        if(request.getParameter("text").isEmpty())
        {
            if (obj == null) {
                throw new FileNotFoundException();
            } else {
                filename = (String) obj;
            }
        }
        else
        {
            filename = request.getParameter("text");
        }
        session.setAttribute("absoluteValue", filename);

        myReceiver.setFilePath(filename);
        myReceiver.proceedDocument();
        String documentOutput = "";
        String pressedButton = request.getParameter("processButton_2");
        if(pressedButton.equals("showRawDocumentButton")){
            documentOutput = createDocumentOutputWithNewline(myReceiver.getRawDocument());
        }
        else if(pressedButton.equals("showWithErrorsNotifiedButton")){
            documentOutput = createDocumentOutputWithNewline(myReceiver.getDocumentWithErrorsNotification());
        }
        else if(pressedButton.equals("showWithErrorsCorrectedButton")){
            documentOutput = createDocumentOutputWithNewline(myReceiver.getDocumentWithErrorsCorrected());
        }

        return documentOutput;
    }

    /**
     * Method for adding name of actually processed file
     * to cookie file, so it can be later retrieved and displayed.
     * @param request servlet request
     * @param resp servlet response
     */
    private void manageCookies(HttpServletRequest request, HttpServletResponse resp, String filename){
        Cookie[] existing = request.getCookies();
        String cookieName = filename
                .replaceAll(" ","")
                .replaceAll(":", "")
                .replaceAll(",", "");
        for(Cookie myCookie : existing){
            if(myCookie.getName().equals(filename)){
                cookieName += "_next";
            }
        }
        Cookie cookie = new Cookie(cookieName, filename);
        resp.addCookie(cookie);

    }

    /**
     * Method for obtaining strings from input stream.
     * @param stream Input stream from which contents are to be extracted.
     * @return List of strings created from input stream.
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private ArrayList<String> readFromRequest(InputStream stream) throws ServletException, IOException{
        byte[] insides = new byte[stream.available()];
        stream.read(insides);
        String inputString = new String(insides);
        char[] inputCharacters = inputString.toCharArray();
        ArrayList<String> myLines = new ArrayList<>();
        String toAdd="";
        for (char inputCharacter : inputCharacters) {
            toAdd += inputCharacter;
            if (inputCharacter == '\n') {
                myLines.add(toAdd);
                toAdd = "";
            }
        }
        myLines.add(toAdd);
        return myLines;
    }

    /**
     * Method for converting list of strings into a single string
     * which can be displayed in a HTML form. It assumes the strings
     * already have appropriate newline character.
     * @param myDocument List of strings to be merged.
     * @return Created string.
     */
    private String createDocumentOutput(ArrayList<String> myDocument){
        String outputString = "";

        for(String line : myDocument){
            outputString += line.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }

        return outputString;
    }

    /**
     * Method for converting list of strings into a single string
     * which can be displayed in a HTML form. It assumes the strings
     * don't have appropriate newline character.
     * @param myDocument List of strings to be merged.
     * @return Created string.
     */
    private String createDocumentOutputWithNewline(ArrayList<String> myDocument){
        String outputString = "";

        for(String line : myDocument){
            outputString += line.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "\n";
        }

        return outputString;
    }
}
