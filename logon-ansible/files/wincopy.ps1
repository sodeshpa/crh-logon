Param(
  [string]$src,
  [string]$dest
)
net use U: \\samba.adnovum.ch\share LogonAnsible /user:adnovum\logon-ansible
Copy-Item -force $src $dest

