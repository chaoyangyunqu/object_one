/**
 *
 * 格尔客户端SCRIPT脚本。本脚本为对外接口，基本实现为csp.xxxx.vs及kpsdk.xxxx.js
 *
 */

var KOAL_TOKEN 				= 0;
var IE_TOKEN 				= 1;

var KEN_GEN_EXPORTABLE 		= 1;
var KEN_GEN_NO_EXPORTABLE 	= 2;

function _raclient_util_IsNull(value)
{
	return value == null || value.length == 0;
}

function _raclient_util_IsNullWithTrim(value) 
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
 * 查找可用的加密介质设备，查找结果填充到theSelectCtrl对象
 *
 * deviceType:			加密介质设备类型
 *							0	格尔加密介质设备
 *							1	CSP加密介质设备
 * theSelectCtrl:		Html SELECT Element
 * useFileSlot:			格尔加密介质设备时可用。是否列举格尔文件加密介质设备
 *							true	列举文件加密介质
 *							false	不列举文件加密介质
 * unUsedSlotOnly:		格尔加密介质设备时可用。是否查找已经签发过证书的介质设备
 *							true	包含已发过证书的介质设备
 							false	不包含，仅列举空白的介质设备
 * 返回值:				无
 */
function EnumDevcieList(deviceType, theSelectCtrl, useFileSlot, unUsedSlotOnly)
{
	if (deviceType == 0) {
		FindKLTokenSlot(theSelectCtrl, useFileSlot, unUsedSlotOnly);
	}
	else if (deviceType == 1) {
		FindProviders(theSelectCtrl);
	}
	else {
		alert("无效的加密介质设备类型: " + deviceType);
	}
}

/**
 * 产生证书签发请求。适用于:
 *		<CSP方式证书签发请求生成>
 *		<CSP方式主题更新请求生成>
 *		<CSP方式密钥更新请求生成>
 *		<CSP方式证书恢复请求生成>
 *		<KPSDK方式证书签发请求生成>
 *		<KPSDK方式密钥更新请求生成 >
 *
 * deviceType:	加密介质设备类型
 *					0	格尔加密介质设备
 *					1	CSP加密介质设备
 * cspName:		CSP加密介质设备时可用。CSP Provider名称，该值为EnumDevcieList填充的SELECT.options.text
 * cspType:		CSP加密介质设备时可用。CSP Provider类型，该值为EnumDevcieList填充的SELECT.options.value
 * keyFlags:	CSP加密介质设备时可用。密钥生成标识。
 *					1		生成的密钥可以导出
 *					2		生成的密钥不可以导出
 * tokenIndex:	格尔加密介质设备时可用。格尔介质设备索引值。EnumDevcieList函数所填充的SELECT.options.value
 * 返回值:		""	请求产生失败
 *				使用格尔加密介质设备，返回BASE64编码的CMP格式证书请求
 *				使用CSP加密介质设备，返回PEM编码的PKCS#10格式请求
 */
function GenCertRequest(deviceType, cspName, cspType, keyFlags, tokenIndex)
{
	var certReq = null;
	
	if (deviceType == 0) {
		certReq = GenRequestByEAClient(tokenIndex);
	}
	else if (deviceType == 1) {
		certReq = GenPKCS10RequestByEnroll(cspName, cspType, keyFlags);
	}
	else {
		alert("无效的加密介质设备类型: " + deviceType);
	}
	
	return certReq;
}

/**
 * 产生CMP格式证书更新请求。适用于:
 *		<KPSDK方式证书更新请求生成>
 *		<KPSDK方式证书恢复请求生成>
 *
 * tokenIndex:		格尔加密介质设备时可用。格尔介质设备索引值。EnumDevcieList函数所填充的SELECT.options.value
 * updateType:		更新请求类型
 *							0 证书主题更新请求
 *							1 证书恢复请求
 * sigCert:			BASE64编码X509格式签名证书
 * encCert:			BASE64编码X509格式加密证书
 * 返回值:			""	请求产生失败
 *					BASE编码的CMP格式证书请求
 */
function GenUpdateCertRequestWithKPSDK(tokenIndex, updateType, sigCert, encCert)
{
	var userDN1 = oCertX.ParseB64Cert2(sigCert, "DN");
	if (!_raclient_util_IsNull(encCert)) {
		var userDN2 = oCertX.ParseB64Cert2(encCert, "DN");
		if (userDN1 != userDN2) {
			alert("签名证书的证书主题和加密证书不一致");
			return null;
		}
	}
	
	var userName = oCertX.ParseB64Cert2(sigCert, "CN");	
	return GenUpdateRequestByEAClient(updateType, tokenIndex, userName, sigCert, encCert);
}

/**
 * 安装证书签发码到加密介质设备
 * 安装前务必格式化介质设备，保证介质设备空白。适用于:
 *		<CSP方式证书签发码安装>
 *		<CSP方式证书恢复码安装>
 *		<CSP方式密钥更新码安装>
 *		<KPSDK方式证书签发码安装>
 *		<KPSDK方式密钥更新码安装>
 *		<KPSDK方式证书恢复码安装 >
 * deviceType:	加密介质设备类型
 *					0	格尔加密介质设备
 *					1	CSP加密介质设备
 * cspName:		CSP加密介质设备时可用。CSP Provider名称，该值为EnumDevcieList填充的SELECT.options.text
 * tokenIndex:	格尔加密介质设备时可用。格尔介质设备索引值。EnumDevcieList函数所填充的SELECT.options.value
 *				-1		表示忽略该值。
 * lraInfo:		BASE64编码的证书签发码(CMP或PKCS7_AND_PFX(PFX)格式)
 * 返回值:		0		证书安装成功
 *				其它	证书安装失败，返回错误代码
 */
