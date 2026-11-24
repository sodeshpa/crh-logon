#!/bin/bash

################
##  FEATURES  ##
################

#* generate new LitDict key and print to STO

##################
##  PARAMETERS  ##
##################

if [[ "$@" =~ -h|--help ]]; then
	cat <<END
		
This helper script generates new LitDict key based on your input, and prints it to the STO.
Then you can copy/paste it to the LitDict.global file.

END
	exit 0;
fi

declare -A _languages=( [de]=German [fr]=French [it]=Italian [es]=Spanish [cs]=Czech [sk]=Slovak [hu]=Hungarian [ro]=Romanian [bg]=Bulgarian [hr]=Croatian [sr]=Serbian [ru]=Russian [pl]=Polish [az]=Azerbaijani [tr]=Turkish  [nl]=Netherlands)
_date=$(date +%d.%m.%y)
_fix_version=2.2.0.0
echo "Enter the fix version! (press return to leave it '$_fix_version')"
read _fix_version_input
[ ! -z "$_fix_version_input" ] && _fix_version=$_fix_version_input

echo "Enter the key ID!"
read _key
echo "Enter the key value in English!"
read _value

function printLitDictEntryLine() {
	local langCode=${1?Missing language code!}
	printf "[%s][AdN %s: TODO translate: %11s translation (%s)]%s (%s)\n"\
			"$langCode" "$_date" "${_languages[$langCode]}" "$_fix_version" "$_value" "$langCode"
}

#############
##  MAGIC  ##
#############

echo "$_key:"
printLitDictEntryLine "de"
echo "[en][AdN $_date: TODO    verify:     English translation ($_fix_version)]$_value"
printLitDictEntryLine "fr"
printLitDictEntryLine "it"
printLitDictEntryLine "es"
printLitDictEntryLine "cs"
printLitDictEntryLine "sk"
printLitDictEntryLine "hu"
printLitDictEntryLine "ro"
printLitDictEntryLine "bg"
printLitDictEntryLine "hr"
printLitDictEntryLine "sr"
printLitDictEntryLine "ru"
printLitDictEntryLine "pl"
printLitDictEntryLine "az"
printLitDictEntryLine "tr"
printLitDictEntryLine "nl"
