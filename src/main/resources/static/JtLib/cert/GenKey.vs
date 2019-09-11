'
' Microsoft CSP Provider Helper With Enroll Ctrl
'

' --------------------------------------------------
' 查找CSP提供者，查找的结果填充到theSelectCtrl对象
' 
' theSelectCtrl:	Html SELECT Element
' 返回值:			无
' --------------------------------------------------
	Sub FindProviders(theSelectCtrl)
		If isRunningOnVista  Then
			FindProvidersOnVista(theSelectCtrl)
    	Else
       		FindProvidersOnNonVista(theSelectCtrl)
    	End If
	End Sub

' --------------------------------------------------
' 查找CSP提供者，查找的结果填充到theSelectCtrl对象
' 说明: 			Non Vista系统调用方法
' theSelectCtrl:	Html SELECT Element
' 返回值:			无
' --------------------------------------------------
	Sub FindProvidersOnNonVista(theSelectCtrl)

		Dim i, j, count
		Dim providers()
		i = 0
		j = 1
		count=0
		
		Dim el
		Dim temp
		Dim first 
		
		On Error Resume Next
	
		first =  0
		FixAcceptButton()
	
		Do While True
	  		temp = ""
	  		cenroll.providerType = j
	  		temp = cenroll.enumProviders(i,0)
	  		If Len(Temp) = 0 Then
	    		If j < 1 Then          'Look for RSA_FULL only.
	      			j = j + 1
	      			i = 0 
	    		Else
	      			Exit Do
	    		End If
	  		Else
	    		set el = document.createElement("OPTION")
	    		el.text = temp
	    		el.value = j 
	    		theSelectCtrl.add(el)
				count = count + 1
				
				If el.text = "Microsoft Enhanced Cryptographic Provider v1.0" Then
	  				theSelectCtrl.selectedIndex = count
				End If
				
	    		If first = 0  Then
	      			first = 1
	      			theSelectCtrl.selectedIndex = 0
	    		End If
	    		
	    		i = i + 1
	  		End If
		Loop
	End Sub
  
 
' --------------------------------------------------
' 查找CSP提供者，查找的结果填充到theSelectCtrl对象
' 说明: 			Vista系统调用方法
' theSelectCtrl:	Html SELECT Element
' 返回值:			无
' --------------------------------------------------
	Sub FindProvidersOnVista(theSelectCtrl)
		Dim cspInfos
		Dim cspCount
		On Error Resume Next
	
		Set cspInfos = CreateObject("X509Enrollment.CCspInformations")
	
		If ( err.number <> 0 ) then
			MsgBox Err.Number & " " & Err.Description
			Exit Sub
		End If

		On Error Resume Next
		cspInfos.AddAvailableCsps()
		If ( err.number <> 0 ) then
			MsgBox Err.Number & " " & Err.Description
			Exit Sub
		End If

		cspCount = cspInfos.Count
		Dim i ,j
		Dim el
		Dim first
		Dim temp
		i = 1
		j = 1
		first = 0

		Do While True
			If i = cspCount Then
				Exit Do
			End If
			set cspInfo = cspInfos.ItemByIndex(i)
			If Len( cspInfo.Name) = 0 Then
				If j < 1 Then 
					j = j + 1
					i = 0
				Else
					Exit Sub
				End If
			Else
				set el = document.createElement("OPTION")
				el.text = cspInfo.Name
				el.value = j
				theSelectCtrl.add(el)
				If first = 0  Then
					first = 1
					enrlform.selectedIndex = 0
				End If
			End If
			i = i + 1
		Loop
	End Sub

