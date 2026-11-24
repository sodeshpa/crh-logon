var logonPackageHandlingHelper = {
		packageCdBag:null,
		packageCdBulkPowder:null,
		packageCdBulkDumped:null,
		packageCdService:null,
		initialized:false,
		init:function(packageCdBulkPowder, packageCdBulkDumped, packageCdBag, packageCdService){
			this.packageCdBag=packageCdBag;
			this.packageCdBulkPowder=packageCdBulkPowder;
			this.packageCdBulkDumped=packageCdBulkDumped;
			this.packageCdService=packageCdService;
			this.initialized=true;
		},	
		isBag: function (logonPackageTypeCd) {
			if (this.initialized && logonPackageTypeCd && logonPackageTypeCd == this.packageCdBag){
				return true;
			}
			return false;
		},
		isBulk:function(logonPackageTypeCd) {
			if (this.initialized && logonPackageTypeCd && (logonPackageTypeCd == this.packageCdBulkPowder || logonPackageTypeCd == this.packageCdBulkDumped)){
				return true;
			}
			return false;	
		},
		isService:function(logonPackageTypeCd) {
			if (this.initialized && logonPackageTypeCd && logonPackageTypeCd == this.packageCdService){
				return true;
			}
			return false;
		}
}