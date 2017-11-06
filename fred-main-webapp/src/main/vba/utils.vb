Option Explicit

Public userName As String
Public password As String
Public noInternet As Boolean
Public sessionId As String
Public refreshFlag As Boolean
Private grayCells(5, 51) As String
Private orangeCells(7) As String
Private initialiseFlag As Boolean
Private previousRow As Integer
Private previousDocType As String
Private previousLocType As String

Public Sub greyCells(row As Integer)
  Dim locDoc As Variant
  Dim docType As String
  Dim locType As String
  Dim i As Integer, j As Integer

  utils.initialiseGrayArray
  If InStr(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & row).value, ":") = 0 Then
    docType = ""
    locType = ""
  Else
    locDoc = Split(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & row).value, ":")
    docType = locDoc(0)
    locType = Trim(locDoc(1))
  End If
  
  If row >= locality.startLine And (previousRow <> row Or previousDocType <> docType Or previousLocType <> locType) Then
    Application.ScreenUpdating = False
    previousRow = row
    previousDocType = docType
    previousLocType = locType
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_USER_DATA_START & row & ":" & Constants.LOC_USER_DATA_STOP & row).Interior.ColorIndex = xlColorIndexNone
    ActiveWorkbook.Sheets("Locality").Range(Constants.SAMP_USER_DATA_START & row & ":" & Constants.SAMP_USER_DATA_STOP & row).Interior.ColorIndex = xlColorIndexNone
    ActiveWorkbook.Sheets("Locality").Rows(Constants.DEFAULT_HEADING_ROW).RowHeight = 0
    ActiveWorkbook.Sheets("Locality").Rows(Constants.OUTCROP_HEADING_ROW).RowHeight = 0
    ActiveWorkbook.Sheets("Locality").Rows(Constants.DRILLHOLE_HEADING_ROW).RowHeight = 0
    ActiveWorkbook.Sheets("Locality").Rows(Constants.VERT_SECT_HEADING_ROW).RowHeight = 0
    ActiveWorkbook.Sheets("Locality").Rows(Constants.DRILLHOLE_SAMPLE_HEADING_ROW).RowHeight = 0
    ActiveWorkbook.Sheets("Locality").Rows(Constants.VERT_SECT_SAMPLE_HEADING_ROW).RowHeight = 0
    If docType = "Locality" Then
      If locType = "Outcrop" Then
        i = 0
        ActiveWorkbook.Sheets("Locality").Rows(Constants.OUTCROP_HEADING_ROW).RowHeight = 39
      ElseIf locType = "Drillhole" Then
        i = 1
        ActiveWorkbook.Sheets("Locality").Rows(Constants.DRILLHOLE_HEADING_ROW).RowHeight = 39
      ElseIf locType = "Vertical Section" Then
        i = 2
        ActiveWorkbook.Sheets("Locality").Rows(Constants.VERT_SECT_HEADING_ROW).RowHeight = 39
      Else
        ActiveWorkbook.Sheets("Locality").Rows(Constants.DEFAULT_HEADING_ROW).RowHeight = 39
        Exit Sub
      End If
    ElseIf docType = "Sample" Then
      If locType = "Drillhole" Then
        i = 3
        ActiveWorkbook.Sheets("Locality").Rows(Constants.DRILLHOLE_SAMPLE_HEADING_ROW).RowHeight = 39
      ElseIf locType = "Vertical Section" Then
        i = 4
        ActiveWorkbook.Sheets("Locality").Rows(Constants.VERT_SECT_SAMPLE_HEADING_ROW).RowHeight = 39
      ElseIf locType = "Outcrop" Then
        ActiveWorkbook.Sheets("Locality").Rows(Constants.DEFAULT_HEADING_ROW).RowHeight = 39
        MsgBox "For Outcrops 'Document Type' must be set to Locality", vbInformation + vbOKOnly
        Exit Sub
      Else
        ActiveWorkbook.Sheets("Locality").Rows(Constants.DEFAULT_HEADING_ROW).RowHeight = 39
        Exit Sub
      End If
    Else
      ActiveWorkbook.Sheets("Locality").Rows(Constants.DEFAULT_HEADING_ROW).RowHeight = 39
      Exit Sub
    End If
    
    'orange cells
    For j = 0 To 6
      ActiveWorkbook.Sheets("Locality").Range(orangeCells(j) & row).Interior.Color = RGB(250, 180, 130)
      ActiveWorkbook.Sheets("Locality").Range(orangeCells(j) & row).WrapText = True
    Next
    
    'gray cells
    For j = 0 To 49
      If grayCells(i, j) <> "" Then
        ActiveWorkbook.Sheets("Locality").Range(grayCells(i, j) & row).Interior.Color = RGB(255, 255, 255)
        ActiveWorkbook.Sheets("Locality").Range(grayCells(i, j) & row).Interior.Pattern = xlPatternCrissCross
      Else
        Exit For
      End If
    Next
    
    Application.ScreenUpdating = True
  End If
 
