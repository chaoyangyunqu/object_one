/**
 *
 * KOAL KPSDK Helper With EAClient Ctrl 
 *
 */

function _kpsdk_util_IsNull(value)
{
	return value == null || value.length == 0;
}

function _kpsdk_util_IsNullWithTrim(value) 
{
	if (value == null)
		return true;
	for (var n = 0; n < value.length; n++) {
		if (value.charAt(n) != ' ')
			return false;
	}
	return true;
}
 
/**
 * 查找可用的格尔介质设备，查找结果填充到theSelectCtrl对象
 *
 * theSelectCtrl:		Html SELECT Element
 * useFileSlot:			是否列举格尔文件加密介质设备
 *							true	列举文件加密介质
 *							false	不列举文件加密介质
 * unUsedSlotOnly:		是否查找已经签发过证书的介质设备
 *							true	包含已发过证书的介质设备
 							false	不包含，仅列举空白的介质设备
 * 返回值:				无
 */
function FindKLTokenSlot(theSelectCtrl, useFileSlot, unUsedSlotOnly) {
	oCertX.SmartToken = 0;
	oCertX.KeySize = 1024;
	oCertX.NotUseFileSlot = !useFileSlot;
	oCertX.UnusedSlotOnly = unUsedSlotOnly;
	oCertX.updateKey = false;
	
	var nTokenCount = oCertX.TokenCount;
	
	for (var i = 0; i < nTokenCount; i++) {
  		var TokenInfo = oCertX.TokenInfo(i);
  		var OptionItem = new Option(TokenInfo, i, false, false);
  		theSelectCtrl.options[i] = OptionItem;
	}
	
	if (theSelectCtrl.length > 0) {
		theSelectCtrl.options[0].selected = true ;
	}
	else {
  		var OptionItem = new Option("-- 无任何可用设备 --", -1, false, false);
  		theSelectCtrl.options[i] = OptionItem;
  		theSelectCtrl.disabled = true;
	}
}

/**
 * 根据指定的TokenIndex，产生CMP格式证书请求。
 *
 * tokenIndex:			FindKLTokenSlot函数所填充的SELECT.options.value
 * 返回值:				""	请求产生失败
 *						BASE编码的CMP格式证书请求
 */
function GenRequestByEAClient(tokenIndex) {
	var DNCN = "TestUser";
	var Issuer = "C=CN,CN=TestCA";
	
    oCertX.UserName = "2-5-4-3:" + DNCN.length + ":" + DNCN;
    oCertX.issuerName = "2-5-4-3:" + Issuer.length + ":" + Issuer;
    
    var tokenId = oCertX.GetSelTokenID(tokenIndex);
    oCertX.TokenID = tokenId;
    var ret = oCertX.GenCertReq;
    if (ret != 0) {
    	alert("无法产生证书请求，错误原因: " + getErrDesc(ret) + "(" + ret + ")");
    	return "";
    }
    
    return oCertX.CertReq;
}

/**
 * 根据指定的TokenIndex，产生CMP格式证书更新请求(用于证书主题更新和证书恢复)。
 *
 * updateType:			更新请求类型
 *							0 证书主题更新请求
 *							1 证书恢复请求
 * userName:			待更新的证书CN名称，必须和待更新介质设备的证书一致
 * sigCert:				BASE64编码X509格式签名证书
 * encCert:				BASE64编码X509格式加密证书
 * tokenIndex:			FindKLTokenSlot函数所填充的SELECT.options.value
 * 返回值:				""	请求产生失败
 *						BASE编码的CMP格式证书更新请求
 */
function GenUpdateRequestByEAClient(updateType, tokenIndex, userName, sigCert, encCert) {
	
	if (_kpsdk_util_IsNull(sigCert)) {
		alert("签名证书内容不能为空，请检查函数输入参数");
		return "";
	}
	var tokenId = oCertX.GetSelTokenID(tokenIndex);
	oCertX.TokenID = tokenId;
	if (updateType == 0) {
	    var nExistedCertCount = oCertX.GetCertCount(tokenId);
		if (nExistedCertCount == 0) {
			alert("介质设备没有查找到任何证书，请确认该介质设备为待更新或恢复的设备");
			return "";
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
			return "";
		}
	}
	
	if (updateType == 1 && _kpsdk_util_IsNull(encCert)) {
		alert("单证书不能进行证书恢复，无法产生证书恢复请求");
		return "";
	}
	
	
	if (updateType == 0) {
		// 重新产生签名密钥对
		oCertX.UpdateKey = true;
        ret = oCertX.GenUpdateCertReq(sigCert, encCert);
	} else if (updateType == 1) {
		// 重新产生签名密钥对
		oCertX.UpdateKey = true;
		ret = oCertX.GenRevCertReq(1, encCert);
	}
	else {
		alert("更新请求类型错误 '" + updateType + "'，请确认类型参数值");
		return "";
	}
	
	if (ret != 0) {
		alert("产生更新或恢复请求失败，错误原因: " + getErrDesc(ret) + "(" + ret + ")");
		return "";
	}
	
	return oCertX.CertReq;
}

