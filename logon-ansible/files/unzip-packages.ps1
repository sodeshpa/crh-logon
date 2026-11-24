Param(
	$sourcelocation,
	$destlocation
)

write-host "unzipping files in: $($sourcelocation) to $($destlocation)"
write-host "Deleting destination folder if exists, then recreating"
if(Test-Path $destlocation) {
	Remove-Item -Recurse -Force $destlocation
}
New-Item $destlocation -itemtype directory
write-host Done.
write-host Unzipping...
$pkgs = Get-ChildItem -Path $sourcelocation -Filter *logondb*.zip
$pkgs | Foreach-Object{
	write-host "Processing: $($_.FullName)"
	$source = $($_.FullName)
	$version = $_.Name.Split("-")[1]
	$target = "$($destlocation)\$($version)";
	$folder = "sql";
	write-host unzipping from $source to $target
	$shell = New-Object -ComObject Shell.Application

	# find script folder e.g. c:\tmp\test.zip\test\etc\script
	$item = $shell.NameSpace("$source\$folder")

	# actual destination directory
	$path = Split-Path (Join-Path $target $folder)
	if (!(Test-Path $path)) {$null = mkdir $path}

	# unzip c:\tmp\test.zip\test\etc\script to c:\tmp\output\test\etc\script
	$shell.NameSpace($path).CopyHere($item)
}
write-host Done.