End Sub

Public Sub initialiseGrayArray()
  Dim i As Integer
  Dim j As Integer
  
  'If Not initialiseFlag Then
  For i = 0 To 5
    For j = 0 To 51
        grayCells(i, j) = "ZZ"
    Next
  Next

  ' outcrops
  grayCells(0, 0) = Constants.OP_COMPANY
  grayCells(0, 1) = Constants.START_DATE
  grayCells(0, 2) = Constants.START_DATE_RND
  grayCells(0, 3) = Constants.COMPLETION_DATE
  grayCells(0, 4) = Constants.COMPLETION_DATE_RND
  grayCells(0, 5) = Constants.LICENCE_AREA
  grayCells(0, 6) = Constants.DATUM_TYPE
  grayCells(0, 7) = Constants.DATUM_ELEVATION
  grayCells(0, 8) = Constants.START_DEPTH
  grayCells(0, 9) = Constants.STOP_DEPTH
  grayCells(0, 10) = Constants.TOP_DEPTH
  grayCells(0, 11) = Constants.BOTTOM_DEPTH
  grayCells(0, 12) = Constants.DRILL_TYPE
  grayCells(0, 13) = Constants.CONFIDENTIAL
  
  ' drillhole localities
  grayCells(1, 0) = Constants.COLL_DATE
  grayCells(1, 1) = Constants.COLL_DATE_RND
  grayCells(1, 2) = Constants.COLLECTOR
  grayCells(1, 3) = Constants.STRAT_NAME
  grayCells(1, 4) = Constants.FOSSIL_IN_PLACE
  grayCells(1, 5) = Constants.SENT_TO
  grayCells(1, 6) = Constants.NOT_COLLECTED
  grayCells(1, 7) = Constants.SIG
  grayCells(1, 8) = Constants.INF_STAGE_START
  grayCells(1, 9) = Constants.INF_STAGE_START_MOD
  grayCells(1, 10) = Constants.INF_STAGE_STOP
  grayCells(1, 11) = Constants.INF_STAGE_STOP_MOD
  grayCells(1, 12) = Constants.KNW_STAGE_START
  grayCells(1, 13) = Constants.KNW_STAGE_START_MOD
  grayCells(1, 14) = Constants.KNW_STAGE_STOP
  grayCells(1, 15) = Constants.KNW_STAGE_STOP_MOD
  grayCells(1, 16) = Constants.PREV_SAMPLE
  grayCells(1, 17) = Constants.SAMP_REL
  grayCells(1, 18) = Constants.STRAT_REL
  grayCells(1, 19) = Constants.COLUMN_MAP
  grayCells(1, 20) = Constants.DIP
  grayCells(1, 21) = Constants.DIP_DIRECTION
  grayCells(1, 22) = Constants.STRIKE
  grayCells(1, 23) = Constants.FACING
  grayCells(1, 24) = Constants.GRAINSIZE_P
  grayCells(1, 25) = Constants.GRAINSIZE_S
  grayCells(1, 26) = Constants.COMPARATOR
  grayCells(1, 27) = Constants.THICKNESS
  grayCells(1, 28) = Constants.BEDDING_P
  grayCells(1, 29) = Constants.BEDDING_S
  grayCells(1, 30) = Constants.WEATHERING
  grayCells(1, 31) = Constants.HARDNESS
  grayCells(1, 32) = Constants.CARBONATE
  grayCells(1, 33) = Constants.COLOUR_MOD
  grayCells(1, 34) = Constants.COLOUR_P
  grayCells(1, 35) = Constants.COLOUR_S
  grayCells(1, 36) = Constants.WET_DRY
  grayCells(1, 37) = Constants.SED_FEATURE
  grayCells(1, 38) = Constants.DEP_ENVIRONMENT1
  grayCells(1, 39) = Constants.DEP_ENVIRONMENT2
  grayCells(1, 40) = Constants.ROCK_NATURE
  grayCells(1, 41) = Constants.CORRESPONDENCE
  grayCells(1, 42) = Constants.TOP_DEPTH
  grayCells(1, 43) = Constants.BOTTOM_DEPTH
  grayCells(1, 44) = Constants.DRILL_TYPE
  grayCells(1, 45) = Constants.CONFIDENTIAL
  
  ' vert section localities
  For i = 0 To 45
    grayCells(2, i) = grayCells(1, i)
  Next
  grayCells(2, 46) = Constants.LICENCE_AREA
  grayCells(2, 47) = Constants.DRILL_TYPE

  'drillhole samples
  grayCells(3, 0) = Constants.REG_AREA
  grayCells(3, 1) = Constants.RECOLLECTION
  grayCells(3, 2) = Constants.datum
  grayCells(3, 3) = Constants.MAP_SHEET
  grayCells(3, 4) = Constants.easting
  grayCells(3, 5) = Constants.NORTHING
  grayCells(3, 6) = Constants.MAP_YEAR
  grayCells(3, 7) = Constants.LOCATION_METHOD
  grayCells(3, 8) = Constants.ACCURACY
  grayCells(3, 9) = Constants.LOCALITY_DESC
  grayCells(3, 10) = Constants.COUNTRY
  grayCells(3, 11) = Constants.OP_COMPANY
  grayCells(3, 12) = Constants.START_DATE
  grayCells(3, 13) = Constants.START_DATE_RND
  grayCells(3, 14) = Constants.COMPLETION_DATE
  grayCells(3, 15) = Constants.COMPLETION_DATE_RND
  grayCells(3, 16) = Constants.LICENCE_AREA
  grayCells(3, 17) = Constants.DATUM_TYPE
  grayCells(3, 18) = Constants.DATUM_ELEVATION
  grayCells(3, 19) = Constants.START_DEPTH
  grayCells(3, 20) = Constants.STOP_DEPTH

  ' vert section samples?
  For i = 0 To 20
    grayCells(4, i) = grayCells(3, i)
  Next
  grayCells(4, 21) = Constants.DRILL_TYPE
  
  
  'orange cells (builders)
  orangeCells(0) = Constants.COLLECTOR
  orangeCells(1) = Constants.SENT_TO
  orangeCells(2) = Constants.PREV_SAMPLE
  orangeCells(3) = Constants.SAMP_REL
  orangeCells(4) = Constants.STRAT_REL
  orangeCells(5) = Constants.SED_FEATURE
  orangeCells(6) = Constants.CONFIDENTIAL

  initialiseFlag = True
  
  'End If

