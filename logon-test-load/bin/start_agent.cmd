echo off
call bin/set_grinder_env.cmd
java net.grinder.Grinder %GRINDERPROPERTIES%
