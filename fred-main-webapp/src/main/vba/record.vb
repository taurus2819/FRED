Option Explicit

Public Const startColumn As String = "D"
Private errFlag As Boolean
Public sel As Range

Public Sub getPalRecordList(folderId As String)
  Dim msg As String
  
  If folderId <> "" Then
    If authenticate Then
      msg = getSecureList("Lists", "palList", "ext_palRecordList", "folderID=" & folderId, "palRecordList")
            
      If InStr(msg, "Error") > 0 Then
        MsgBox msg, vbCritical, "Error"
        utils.clearUser
        With ActiveWorkbook.Sheets("Lists").QueryTables("ext_palRecordList")
          .PostText = "listName=blankPalList"
          .Connection = "URL;" & baseURL & "list.jsp"
          .refresh BackgroundQuery:=False
        End With
        ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_RECORD_LIST).value = ""
      Else
        ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_RECORD_LIST).value = msg
      End If
    End If
  End If
  getLocSampleList (folderId)

End Sub

Public Sub getLocSampleList(folderId As String)
  Dim msg As String
  
  If folderId <> "" Then
    If authenticate Then
      msg = getSecureList("Lists", "locSampleList", "ext_locSampleList", "folderID=" & folderId, "locSampleList")

        
      If InStr(msg, "Error") > 0 Then
        MsgBox msg, vbCritical, "Error"
        utils.clearUser
        With ActiveWorkbook.Sheets("Lists").QueryTables("ext_locSampleList")
          .PostText = "listName=blankLocSampleList"
          .Connection = "URL;" & baseURL & "list.jsp"
          .refresh BackgroundQuery:=False
        End With
      End If
    End If
  End If

End Sub

Public Sub getAllRecords()
  Dim folderId As String
  Dim rng As Range
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value <> "" Then
    folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value, "folderList", 1)
    getPalRecordList (folderId)
    For Each rng In Range("palRecordList")
      ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_RECORD_LIST).value = rng.value
      getRecord False
    Next rng
  End If
  
  refreshFlag = False
  Application.ScreenUpdating = True

End Sub

Public Sub getWorkingRecords()
  Dim folderId As String
  Dim rng As Range
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value <> "" Then
    folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value, "folderList", 1)
    getPalRecordList (folderId)
    For Each rng In Range("palRecordList")
      ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_RECORD_LIST).value = rng.value
      getRecord True
    Next rng
  End If
  
  refreshFlag = False
  Application.ScreenUpdating = True

End Sub

Public Sub getSelectedRecord()

  getRecord False

End Sub

Public Sub getRecord(onlyWorking As Boolean)
  Dim rng As Range
  Dim recordID As String
  Dim folderId As String
  Dim currentColumn As String
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_RECORD_LIST).value <> "" Then
    recordID = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_RECORD_LIST).value, "palRecordList", 1)
    currentColumn = startColumn
    Do While True
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value = "" Then Exit Do
      currentColumn = incrementColumn(currentColumn)
    Loop
    folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value, "folderList", 1)
    getSavedQueryData recordID, folderId, "Paleontological", currentColumn, onlyWorking
    
  End If
  
  refreshFlag = False
  Application.ScreenUpdating = True
    
End Sub

