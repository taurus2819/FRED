Option Explicit

Public Const startLine As Integer = 15
Private errFlag As Boolean
Public sel As Range

Public Sub saveLocality()

  save

End Sub

Private Function save() As Boolean

  If authenticate Then
    constructSaveSubmitURL "save"
    save = True
  Else
    MsgBox "Unable to save data to FRED", vbOKOnly + vbInformation
    save = False
  End If
    
End Function

Public Sub saveAllLocalities()
  Dim currentLine As Integer
  
  currentLine = startLine
  Do While True
    If ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value = "" Then Exit Do
    If ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SYSTEM_MESSAGE & currentLine).value <> "Synced OK" Then
      If ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & currentLine).value <> "" Then
        ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).Select
        If save = False Then Exit Do
      End If
    End If
    currentLine = currentLine + 1
  Loop

End Sub

Public Sub submitLocality()

  If authenticate Then
    constructSaveSubmitURL "submit"
  Else
    MsgBox "Unable to submit data to FRED", vbOKOnly + vbInformation
  End If
  
End Sub

Public Sub refreshLocality()
  Dim userSel As Range
  Dim currentLine As Integer
  Dim id As String
  Dim folderId As String
  Dim locDoc As Variant
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  currentLine = startLine
  'save current user selection before making selections in code
  Set userSel = sel
  
  Do While True
    'check row has been selected by user
    If userSel Is Nothing Then
      MsgBox "Please select a row"
    ElseIf Not Intersect(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_DATA_START & currentLine & ":" & Constants.LOC_DATA_STOP & currentLine), userSel) Is Nothing Then
      If InStr(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value, ":") > 0 Then
        locDoc = Split(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value, ":")
        If locDoc(0) = "Locality" Then
          id = ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & currentLine).value
        ElseIf locDoc(0) = "Sample" Then
          id = ActiveWorkbook.Sheets("Locality").Range(Constants.SAMPLE_ID & currentLine).value
        End If
      End If
      If ActiveWorkbook.Sheets("Locality").Range(Constants.status & currentLine).value = "approved" Then
        folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value, "folderList", 1)
      Else
        folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.folder & currentLine).value, "folderList", 1)
      End If
      If id <> "" And folderId <> "" Then getSavedQueryData id, folderId, CStr(locDoc(0)), currentLine, False
    End If
    currentLine = currentLine + 1
    If ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value = "" Then Exit Do
  Loop
    
  Application.ScreenUpdating = True
  refreshFlag = False
    
End Sub

Public Sub refreshAllLocalities()
  Dim currentLine As Integer
  Dim id As String
  Dim folderId As String
  Dim locDoc As Variant
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  currentLine = startLine
  
  Do While True
    If InStr(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value, ":") <= 0 Then
      Exit Do
    Else
      locDoc = Split(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value, ":")
      If locDoc(0) = "Locality" Then
        id = ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & currentLine).value
      ElseIf locDoc(0) = "Sample" Then
        id = ActiveWorkbook.Sheets("Locality").Range(Constants.SAMPLE_ID & currentLine).value
      Else
        Exit Do
      End If
    End If
    If ActiveWorkbook.Sheets("Locality").Range(Constants.status & currentLine).value = "approved" Then
      folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value, "folderList", 1)
    Else
      folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.folder & currentLine).value, "folderList", 1)
    End If
    If id <> "" And folderId <> "" Then getSavedQueryData id, folderId, CStr(locDoc(0)), currentLine, False
    currentLine = currentLine + 1
  Loop

  Application.ScreenUpdating = True

End Sub

Public Sub createFolder()
  Dim query As String
  
  If authenticate Then
    query = "Type=NewFold"
    query = query & "&FoldName=" & encodeURL(InputBox("Please enter a name for your folder", "Folder Name", "New Folder"))
  
    sendSecureImport (query)
  
    'Application.Goto reference:="status"
    'STATUS = ActiveCell.value
    'Application.Goto reference:="message"
    'msg = ActiveCell.value
    'Application.Goto reference:="sessionId"
    'utils.sessionId = ActiveCell.value
    
    getFolderList
   End If

End Sub

Public Sub invalidateRow(line As Integer)
  
  If Not refreshFlag Then
    If ActiveWorkbook.Sheets("Locality").Range(Constants.status & line).value = "approved" Then
      MsgBox "Approved localities cannot be edited. Your edits have been reversed", vbInformation
      refreshLocality
    Else
      ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SYSTEM_MESSAGE & line).value = ""
      ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_USER_DATA_START & line & ":" & Constants.LOC_DATA_STOP & line).Font.Color = RGB(0, 0, 0)
    End If
  End If