End Sub

Public Sub clearUser()

  userName = ""
  password = ""
  sessionId = ""

End Sub

Public Function authenticate() As Boolean
  Dim url As String
  Dim objHTTP As Object
  Dim strCookie As String
  Dim requestBody As String

  If userName = "" Or sessionId = "" Then
    With passwordFrm
      .userNameTxt = Null
      .passwordTxt = Null
      .noInternetChk = noInternet
      .Show
    End With
  End If
  If userName <> "" Then
    If sessionId = "" Then
        Set objHTTP = CreateObject("WinHttp.WinHttpRequest.5.1")
        url = Constants.secureBaseURL & "login.jsp"
        requestBody = "loginname=" & encodeURL(userName) & "&loginpass=" & encodeURL(password)
        objHTTP.Open "POST", url, False
        objHTTP.SetRequestHeader "Content-Type", "application/x-www-form-urlencoded"
        'Prevent following redirect with Option(6) = False, as only original response is
        'guaranteed to contain the session ID cookie
        objHTTP.Option(6) = False
        objHTTP.send requestBody
        authenticate = (objHTTP.status = 302)
        If authenticate Then
            sessionId = GetJsessionId(objHTTP.GetResponseHeader("Set-Cookie"))
        ElseIf objHTTP.status = 401 Then
            MsgBox "Incorrect username or password", vbCritical, "Login Error"
            clearUser
        Else
            MsgBox "An unexpected error occurred logging in, please try again or contact GNS IT Support (Applications.Support@gns.cri.nz)", vbCritical, "Login Error"
            clearUser
        End If
    Else
        authenticate = True
    End If
  Else
    authenticate = False
  End If

End Function



Public Function incrementColumn(col As String) As String
  Dim first As String
  Dim last As String

  If col = "" Then
    incrementColumn = "A"
  Else
    last = Right(col, 1)
    If Len(col) = 1 Then
      first = ""
    Else
      first = Left(col, 1)
    End If
    If last <> "Z" Then
      last = Chr(Asc(last) + 1)
    Else
      If first = "" Then
        first = "A"
      Else
        first = Chr(Asc(first) + 1)
      End If
      last = "A"
    End If
    incrementColumn = first & last
  End If

