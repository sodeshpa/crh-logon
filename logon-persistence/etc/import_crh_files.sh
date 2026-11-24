#!/bin/sh

# Copies and converts master data files from Crh into working repository.
# Specify the master data files from Crh either by path to ZIP file or path to parent of ZIP content.
# Data from Crh is expected to be in UTF-8 with BOM characters at the beginning
# --> remove BOM
# Converting to ISO-8859-1 is not possible anymore, as we now really have exotic characters.

if [ $# == 0 ]; then
	#echo "Usage: $0 <ZIP|path>"
	echo "Usage: $0 <path to parent containing csv/* sql/*>"
	exit 1
fi

#TODO add support for ZIP file as source.

for f in $( cd "$1"; find . -type f ); do
	src="$1/$f"
	dst="$f"
	mkdir -p $( dirname $dst )

	if [ $( basename "$src" ) == 'config.csv' ]; then
		echo "Skipping '$src' (sticking with AdNovum version). Please manually import the new configuration file 'config.csv' using etc/tools/config.pl"
		continue
	fi

	encoding=$( file "$src" | awk '{print $2}' )

	if [ $( file "$src" | grep -c -e 'UTF-8.*BOM' ) == 1 ]; then
		# UTF-8 with BOM, we cut away the BOM
		tail --bytes=+4 "$src" > "$dst"
		echo "File converted (UTF-8 BOM removed): '$dst'"
	elif [ $encoding == 'UTF-8' ]; then
		cp "$src" "$dst"
		echo "File copied (no change: $encoding): '$dst'"
	elif [ $encoding == 'ASCII' ]; then
		# already in correct format
		cp "$src" "$dst"
		echo "File copied (no change: $encoding): '$dst'"
	elif [ $encoding == 'ISO-8859' ]; then
		iconv -f ISO-8859-1 -t UTF8 -o "$dst" "$src"
		echo "File converted from $encoding to UTF-8: '$dst'"
	else
		# anything else than UTF-8, just copy for now
		cp "$src" "$dst"
		echo "File copied ($encoding): '$dst'"
	fi
done

exit 0
