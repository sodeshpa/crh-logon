import re

from error_entry import ErrorEntry

def parse_log_lines(lines):

	error_entry = None
	error_entries_for_log_file = []

	for line in lines:
		#                       Timestamp								  LEVEL      Class       Thread      Message
		#pattern = re.compile("^(\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d.\d\d\d) ([^\s]+)\s+(\[(.*?)\]) (\[(.*?)\]) ([^\s]+.*$)")
		pattern = re.compile("^(\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d.\d\d\d) ([^\s]+)\s+(\[(.*?)\]) (\[(.*?)\])*(\s)*(.*?)$")
		if pattern.match(line):

			splitted = pattern.split(line)
			timestamp 	= splitted[1]
			loglevel 	= splitted[2]
			classname	= splitted[4].strip()
			thread 		= splitted[6].strip()
			message		= splitted[8]
			if loglevel == "INFO":
				error_entry = None
			elif loglevel in ["ERROR", "WARN"]:
				error_entry = ErrorEntry(timestamp, loglevel, classname, thread, message)
				error_entries_for_log_file.append(error_entry)

		elif error_entry != None:
			error_entry.add_message_line(line)

	return error_entries_for_log_file