End Function

Public Function decrementColumn(col As String) As String
  Dim first As String
  Dim last As String

  If col = "" Then
    decrementColumn = "A"
  Else
    last = Right(col, 1)
    If Len(col) = 1 Then
      first = ""
    Else
      first = Left(col, 1)
    End If
    If last <> "A" Then
      last = Chr(Asc(last) - 1)
    Else
      If first = "" Then
        first = "Z"
      Else
        first = Chr(Asc(first) - 1)
      End If
      last = "Z"
    End If
    decrementColumn = first & last
  End If

End Function

Public Function formatDate(dateText As String, rndText As String) As String
  Dim d As Date
  
  If dateText <> "" Then
    If IsDate(dateText) And (rndText = "" Or rndText = "Month" Or rndText = "Year") Then
      d = CDate(dateText)
      If rndText = "" Then
        formatDate = paddZeros(Day(d), 2) & "/" & paddZeros(Month(d), 2) & "/" & Year(d)
      ElseIf rndText = "Month" Then
        formatDate = paddZeros(Month(d), 2) & "/" & Year(d)
      Else
        formatDate = Year(d)
      End If
    Else
      formatDate = "bad date"
    End If
  Else
    formatDate = ""
  End If
  
End Function

Public Function decodeDateString(dte As String) As String()
  Dim dateStr As String
  Dim dateArray(2) As String
  
  If dte = "" Or dte = "##" Then
    dateArray(0) = ""
    dateArray(1) = ""
  Else
    dateStr = Mid(dte, 2, Len(dte) - 2)
    If InStr(dateStr, "/") > 0 Then
      If InStr(InStr(dateStr, "/") + 1, dateStr, "/") > 0 Then
        dateArray(0) = dateStr
        dateArray(1) = ""
      Else
        dateArray(0) = "1/" & dateStr
        dateArray(1) = "Month"
      End If
    Else
      dateArray(0) = "1/1/" & dateStr
      dateArray(1) = "Year"
    End If
  End If
  decodeDateString = dateArray
  
End Function

Private Function paddZeros(str As String, length As Integer) As String

  While Len(str) < length
    str = "0" & str
  Wend
  paddZeros = str
  
End Function

Public Function getStatusColour(status As String) As Variant

  If status = "working" Then
    getStatusColour = RGB(0, 255, 0)
  ElseIf status = "waiting" Then
    getStatusColour = RGB(255, 99, 0)
  ElseIf status = "rejected" Then
    getStatusColour = RGB(255, 0, 0)
  Else
    getStatusColour = RGB(0, 0, 0)
  End If

End Function

Public Function encodeURL(urlText As String) As String
  Dim thisChr As String
  Dim out As String
  Dim i As Integer

  out = ""
  For i = 1 To Len(urlText)
    thisChr = Mid$(urlText, i, 1)
    If needChange(thisChr) Then
      If thisChr = " " Then
        out = out & "+"
      Else
        out = out & "%" & Hex(Asc(thisChr))
      End If
    Else
      out = out & thisChr
    End If
  Next
  encodeURL = out
    
End Function

Private Function needChange(oneChar As String) As Boolean
  Dim out As Boolean
  
  out = True
  If oneChar >= "A" And oneChar <= "Z" Then
    out = False
  ElseIf oneChar >= "a" And oneChar <= "z" Then
    out = False
  ElseIf oneChar >= "0" And oneChar <= "9" Then
    out = False
  ElseIf oneChar = "-" Or oneChar = "_" Or oneChar = "*" Or oneChar = "." Then
    out = False
  End If
  needChange = out
        
End Function

Public Function decodeCode(value As Variant, valueRange As String, colOffset As Integer) As String
  Dim whre As Range
  Dim inRow As Long
  Dim inCol As Long
  Dim i As Integer
  
  Set whre = ActiveWorkbook.Sheets("Lists").Range(valueRange).Find(value, lookAt:=xlWhole)
  If whre Is Nothing Then
    decodeCode = ""
  Else
    inRow = whre.row
    inCol = ActiveWorkbook.Sheets("Lists").Range(valueRange).column + colOffset
    decodeCode = ActiveWorkbook.Sheets("Lists").Cells(inRow, inCol).value
  End If
    
End Function

