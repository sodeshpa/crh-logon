Param(
	$pkgsource,
	$pkgdest,
	$versions
)

write-host Mapping network drive
net use U: \\samba.adnovum.ch\share LogonAnsible /user:adnovum\logon-ansible
write-host "listing db zips in: $($pkgsource)"

$folders = Get-ChildItem -Path $pkgsource
# Get directories only, that belong to a version
$folders = $folders | ?{ $_.PSIsContainer -and $_.name -Match "\d\.\d\."}
# Get those directories only that are after the current version
$folders = $folders | ?{ ($_.name -gt "$($dbdumpversion)")}
# The folders should be sorted by version. (i.e. != sorted alphabetically)
$folders = $folders | Sort-Object {$($_.Name)}
$versions = $versions.split(",")
$versions
write-host -------------------------------------------------------

#this is how one creates an empty array
$zips = @()
$zips.length
# The following code block goes over the folders of releases in /share/pkg
# and tries to find all of the necessary DB packages.
# these are the last one of each Build for the same Major, minor and release number
# Sometimes there is no DB patch added, then there is no zip.

$folders | Foreach-Object {
	$_.Name
	$pkg = Get-ChildItem -Path $_.FullName -filter "logondb*.zip"
	if($versions -contains $_.Name){
		if($pkg.length -gt 0) {
			$zips += $pkg[0].FullName
		}
	}
}

# And get each patchfile, add them to the list in execution order.
write-host -------------------------------------------------------
write-host "The following db packages will be copied over:"
write-host -------------------------------------------------------
$zips | Foreach-Object{
	$_
}
write-host -------------------------------------------------------
# write-host Removing previous directory and recreating...
# if(Test-Path $pkgdest) {
	# Remove-Item -Recurse -Force $pkgdest
# }
# New-Item $pkgdest -itemtype directory
# write-host Copying files...
# $zips | Foreach-Object{
	# Copy-Item -force $_ $pkgdest
# }
# write-host Done.
write-host -------------------------------------------------------
