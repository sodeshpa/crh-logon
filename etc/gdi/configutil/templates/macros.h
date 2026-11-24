#macro (getType $row)
#set ($min = "undef")
#set ($min = $row.minValue)
#set ($max = "undef")
#set ($max = $row.maxValue)
#set ($val = "undef")
#set ($val = $row.value)
#if ($val == "true" || $val == false || $val == "yes" || $val == "no")
boolean#else
#if ($min == "undef" && $max == "undef")
String#else
#if ($row.value.contains("."))
double#else
int#end
#end
#end
#end
