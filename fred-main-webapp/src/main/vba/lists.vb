Option Explicit

Public Sub refreshOracleLists()
  Dim list(21) As String
  Dim i As Integer
  
  list(0) = "regArea"
  list(1) = "country"
  list(2) = "datum"
  list(3) = "locMethod"
  list(4) = "drillType"
  list(5) = "person"
  list(6) = "fossilGroup"
  list(7) = "lab"
  list(8) = "stratName"
  list(9) = "stageName"
  list(10) = "grainSize"
  list(11) = "thickness"
  list(12) = "bedding"
  list(13) = "weathering"
  list(14) = "hardness"
  list(15) = "carbonate"
  list(16) = "colourMod"
  list(17) = "colour"
  list(18) = "sedFeature"
  list(19) = "labSection"
  list(20) = "taxaGroup"
  list(21) = "confidGroup"

  For i = 0 To 21
    With ActiveWorkbook.Sheets("Lists").QueryTables("ext_" & list(i))
      .PostText = "listName=" & list(i)
      .Connection = "URL;" & baseURL & "list.jsp"
      .refresh False
    End With
  Next
  
End Sub