Public Function getSavedQueryData(id As String, folderId As String, docType As String, toColumn As String, onlyWorking As Boolean, Optional checkDups As Boolean = True) As String
  Dim currentColumn As String
  Dim cellData As String
  Dim cellArray() As String
  Dim recordID As String
  Dim downloadTaxaLine As Integer
  Dim taxaListLine As Integer
  Dim taxaGroup As String
  Dim taxaName As String
  Dim status As String
  Dim taxaStatus As String
  Dim queryData As String
  Dim errorMsg As String
  
  If authenticate Then
  
  Application.ScreenUpdating = False
  
  If checkDups Then
    'check not already in spreadsheet
    currentColumn = startColumn
    Do While True
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_ID) = id Then Exit Function
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME) = "" Then Exit Do
      currentColumn = incrementColumn(currentColumn)
    Loop
  End If
  
  queryData = "docType=" & docType & "&id=" & id & "&folderID=" & folderId
  errorMsg = getSecureList("Lists", "document", "ext_downloadFREDLocality", queryData)

  'copy from web query to main worksheet
  cellData = ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_RECORD_ID & Constants.DOWNLOAD_DATA_ROW).value
  If cellData <> "Error" Then
  
    status = ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STATUS & Constants.DOWNLOAD_DATA_ROW).value
    If Not onlyWorking Or (onlyWorking And status = "working") Then
   
    recordID = cellData
    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_RECORD_ID).value = recordID

    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_LOCALITY_NAME).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_LOCALITY_NAME & Constants.DOWNLOAD_DATA_ROW).value
 
    If ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STATUS & Constants.DOWNLOAD_DATA_ROW).value = "rejected" Then
      ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STATUS).value = _
          "rejected" & Chr(10) & "Curator Comments: " & _
          ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_CUR_COMMENTS & Constants.DOWNLOAD_DATA_ROW).value
    Else
      ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STATUS).value = _
          ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STATUS & Constants.DOWNLOAD_DATA_ROW).value
    End If
    
    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_FOLDER).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_FOLDER & Constants.DOWNLOAD_DATA_ROW).value, "folderListID", -1)

    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_WORKING_COMMENTS).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_WORKING_COMMENTS & Constants.DOWNLOAD_DATA_ROW).value

    cellArray = decodeDateString(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_ID_DATE & Constants.DOWNLOAD_DATA_ROW).value)
    If cellArray(0) <> "" Then
      ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_ID_DATE).value = CDate(cellArray(0))
      ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_DATE_ROUNDING).value = cellArray(1)
    End If
   
    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_IDENTIFIERS).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_IDENTIFIERS & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STAGE_START).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STAGE_START & Constants.DOWNLOAD_DATA_ROW).value, "stageNameID", -1)

    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STAGE_START_MOD).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STAGE_START_MOD & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STAGE_STOP).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STAGE_STOP & Constants.DOWNLOAD_DATA_ROW).value, "stageNameID", -1)

    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STAGE_STOP_MOD).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STAGE_STOP_MOD & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STAGE_COMMENTS).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_STAGE_COMMENTS & Constants.DOWNLOAD_DATA_ROW).value
 
    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_LABORATORY).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_LABORATORY & Constants.DOWNLOAD_DATA_ROW).value, "labSectionID", -1)

     ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_LAB_NUMBER).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_LAB_NUMBER & Constants.DOWNLOAD_DATA_ROW).value
 
     ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_COLLECTION_COMMENTS).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PAL_COLLECTION_COMMENTS & Constants.DOWNLOAD_DATA_ROW).value
  
    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_SYSTEM_MESSAGE).value = "Synced OK"

    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STATUS).Font.Color = getStatusColour(ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_STATUS))
    ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & Constants.PAL_RECORD_USER_DATA_START & ":" & toColumn & Constants.PAL_RECORD_DATA_STOP).Font.Color = RGB(100, 100, 255)
    
    'clear taxonomic data for this record
    taxaListLine = PAL_RECORD_TAXA_START
    Do While True
      If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value = "" Then Exit Do
      ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & taxaListLine).value = ""
      taxaListLine = taxaListLine + 1
    Loop
    
    'copy taxa list
    downloadTaxaLine = CInt(Constants.DOWNLOAD_DATA_ROW) + 1
    Do While True
      If ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_TAXONOMIC_GROUP & downloadTaxaLine).value = "" _
          Or ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_TAXONOMIC_GROUP & downloadTaxaLine).value = "DO NOT EDIT" Then Exit Do
      taxaGroup = ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_TAXONOMIC_GROUP & downloadTaxaLine).value
      taxaName = ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_TAXONOMIC_NAME & downloadTaxaLine).value
      taxaStatus = ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_STATUS & downloadTaxaLine).value
      taxaListLine = PAL_RECORD_TAXA_START
      Do While True
        If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value = "" Then
          ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value = _
             ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_TAXONOMIC_GROUP & downloadTaxaLine).value
          ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_NAME & taxaListLine).value = _
             ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_TAXONOMIC_NAME & downloadTaxaLine).value
          ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_AUTHOR & taxaListLine).value = _
             ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_AUTHOR & downloadTaxaLine).value
          Exit Do
        ElseIf Trim(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value) = taxaGroup _
            And fixTaxaName(Trim(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_NAME & taxaListLine).value)) = taxaName _
            And ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & taxaListLine).value = "" Then
            Exit Do
        End If
        taxaListLine = taxaListLine + 1
      Loop
      ActiveWorkbook.Sheets("Pal_Record").Range(toColumn & taxaListLine).value = "'" & _
         ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TAXA_COMMENTS & downloadTaxaLine).value
      If taxaStatus = "approved" Then
        ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine & ":" & Constants.PAL_AUTHOR & taxaListLine).Font.Color = RGB(0, 0, 0)
        ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine & ":" & Constants.PAL_AUTHOR & taxaListLine).Interior.Color = 16751052
      Else
        ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine & ":" & Constants.PAL_AUTHOR & taxaListLine).Font.Color = RGB(0, 0, 0)
        ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine & ":" & Constants.PAL_AUTHOR & taxaListLine).Interior.Color = RGB(255, 0, 0)
      End If
      downloadTaxaLine = downloadTaxaLine + 1
    Loop
    
    End If
    
  End If

  Application.ScreenUpdating = True
  
  End If
  
