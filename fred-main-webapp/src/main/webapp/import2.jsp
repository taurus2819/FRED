<%@page pageEncoding="utf-8"%><%@page language="Java"
%><html>
    <body>
        <h2>MG Demo</h2>

        <p><a href="template.xlsx?CODE=MG_IMPORT_SPREADSHEET_TYPE">Get the Metaimporter template spreadsheet</a></p>
        <p><a href="template.xlsx?CODE=FRED_OUTCROP">Get the FRED template spreadsheet</a></p>

        <br/><br/><br/>
        
        <form action="xlsUploader" method="post" enctype="multipart/form-data">
            <input type="file" name="file" />
            <input value="Upload" type="submit" />
        </form>

    </body>
</html>
