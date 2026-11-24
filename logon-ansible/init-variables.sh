#!/bin/sh
declare release
declare version
release=$(grep "^RELEASE" ../config/build.properties | sed s/=/\\n/g | tail -n 1)
version=$(grep "^VERSION" ../config/build.properties | sed s/=/\\n/g | tail -n 1)
version=$version.$release
echo removing group_vars and recreating it.
rm -rf group_vars
mkdir group_vars

echo logon_version: $version
declare templates
templates=$(ls ./group_vars_tmpl)
for file in $templates
do
	echo "processing group_vars_tmpl/$file into group_vars/$file"
	cat "group_vars_tmpl/$file" | sed  -e "s|VERSION|$version|g" > "group_vars/$file"
done

