Option Explicit

Private Sub Workbook_BeforeClose(Cancel As Boolean)

  utils.syncBeforeClose

End Sub

Private Sub Workbook_Open()

  On Error GoTo Error
  
  Sheets("Start Here").Range("A7").value = "Version: " & utils.getTemplateVersion() & " (" & utils.getTemplateBuildDate() & ")"
  Sheets("Lists").Range("A3").value = Constants.baseURL

  utils.noInternet = False
  utils.syncBeforeOpen
  
  If utils.noInternet = False Then
    refreshSplash.Show False
    refreshSplash.Repaint
    lists.refreshOracleLists
    refreshSplash.Hide
  End If
  
  ActiveWorkbook.Sheets("Locality").Activate
  
  Exit Sub
  
Error:
  refreshSplash.Hide
  MsgBox Err.Description & " - unable to connect to the FRED database server to retrieve updated data.  This is probably because you are not currently connected to the internet. You can still enter data into the spreadsheet, but will be unable to save to the database." & Err.Description _
  , vbInformation + vbOKOnly

End Sub