' ----------------------------------------------------------------------------
' 根据指定的CSP Provider名称、类型及密钥生成标识，生成PEM编码PKCS#10证书请求
' 
' cspName:			CSP Provider名称，该值为FindProviders填充的SELECT.options.text
' cspType:			CSP Provider类型，该值为FindProviders填充的SELECT.options.type
' genKeyFlags:		密钥生成标识。
'						enroll控件支持以下参数:
'							1		生成的密钥可以导出
'							2		生成的密钥不可以导出
'						CertEnroll控件支持以下参数:
'                   		0       Export is not allowed.
'							1		The private key can be exported
'							2		The private key can be exported in plaintext form
'							4		The private key can be exported once for archiving
'                   		8       The private key can be exported in plaintext form once for archiving
' keySpec:			密钥容器位置
'						<XP>	AT_SIGNATURE(2)	签名容器
'								AT_KEYEXCHANGE(1) 加密容器
'						<WIN7>	XCN_AT_SIGNATURE(2)	签名容器
'								XCN_AT_KEYEXCHANGE(1) 加密容器
'								XCN_AT_NONE(0) 未指定，适用于KSP Provider。
' containerName: 	容器名称
'
' 返回值:			""							生成失败
'					PEM编码的PKCS#10格式请求	生成成功
' ----------------------------------------------------------------------------
	
	' zhzeng 2010/06/03 增加容器名称参数。
	Function GenPKCS10RequestByEnroll(cspName, cspType, genKeyFlags, keySpec, containerName)
		If isRunningOnVista  Then
			GenPKCS10RequestByEnroll= GenPKCS10RequestByEnrollOnVista(cspName, cspType, genKeyFlags, keySpec, containerName, "C=CN,CN=TestUser")
    	Else
			GenPKCS10RequestByEnroll= GenPKCS10RequestByEnrollOnNonVista(cspName, cspType, genKeyFlags, keySpec, containerName, "C=CN,CN=TestUser")
    	End If
	End Function

	Function GenPKCS10RequestByEnrollEx(cspName, cspType, genKeyFlags, keySpec, containerName, subjectDN)
		If isRunningOnVista  Then
			GenPKCS10RequestByEnrollEx= GenPKCS10RequestByEnrollOnVista(cspName, cspType, genKeyFlags, keySpec, containerName, subjectDN)
    	Else
			GenPKCS10RequestByEnrollEx= GenPKCS10RequestByEnrollOnNonVista(cspName, cspType, genKeyFlags, keySpec, containerName, subjectDN)
    	End If
	End Function

' ----------------------------------------------------------------------------
' 根据指定的CSP Provider名称、类型及密钥生成标识，生成PEM编码PKCS#10证书请求
' 非Vista系统调用
' cspName:			CSP Provider名称，该值为FindProviders填充的SELECT.options.text
' cspType:			CSP Provider类型，该值为FindProviders填充的SELECT.options.type
' genKeyFlags:		密钥生成标识。
'					1		生成的密钥可以导出
'					2		生成的密钥不可以导出
' keySpec:			密钥容器位置
'						<XP>	AT_SIGNATURE(2)	签名容器
'								AT_KEYEXCHANGE(1) 加密容器
'						<WIN7>	XCN_AT_SIGNATURE(2)	签名容器
'								XCN_AT_KEYEXCHANGE(1) 加密容器
'								XCN_AT_NONE(0) 未指定，适用于KSP Provider。
' containerName: 	容器名称
' subjectDN:		请求主题
'
' 返回值:			""							生成失败
'					PEM编码的PKCS#10格式请求	生成成功
' ----------------------------------------------------------------------------

	Function GenPKCS10RequestByEnrollOnNonVista(cspName, cspType, genKeyFlags, keySpec, containerName, subjectDN)
	
		Dim pkcs10req
		Dim DNItem
		Dim keyLength
		
		DNItem 		= subjectDN
		keyLength	= &H00000000
		
		IF (InStr(cspName, "Microsoft Strong") > 0) Then
			keyLength = &H08000000
			MsgBox "将生成2048位密钥对，可能需要比较长的时间。点击确定后继续 ... ", vbOKOnly, "警告"
		End If
		
		On Error Resume Next
		cenroll.providerName 	= cspName
		cenroll.providerType 	= cspType
		cenroll.HashAlgorithm 	= "MD5"
		cenroll.KeySpec 		= keySpec
		cenroll.GenKeyFlags 	= genKeyFlags or keyLength
		
		IF (containerName = Empty) Then
			cenroll.UseExistingKeySet = False
		Else
			cenroll.ContainerName = containerName			
		End If
		
		pkcs10req = cenroll.CreatePKCS10(DNItem, "1.3.6.1.5.5.7.3.2")
		if (pkcs10req = Empty) Then
			If Err.Number = &H8009000F Then
				cenroll.UseExistingKeySet = True
				pkcs10req = cenroll.CreatePKCS10(DNItem, "1.3.6.1.5.5.7.3.2")
			End If
		End If
		
		if (pkcs10req = Empty) Then
			GenPKCS10RequestByEnrollOnNonVista = ""
			errorString = "产生请求时出现错误，错误代码为: '0x" & hex(Err.Number) & "'"
			err = MsgBox(errorString, 0, "请求生成失败")
			on error goto 0
			exit function
		else 
			GenPKCS10RequestByEnrollOnNonVista = pkcs10req
			exit function
		end if
		
	End Function

