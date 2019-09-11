'
' 老版本兼容。实际的代码为GenKey.vs
'

Sub FindSpecProviders(theForm, CryptoDevice)

	FindProviders(theForm.IeTokenList)
	
	Dim count
	Dim n
	Dim cspName

	' 去除不常用的CSP Provider	
	count = theForm.IeTokenList.options.length
	For n = count To 1 Step -1
		cspName = theForm.IeTokenList.options.item(n-1).text
		If isRunningOnVista Then
			If InStr(cspName, "DH") > 0 Or InStr(cspName, "CopLock") > 0  Or InStr(cspName, "DSS") > 0 Or InStr(cspName, "Smart Card") > 0 Or InStr(cspName, "AES") > 0 Then
				theForm.IeTokenList.options.remove(n-1)
			End If
		Else
			If InStr(cspName, "Gemplus") > 0 Or InStr(cspName, "Infineon") > 0 Or InStr(cspName, "Schlumberger") > 0 Then
				theForm.IeTokenList.options.remove(n-1)
			End If
		End If
	Next
End Sub