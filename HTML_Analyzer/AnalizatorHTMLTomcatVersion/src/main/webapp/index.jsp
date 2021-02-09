<%--
  Created by IntelliJ IDEA.
  User: gazda
  Date: 30.11.2020
  Time: 13:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title> HTML Analyzer</title>
</head>
<body>

<h3>Analizator HTML</h3>

<form  enctype = "multipart/form-data">

    <label for="file_input">Select a file to upload</label>
    <input type = "file" id="file_input" name = "file" size = "50" />
    <br><br>
    <button type="submit" name="processButton_1" formaction = "myProcessDocumentServlet"
            formmethod = "post" value="showRawDocumentButton">Show raw document</button><br>
    <button type="submit" name="processButton_1" formaction = "myProcessDocumentServlet"
            formmethod = "post" value="showWithErrorsNotifiedButton">Process and show with errors notified</button><br>
    <button type="submit" name="processButton_1" formaction = "myProcessDocumentServlet"
            formmethod = "post" value="showWithErrorsCorrectedButton">Process and show with errors corrected</button><br>
</form>

<form>

    <label for="text_input">
        You can alternatively choose to provide full path to the file
    </label><br>
    <input type = "text" id="text_input" onfocus="this.value=''" name = "text" size = "100" /><br><br>
    <button type="submit" name="processButton_2" formaction = "myProcessDocumentServlet"
            formmethod = "get" value="showRawDocumentButton">Show raw document</button><br>
    <button type="submit" name="processButton_2" formaction = "myProcessDocumentServlet"
            formmethod = "get" value="showWithErrorsNotifiedButton">Process and show with errors notified</button><br>
    <button type="submit" name="processButton_2" formaction = "myProcessDocumentServlet"
            formmethod = "get" value="showWithErrorsCorrectedButton">Process and show with errors corrected</button><br>

</form>
<br><br><br>
<form>
    <button type="submit" formaction="myCalculationsHistoryServlet" formmethod="get">
        Show processing history
    </button>


</form>
<br>
<form>
    <button type="submit" name="databaseButton" formaction="myProcessDocumentServlet" formmethod="get">
        Show operations history
    </button>
</form>

</body>

</html>
