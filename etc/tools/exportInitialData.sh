#!/bin/bash

##################
##  PARAMETERS  ##
##################

relativeBaseDir=$( dirname "${BASH_SOURCE[0]}" )/../..;
baseDir=$(cd "$relativeBaseDir"; pwd);

echo "Base directory used: $baseDir"

workingDirectory="$baseDir/initialDataExport";
zipFileName=$(date +%Y%m%d%H%M)_logon_initialdata.zip

#################
##  FUNCTIONS  ##
#################

function printHelp() {
cat <<END
Usage: $0

Features:
* creates a zip file of the current initial data files
* it is based on the 2014.10.16 version of https://aww.adnovum.ch/confluence/display/HolcimPegasus/How+to+add+a+new+language+to+LOGON
END
}

############
##  MAIN  ##
############

if [[ "$@" =~ -h|--help ]]; then
  printHelp;
  exit 0;
fi

test -d  "$workingDirectory" || mkdir "$workingDirectory" || exit -1;

echo "Creating zip file of the current initial data files..."

cd "$workingDirectory";
cp -r "$baseDir/logon-persistence/etc/sql" .
cp -r "$baseDir/logon-persistence/etc/csv" .

cd "$workingDirectory/sql";
rm -rf *.sql loadtestdata/ nge_initialdata/ patch/ admin/ adza_initialdata/ adza_testdata/ fbnl_testdata/ config testdata/ tools/
echo ""
echo "Content of sql/:"
ls -al
cd ..;

cd "$workingDirectory/csv";
rm -rf adza_initialdata/ adza_testdata/ deletebp/ loadtestdata/ README testdata/
echo ""
echo "Content of csv/:"
ls -al
cd ..;

echo ""
zip -r "$zipFileName" csv sql

echo ""
echo "Created initial data zip file:"
echo "$workingDirectory/$zipFileName"
echo ""
echo "Please attach the above ZIP file to the corresponding ticket (e.g. LOGON-6316, LOGON-7949)."
echo "Also add a comment to inform the watchers that a new ZIP file has been added."
echo ""
echo "Thank you for choosing $0."
echo ""