/**
 * 安装CMP格式的证书签发码到指定TokenIndex
 *
 * tokenIndex:			FindKLTokenSlot函数所填充的SELECT.options.value
 * cmpValue:			BASE64编码的CMP格式证书安装码
 * 返回值:				0		证书安装成功
 *						其它	证书安装失败，返回错误代码
 */
function InstallCMPCert(tokenIndex, cmpValue) {
	if (tokenIndex != -1) {
		var tokenId = oCertX.GetSelTokenID(tokenIndex);
		oCertX.TokenID = tokenId;
	}
	var nRet = oCertX.InstallCert(1, cmpValue);
	return nRet;
}

/**
 * 更新CMP格式的证书更新码到指定TokenIndex
 *
 * tokenIndex:			FindKLTokenSlot函数所填充的SELECT.options.value
 * cmpValue:			BASE64编码的CMP格式证书安装码
 * 返回值:				0		证书安装成功
 *						其它	证书安装失败，返回错误代码
 */
function UpdateCMPCert(tokenIndex, cmpValue)
{
	if (tokenIndex != -1) {
		var tokenId = oCertX.GetSelTokenID(tokenIndex);
		oCertX.TokenID = tokenId;
	}
	nRet = oCertX.InstallUpdateCert(1, "","",cmpValue);				
	return nRet;
}

/**
 * 使用CSP安装PFX证书内容
 *
 * cspName:				CSP Provider名称，该值为FindProviders填充的SELECT.options.text
 * pfxValue:			BASE64编码的PFX格式证书内容
 * 返回结果:			0		安装成功
 *						其它	安装失败，返回错误代码。
 */
function InstallPfxCert(cspName, pfxValue) {
	var ret = oKoalCert.ImportPfxCert(cspName, pfxValue, "123456");
	if (!ret) {
		alert(oKoalCert.getLastErrMsg());
		return oKoalCert.getLastError();
	}	
	return 0;
}

function UpdateX509Cert(cspName, x509Cert) {
	var ret = oKoalCert.UpdateX509Cert(cspName, x509Cert);
	if (!ret) {
		alert(oKoalCert.getLastErrMsg());
		return oKoalCert.getLastError();
	}	
	return 0;
}

/**
 * 删除缺省的Container(一般是第一对证书)
 */
function DeleteDefaultContainer(cspName)
{
	// oKoalCert.DeleteDefaultContainer(cspName);
}

/**
 * 根据序列号删除指定容器
 */
function DeleteContainerBySN(serialNumber)
{
}

/**
 * 列举所有container，将不包含新证书的容器删除
 */
function DeleteInvalidContainer(cspName, b64NewSigCert, b64NewEncCert)
{
	oKoalCert.DeleteInvalidContainer(cspName, b64NewSigCert, b64NewEncCert);
}

var aErrorCodes = new Array(59);
var aErrorDescs = new Array(59);

aErrorCodes[0]="-4001";
aErrorCodes[1]="-4002";
aErrorCodes[2]="-4003";
aErrorCodes[3]="-4004";
aErrorCodes[4]="-4005";
aErrorCodes[5]="-4006";
aErrorCodes[6]="-4007";
aErrorCodes[7]="-4008";
aErrorCodes[8]="-4009";
aErrorCodes[9]="-4010";

aErrorCodes[10]="-4011";
aErrorCodes[11]="-4012";
aErrorCodes[12]="-4013";
aErrorCodes[13]="-4014";
aErrorCodes[14]="-4015";
aErrorCodes[15]="-4016";
aErrorCodes[16]="-4017";
aErrorCodes[17]="-4018";
aErrorCodes[18]="-4019";
aErrorCodes[19]="-4020";

aErrorCodes[20]="-4021";
aErrorCodes[21]="-4022";
aErrorCodes[22]="-4023";
aErrorCodes[23]="-4024";
aErrorCodes[24]="-4025";
aErrorCodes[25]="-4026";
aErrorCodes[26]="-4027";
aErrorCodes[27]="-4028";
aErrorCodes[28]="-4029";
aErrorCodes[29]="-4030";