' ----------------------------------------------------------------------------
' 根据指定的CSP Provider名称、类型及密钥生成标识，生成PEM编码PKCS#10证书请求
' Vista系统调用
' cspName:			CSP Provider名称，该值为FindProviders填充的SELECT.options.text
' cspType:			CSP Provider类型，该值为FindProviders填充的SELECT.options.type
' genKeyFlags:		密钥生成标识。
'                   0       Export is not allowed.
'					1		The private key can be exported
'					2		The private key can be exported in plaintext form
'					4		The private key can be exported once for archiving
'                   8       The private key can be exported in plaintext form once for archiving
' keySpec:			密钥容器位置
'						<XP>	AT_SIGNATURE(2)	签名容器
'								AT_KEYEXCHANGE(1) 加密容器
'						<WIN7>	XCN_AT_SIGNATURE(2)	签名容器
'								XCN_AT_KEYEXCHANGE(1) 加密容器
'								XCN_AT_NONE(0) 未指定，适用于KSP Provider。
' containerName: 	容器名称
' subjectDN:		请求主题
'
' 返回值:			""							生成失败
'					PEM编码的PKCS#10格式请求	生成成功
' ----------------------------------------------------------------------------

	Function GenPKCS10RequestByEnrollOnVista(cspName, cspType, genKeyFlags, keySpec, containerName, subjectDN)
		
		On Error Resume Next
		err.clear
		
		Dim pkcs10req
		Dim keyLength
		Dim g_objClassFactory
		Dim obj
		Dim objPrivateKey
		Dim g_objRequest
		Dim g_objRequestCMC
	
		Set g_objClassFactory		= CreateObject("X509Enrollment.CX509EnrollmentWebClassFactory")
		Set obj						= g_objClassFactory.CreateObject("X509Enrollment.CX509Enrollment")
		Set objPrivateKey			= g_objClassFactory.CreateObject("X509Enrollment.CX509PrivateKey")
		Set objRequest				= g_objClassFactory.CreateObject("X509Enrollment.CX509CertificateRequestPkcs10")
		Set objSubjectDN			= g_objClassFactory.CreateObject("X509Enrollment.CX500DistinguishedName")
		
		keyLength = 1024
		IF (InStr(cspName, "Microsoft Strong") > 0) Then
			keyLength = 2048
			MsgBox "将生成2048位密钥对，可能需要比较长的时间。点击确定后继续 ... ", vbOKOnly, "警告"
		End If
	
		objPrivateKey.ProviderName 	= cspName
		If err.Number <> 0 Then
			msgbox "无法产生证书签发请求，可能是没有插入正确的介质设备。(" + err.Description + ")"
			GenPKCS10RequestByEnrollOnVista = ""
			exit function
		End If
				
		objPrivateKey.ProviderType 	= cspType
		' zhzeng 2010/06/03 bugfix: KeySpec为容器位置 c {{
		' objPrivateKey.KeySpec 	= genKeyFlags
		objPrivateKey.KeySpec 		= keySpec
		' }}
		objPrivateKey.ExportPolicy 	= genKeyFlags
		objPrivateKey.Length		= keyLength
		objPrivateKey.ContainerName = containerName
	
		objSubjectDN.Encode subjectDN, 0
		
		' 1 ContextUser 2 ContextMachine 3 ContextAdministratorForceMachine
		objRequest.InitializeFromPrivateKey 1, objPrivateKey, ""
		objRequest.Subject = objSubjectDN
		
		obj.InitializeFromRequest(objRequest)
		obj.CertificateDescription = "Description"
		pkcs10req = obj.CreateRequest(1)
		if(pkcs10req = Empty) Then
			GenPKCS10RequestByEnrollOnVista = ""
			errorString = "产生请求时出现错误，错误代码为: '0x" & hex(Err.Number) & "'"
			err = MsgBox(errorString, 0, "请求生成失败")
			on error goto 0
			exit function
		else 
			GenPKCS10RequestByEnrollOnVista = pkcs10req
			exit function
		end if
		
	End Function
   
 
' ------------------------------------------------------------
' 使用CSP安装PKCS#7证书内容
'
' pkcs7Value:		BASE64编码的PKCS7格式证书内容(可为PEM编码) 
' 返回结果:			0		安装成功
'					其它	安装失败，返回错误代码。
' ------------------------------------------------------------
	Function InstallPKCS7Cert(pkcs7Value)
		If isRunningOnVista  Then
			InstallPKCS7Cert = InstallPKCS7CertOnVista(pkcs7Value)
    	Else
       		InstallPKCS7Cert = InstallPKCS7CertOnNonVista(pkcs7Value)
    	End If	
	End Function