Public Sub syncBeforeClose()
  Dim currentLine As Integer
  Dim currentColumn As String
  Dim id As String
  Dim saveFlag As Boolean
  
  Application.ScreenUpdating = False
  
  saveFlag = False
  
  'check for changes
  currentLine = locality.startLine
  Do While True
    If ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value = "" Then Exit Do
    If ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SYSTEM_MESSAGE & currentLine).value <> "Synced OK" Then
      If ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & currentLine).value <> "" Then
        saveFlag = True
        Exit Do
      End If
    End If
    currentLine = currentLine + 1
  Loop
  
  If Not saveFlag Then
    currentColumn = record.startColumn
    Do While True
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value = "" Then Exit Do
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_SYSTEM_MESSAGE).value <> "Synced OK" Then
        If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_ID).value <> "" Then
          saveFlag = True
          Exit Do
        End If
      End If
      currentColumn = incrementColumn(currentColumn)
    Loop
  End If
  
  'save
  If saveFlag Then
    If MsgBox("Do you want to save edited data to FRED", vbYesNo + vbQuestion, "Save to FRED") = vbYes Then
      saveAllLocalities
      saveAllPalRecords
    ElseIf MsgBox("Are you sure?  If you do not save your data to FRED the spreadsheet and the database will not be synchronised and when you next open this spreadsheet you could retrieve old data from FRED.", vbYesNo + vbQuestion, "Save to FRED") = vbNo Then
      saveAllLocalities
      saveAllPalRecords
    End If
  End If

  Application.ScreenUpdating = True

End Sub

Public Sub syncBeforeOpen()
  Dim currentLine As Integer
  Dim currentColumn As String
  Dim id As String
  Dim savedFlag As Boolean
  
  Application.ScreenUpdating = False
  
  getFolderList
  
  savedFlag = False
  
  'check for saved localities
  currentLine = locality.startLine
  Do While True
    If ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value = "" Then Exit Do
    If ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & currentLine).value <> "" Then
      savedFlag = True
      Exit Do
    End If
    currentLine = currentLine + 1
  Loop
  
  If Not savedFlag Then
    currentColumn = record.startColumn
    Do While True
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value = "" Then Exit Do
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_ID).value <> "" Then
        savedFlag = True
        Exit Do
      End If
      currentColumn = incrementColumn(currentColumn)
    Loop
  End If
  
  'refresh
  If savedFlag Then
    If MsgBox("Do you want to refresh the spreadsheet data from FRED", vbYesNo + vbQuestion, "Refresh from FRED") = vbYes Then
      refreshAllLocalities
      refreshAllPalRecords
    ElseIf MsgBox("Are you sure?  If you do not refresh from FRED the data in the spreadsheet may not be synchronised with the database", vbYesNo + vbQuestion, "Refresh from FRED") = vbNo Then
      refreshAllLocalities
      refreshAllPalRecords
    End If
  End If

  Application.ScreenUpdating = True

End Sub

Public Function getSecureList(sheetName As String, listName As String, queryTableName As String, Optional urlParams As String = "", Optional listRef As String = "")

    Dim msg As String
    Dim htmlContent As Object
    Dim htmlTables As Object
    Dim firstResultVal As String
    Dim url As String
    Dim rowCount As Integer
    
    url = getSecureUrlBase("list.jsp") & "&listName=" & listName
    If Len(urlParams) > 0 Then
        url = url & "&" & urlParams
    End If
        
    Set htmlContent = sendSecure(url)
    
    
    Set htmlTables = htmlContent.getElementsByTagName("table")
    rowCount = 0
    If htmlTables.length > 0 Then rowCount = htmlTables(0).Rows.length
    If rowCount > 0 Then
        firstResultVal = htmlTables(0).Rows(0).Cells(0).innerText
    Else
        firstResultVal = ""
    End If
    
    msg = firstResultVal
    If InStr(firstResultVal, "Error") = 1 Then
        MsgBox msg, vbCritical, "Error"
        utils.clearUser
    ElseIf rowCount = 0 Then
        MsgBox "No data found", vbInformation, "Message"
    Else
        Call copyHtmlTableToQueryTable(htmlContent, sheetName, queryTableName, listRef)
    End If
  
  getSecureList = msg

End Function

