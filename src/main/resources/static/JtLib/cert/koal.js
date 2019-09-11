/**
 *
 * 格尔RA SCRIPT脚本
 *
 */


 function IsCspWrapperInstall()
    {
        if(typeof(oCspWrapper) == "object")
        {
            if( (oCspWrapper.object != null) )
            {
                // We found CAPICOM!
                return true;
            }
        }
    }
 
 
 function IsFileRWInstall()
 {
     if(typeof(oKit) == "object")
     {
         if( (oKit.object != null) )
         {
             // We found CAPICOM!
             return true;
         }
     }
 }
 
 function IsCapicaomInstall()
 {
	 if(typeof(oCAPICOM) == "object")
     {
         if( (oCAPICOM.object != null) )
         {
             // We found CAPICOM!
             return true;
         }
     }
 }


function showError(e)
{
	if (e.showed == null) {
		alert(e.message);
		e.showed = true;
	}
}


function setVisible(id, show)
{
	var style = document.getElementById(id).style;
	if (show)
		style.display = "";
	else
		style.display = "none";
}



function getObj(name) {
	var obj = document.getElementsByName(name).item(0);
	if (obj == null) {
		alert("当前页面不存在名称为'" + name + "'的对象!");
		return null;
	}
	
	return obj;
}



function getObjWithoutAlter(name) {
	var obj = document.getElementsByName(name).item(0);
	return obj;
}

function ShowDiv(Object,bShow)
{
	if (bShow)
	{
		Object.style.visibility="visible";
		Object.style.position="";
	}
	else
	{			
		Object.style.visibility="hidden";
		Object.style.position="absolute";		
	}
}



function genCertReq(){
	
	var selectDevice = document.getElementById("certDevice");
	var cspname = selectDevice.options[selectDevice.selectedIndex].text; 
	var cspType = selectDevice.options[selectDevice.selectedIndex].value;
	var operationId = getByID("operationId").value;
	var deviceType = 0;
	var tokenIndex = 0;
	var userName = getByID("CERT_CN").value;

	var device = getByID("deviceType").value;
	
	
	getByID("cspName").value = cspname;
	getByID("cspType").value = cspname;
	
	var sigCert ="";
	var encCert =""
	if ("certrenew" == operationId || "certrecover" == operationId) {
		sigCert = getByID("signCert").value;
		encCert = getByID("encCert").value;
	}
		
//	
	var operationId = getByID("operationId").value;  
//	// 证书签发、更新、延期、恢复
	if ("certsign" != operationId 
			&& "certrenew" != operationId 
			&& "certpostpone" != operationId 
			&& "certrecover" != operationId
			&& "keyupdate" != operationId) {
		alert("无法生成证书请求，原因: 无效的证书操作类型 '" + operationId + "'");
		return false;
	}


//	
//  	// 格尔控件
	if (device == 0) {
		deviceType = 0;
  		tokenIndex = selectDevice.options[selectDevice.selectedIndex].value;
	} else {
		deviceType = 1;		
		if ("certrenew" == operationId) {
			if (encCert != null && encCert.length > 0) {
				if (cspname.indexOf("Microsoft") >= 0) {
					alert("双证书的主题更新不支持微软CSP设备(" + cspname + ")");
					return false;
				}
			}
		}
		
		if ("certpostpone" == operationId) {
			if (cspname.indexOf("Microsoft") >= 0) {
				alert("证书延期不支持微软CSP设备(" + cspname + ")");
				return false;
			}
		}
	}
//	
//	// 证书延期不产生新的请求
	if ("certpostpone" == operationId && device == 0){
		 var tokenId = oCertX.GetSelTokenID(selectDevice.options[selectDevice.selectedIndex].value);
	  	 oCertX.TokenID = tokenId;
	  	var nExistedCertCount = oCertX.GetCertCount(tokenId);
		if (nExistedCertCount == 0) {
			alert("介质设备没有查找到任何证书，请确认该介质设备为待更新或恢复的设备");
			return false;
		}
		
		var isFindCert = false;
		for (var n = 0; n < nExistedCertCount; n++) {
			var userNameInKEY = oCertX.CertCommonName(n);
			if (userNameInKEY == userName) {
				isFindCert = true;
			}
		}
		
		if (!isFindCert) {
			alert("在介质设备中无法查找到证书用户名为 '" + userName + "' 的证书，请更换为正确的介质设备");
			return false;
		}
		return true;
	}

	// 证书延期不产生新的请求
	if("certpostpone" == operationId && device != 0){
		return true;
	}
	
	var certReq = null;
	if (0 == device) {
		// 主题更新或证书恢复
		if ("certrenew" == operationId || "certrecover" == operationId) {
			certReq = GenUpdateCertRequestWithKPSDK(tokenIndex, "certrenew" == operationId ? 0 : 1, sigCert, encCert);
		}
		else {
			certReq = GenCertRequest(deviceType, cspname, cspType, 1, tokenIndex);
		}
	}
	else {
		certReq = GenCertRequest(deviceType, cspname, cspType, 1, tokenIndex);
	}
	
	if(certReq==""){
		alert("产生请求失败");
		return false;
	}
	
	
	getByID("certReq").value = certReq;
	return true;
}