' ------------------------------------------------------------
' 使用CSP安装PKCS#7证书内容，非Vista系统调用
' pkcs7Value:		BASE64编码的PKCS7格式证书内容(可为PEM编码) 
' 返回结果:			0		安装成功
'					其它	安装失败，返回错误代码。
' ------------------------------------------------------------
	Function InstallPKCS7CertOnNonVista(pkcs7Value)
		On Error Resume Next
		err.clear
	
		Call cenroll.acceptPKCS7(pkcs7Value)
		If err.Number <> 0 Then
			msgbox err.Description
			InstallPKCS7CertOnNonVista = err.Number
		Else
			InstallPKCS7CertOnNonVista = 0
		End If
	
		cenroll.WriteCertToCSP = false
		WriteMessage	
	End Function


' ------------------------------------------------------------
' vista 系统上使用CSP安装PKCS#7证书内容，Vstia以后系统调用
' pkcs7Value:		BASE64编码的PKCS7格式证书内容(可为PEM编码) 
' 返回结果:		0	安装成功
'			其它	安装失败，返回错误代码。
' ------------------------------------------------------------
	Function InstallPKCS7CertOnVista(pkcs7Value)
		On Error Resume Next
		err.clear
		
		Dim CertEnroll
		Set CertEnroll = CreateObject("X509Enrollment.CX509Enrollment")
		CertEnroll.Initialize(1)
		
		' zhzeng 2010/06/03 第三个参数1为XCN_CRYPT_STRING_BASE64，修改成XCN_CRYPT_STRING_BASE64_ANY(0x06) 
		'                   第一个参数0为AllowNone，修改为AllowUntrustedRoot(0x04) c {{
		If isRunningOnWin7  Then
			Call  CertEnroll.InstallResponse(4, pkcs7Value, 6, "")
    	Else
       		Call  CertEnroll.InstallResponse(0, pkcs7Value, 6, "")
    	End If	
		
		InstallPKCS7CertOnVista = 0
		If err.Number <> 0 Then
			msgbox err.Description
			InstallPKCS7CertOnVista = err.Number
		Else
			InstallPKCS7CertOnVista = 0
		End If
	
	End Function



' ------------------------------------------------------------
'
' 判断操作系统是否是Windows Vista 和IE7.0系统
'
' ------------------------------------------------------------
Function isRunningOnVista ()
 	Dim Info
	Dim b
	dim s
	Info = Navigator.appVersion 
	b = Browser(Info)
	s = System(Info) 
	If b >= 7.0 And s >= 6.0 Then
		isRunningOnVista = true
	Else
		isRunningOnVista = false
	End if 	
 End Function 
 
 ' ------------------------------------------------------------
'
' 判断操作系统是否是Windows7WithIE8或者Windows7WithIE9或者Windows8WithIE10系统
'
' ------------------------------------------------------------
Function isRunningOnWin7 ()
 	Dim Info
	Dim b
	dim s
	Info = Navigator.appVersion 
	b = Browser(Info)
	s = System(Info) 
	If b >= 8.0 And s >= 6.1 Then
		isRunningOnWin7 = true
	Else
		isRunningOnWin7 = false
	End if 	
 End Function 

' ------------------------------------------------------------
'
' 判断操作系统类型和浏览器类型
'
' ------------------------------------------------------------
	Function Browser(Info) 
		If InStr(Info, "MSIE 10.0") > 0 Then
			Browser = 9.1
		ElseIf InStr(Info, "MSIE 9.0") > 0 Then
			Browser = 9.0
		ElseIf InStr(Info, "MSIE 8.0") > 0 Then
			Browser = 8.0
		ElseIf InStr(Info, "MSIE 7.0") > 0 Then 
			Browser = 7.0
		ElseIf InStr(Info, "MSIE 6") > 0 Then 
			Browser = 6.0
		ElseIf InStr(Info, "MSIE 5.5") > 0 Then 
			Browser = 5.5
		ElseIf InStr(Info, "MSIE 5.0") > 0 Then 
			Browser = 5.0
		ElseIf InStr(Info, "MSIE 4") > 0 Then 
			Browser = 4.0
		Else 
			Browser = "-1" 
		End if 
	End Function 
	
	Function System(Info) 
		If InStr(Info, "NT 6.2") > 0 Then 'Windows 8
			System = 6.2
		ElseIf InStr(Info, "NT 6.1") > 0 Then 'Windows 7
			System = 6.1
		ElseIf InStr(Info, "NT 6.0") > 0 Then 'Windows Vista
			System = 6.0
		ElseIf InStr(Info, "NT 5.1") > 0 Then 'Windows XP
			System = 5.1
		ElseIf InStr(Info, "NT 5.0") > 0 Then 'Windows 2000
			System = 5.0
		Else 
			System = "-1" 
		End if 	
	End Function
	
' ------------------------------------------------------------
' END
' ------------------------------------------------------------