aErrorCodes[30]="-4031";
aErrorCodes[31]="-4032";
aErrorCodes[32]="-4033";
aErrorCodes[33]="-4034";
aErrorCodes[34]="-4035";
aErrorCodes[35]="-4036";
aErrorCodes[36]="-4037";
aErrorCodes[37]="-4038";
aErrorCodes[38]="-4040";
aErrorCodes[39]="-4050";

aErrorCodes[40]="-4100";
aErrorCodes[41]="-4101";
aErrorCodes[42]="-4102";
aErrorCodes[43]="-4103";
aErrorCodes[44]="-4104";
aErrorCodes[45]="-4105";
aErrorCodes[46]="-4106";
aErrorCodes[47]="-4107";
aErrorCodes[48]="-4108";
aErrorCodes[49]="-4200";

aErrorCodes[50]="-4210";
aErrorCodes[51]="-4300";

aErrorCodes[52]="-1001";
aErrorCodes[53]="-1002";
aErrorCodes[54]="-1003";
aErrorCodes[55]="-1004";
aErrorCodes[56]="-1005";
aErrorCodes[57]="-1006";
aErrorCodes[58]="-1007";
aErrorCodes[59]="-1008";
aErrorCodes[60]="-1009";
aErrorCodes[61]="-1010";
aErrorCodes[62]="-1011";

aErrorDescs[0]="PKI SDK内部错误";
aErrorDescs[1]="PKI SDK内部错误";
aErrorDescs[2]="未知错误";
aErrorDescs[3]="不支持指定运算";
aErrorDescs[4]="TOKEN没有正确初始化";
aErrorDescs[5]="TOKEN不可用";
aErrorDescs[6]="消息格式不正确";
aErrorDescs[7]="处理失败";
aErrorDescs[8]="操作暂时不能完成，但以后再试可能成功";
aErrorDescs[9]="需要签名";

aErrorDescs[10]="没有结构验证";
aErrorDescs[11]="TOKEN内部错误";
aErrorDescs[12]="TOKEN不支持此操作";
aErrorDescs[13]="文件操作中到达文件尾";
aErrorDescs[14]="空指针";
aErrorDescs[15]="传入缓冲区太小";
aErrorDescs[16]="数组越界";
aErrorDescs[17]="内存空间不够";
aErrorDescs[18]="参数无效";
aErrorDescs[19]="结构中的成员无效";

aErrorDescs[20]="没有经过登录无法访问私有对象";
aErrorDescs[21]="实例化失败";
aErrorDescs[22]="数字格式错误";
aErrorDescs[23]="无效的强制类型转换";
aErrorDescs[24]="输入/输出错误";
aErrorDescs[25]="输出错误";
aErrorDescs[26]="输入错误";
aErrorDescs[27]="不可序列化";
aErrorDescs[28]="用户取消操作";
aErrorDescs[29]="无效状态";

aErrorDescs[30]="没有找到找到的对象";
aErrorDescs[31]="对象已经存在";
aErrorDescs[32]="操作被中断";
aErrorDescs[33]="连接失败";
aErrorDescs[34]="验证失败";
aErrorDescs[35]="MD2运算时输入数据长度不对";
aErrorDescs[36]="RSA解密失败";
aErrorDescs[37]="RSA运算时输入数据长度不对";
aErrorDescs[38]="无效日期";
aErrorDescs[39]="证书已经过期";

aErrorDescs[40]="IC卡内部错误";
aErrorDescs[41]="IC卡生成密钥出错";
aErrorDescs[42]="IC卡加密出错";
aErrorDescs[43]="IC卡解密出错";
aErrorDescs[44]="设备出错";
aErrorDescs[45]="读卡器出错";
aErrorDescs[46]="不是个人数据";
aErrorDescs[47]="无效密钥";
aErrorDescs[48]="IC卡导入密钥失败";
aErrorDescs[49]="数据校验失败";

aErrorDescs[50]="PIN长度不满足要求";
aErrorDescs[51]="证书链不完整";

aErrorDescs[52]="未知错误";
aErrorDescs[53]="未初始化";
aErrorDescs[54]="系统内部错误";
aErrorDescs[55]="错误的PFX格式";
aErrorDescs[56]="错误的X509或PKCS7格式";
aErrorDescs[57]="无法找到证书的私钥";
aErrorDescs[58]="证书更新失败";
aErrorDescs[59]="非法的参数(NULL)";
aErrorDescs[60]="非法的参数";
aErrorDescs[61]="无法从IE的'MY'容器找到待更新证书(根据公钥查找)";
aErrorDescs[62]="无法找到待删除的证书";

/**
 * 获取错误代码描述
 */
function getErrDesc(errorCode) {
	for (var i = 0; i <= 62; i++) {
		if (aErrorCodes[i] == errorCode) {
			return aErrorDescs[i];
		}
	}
	return "_UNKNOW_ERR_";
}