End Sub

Public Sub getFolderList()
  Dim msg As String
  Dim folderId As String
  
  If authenticate Then
    msg = getSecureList("Lists", "folderList", "ext_folderList", , "folderList")
        
    If InStr(msg, "Error") > 0 Then
      MsgBox msg, vbCritical, "Error"
      utils.clearUser
      With ActiveWorkbook.Sheets("Lists").QueryTables("ext_folderList")
        .PostText = "listName=blankFolderList"
        .Connection = "URL;" & baseURL & "list.jsp"
        .refresh BackgroundQuery:=False
      End With
      ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value = ""
      ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value = ""
    Else
      folderId = decodeCode(msg, "folderList", 1)
      ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value = msg
      ActiveWorkbook.Sheets("Pal_Record").Range(Constants.PAL_FOLDER_LIST).value = msg
      getLocalityList (folderId)
      record.getPalRecordList (folderId)
    End If
  End If

End Sub

Public Sub getLocalityList(folderId As String)
  Dim msg As String
  
  If folderId <> "" Then
    If authenticate Then
      msg = getSecureList("Lists", "localityList", "ext_localityList", "folderID=" & folderId, "localityList")
       
      If InStr(msg, "Error") > 0 Then
        MsgBox msg, vbCritical, "Error"
        utils.clearUser
        With ActiveWorkbook.Sheets("Lists").QueryTables("ext_localityList")
          .PostText = "listName=blankLocalityList"
          .Connection = "URL;" & baseURL & "list.jsp"
          .refresh BackgroundQuery:=False
        End With
        ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_LOCALITY_LIST).value = ""
      Else
        ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_LOCALITY_LIST).value = msg
        getSampleList decodeCode(msg, "localityList", 1)
      End If
    End If
  End If

End Sub

Public Sub getSampleList(featureID As String)
  Dim msg As String
  
  If featureID <> "" Then
    If authenticate Then
      msg = getSecureList("Lists", "sampleList", "ext_sampleList", "featureID=" & featureID, "sampleList")
       
      If InStr(msg, "Error") > 0 Then
        MsgBox msg, vbCritical, "Error"
        utils.clearUser
        With ActiveWorkbook.Sheets("Lists").QueryTables("ext_sampleList")
          .PostText = "listName=blankSampleList"
          .Connection = "URL;" & baseURL & "list.jsp"
          .refresh BackgroundQuery:=False
        End With
        ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SAMPLE_LIST).value = ""
      Else
        ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SAMPLE_LIST).value = msg
      End If
    
    End If
  End If

End Sub

Public Sub getAllLocalities()
  Dim folderId As String
  Dim rng As Range
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  If ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value <> "" Then
    folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value, "folderList", 1)
    getLocalityList (folderId)
    For Each rng In Range("LocalityList")
      ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_LOCALITY_LIST).value = rng.value
      getLocality
    Next rng
  End If
  
  refreshFlag = False
  Application.ScreenUpdating = True

End Sub

Public Sub getLocality()
  Dim rng As Range
  Dim featureID As String
  Dim folderId As String
  Dim featureType As String
  Dim currentLine As Integer
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  If ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_LOCALITY_LIST).value <> "" Then
    featureID = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_LOCALITY_LIST).value, "localityList", 1)
    currentLine = startLine
    Do While True
      If ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value = "" Then Exit Do
      currentLine = currentLine + 1
    Loop
    folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value, "folderList", 1)
    getSavedQueryData featureID, folderId, "Locality", currentLine
    
    'get any samples
    getSampleList (featureID)
    For Each rng In Range("sampleList")
      ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SAMPLE_LIST).value = rng.value
      getSample
    Next rng
    
  End If
  
  refreshFlag = False
  Application.ScreenUpdating = True
    
End Sub

Public Sub getSample()
  Dim sampleID As String
  Dim folderId As String
  Dim currentLine As Integer
  
  Application.ScreenUpdating = False
  refreshFlag = True
  
  If ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SAMPLE_LIST).value <> "" And ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SAMPLE_LIST).value <> "** Outcrop **" Then
    sampleID = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SAMPLE_LIST).value, "sampleList", 1)
    currentLine = startLine
    Do While True
      If ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value = "" Then Exit Do
      currentLine = currentLine + 1
    Loop
    folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value, "folderList", 1)
    getSavedQueryData sampleID, folderId, "Sample", currentLine
  End If
  
  refreshFlag = False
  Application.ScreenUpdating = True
    
