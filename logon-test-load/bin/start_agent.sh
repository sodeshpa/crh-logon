#!/bin/bash

# assuming the cwd is in the test/load directory
. bin/set_grinder_env.sh

# the -cp options does not work with classpath wildcards, because the shell does the expansion

# used the passed command line arguments if there are any
if [ ! -n "$1" ]
then
	java net.grinder.Grinder $GRINDERPROPERTIES
else
	java net.grinder.Grinder $1
fi
