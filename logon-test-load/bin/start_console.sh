#!/bin/bash

# assuming the cwd is in the test/load directory
. bin/set_grinder_env.sh

# the -cp options does not work with classpath wildcards, because the shell does the expansion
java net.grinder.Console