End Sub

Public Function getSavedQueryData(id As String, folderId As String, docType As String, toLine As Integer, Optional checkDups As Boolean = True) As String
  Dim currentLine As Integer
  Dim cellData As String
  Dim cellArray() As String
  Dim oldRefreshFlag As Boolean
  Dim errorMsg As String
  Dim queryData As String
  
  If authenticate Then
  
    Application.ScreenUpdating = False
  
    If checkDups Then
      'check not already in spreadsheet
      currentLine = startLine
      Do While True
        If ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & currentLine) = id Then Exit Function
        If ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine) = "" Then Exit Do
        currentLine = currentLine + 1
      Loop
    End If
  
    queryData = "docType=" & docType & "&id=" & id & "&folderID=" & folderId
    errorMsg = getSecureList("OracleData", "document", "ext_downloadFREDLocality", queryData)
        
    'copy from web query to main worksheet
    cellData = ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_FEATURE_ID & Constants.DOWNLOAD_DATA_ROW).value
  If cellData <> "Error" Then
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & toLine).value = cellData
  
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DOC_TYPE & Constants.DOWNLOAD_DATA_ROW).value & ": " & _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_LOCALITY_TYPE & Constants.DOWNLOAD_DATA_ROW).value
  
    ActiveWorkbook.Sheets("Locality").Range(Constants.folder & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_FOLDER & Constants.DOWNLOAD_DATA_ROW).value, "folderListID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.FR_NUMBER & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_FR_NUMBER & Constants.DOWNLOAD_DATA_ROW).value
          
    ActiveWorkbook.Sheets("Locality").Range(Constants.YARD_FR_NUMBER & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_YARD_FR_NUMBER & Constants.DOWNLOAD_DATA_ROW).value
         
    ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_NAME & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_FEATURE_NAME & Constants.DOWNLOAD_DATA_ROW).value
          
    ActiveWorkbook.Sheets("Locality").Range(Constants.REG_AREA & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_REG_AREA & Constants.DOWNLOAD_DATA_ROW).value, "regAreaID", -1)
         
    ActiveWorkbook.Sheets("Locality").Range(Constants.RECOLLECTION & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_RECOLLECTION & Constants.DOWNLOAD_DATA_ROW).value
         
    ActiveWorkbook.Sheets("Locality").Range(Constants.WORKING_COMMENTS & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_WORKING_COMMENTS & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Locality").Range(Constants.datum & toLine).value = _
        decodeDatum(decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DATUM & Constants.DOWNLOAD_DATA_ROW).value, "datumID", -1), _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_MAP_SHEET & Constants.DOWNLOAD_DATA_ROW).value, _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_EASTING & Constants.DOWNLOAD_DATA_ROW).value)
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.MAP_SHEET & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_MAP_SHEET & Constants.DOWNLOAD_DATA_ROW).value
        
    ActiveWorkbook.Sheets("Locality").Range(Constants.easting & toLine).value = _
        Replace(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_EASTING & Constants.DOWNLOAD_DATA_ROW).value, "#", "")
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.NORTHING & toLine).value = _
        Replace(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_NORTHING & Constants.DOWNLOAD_DATA_ROW).value, "#", "")
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.MAP_YEAR & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_MAP_YEAR & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOCATION_METHOD & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_LOCATION_METHOD & Constants.DOWNLOAD_DATA_ROW).value, "locMethodID", -1)
    
    If ActiveWorkbook.Sheets("Locality").Range(Constants.ACCURACY & toLine).value <> _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_ACCURACY & Constants.DOWNLOAD_DATA_ROW).value Then
      ActiveWorkbook.Sheets("Locality").Range(Constants.ACCURACY & toLine).value = _
          ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_ACCURACY & Constants.DOWNLOAD_DATA_ROW).value
    End If
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_DESC & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_LOCALITY_DESC & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.COUNTRY & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COUNTRY & Constants.DOWNLOAD_DATA_ROW).value, "countryCode", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.OP_COMPANY & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_OP_COMPANY & Constants.DOWNLOAD_DATA_ROW).value
        
    cellArray = decodeDateString(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_START_DATE & Constants.DOWNLOAD_DATA_ROW).value)
    If cellArray(0) <> "" Then
      ActiveWorkbook.Sheets("Locality").Range(Constants.START_DATE & toLine).value = CDate(cellArray(0))
      ActiveWorkbook.Sheets("Locality").Range(Constants.START_DATE_RND & toLine).value = cellArray(1)
    End If
    
    cellArray = decodeDateString(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COMPLETION_DATE & Constants.DOWNLOAD_DATA_ROW).value)
    If cellArray(0) <> "" Then
      ActiveWorkbook.Sheets("Locality").Range(Constants.COMPLETION_DATE & toLine).value = CDate(cellArray(0))
      ActiveWorkbook.Sheets("Locality").Range(Constants.COMPLETION_DATE_RND & toLine).value = cellArray(1)
    End If
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.LICENCE_AREA & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_LICENCE_AREA & Constants.DOWNLOAD_DATA_ROW).value
        
    ActiveWorkbook.Sheets("Locality").Range(Constants.DATUM_TYPE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DATUM_TYPE & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.DATUM_ELEVATION & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DATUM_ELEVATION & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.START_DEPTH & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_START_DEPTH & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.STOP_DEPTH & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_STOP_DEPTH & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.SAMPLE_ID & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_SAMPLE_ID & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.TOP_DEPTH & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_TOP_DEPTH & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Locality").Range(Constants.BOTTOM_DEPTH & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_BOTTOM_DEPTH & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.DRILL_TYPE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DRILL_TYPE & Constants.DOWNLOAD_DATA_ROW).value
    
    cellArray = decodeDateString(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COLL_DATE & Constants.DOWNLOAD_DATA_ROW).value)
    If cellArray(0) <> "" Then
      ActiveWorkbook.Sheets("Locality").Range(Constants.COLL_DATE & toLine).value = CDate(cellArray(0))
      ActiveWorkbook.Sheets("Locality").Range(Constants.COLL_DATE_RND & toLine).value = cellArray(1)
    End If
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.COLLECTOR & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COLL & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.STRAT_NAME & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_STRAT_NAME & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Locality").Range(Constants.FOSSIL_IN_PLACE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_FOSSIL_IN_PLACE & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Locality").Range(Constants.SENT_TO & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_SENT_TO & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.NOT_COLLECTED & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_NOT_COLLECTED & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.SIG & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_SIG & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_START & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_INF_STAGE_START & Constants.DOWNLOAD_DATA_ROW).value, "stageNameID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_START_MOD & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_INF_STAGE_START_MOD & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_STOP & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_INF_STAGE_STOP & Constants.DOWNLOAD_DATA_ROW).value, "stageNameID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_STOP_MOD & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_INF_STAGE_STOP_MOD & Constants.DOWNLOAD_DATA_ROW).value
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_START & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_KNW_STAGE_START & Constants.DOWNLOAD_DATA_ROW).value, "stageNameID", -1)
   
    ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_START_MOD & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_KNW_STAGE_START_MOD & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_STOP & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_KNW_STAGE_STOP & Constants.DOWNLOAD_DATA_ROW).value, "stageNameID", -1)
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_STOP_MOD & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_KNW_STAGE_STOP_MOD & Constants.DOWNLOAD_DATA_ROW).value

    ActiveWorkbook.Sheets("Locality").Range(Constants.PREV_SAMPLE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_PREV_SAMPLE & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.SAMP_REL & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_SAMP_REL & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.STRAT_REL & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_STRAT_REL & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.COLUMN_MAP & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COLUMN_MAP & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.DIP & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DIP & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.DIP_DIRECTION & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DIP_DIRECTION & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.STRIKE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_STRIKE & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.FACING & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_FACING & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.STRAT_COMMENTS & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_STRAT_COMMENTS & Constants.DOWNLOAD_DATA_ROW).value
          
    ActiveWorkbook.Sheets("Locality").Range(Constants.GRAINSIZE_P & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_GRAINSIZE_P & Constants.DOWNLOAD_DATA_ROW).value, "grainSizeID", -1)
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.GRAINSIZE_S & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_GRAINSIZE_S & Constants.DOWNLOAD_DATA_ROW).value, "grainSizeID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.COMPARATOR & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COMPARATOR & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.THICKNESS & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_THICKNESS & Constants.DOWNLOAD_DATA_ROW).value, "thicknessID", -1)
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.BEDDING_P & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_BEDDING_P & Constants.DOWNLOAD_DATA_ROW).value, "beddingID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.BEDDING_S & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_BEDDING_S & Constants.DOWNLOAD_DATA_ROW).value, "beddingID", -1)
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.WEATHERING & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_WEATHERING & Constants.DOWNLOAD_DATA_ROW).value, "weatheringID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.HARDNESS & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_HARDNESS & Constants.DOWNLOAD_DATA_ROW).value, "hardnessID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.CARBONATE & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_CARBONATE & Constants.DOWNLOAD_DATA_ROW).value, "carbonateID", -1)
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.COLOUR_MOD & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COLOUR_MOD & Constants.DOWNLOAD_DATA_ROW).value, "colourModID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.COLOUR_P & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COLOUR_P & Constants.DOWNLOAD_DATA_ROW).value, "colourID", -1)

    ActiveWorkbook.Sheets("Locality").Range(Constants.COLOUR_S & toLine).value = _
        decodeCode(ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_COLOUR_S & Constants.DOWNLOAD_DATA_ROW).value, "colourID", -1)
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.WET_DRY & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_WET_DRY & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.SED_FEATURE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_SED_FEATURE & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.DEP_ENVIRONMENT1 & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DEP_ENVIRONMENT1 & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.DEP_ENVIRONMENT2 & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_DEP_ENVIRONMENT2 & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.ROCK_NATURE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_ROCK_NATURE & Constants.DOWNLOAD_DATA_ROW).value
    
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.CORRESPONDENCE & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_CORRESPONDENCE & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.CONFIDENTIAL & toLine).value = _
        ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_CONFIDENTIAL & Constants.DOWNLOAD_DATA_ROW).value
    
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SYSTEM_MESSAGE & toLine).value = "Synced OK"

    ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_USER_DATA_START & toLine & ":" & Constants.LOC_DATA_STOP & toLine).Font.Color = RGB(100, 100, 255)
    
    oldRefreshFlag = refreshFlag
    refreshFlag = True
    If ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_STATUS & Constants.DOWNLOAD_DATA_ROW).value = "rejected" Then
      ActiveWorkbook.Sheets("Locality").Range(Constants.status & toLine).value = _
          "rejected" & Chr(10) & "Curator Comments: " & _
          ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_CUR_COMMENTS & Constants.DOWNLOAD_DATA_ROW).value
    Else
      ActiveWorkbook.Sheets("Locality").Range(Constants.status & toLine).value = _
          ActiveWorkbook.Sheets("OracleData").Range(Constants.DOWNLOAD_STATUS & Constants.DOWNLOAD_DATA_ROW).value
    End If
    ActiveWorkbook.Sheets("Locality").Range(Constants.status & toLine).Font.Color = getStatusColour(ActiveWorkbook.Sheets("Locality").Range(Constants.status & toLine))
    refreshFlag = oldRefreshFlag
    
  End If

  Application.ScreenUpdating = True
  
  End If
  