End Function

Private Function fixTaxaName(name As String) As String
  name = Replace(name, "''", "'")
  fixTaxaName = Replace(name, "'", Chr(34))
  
End Function

Public Sub submitTaxonomicName()

  If authenticate Then
    constructSubmitTaxaURL
  Else
    MsgBox "Unable to save data to FRED", vbOKOnly + vbInformation
  End If

End Sub

Public Sub savePalRecord()

  save

End Sub

Private Function save() As Boolean

  If authenticate Then
    constructSaveSubmitURL "Paleontological", "save"
    save = True
  Else
    MsgBox "Unable to save data to FRED", vbOKOnly + vbInformation
    save = False
  End If
    
End Function

Public Sub submitPalRecord()

  If authenticate Then
    constructSaveSubmitURL "Paleontological", "submit"
  Else
    MsgBox "Unable to submit data to FRED", vbOKOnly + vbInformation
  End If
  
End Sub

Public Sub saveAllPalRecords()
  Dim currentLine As Integer
  
  currentLine = startLine
  Do While True
    If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_LOCALITY_NAME & currentLine).value = "" Then Exit Do
    If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_SYSTEM_MESSAGE & currentLine).value <> "Synced OK" Then
      If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_RECORD_ID & currentLine).value <> "" Then
        ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_LOCALITY_NAME & currentLine).Select
        If save = False Then Exit Do
      End If
    End If
    currentLine = currentLine + 1
  Loop

End Sub

Private Function constructSubmitTaxaURL()
  Dim userSel As Range
  Dim currentLine As Integer
  Dim query As String
  
  errFlag = False
  
  Application.ScreenUpdating = False

  currentLine = Constants.PAL_RECORD_TAXA_START

  'save current user selection before making selections in code
  Set userSel = sel
  
  Do While True
    query = ""
    If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & currentLine).value = "" Then Exit Do

    'check row has been selected by user
    If userSel Is Nothing Then
      MsgBox "Please select a row"
      currentLine = currentLine + 1
      errFlag = True
      Exit Do
    Else
      If Not Intersect(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & currentLine & ":" & Constants.PAL_AUTHOR & currentLine), userSel) Is Nothing Then
        If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & currentLine).value <> "" _
            And ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_NAME & currentLine).value <> "" Then
          'ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & currentLine & ":" & Constants.PAL_AUTHOR & currentLine).Font.Color = RGB(0, 0, 0)
          Application.StatusBar = "Processing taxonomic entry " & (currentLine - Constants.PAL_RECORD_TAXA_START + 1) & ": " & ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_NAME & currentLine).value
          query = "button=submit&Type=Taxa"
          query = query & "&TaxaGroup=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & currentLine).value)
          query = query & "&TaxaName=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_NAME & currentLine).value)
          query = query & "&Author=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_AUTHOR & currentLine).value)
        Else
          MsgBox "Please select a group and enter a name"
          Exit Do
        End If
      End If
    End If
      
    If query <> "" Then
      'MsgBox query
      
      If Not sendSubmitTaxaURL(query, currentLine) Then
        errFlag = True
        currentLine = currentLine + 1
        Exit Do
      End If
      
    End If
    currentLine = currentLine + 1
  Loop
    
  Application.StatusBar = False
  Application.ScreenUpdating = True
  
  If currentLine = Constants.PAL_RECORD_TAXA_START Then
    MsgBox "No data found in spreadsheet.  Taxonomic names must begin on line " & Constants.PAL_RECORD_TAXA_START, vbExclamation
  ElseIf Not errFlag Then
    MsgBox "All names successfully submitted", vbInformation
  End If

