Function Scan(scanip, scanports)
    On Error Resume Next
    dim conn,result,ports
    ports = Split(scanports,",")
    For i=0 To Ubound(ports)
        If Isnumeric(ports(i)) Then
            set conn=Server.CreateObject("ADODB.connection")
            connstr="Provider=SQLOLEDB.1;Data Source="&scanip&","&ports(i)&";User ID=a;Password=a;"
            conn.ConnectionTimeout=1
            conn.open connstr
            If Err Then
                If Err.number=-2147217843 or Err.number=-2147467259 Then
                    If InStr(Err.description, "(Connect()).") > 0 Then
                        result=result&scanip&chr(9)&ports(i)&chr(9)&"0"&chr(10)
                    Else
                        result=result&scanip&chr(9)&ports(i)&chr(9)&"1"&chr(10)
                    End If
                End If
                Err.Clear
            End If
        End If
    Next
    Scan=result
End Function
GlobalResult=Scan(getParameterValue("ip"),getParameterValue("ports"))