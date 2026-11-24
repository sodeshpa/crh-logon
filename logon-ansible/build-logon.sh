#!/bin/bash
declare release
declare version
release=$(grep "^RELEASE" ../config/build.properties | sed s/=/\\n/g | tail -n 1)
version=$(grep "^VERSION" ../config/build.properties | sed s/=/\\n/g | tail -n 1)
#checking if the current version exists in /share/pkg/logon, incrementing release by 1 if so.
while [ $(ls /share/pkg/logon | egrep ^$version.$release | tail -n 1) ]
do
	echo Version $version.$release exists;
	((release+=1));
done
echo Next Logon version is $version.$release

#replacing release version
echo Modifiying ../config/build.properties...
sed -i '/^RELEASE/c\RELEASE='$release ../config/build.properties
echo Commiting...
#commit
svn commit --depth empty -m "LOGON-9206 Ansible for Logon "$'\n\n'"Incrementing RELEASE for ansible build." ../config/build.properties

#build
#sometimes the command ssh adnbuild@adnbuildw2003r2.. fails for the first time, but passes for the second. Retrying maximum 5 times, 2 should be enough.
x=1
while [ $x -le 5 ]
do
	echo Trying to add logon-ansible user and password to .bashrc on adnbuildw2003r2...
	cmdout=$(ssh adnbuild@adnbuildw2003r2 exec "cat .bashrc-temp | sed  -e 's|__PASSWD__|LogonAnsible|g' -e 's|__LOGIN__|logon-ansible|g' >.bashrc")
    success='successfully'
	if [[ $cmdout =~ $success ]]; 
		then
			echo "Command successful, invoking release build.";
			break;
		else
			echo "Command failed, retrying if within less than 5 tries.";
	fi;
done
echo Building...
#release_build logonjboss7/$version.$releasei
echo Removing user/password from .bashrc on adnbuildw2003r2
ssh adnbuild@adnbuildw2003r2 exec "cat .bashrc-temp | sed  -e 's|__PASSWD__|XXXXXX|g' -e 's|__LOGIN__|XXXXXX|g' >.bashrc"
echo Build done.