End Function

Private Function constructSaveSubmitURL(button As String) As Boolean
  Dim locDoc As Variant
  Dim userSel As Range
  Dim query As String
  Dim folderName As String
  Dim folderId As String
  Dim docType As String
  Dim currentLine As Integer
  Dim localityType As String
  Dim featureName As String
  Dim datum As String
  Dim countryCode As String
  Dim mapSheet As String
  Dim east As String
  Dim north As String
  Dim status As String
  Dim dte As String
  Dim rnd As String
  Dim st_group As String
  Dim st_person As String
  Dim st_lab As String
  Dim st_comments As String
  Dim sr_dist As String
  Dim sr_rel As String
  Dim sr_samp As String
  Dim sr_unit As String
  
  errFlag = False
  
  Application.ScreenUpdating = False
  currentLine = startLine
  'save current user selection before making selections in code
  Set userSel = sel
  
  Do While True

    If InStr(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value, ":") > 0 Then
      locDoc = Split(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_TYPE & currentLine).value, ":")
      docType = locDoc(0)
      localityType = Trim(locDoc(1))
    Else
      localityType = ""
      docType = ""
    End If
    If localityType = "" Or docType = "" Then Exit Do
    
    'check row has been selected by user
    If userSel Is Nothing Then
      MsgBox "Please select a row"
      currentLine = currentLine + 1
      errFlag = True
      Exit Do
    ElseIf Not Intersect(ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_DATA_START & currentLine & ":" & Constants.LOC_DATA_STOP & currentLine), userSel) Is Nothing Then
        
      If ActiveWorkbook.Sheets("Locality").Range(Constants.folder & currentLine).value <> "" Then
        folderId = decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.folder & currentLine).value, "folderList", 1)
      Else
        folderName = ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_FOLDER_LIST).value
        If folderName <> "" Then
          folderId = decodeCode(folderName, "folderList", 1)
          If folderId = -1 Then
            MsgBox "No folders defined.  Please refresh folder list", vbOKOnly + vbExclamation
            Exit Function
          End If
          ActiveWorkbook.Sheets("Locality").Range(Constants.folder & currentLine).value = folderName
        End If
      End If
    
      If folderId = "" Then
        MsgBox ("Please select a folder")
        Exit Do
      End If
    
      featureName = ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_NAME & currentLine).value
      ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_USER_DATA_START & currentLine & ":" & Constants.LOC_DATA_STOP & currentLine).Font.Color = RGB(0, 0, 0)
      Application.StatusBar = "Processing record " & (currentLine - startLine + 1) & ": " & featureName
    
      query = "button=" & button
      query = query & "&FoldID=" & folderId
      
      If docType = "Locality" Then
        query = query & "&FeatID=" & ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & currentLine).value
        query = query & "&Type=" & localityType
        query = query & "&FeatName=" & encodeURL(featureName)
        If ActiveWorkbook.Sheets("Locality").Range(Constants.FR_NUMBER & currentLine).value <> "" Then _
          query = query & "&FRNumber=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.FR_NUMBER & currentLine).value)
        If ActiveWorkbook.Sheets("Locality").Range(Constants.YARD_FR_NUMBER & currentLine).value <> "" Then _
          query = query & "&YardFRNumber=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.YARD_FR_NUMBER & currentLine).value)
        If ActiveWorkbook.Sheets("Locality").Range(Constants.REG_AREA & currentLine).value = "" Then ActiveWorkbook.Sheets("Locality").Range(Constants.REG_AREA & currentLine).value = "Mainland NZ"
        query = query & "&RegAreaId=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.REG_AREA & currentLine).value, "regArea", 1))
        query = query & "&Recoll=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.RECOLLECTION & currentLine).value)
        query = query & "&S111/f2WorkComm=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.WORKING_COMMENTS & currentLine).value)
        query = query & "&CoordType=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.datum & currentLine).value, "datum", 1))
        query = query & "&MapSheet=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.MAP_SHEET & currentLine).value)
        query = query & "&East=" & encodeURL(generateCoord(ActiveWorkbook.Sheets("Locality").Range(Constants.easting & currentLine).value, ActiveWorkbook.Sheets("Locality").Range(Constants.datum & currentLine).value))
        query = query & "&North=" & encodeURL(generateCoord(ActiveWorkbook.Sheets("Locality").Range(Constants.NORTHING & currentLine).value, ActiveWorkbook.Sheets("Locality").Range(Constants.datum & currentLine).value))
        query = query & "&MapYear=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.MAP_YEAR & currentLine).value)
        query = query & "&LocMethodID=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCATION_METHOD & currentLine).value, "locMethod", 1))
        query = query & "&Accuracy=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.ACCURACY & currentLine).value)
        query = query & "&Loc=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.LOCALITY_DESC & currentLine).value)
        query = query & "&Country=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.COUNTRY & currentLine), "country", 1))
        query = query & "&Person=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.OP_COMPANY & currentLine).value)
        dte = ActiveWorkbook.Sheets("Locality").Range(Constants.START_DATE & currentLine).value
        rnd = ActiveWorkbook.Sheets("Locality").Range(Constants.START_DATE_RND & currentLine).value
        query = query & "&StartDate=" & encodeURL(utils.formatDate(dte, rnd))
        dte = ActiveWorkbook.Sheets("Locality").Range(Constants.COMPLETION_DATE & currentLine).value
        rnd = ActiveWorkbook.Sheets("Locality").Range(Constants.COMPLETION_DATE_RND & currentLine).value
        query = query & "&FinishDate=" & encodeURL(utils.formatDate(dte, rnd))
        query = query & "&LicArea=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.LICENCE_AREA & currentLine).value)
        query = query & "&DatumType=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.DATUM_TYPE & currentLine).value)
        query = query & "&DatumEl=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.DATUM_ELEVATION & currentLine).value)
        query = query & "&StartDepth=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.START_DEPTH & currentLine).value)
        query = query & "&FinishDepth=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.STOP_DEPTH & currentLine).value)
      ElseIf docType = "Sample" Then
        query = query & "&Type=Sample"
        query = query & "&WorkComm=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.WORKING_COMMENTS & currentLine).value)
        query = query & "&featName=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_NAME & currentLine).value)
        query = query & "&SampID=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.SAMPLE_ID & currentLine).value)
        query = query & "&TopDepth=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.TOP_DEPTH & currentLine).value)
        query = query & "&BottomDepth=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.BOTTOM_DEPTH & currentLine).value)
        query = query & "&DrillType=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.DRILL_TYPE & currentLine).value, "drillType", 1))
      End If
      
      dte = ActiveWorkbook.Sheets("Locality").Range(Constants.COLL_DATE & currentLine).value
      rnd = ActiveWorkbook.Sheets("Locality").Range(Constants.COLL_DATE_RND & currentLine).value
      query = query & "&CollDate=" & encodeURL(utils.formatDate(dte, rnd))
      query = query & "&Coll=" & Replace(encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.COLLECTOR & currentLine).value), "%23", "%0A")
      query = query & "&StratName=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.STRAT_NAME & currentLine).value)
      query = query & "&InPlace=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.FOSSIL_IN_PLACE & currentLine).value)
      query = query & "&SentTo=" & Replace(encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.SENT_TO & currentLine).value), "%23", "%0A")
      query = query & "&NotColl=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.NOT_COLLECTED & currentLine).value)
      query = query & "&Sig=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.SIG & currentLine).value)
      query = query & "&InfStageStart=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_START & currentLine).value, "stageName", 1))
      query = query & "&InfStartMod=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_START_MOD & currentLine).value)
      query = query & "&InfStageStop=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_STOP & currentLine).value, "stageName", 1))
      query = query & "&InfStopMod=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.INF_STAGE_STOP_MOD & currentLine).value)
      query = query & "&KnwStageStart=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_START & currentLine).value, "stageName", 1))
      query = query & "&KnwStartMod=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_START_MOD & currentLine).value)
      query = query & "&KnwStageStop=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_STOP & currentLine).value, "stageName", 1))
      query = query & "&KnwStopMod=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.KNW_STAGE_STOP_MOD & currentLine).value)
      query = query & "&PrevSamp=" & Replace(encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.PREV_SAMPLE & currentLine).value), "%23", ";")
      query = query & "&SampRel=" & Replace(encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.SAMP_REL & currentLine).value), "%23", "%0A")
      query = query & "&StratRel=" & Replace(encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.STRAT_REL & currentLine).value), "%23", "%0A")
      query = query & "&ColMap=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.COLUMN_MAP & currentLine).value)
      query = query & "&Dip=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.DIP & currentLine).value)
      query = query & "&DipDir=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.DIP_DIRECTION & currentLine).value)
      query = query & "&Strike=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.STRIKE & currentLine).value)
      query = query & "&Facing=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.FACING & currentLine).value)
      query = query & "&StratComm=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.STRAT_COMMENTS & currentLine).value)
      query = query & "&GrainSizeP=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.GRAINSIZE_P & currentLine).value, "grainSize", 1))
      query = query & "&GrainSizeS=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.GRAINSIZE_S & currentLine).value, "grainSize", 1))
      query = query & "&GSComp=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.COMPARATOR & currentLine).value)
      query = query & "&BedThick=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.THICKNESS & currentLine).value, "thickness", 1))
      query = query & "&BeddingP=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.BEDDING_P & currentLine).value, "bedding", 1))
      query = query & "&BeddingS=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.BEDDING_S & currentLine).value, "bedding", 1))
      query = query & "&Weath=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.WEATHERING & currentLine).value, "weathering", 1))
      query = query & "&Hard=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.HARDNESS & currentLine).value, "hardness", 1))
      query = query & "&Carb=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.CARBONATE & currentLine).value, "carbonate", 1))
      query = query & "&ColMod=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.COLOUR_MOD & currentLine).value, "colourMod", 1))
      query = query & "&ColourP=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.COLOUR_P & currentLine).value, "colour", 1))
      query = query & "&ColourS=" & encodeURL(decodeCode(ActiveWorkbook.Sheets("Locality").Range(Constants.COLOUR_S & currentLine).value, "colour", 1))
      query = query & "&Wet=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.WET_DRY & currentLine).value)
      query = query & "&SedFeat=" & Replace(encodeURL(Replace(ActiveWorkbook.Sheets("Locality").Range(Constants.SED_FEATURE & currentLine).value, "$", Chr(42))), "%23", ";")
      query = query & "&DepEnv1=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.DEP_ENVIRONMENT1 & currentLine).value)
      query = query & "&DepEnv2=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.DEP_ENVIRONMENT2 & currentLine).value)
      query = query & "&RockNat=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.ROCK_NATURE & currentLine).value)
      query = query & "&Corr=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.CORRESPONDENCE & currentLine).value)
      query = query & "&Confid=" & encodeURL(ActiveWorkbook.Sheets("Locality").Range(Constants.CONFIDENTIAL & currentLine).value)
    
      'MsgBox query
        
      If Not sendSaveSubmitURL(docType, query, currentLine, folderId) Then
        errFlag = True
        currentLine = currentLine + 1
        Exit Do
      End If
        
    End If
    currentLine = currentLine + 1
  Loop
    
  Application.StatusBar = False
  Application.ScreenUpdating = True
  
  If currentLine = startLine Then
    MsgBox "No data found in spreadsheet.  Data must begin on row " & startLine, vbExclamation
  ElseIf Not errFlag And button = "submit" Then
    MsgBox "All rows successfully submitted", vbInformation
  ElseIf Not errFlag And button = "save" Then
    MsgBox "All rows successfully saved", vbInformation
  End If
    