End Function

Private Function constructSaveSubmitURL(docType As String, button As String) As Boolean
  Dim userSel As Range
  Dim query As String
  Dim folderName As String
  Dim recordID As String
  Dim folderId As String
  Dim sampleID As String
  Dim featureName As String
  Dim currentColumn As String
  Dim dte As String
  Dim rnd As String
  Dim taxaListLine As Integer
  Dim taxaString As String
  
  errFlag = False
  
  Application.ScreenUpdating = False

  currentColumn = startColumn

  'save current user selection before making selections in code
  Set userSel = sel
  
  Do While True
    query = ""
    If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value = "" Then Exit Do

    'check row has been selected by user
    If userSel Is Nothing Then
      MsgBox "Please select a row"
      currentColumn = incrementColumn(currentColumn)
      errFlag = True
      Exit Do
    Else
      If Not Intersect(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_DATA_START & ":" & currentColumn & Constants.PAL_RECORD_DATA_STOP), userSel) Is Nothing Then
        If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_ID).value <> "" Then
          recordID = ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_ID).value
        ElseIf ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value <> "" Then
          sampleID = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value, "locSampleList", 1)
          If sampleID = "" Then sampleID = findSampleID(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value)
          If InStr(sampleID, "Error") Then
            MsgBox (sampleID)
            Exit Do
          End If
          ActiveWorkbook.Sheets("Pal_Record").Select
        End If

        If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_FOLDER).value <> "" Then
          folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_FOLDER).value, "folderList", 1)
        Else
          folderName = ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value
          If folderName <> "" Then
            folderId = decodeCode(folderName, "folderList", 1)
            If folderId = -1 Then
              MsgBox "No folders defined.  Please refresh folder list", vbOKOnly + vbExclamation
              Exit Function
            End If
            ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_FOLDER).value = folderName
          End If
        End If
    
        If (recordID = "" And sampleID = "") Or folderId = "" Then
          MsgBox ("Please select a locality and a folder")
          Exit Do
        End If
    
        featureName = ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value
        ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_USER_DATA_START & ":" & currentColumn & Constants.PAL_RECORD_DATA_STOP).Font.Color = RGB(0, 0, 0)
        Application.StatusBar = "Processing record " & featureName
        query = "button=" & button & "&Type=" & docType

        query = query & "&FoldID=" & folderId
        query = query & "&RecID=" & recordID
        query = query & "&SampID=" & sampleID
        query = query & "&WorkComm=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_WORKING_COMMENTS).value)
        
        dte = ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_ID_DATE).value
        rnd = ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_DATE_ROUNDING).value
        query = query & "&PalDate=" & encodeURL(utils.formatDate(dte, rnd))
        query = query & "&Identifier=" & Replace(encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_IDENTIFIERS).value), "%23", Chr(10))
        query = query & "&StageStageStart=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_STAGE_START).value, "stageName", 1))
        query = query & "&StageStartMod=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_STAGE_START_MOD).value)
        query = query & "&StageStageStop=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_STAGE_STOP).value, "stageName", 1))
        query = query & "&StageStopMod=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_STAGE_STOP_MOD).value)
        query = query & "&StComm=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_STAGE_COMMENTS).value)
        query = query & "&SectID=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LABORATORY).value, "labSection", 1))
        query = query & "&LabNum=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LAB_NUMBER).value)
        query = query & "&CollComm=" & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_COLLECTION_COMMENTS).value)
      
        taxaListLine = PAL_RECORD_TAXA_START
        Do While True
          If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value = "" Then Exit Do
          If Trim(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & taxaListLine).value) <> "" Then
            query = query & "&Taxa2="
            query = query & encodeURL(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value & "*" & _
                ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_NAME & taxaListLine).value & "*" & _
                ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_AUTHOR & taxaListLine).value & "*" & _
                stripStar(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & taxaListLine).value))
          End If
          taxaListLine = taxaListLine + 1
        Loop
      End If
    End If
      
    If query <> "" Then
    'MsgBox query
      
    If Not sendSaveSubmitURL(docType, query, currentColumn, folderId, recordID) Then
      errFlag = True
      currentColumn = incrementColumn(currentColumn)
      Exit Do
    End If
    End If

    currentColumn = incrementColumn(currentColumn)
  Loop
    
  Application.StatusBar = False
  Application.ScreenUpdating = True
  
  If currentColumn = startColumn Then
    MsgBox "No data found in spreadsheet.  Data must begin on column " & startColumn, vbExclamation
  ElseIf Not errFlag And button = "submit" Then
    MsgBox "All columns successfully submitted", vbInformation
  ElseIf Not errFlag And button = "save" Then
    MsgBox "All columns successfully saved", vbInformation
  End If
    