Public Function sendSecureImport(urlParams As String, Optional ByVal httpMethod As String = "GET") As String

    Dim msg As String
    Dim htmlContent As Object
    Dim firstResultVal As String
    Dim htmlTables As Object
    Dim rowCount As Integer

    If httpMethod = "GET" Then
        Set htmlContent = sendSecure(getSecureUrlBase("import.jsp") & "&" & urlParams)
    Else
        Set htmlContent = sendSecure(getSecureUrlBase("import.jsp"), httpMethod, urlParams)
    End If
    Call copyHtmlTableToQueryTable(htmlContent, "Lists", "import")
    
    Set htmlTables = htmlContent.getElementsByTagName("table")
    rowCount = 0
    If htmlTables.length > 0 Then rowCount = htmlTables(0).Rows.length
    If rowCount > 0 Then
        firstResultVal = htmlContent.getElementsByTagName("table")(0).Rows(0).Cells(0).innerText
        If InStr(firstResultVal, "Error") = 1 Then
            msg = firstResultVal
            utils.clearUser
        Else
            msg = ""
        End If
    Else
        msg = "Error: invalid response, data may not have been saved."
    End If
  
  sendSecureImport = msg

End Function

Private Function getSecureUrlBase(jspFile As String) As String
    Dim url As String
    
    url = ""
    If authenticate Then
        url = Constants.secureBaseURL & jspFile & ";jsessionid=" & utils.sessionId & "?template-version=" & getTemplateVersion()
    End If
    
    getSecureUrlBase = url
End Function


Private Function sendSecure(secureUrl As String, Optional ByVal httpMethod As String = "GET", Optional ByVal requestBody As String = "") As Object
    Dim htmlContent As Object
    
    Set htmlContent = CreateObject("htmlfile")
    'WinHTTP.WinHTTPrequest used instead of MSXML2.XMLHTTP as it does not cache responses (caching breaks PNumber retrieval)
    If httpMethod = "POST" Then
        With CreateObject("WinHTTP.WinHTTPrequest.5.1")
            .Open "POST", secureUrl, False
            .SetRequestHeader "Content-Type", "application/x-www-form-urlencoded"
            .send requestBody
            htmlContent.Body.Innerhtml = .responseText
        End With
    Else
        With CreateObject("WinHTTP.WinHTTPrequest.5.1")
            .Open "GET", secureUrl, False
            .send
            htmlContent.Body.Innerhtml = .responseText
        End With
    End If
    Set sendSecure = htmlContent
End Function

Private Sub copyHtmlTableToQueryTable(htmlContent As Object, sheetName As String, queryTableName As String, Optional listRef As String = "")

    Dim columnNumToStart As Integer
    Dim iRow As Integer
    Dim iCol As Integer
    Dim Tr As Object
    Dim Td As Object
    Dim queryTableRows As Object
    
    Dim name As name
    Dim rowCount As Integer
    Dim htmlTables As Object

    Set queryTableRows = ActiveWorkbook.Sheets(sheetName).QueryTables(queryTableName).ResultRange
    columnNumToStart = queryTableRows.column
    iRow = queryTableRows.row
    iCol = columnNumToStart
      
    Set htmlTables = htmlContent.getElementsByTagName("table")
    If htmlTables.length > 0 Then
        With htmlTables(0)
            For Each Tr In .Rows
                For Each Td In Tr.Cells
                    ActiveWorkbook.Sheets(sheetName).Cells(iRow, iCol).value = Td.innerText
                    iCol = iCol + 1
                Next Td
                iCol = columnNumToStart
                iRow = iRow + 1
            Next Tr
        End With
    End If
    
    'Update the cell block range so any linked drop-downs include entire list of options
    If Len(listRef) > 0 Then
        Set name = ActiveWorkbook.Names.Item(listRef)
        rowCount = htmlTables(0).Rows.length
        With name
            .RefersTo = .RefersToRange.Resize(rowCount, 1)
        End With
    End If
End Sub

Public Function getTemplateVersion() As String
    getTemplateVersion = Sheets(Constants.hiddenSheetName).Range("A1")
End Function

Public Function getTemplateBuildDate() As String
    getTemplateBuildDate = Sheets(Constants.hiddenSheetName).Range("B1")
End Function


' Takes a string of the form "JSESSIONID=40DD2DFCAF24A2D64544F55194FCE04E;path=/pamsservices;HttpOnly"
' and returns only the portion "40DD2DFCAF24A2D64544F55194FCE04E"
Public Function GetJsessionId(setCookieStr As String) As String

    Dim jsessionid As String

    Dim words() As String
    Dim word As Variant

    words = Split(setCookieStr, ";")
    For Each word In words
        If InStr(1, word, "JSESSIONID") > 0 Then
            jsessionid = Split(word, "=")(1)
        End If
    Next word

    GetJsessionId = jsessionid
End Function