End Function

Private Function sendSaveSubmitURL(docType As String, queryString As String, line As Integer, folderId As String) As Boolean
  Dim status As String
  Dim msg As String

  sendSecureImport (queryString)
   
  Application.GoTo reference:="status"
  status = Range("status").value
  Application.GoTo reference:="message"
  msg = Range("message").value
  Application.GoTo reference:="sessionId"
  utils.sessionId = Range("sessionId").value
  
  If status = "Saved OK" Or status = "Submitted OK" Then
    If docType = "Locality" Then
      ActiveWorkbook.Sheets("Locality").Range(Constants.FEATURE_ID & line).value = msg
    ElseIf docType = "Sample" Then
      ActiveWorkbook.Sheets("Locality").Range(Constants.SAMPLE_ID & line).value = msg
    End If
    getSavedQueryData msg, folderId, docType, line, False
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
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SYSTEM_MESSAGE & line).value = "Login Error: " & msg
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_USER_DATA_START & line & ":" & Constants.LOC_DATA_STOP & line).Font.Color = RGB(255, 0, 0)
    Application.StatusBar = False
    sendSaveSubmitURL = False
  Else
    MsgBox "Data error in record " & (line - startLine + 1) & ": " & msg
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SYSTEM_MESSAGE & line).value = "Error:"
    'ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_SYSTEM_MESSAGE & line).value = queryString
    ActiveWorkbook.Sheets("Locality").Range(Constants.LOC_USER_DATA_START & line & ":" & Constants.LOC_DATA_STOP & line).Font.Color = RGB(255, 0, 0)
    Application.StatusBar = False
    sendSaveSubmitURL = False
  End If

