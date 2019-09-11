    var CAPICOM_CERTIFICATE_FIND_SHA1_HASH 						= 0;
   
    var CAPICOM_MEMORY_STORE                                    = 0;
    var CAPICOM_LOCAL_MACHINE_STORE                             = 1;
    var CAPICOM_CURRENT_USER_STORE                              = 2;
    var CAPICOM_ACTIVE_DIRECTORY_USER_STORE                     = 3;
    var CAPICOM_SMART_CARD_USER_STORE                           = 4;
      
    var CAPICOM_STORE_OPEN_READ_ONLY                            = 0;
    var CAPICOM_STORE_OPEN_READ_WRITE                           = 1;
    
    var CAPICOM_INFO_SUBJECT_SIMPLE_NAME= 0;
    var CAPICOM_INFO_ISSUER_SIMPLE_NAME = 1;
    var CAPICOM_INFO_SUBJECT_EMAIL_NAME = 2;
    var CAPICOM_INFO_ISSUER_EMAIL_NAME  = 3;
    
    var CAPICOM_CHECK_NONE = 0;
    var CAPICOM_CHECK_TRUSTED_ROOT = 1;
    var CAPICOM_CHECK_TIME_VALIDITY = 2;
    var CAPICOM_CHECK_SIGNATURE_VALIDITY = 4;
    var CAPICOM_CHECK_ONLINE_REVOCATION_STATUS = 8;
    var CAPICOM_CHECK_OFFLINE_REVOCATION_STATUS = 16;
    
    var CAPICOM_TRUST_IS_NOT_TIME_VALID = 1;
    var CAPICOM_TRUST_IS_NOT_TIME_NESTED = 2;
    var CAPICOM_TRUST_IS_REVOKED = 4;
    var CAPICOM_TRUST_IS_NOT_SIGNATURE_VALID = 8;
    var CAPICOM_TRUST_IS_NOT_VALID_FOR_USAGE = 16;
    var CAPICOM_TRUST_IS_UNTRUSTED_ROOT = 32;
    var CAPICOM_TRUST_REVOCATION_STATUS_UNKNOWN = 64;
    var CAPICOM_TRUST_IS_CYCLIC = 128;
    var CAPICOM_TRUST_IS_PARTIAL_CHAIN = 65536;
    var CAPICOM_TRUST_CTL_IS_NOT_TIME_VALID = 131072;
    var CAPICOM_TRUST_CTL_IS_NOT_SIGNATURE_VALID = 262144;
    var CAPICOM_TRUST_CTL_IS_NOT_VALID_FOR_USAGE = 524288;
    
    var CAPICOM_CERTIFICATE_SAVE_AS_PFX = 0;
    var CAPICOM_CERTIFICATE_SAVE_AS_CER = 2;
    
    var CAPICOM_CERTIFICATE_INCLUDE_CHAIN_EXCEPT_ROOT = 0;
    var CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN  = 1;
    var CAPICOM_CERTIFICATE_INCLUDE_END_ENTITY_ONLY = 2;
    var CERT_SN = "61CF3F";


    function IsCAPICOMInstalled()
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

	function CapicomFindRootCertificateBySN(sn, hexSN)
	{
		var RootStore;
		var FoundCertificates;
		
		var hexLowerSN = (hexSN != null ? hexSN.toLowerCase() : "");
		var hexUpperSN = (hexSN != null ? hexSN.toUpperCase() : "");
		
		var hexLowerSNWithZeroPadding = (hexLowerSN % 2 == 0 ? hexLowerSN : "0" + hexLowerSN);
		var hexUpperSNWithZeroPadding = (hexUpperSN % 2 == 0 ? hexUpperSN : "0" + hexUpperSN);
		
		try
		{
	  		RootStore = new ActiveXObject("CAPICOM.Store");
	  		FoundCertificates = new ActiveXObject("CAPICOM.Certificates");
	  		RootStore.Open(CAPICOM_CURRENT_USER_STORE, "Root", CAPICOM_STORE_OPEN_READ_ONLY);
	  		CAPICOM_DLL_ISOK = true;
	
	  		Certificates = RootStore.Certificates;
	  		for (iCert = 1; iCert <= (Certificates.Count); iCert++)
	  		{
				var foundSN = Certificates.Item(iCert).SerialNumber;
				if (foundSN == sn || foundSN == hexLowerSN || foundSN == hexUpperSN 
							|| foundSN == hexLowerSNWithZeroPadding || foundSN == hexUpperSNWithZeroPadding)
					return true;
	  		}
		}
		catch (e)
		{
	  		alert("ȫݬөCapicom࠘ݾвװˇرֽӣ");
		}
		
		return false;
	}
	
	function CapicomFindCACertificateBySN(sn, hexSN)
	{
		var RootStore;
		var FoundCertificates;
		
		var hexLowerSN = (hexSN != null ? hexSN.toLowerCase() : "");
		var hexUpperSN = (hexSN != null ? hexSN.toUpperCase() : "");
		
		var hexLowerSNWithZeroPadding = (hexLowerSN % 2 == 0 ? hexLowerSN : "0" + hexLowerSN);
		var hexUpperSNWithZeroPadding = (hexUpperSN % 2 == 0 ? hexUpperSN : "0" + hexUpperSN);
		
		try
		{
	  		RootStore = new ActiveXObject("CAPICOM.Store");
	  		FoundCertificates = new ActiveXObject("CAPICOM.Certificates");
	  		RootStore.Open(CAPICOM_CURRENT_USER_STORE, "CA", CAPICOM_STORE_OPEN_READ_ONLY);
	  		CAPICOM_DLL_ISOK = true;
	
	  		Certificates = RootStore.Certificates;
	  		for (iCert = 1; iCert <= (Certificates.Count); iCert++)
	  		{
				var foundSN = Certificates.Item(iCert).SerialNumber;
				if (foundSN == sn || foundSN == hexLowerSN || foundSN == hexUpperSN 
							|| foundSN == hexLowerSNWithZeroPadding || foundSN == hexUpperSNWithZeroPadding)
					return true;
	  		}
		}
		catch (e)
		{
	  		// alert("ȫݬөCapicom࠘ݾвװˇرֽӣ");
		}
		
		return false;
	}
	
	function CapicomFindUserCertificateBySN(sn, hexSN)
	{
		var RootStore;
		var FoundCertificates;
		
		var hexLowerSN = (hexSN != null ? hexSN.toLowerCase() : "");
		var hexUpperSN = (hexSN != null ? hexSN.toUpperCase() : "");
		
		var hexLowerSNWithZeroPadding = (hexLowerSN % 2 == 0 ? hexLowerSN : "0" + hexLowerSN);
		var hexUpperSNWithZeroPadding = (hexUpperSN % 2 == 0 ? hexUpperSN : "0" + hexUpperSN);
		
		try
		{
	  		UserStore = new ActiveXObject("CAPICOM.Store");
	  		FoundCertificates = new ActiveXObject("CAPICOM.Certificates");
	  		UserStore.Open(CAPICOM_CURRENT_USER_STORE, "My", CAPICOM_STORE_OPEN_READ_ONLY);
	  		CAPICOM_DLL_ISOK = true;
	
	  		Certificates = UserStore.Certificates;
	  		for (iCert = 1; iCert <= (Certificates.Count); iCert++)
	  		{
				var foundSN = Certificates.Item(iCert).SerialNumber;
				if (foundSN == sn || foundSN == hexLowerSN || foundSN == hexUpperSN 
							|| foundSN == hexLowerSNWithZeroPadding || foundSN == hexUpperSNWithZeroPadding)
					return true;
	  		}
		}
		catch (e)
		{
	  		alert("ȫݬөCapicom࠘ݾвװˇرֽӣ");
		}
		
		return false;
	}

	function CapicomFindRootCertificateByCN(cn)
	{
		var RootStore;
		var FoundCertificates;
		try
		{
	  		RootStore = new ActiveXObject("CAPICOM.Store");
	  		FoundCertificates = new ActiveXObject("CAPICOM.Certificates");
	  		RootStore.Open(CAPICOM_CURRENT_USER_STORE, "Root", CAPICOM_STORE_OPEN_READ_ONLY);
	  		CAPICOM_DLL_ISOK = true;
	
	  		Certificates = RootStore.Certificates;
	  		for (iCert = 1; iCert <= (Certificates.Count); iCert++)
	  		{
	     		if (Certificates.Item(iCert).GetInfo(CAPICOM_INFO_SUBJECT_SIMPLE_NAME) == cn)
	     			return true;
	  		}
		}
		catch (e)
		{
	  		alert("ȫݬөCapicom࠘ݾвװˇرֽӣ");
		}
		
		return false;
	}

    function CapicomFindCertificateByCN(issuerName, cn)
    {
        // instantiate the CAPICOM objects
        var MyStore = new ActiveXObject("CAPICOM.Store");
        var FoundCertificates = new ActiveXObject("CAPICOM.Certificates");

        // open the store objects
        try
        {
            MyStore.Open(CAPICOM_CURRENT_USER_STORE, "My", CAPICOM_STORE_OPEN_READ_WRITE);
        }
        catch (e)
        {
            alert("An error occurred while opening your certificate stores, aborting");
            return false;
        }

        MyStores = new Array(MyStore);

        // enumerate through the stores
        for (iStore = 0; iStore <= (MyStores.length -1); iStore++)
        {
            Certificates = MyStores[iStore].Certificates;
            
            // enumerate through each of the certificates we found (if any)     
            for (iCert = 1; iCert <= (Certificates.Count); iCert++)
            {
	    		var name1 = Certificates.Item(iCert).GetInfo(CAPICOM_INFO_SUBJECT_SIMPLE_NAME);
				var name2 = Certificates.Item(iCert).GetInfo(CAPICOM_INFO_ISSUER_SIMPLE_NAME);
				var serialNumber = Certificates.Item(iCert).SerialNumber;
                if (name1 == cn && (issuerName == null || issuerName.indexOf(name2) != -1))
                {
  	                FoundCertificates.Add(Certificates.Item(iCert));
                }
            }
        }

        // return the certificate
        if (typeof(FoundCertificates) == "object")
        {
            return FoundCertificates;
        }
    }
    
    function CapicomDeleteCert(issuerName, cn)
    {
        var Certificates = CapicomFindCertificateByCN(issuerName, cn);
        
		if (Certificates.Count == 0)
			return;

        var MyStore = new ActiveXObject("CAPICOM.Store");
        MyStore.Open(CAPICOM_CURRENT_USER_STORE, "My", CAPICOM_STORE_OPEN_READ_WRITE);
		for (var n = 1; n <= Certificates.Count; n++)
		{
        	MyStore.Remove(Certificates.Item(n));
		}
    }