End Function

Private Function sendSubmitTaxaURL(queryString As String, line As Integer) As Boolean
  Dim status As String
  Dim msg As String
  Dim taxaList As Variant
  Dim taxaGroup As String
  Dim taxaName As String
  Dim i As Integer

  sendSecureImport (queryString)
  
  Application.GoTo reference:="status"
  status = ActiveCell.value
  Application.GoTo reference:="message"
  msg = ActiveCell.value
  Application.GoTo reference:="sessionId"
  utils.sessionId = ActiveCell.value
  
  ActiveWorkbook.Sheets("Pal_Record").Select
  
  refreshFlag = True
  If status = "Submitted OK" Then
    ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & line & ":" & Constants.PAL_AUTHOR & line).Font.Color = RGB(0, 0, 0)
    ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & line & ":" & Constants.PAL_AUTHOR & line).Interior.Color = RGB(255, 0, 0)
    Application.StatusBar = False
    sendSubmitTaxaURL = True
  ElseIf status = "AuthError" Then
    If msg = "No username/password" Then
      MsgBox "Username/password not valid", vbCritical, "Login Error"
    ElseIf msg = "User not authorised" Then
      MsgBox "Insufficient rights", vbCritical, "Login Error"
    Else
      MsgBox "Unspecified login error", vbCritical, "Login Error"
    End If
    clearUser
    Application.StatusBar = False
    sendSubmitTaxaURL = False
  Else
    MsgBox "Data error: " & msg
    ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & line & ":" & Constants.PAL_AUTHOR & line).Font.Color = RGB(255, 0, 0)
    Application.StatusBar = False
    sendSubmitTaxaURL = False
  End If
  refreshFlag = False

End Function

Private Function sendSaveSubmitURL(docType As String, queryString As String, column As String, folderId As String, recordID As String) As Boolean
  Dim status As String
  Dim msg As String
  Dim taxaList As Variant
  Dim taxaGroup As String
  Dim taxaName As String
  Dim i As Integer
  Dim taxaListLine As Integer

  sendSecureImport (queryString)
  
  Application.GoTo reference:="status"
  status = ActiveCell.value
  Application.GoTo reference:="message"
  msg = ActiveCell.value
  Application.GoTo reference:="sessionId"
  utils.sessionId = ActiveCell.value
  
  ActiveWorkbook.Sheets("Pal_Record").Select
  
  refreshFlag = True
  If status = "Saved OK" Or status = "Submitted OK" Then
    ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_RECORD_ID).value = msg
    getSavedQueryData msg, folderId, docType, column, False, False
    sendSaveSubmitURL = True
  ElseIf status = "AuthError" Then
    If msg = "No username/password" Then
      MsgBox "Username/password not valid", vbCritical, "Login Error"
    ElseIf msg = "User not authorised" Then
      MsgBox "Insufficient rights", vbCritical, "Login Error"
    Else
      MsgBox "Unspecified login error", vbCritical, "Login Error"
    End If
    clearUser
    ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_SYSTEM_MESSAGE).value = "Login Error: " & msg
    ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_RECORD_USER_DATA_START & ":" & column & Constants.PAL_RECORD_DATA_STOP).Font.Color = RGB(255, 0, 0)
    Application.StatusBar = False
    sendSaveSubmitURL = False
  ElseIf status = "TaxaListError" Then
    MsgBox "Unknown taxonomic entries: " & Chr(10) & Replace(Replace(msg, "*", " - "), "#", Chr(10)) & "Please either edit the entries or submit these taxonomic names to the thesaurus"
    taxaList = Split(msg, "#")
    For i = 0 To UBound(taxaList) - 1
      taxaGroup = Trim(Left(taxaList(i), InStr(taxaList(i), "*") - 1))
      taxaName = Trim(Mid(taxaList(i), InStr(taxaList(i), "*") + 1))
      taxaListLine = Constants.PAL_RECORD_TAXA_START
      Do While True
        If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value = "" Then Exit Do
        If ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine).value = taxaGroup _
            And ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_NAME & taxaListLine).value = taxaName Then
          ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_TAXONOMIC_GROUP & taxaListLine & ":" & Constants.PAL_AUTHOR & taxaListLine).Font.Color = RGB(255, 0, 0)
          Exit Do
        End If
        taxaListLine = taxaListLine + 1
     Loop
    Next i
    Application.StatusBar = False
    sendSaveSubmitURL = False
  Else
    MsgBox "Data error: " & msg
    ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_SYSTEM_MESSAGE).value = "Error:"
    'ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_SYSTEM_MESSAGE & line).value = queryString
    ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_RECORD_USER_DATA_START & ":" & column & Constants.PAL_RECORD_DATA_STOP).Font.Color = RGB(255, 0, 0)
    Application.StatusBar = False
    sendSaveSubmitURL = False
  End If
  refreshFlag = False

