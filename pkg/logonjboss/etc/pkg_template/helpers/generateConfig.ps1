
# TODO: read from config file or registry

$installationPropertiesFile=$(Join-Path $(Get-Location) "InstallationProperties.ini")
$props = [System.IO.File]::ReadAllText($installationPropertiesFile)
$props = [Regex]::Escape($props)
$props = $props -replace "(\\r)?\\n", [Environment]::NewLine
$props = $props -replace "(\\#)", '#'
$props = $props -replace "(\\\ \=(\ )?)", '='
$config = ConvertFrom-StringData($props)

echo =============================
echo "Configuration Parameters: "
$config
echo =============================


###################################
# template standalone.xml
$ConfigurationTemplatePath=$(Join-Path $(Get-Location) 'config\templates\standalone.xml')
# (Get-Content standalone-ext.xml -Encoding UTF8) -replace 'extensions.', 'EXTENSIONS.'  Out-File standalone_test.xml"
$ConfigurationPath=$(Join-Path $(Get-Location) 'config\standalone.xml')

[System.IO.File]::ReadAllText("$ConfigurationTemplatePath"
  ).replace('{{ DB_CONNECTION_URL }}', $config.db_connection_url
  ).replace('{{ ORAUSER_ULGN02_PWD }}', $config.orauser_ulgn02_pwd
  ).replace('{{ ORAUSER_ULGN03_PWD }}', $config.orauser_ulgn03_pwd
  ).replace('{{ ORAUSER_ULGNSAP_PWD }}', $config.orauser_ulgnsap_pwd
  ).replace('{{ KEYSTORE }}', $config.keystore
  ).replace('{{ KEYSTORE_PASSWD }}', $config.keystore_passwd
  ).replace('{{ TRUSTORE }}', $config.trustore
  ).replace('{{ TRUSTORE_PASSWD }}', $config.trustore_passwd
  #
  ).replace('{{ KRB5KDC }}', $config.krb5kdc
  ).replace('{{ KRB5REALM }}', $config.krb5realm
  ).replace('{{ PRINCIPAL }}', $config.principal
  ).replace('{{ KEYTAB }}', $config.keytab
  ).replace('{{ LOTROPRINCIPAL }}', $config.lotroprincipal
  ).replace('{{ LOTROKEYTAB }}', $config.lotrokeytab
  ) | Set-Content $ConfigurationPath

  ###################################
  # template logon.properties
  $ConfigurationTemplatePath=$(Join-Path $(Get-Location) 'config\templates\logon.properties')
  $ConfigurationPath=$(Join-Path $(Get-Location) 'config\logon.properties')

  [System.IO.File]::ReadAllText("$ConfigurationTemplatePath"
    ).replace('{{ INSTANCE }}', $config.instance
    ) | Set-Content $ConfigurationPath

  ###################################
  # template standalone.conf.bat (JVM options)
  $ConfigurationTemplatePath=$(Join-Path $(Get-Location) 'config\templates\standalone.conf.bat')
  $ConfigurationPath=$(Join-Path $(Get-Location) 'config\standalone.conf.bat')

  $javaagentString=""
  $javaagentInstallDirString=""
  if (-not ([string]::IsNullOrEmpty($config.javaagent))){
  	$javaagentString="-javaagent:$config.javaagent"
	$javaagentString=-join("-javaagent:", $config.javaagent)
  }

  if (-not ([string]::IsNullOrEmpty($config.javaagent_install_dir))){
	$javaagentInstallDirString=-join("-Dappagent.install.dir=", $config.javaagent_install_dir)
  }

  [System.IO.File]::ReadAllText("$ConfigurationTemplatePath"
    ).replace('{{ JAVAAGENT }}', $javaagentString
    ).replace('{{ JAVAAGENT_INSTALL_DIR }}', $javaagentInstallDirString
    ) | Set-Content $ConfigurationPath

 ###################################
  # template autoinstall.xml.variables (JVM options)
  $ConfigurationTemplatePath=$(Join-Path $(Get-Location) 'config\templates\autoinstall.xml.variables')
  $ConfigurationPath=$(Join-Path $(Get-Location) 'config\autoinstall.xml.variables')

  [System.IO.File]::ReadAllText("$ConfigurationTemplatePath"
    ).replace('{{ JBOSS_ADMIN_PWD }}', $config.jboss_admin_pwd
    ) | Set-Content $ConfigurationPath