function InstallCertLraInfo(deviceType, cspName, tokenIndex, lraInfo)
{
	alert("cspName="+cspName);
	
	var ret = 0;
	
	if (deviceType == 0) {
		ret = InstallCMPCert(tokenIndex, lraInfo);
	}
	else if (deviceType == 1) {
		var sigLraInfo = null;
		var encLraInfo = null;
		var index = lraInfo.indexOf("|");
		if (index == -1) {
			// 单证书
			sigLraInfo = lraInfo;
		}
		else {
			// 双证书
			sigLraInfo = lraInfo.substring(0, index);
			encLraInfo = lraInfo.substring(index + 1);
		}
		
		// 一定要先安装证书签名，因为在证书更新时，是通过删除缺省容器的方式来删除签名证书
		ret = InstallPKCS7Cert(sigLraInfo);
		if (ret == 0 && !_raclient_util_IsNull(encLraInfo)) {
			ret = InstallPfxCert(cspName, encLraInfo);
		}
	}
	else {
		alert("无效的加密介质设备类型: " + deviceType);
	}
	
	return ret;
}

/**
 * 使用KPSDK方式安装证书签发码到加密介质设备。适用于:
 * 		<KPSDK方式主题更新安装>
 * 		<KPSDK方式证书延期安装>
 *
 * tokenIndex:	格尔加密介质设备时可用。格尔介质设备索引值。EnumDevcieList函数所填充的SELECT.options.value
 *				-1		表示忽略该值。
 * lraInfo:		BASE64编码CMP格式的证书更新码
 * 返回值:		0		证书更新成功
 *				其它	证书更新失败，返回错误代码
 */
function UpdateCertWithKPSDK(tokenIndex, lraInfo)
{
	return UpdateCMPCert(tokenIndex, lraInfo);
}

/**
 * 使用CSP方式更新证书(双证书不支持微软CSP Provider)
 *
 * cspName:		CSP加密介质设备时可用。CSP Provider名称，该值为EnumDevcieList填充的SELECT.options.text
 * sigLraInfo:	BASE64编码的PKCS7编码签名证书
 * sigCert:		BASE64编码的X509格式签名证书
 * encCert:		BASE64编码的X509格式加密证书
 * 返回值:		0		证书更新成功
 *				其它	证书更新失败，返回错误代码
 */
function UpdateCertWithCSP(cspName, sigLraInfo, sigCert, encCert)
{
	var ret = -1;
	if (cspName.indexOf("Microsoft") != -1 && !_raclient_util_IsNull(encCert)) {
		alert("双证书更新不支持微软CSP设备(" + cspName + ")");
		return ret;
	}

	// 安装签名证书	
	ret = InstallPKCS7Cert(sigLraInfo);
	if (ret != 0) {
		return ret;
	}

	// 安装加密证书
	if (!_raclient_util_IsNull(encCert)) {
		ret = UpdateX509Cert(cspName, encCert)
	}
	
	if (ret == 0) {
		DeleteCertExceptAssignedByParams(cspName, sigCert, encCert);	
	}
	
	return ret;
}

/**
 * 使用CSP方式延期证书(不支持微软CSP Provider)
 *
 * cspName:		CSP加密介质设备时可用。CSP Provider名称，该值为EnumDevcieList填充的SELECT.options.text
 * sigCert:		BASE64编码的X509格式签名证书
 * encCert:		BASE64编码的X509格式加密证书
 * 
 * 返回值:		0		证书延期成功
 *				其它	证书延期失败，返回错误代码
 */
function PostponeCertWithCSP(cspName, sigCert, encCert)
{
	var ret = -1;
	if (cspName.indexOf("Microsoft") != -1) {
		alert("证书延期不支持微软CSP设备(" + cspName + ")");
		return ret;
	}

	if (!_raclient_util_IsNull(sigCert)) {
		ret = UpdateX509Cert(cspName, sigCert);
		if (ret != 0) {
			return ret;
		}
	}

	if (!_raclient_util_IsNull(encCert)) {
		ret = UpdateX509Cert(cspName, encCert);
		if (ret != 0) {
			return ret;
		}
	}
			
	return 0;
}

/**
 * 删除除参数指定外的其它所有证书
 *
 * cspName:		CSP加密介质设备时可用。CSP Provider名称，该值为EnumDevcieList填充的SELECT.options.text
 * newSigCert:	BASE64编码的X509格式签名证书
 * newEncCert:	BASE64编码的X509格式加密证书
 */
function DeleteCertExceptAssignedByParams(cspName, newSigCert, newEncCert)
{
	if (_raclient_util_IsNull(newEncCert)) {
		// 删除签名证书(由于函数不能传入空值，将加密证书参数设置为newSigCert)
		DeleteInvalidContainer(cspName, newSigCert, newSigCert);
	}
	else {
		// 删除签名证书及加密证书
		DeleteInvalidContainer(cspName, newSigCert, newEncCert);
	}
}