End Function

'Functions for encoding URLs

Private Function formatDate(dateText As String) As String
  Dim d As Date
  
  If dateText <> "" Then
    If IsDate(dateText) Then
      d = CDate(dateText)
      formatDate = Day(d) & "/" & Month(d) & "/" & Year(d)
    Else
      formatDate = "bad date"
    End If
  Else
    formatDate = ""
  End If
  
End Function

Private Function generateCoord(coord As String, datum As String) As String

  If InStr(datum, "4-digit") > 0 Then
    generateCoord = generateFixedLengthCoord(coord, 4)
  ElseIf InStr(datum, "3-digit") > 0 Then
    generateCoord = generateFixedLengthCoord(coord, 3)
  Else
    generateCoord = coord
  End If
  
End Function

Private Function generateFixedLengthCoord(coord As String, length As Integer) As String

  If InStr(coord, ".") > 0 Then
    If IsNumeric(coord) Then
      coord = CStr(CInt(coord))
    Else
      coord = Left(coord, InStr(coord, ".") - 1)
    End If
  End If
  
  Do Until Len(coord) >= length
    coord = "0" & coord
  Loop
  generateFixedLengthCoord = coord

End Function

Private Function decodeDatum(datum As String, mapSheet As String, easting As String) As String

  If mapSheet = "" Or IsNull(mapSheet) Or Len(Replace(easting, "#", "")) = 3 Then
    decodeDatum = datum
  Else
    decodeDatum = Replace(datum, "3-digit", "4-digit")
  End If

End Function