End Function

Private Function findSampleID(localityName As String) As String
  Dim errorMsg As String
  
  errorMsg = getSecureList("Lists", "sampleId", "import", "localityName=" & encodeURL(localityName))
  
  Application.GoTo reference:="status"
  findSampleID = ActiveCell.value

End Function

Public Sub invalidateRecord(recordID As String)
  Dim column As String

  If Not refreshFlag Then
    'invalidate record in Pal_Record sheet
    column = startColumn
    Do While True
      If ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_RECORD_ID).value = "" Then Exit Sub
      If ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_RECORD_ID).value = recordID Then Exit Do
      column = incrementColumn(column)
    Loop
    If ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_STATUS).value = "approved" Then
      MsgBox "Approved localities cannot be edited. Your edits have been reversed", vbInformation
      refreshPalRecord
      Exit Sub
    Else
      ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_SYSTEM_MESSAGE).value = ""
      ActiveWorkbook.Sheets("Pal_Record").Range(column & Constants.PAL_RECORD_USER_DATA_START & ":" & column & Constants.PAL_RECORD_DATA_STOP).Font.Color = RGB(0, 0, 0)
    End If
  End If

End Sub

Public Sub refreshAllPalRecords()
  Dim currentColumn As String
  Dim id As String
  Dim folderId As String
  Dim locDoc As Variant
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  currentColumn = startColumn
  
  Do While True
    If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value = "" Then Exit Do
    id = ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_ID).value
    If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_STATUS).value = "approved" Then
      folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value, "folderList", 1)
    Else
      folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_FOLDER).value, "folderList", 1)
    End If
    If id <> "" And folderId <> "" Then getSavedQueryData id, folderId, "Paleontological", currentColumn, False
    currentColumn = incrementColumn(currentColumn)
  Loop

  Application.ScreenUpdating = True

End Sub

Public Sub refreshPalRecord()
  Dim userSel As Range
  Dim currentColumn As String
  Dim id As String
  Dim folderId As String
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  currentColumn = startColumn
  'save current user selection before making selections in code
  Set userSel = sel
  
  Do While True
    If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_LOCALITY_NAME).value = "" Then Exit Do
    'check row has been selected by user
    If userSel Is Nothing Then
      MsgBox "Please select a row"
    ElseIf Not Intersect(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_DATA_START & ":" & currentColumn & Constants.PAL_RECORD_DATA_STOP), userSel) Is Nothing Then
      id = ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_RECORD_ID).value
      If ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_STATUS).value = "approved" Then
        folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value, "folderList", 1)
      Else
        folderId = decodeCode(ActiveWorkbook.Sheets("Pal_Record").Range(currentColumn & Constants.PAL_FOLDER).value, "folderList", 1)
      End If
      If id <> "" And folderId <> "" Then getSavedQueryData id, folderId, "Paleontological", currentColumn, False, False
    End If
    currentColumn = incrementColumn(currentColumn)
  Loop
    
  Application.ScreenUpdating = True
  refreshFlag = False
    
End Sub

Private Function stripStar(str As String) As String

  If str = "*" Then
    stripStar = ""
  Else
    stripStar = str
  End If

End Function