Param(
  $pkgsdir,
  $connectionurl
)
# This scripts looks for the extracted logondb packages that are in the $pkgsdir folder and applies them.
# Actually looks for the patch in the $pkgsdir\<somelogondbpkgname>\sql\patch\from_*_*_*_*
# where the part "from_*_*_*_*" contains the bigest version.
#
# EXAMPLE for invocation:
#  .\patchdb-v1-1.ps1 C:\TEMP\logondb\extracted logonebmdb3-int.zh.adnovum.ch:1521/TLGNEBM1

write-host "listing patch folders in: $($pkgsdir)"

$patchfolders = Get-ChildItem -Path $pkgsdir

#this is how one creates an empty array
$patches = @()

# And get each patchfile, add them to the list in execution order.
$patchfolders | Foreach-Object{
	
	#there is a special order in logon patches
	# First, the SAP patch is to be executed if there is any
	# then the normal patch
	# then the update_adnovum_initial_and_testtdata
	#by using the "+=" operator, PS recreates the array each time we add a value.
	
	$patchfolder = Get-ChildItem -Path "$($_.FullName)\sql\patch" -Filter "from*" | select -last 1
	$patchesinversion = Get-ChildItem -Path $patchfolder.FullName
	$patchesinversion | Foreach-Object{
		if($_.Name -match "patch_sap_\d" ) {
			$patches += $_
		}
	}
	$patchesinversion | Foreach-Object{
		if($_.Name -match "patch_\d" ) {
			$patches += $_
		}
	}
	$patchesinversion | Foreach-Object{
		if($_.Name -match "update_adnovum_initial" ) {
			# $patches += $_
		}
	}
}
write-host -------------------------------------------------------
write-host "The following patches will be applied, in this order:"
$patches | Foreach-Object{
	$_.FullName
}
write-host -------------------------------------------------------

# TODO: externalize user/pwd
$ulgnsaplogon = "ulgnsap/ulgnsap@$($connectionurl)"
$ulgn01logon = "ulgn01/ulgn01@$($connectionurl)"
$ulgnsaplogon
$ulgn01logon
$originaldir = $pwd
$patches | Foreach-Object {
	# We need to run the patches in their own folder, because of the relative path references
	cd $($_.Directory.FullName)
	if($_.Name -match  "patch_sap_\d") {
		echo exit | sqlplus.exe -L $($ulgnsaplogon) "@$($_.FullName)" >> C:\temp\dbpatch.log
	}
	else {
		echo exit | sqlplus.exe -L $($ulgn01logon) "@$($_.FullName)" >> C:\temp\dbpatch.log
	}
}
cd $originaldir
