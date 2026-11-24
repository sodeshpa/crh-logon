function Get-RegistryValue {
 param( [String]$key, [String]$value )
 (Get-ItemProperty -Path "Registry::$key" -Name $value).$value
}

$zone="@ZONE@"
$outfile=$(Join-Path $(Get-Location) 'InstallationProperties.ini')

$config = @{ }
$config.jboss_admin_pwd = ""
$config.instance = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" INSTALLENV
$config.db_connection_url = -join("jdbc:oracle:thin:@", (Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" ORACLEURL))
$config.orauser_ulgn02_pwd = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" ORAUSERULGN02PWD
$config.orauser_ulgn03_pwd = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" ORAUSERULGN03PWD
$config.orauser_ulgnsap_pwd = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" ORAUSERSAPPWD
$config.keystore = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" SERVERKEYSTORE
$config.keystore_passwd = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" SERVERKEYSTORPASSWD
$config.trustore_passwd = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" SERVERTRUSTOREPASSWD
$config.trustore = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" SERVERTRUSTORE
$config.javaagent = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" JAVAAGENT

if( $zone -eq "int"){
	$config.krb5kdc = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" KRB5KDC
	$config.krb5realm = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" KRB5REALM
	$config.principal = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" PRINCIPAL
	$config.keytab = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" KEYTAB
	$config.lotroprincipal = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" LOTROPRINCIPAL
	$config.lotrokeytab = Get-RegistryValue "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\AdNovum Informatik AG\logonext" LOTROKEYTAB
}

if ( $outfile | Test-Path) {
	$dtstamp=Get-Date -Format yyyymmddhhmmss
	$dtstamp
	Rename-Item -Path $outfile -NewName "InstallationProperties-bkp_$dtstamp.ini"
}

foreach ($key in $config.keys) {
	$val = $config.$key
	#$val = [Regex]::Escape($config.$key)
	"$key=$val"
	Add-Content $outfile "$key=$($val)"
}
