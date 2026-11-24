#!/bin/bash

shopt -s expand_aliases
source ~/.bashrc

if [[ "$@" =~ -h|--help ]]; then
	cat <<END

Usage: $0 [build params]

Execute it from project root, e.g. with 'etc/tools/buildInRAM.sh dist'

This helper script works as follows.

At first time:
	• Mounts RAM disc.
	• Copies the latest build from '$PWD' to the RAM disc. /Try to avoid clean build./
Every time:
	• Updates source from '$PWD' to RAM disc.
	• Executes adnant with the given parameters.
	• Copies the build back from RAM to '$PWD'.

END
	exit 0;
fi

##################
##  PARAMETERS  ##
##################

sourceDir="$PWD" # Path to the source folder in HDD
branchName="${PWD##*/}"
ramDiskPath="/local/$USER/ramDisk/${branchName}" # Path to the folder where the ram will be mounted

mountSize="4G" # Size of the RAM folder. This obviously can not be more than the RAM available on your system.
mountPermissions="0755" # The permission for the folders. Used when mounting.
mountUser="$USER" # The user and owner for the mounted folders.

declare buildParams="$@"

#################
##  FUNCTIONS  ##
#################

function checkPaths() {
	if [ ! -d "${ramDiskPath}" ]; then
		echo "Creating ram folder \"${ramDiskPath}\""
		mkdir -p "${ramDiskPath}"
		chmod "${mountPermissions}" "${ramDiskPath}"
	fi
}

function checkMount() {
	if ! mountpoint -q "${ramDiskPath}"; then
		echo "Mounting a tmpfs with mountSize ${mountSize} to the ram mountpoint folder \"${ramDiskPath}\"."
		sudo mount -t tmpfs -o "size=${mountSize},mode=${mountPermissions},uid=${mountUser},gid=adnstaff" tmpfs "${ramDiskPath}"
		echo "START: Copy latest build from '${sourceDir}' to '${ramDiskPath}'...";
		time rsync -azv "${sourceDir}/" "${ramDiskPath}" \
			--exclude "*.git*" --exclude "*.svn*"  --exclude "*.metadata*" \
			--include "*build/*" --include "*.bin/*" --include "run/*" \
			--exclude "*.*" \
			--include "*.class" --include "*.ear" --include "*.jar" --include "*.war"
		echo " DONE: Copy latest build from '${sourceDir}' to '${ramDiskPath}'...";
	fi
}
 
function doSync() {
	echo "START: Copy/update source from '${sourceDir}' to '${ramDiskPath}'...";
	time rsync -azv "${sourceDir}/" "${ramDiskPath}" \
		--exclude "*run/*"  --exclude "*build/*" --exclude "*.bin/*" \
		--exclude "*.git*" --exclude "*.svn*"  --exclude "*.metadata*" \
		--delete
	echo " DONE: Copy/update source from '${sourceDir}' to '${ramDiskPath}'...";
}

function doBuild() {
	cd "${ramDiskPath}";
	echo ""
	echo "Starting adnant in '${ramDiskPath}' with the following command:"
	echo "adnant $buildParams"
	echo ""
	adnant "$buildParams"
}

function copyBuildBack() {
	echo "START: Copy/update build back from '${ramDiskPath}' to '${sourceDir}'...";
	time rsync -auz "${ramDiskPath}/" "${sourceDir}"  \
			--exclude "*.git*" --exclude "*.svn*"  --exclude "*.metadata*" \
			--include "*build/*" --include "*.bin/*" \
			--exclude "*.*" \
			--include "*.class" --include "*.ear" --include "*.jar" --include "*.war"
	echo " DONE: Copy/update build back from '${ramDiskPath}' to '${sourceDir}'...";
}

#############
##  MAGIC  ##
#############

checkPaths
checkMount
doSync
doBuild
copyBuildBack

