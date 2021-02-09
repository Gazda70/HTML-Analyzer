package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Main class of the servlet displaying current time, date and image. Site
 * generated by servlet is refreshed every 10 seconds.
 *
 * @author Piotr Gazda
 * @version 1.0
 * @since 4.0
 */
@WebServlet("/myCalculationsHistoryServlet")
public class CalculationsHistoryServlet extends HttpServlet {


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
        showFileProcessingHistory(request, response);
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
        showFileProcessingHistory(req, resp);
    }

    /**
     * Method responsible for displaying names of files
     * already processed during given session, which are stored in cookie files.
     * @param request servlet request
     * @param response servlet response
     */
    private void showFileProcessingHistory(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        try {
            PrintWriter out = response.getWriter();
            out.print("<html>\n<body>\n");
            if(cookies == null || cookies.length == 1){
                out.println("No files processed yet !\n");
            }
            else
            {
                out.print("Files processed during this session:" + "\n");
                for (int i = 1; i < cookies.length; i++) {
                    out.println(cookies[i].getValue() + "\n");
                }
            }
            out.print("</html>\n</body>\n");
        }catch(IOException exception){

        }
    }

}