/**
 * 根据控件ID得到控件
 */
function getByID(id) {
	var ret = document.getElementById(id);
	if (ret == null) {
		alert("[getByID]未发现查找对象： " + id)
	}
	return ret;
}



/**
 * 证书签发/更新/延期/恢复等操作的安装
 */
function InstallCert() {
	
  	var device = getByID("deviceType").value;
  	var tokenType ="";
  	if(device==0){
  		tokenType = "Koal"
  	}else if(device==1){
  		tokenType = "Csp"
  	}
  	
	var sigCert 	= getByID("signCert").value;
	var encCert 	= getByID("encCert").value;
	var operationId = getByID("operationID").value;
	var lraInfo 	= getByID("lraInfo").value;
	
	var ret = 0;
	var deviceType = 0;
	var cspName = null;
	var tokenIndex = 0;
		
  	// 格尔控件
	if ("Koal" == tokenType) {
		deviceType = 0;
  		tokenIndex = -1;
  		
	} else {
		deviceType = 1;
		cspName = getByID("cspName").value;
	}		
	
	if ("Koal" == tokenType) {
		// 使用格尔控件
		if ("certrenew" == operationId || "certpostpone" == operationId) {
			// 主题更新或证书延期安装
			ret = UpdateCertWithKPSDK(tokenIndex, lraInfo);
		}
		else {
			// 证书签发或证书恢复或密钥更新安装
			ret = InstallCertLraInfo(deviceType, cspName, tokenIndex, lraInfo);
		}
	}
	else {
		// 使用IE控件
		if ("certpostpone" == operationId) {
			// 证书延期
			ret = PostponeCertWithCSP(cspName, sigCert, encCert);
		}
		else if ("certrenew" == operationId) {
			// 主题更新
			if (cspName.indexOf("Microsoft") >= 0) {
				// Microsoft提供的CSP
				var cn = getByID("CertCN").value
				CapicomDeleteCert(null, cn);
				ret = InstallCertLraInfo(deviceType, cspName, tokenIndex, lraInfo);
			}
			else {
				// 其它硬件KEY的CSP
				ret = UpdateCertWithCSP(cspName, lraInfo, sigCert, encCert);
			}
		}
		else if("keyupdate" == operationId){
			if (cspName.indexOf("Microsoft") >= 0) {
				// Microsoft提供的CSP
				var cn = getByID("CertCN").value
				CapicomDeleteCert(null, cn);
				ret = InstallCertLraInfo(deviceType, cspName, tokenIndex, lraInfo);
			}else{
				ret = InstallCertLraInfo(deviceType, cspName, tokenIndex, lraInfo);
				if(ret == 0 ){
					DeleteCertExceptAssignedByParams(cspName, sigCert, encCert);	
				}
			}
		}else{
				ret = InstallCertLraInfo(deviceType, cspName, tokenIndex, lraInfo);
		}
	}

	var operationName = null;
	if ("certsign" == operationId) operationName = "证书签发";
	else if ("certrenew" == operationId) operationName = "证书更新";
	else if ("certrecover" == operationId) operationName = "证书恢复";
	else if ("certpostpone" == operationId) operationName = "证书延期";
	else if ("keyupdate" == operationId) operationName = "密钥更新";
	else operationName = "未知操作";
	
	if (ret != 0) {
		alert(operationName + "失败，错误原因: " + getErrDesc(ret) + "(" + ret + ")");
		return false;
	}
	
	alert(operationName + "成功");
	return true;
}
