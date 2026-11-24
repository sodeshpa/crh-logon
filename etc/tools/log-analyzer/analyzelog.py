import os
import shutil
import sys
import webbrowser
import math

import time

from datetime import datetime
from subprocess import call

from log_parser import parse_log_lines
from error_entry import ErrorEntry

# Performance metrics ...
start_time = time.time()

# Constants
max_listing_entry_per_page 	= 100

print ">>> log analyzer starting ..."

error_entries = []

# Input check
if len(sys.argv) != 2:
	error_message  = "This program requires exactly one argument, the folder where the logfiles reside.\n"
	error_message += "Example: 'python analyzelog.py my_log_folder"
	sys.exit(error_message)

logfile_folder = sys.argv[1]

logfile_list = []
for root, subFolders, files in os.walk(logfile_folder):
    for file in files:
        f = os.path.join(root,file)
        logfile_list.append(f)

for logfile_name in logfile_list:

	print "==> processing logfile_name %s starting ..." % logfile_name

	logfile_path = logfile_name
	ins = open( logfile_path, "r" )

	lines = []

	for line in ins:
		lines.append(line)

	ins.close()

	error_entries_for_log_file = parse_log_lines(lines)

	error_entries += error_entries_for_log_file

	print "<== processing logfile_name %s finished" % logfile_name

# ########################
# report folder generation
error_report_folder = 'error_report'

if os.path.exists(error_report_folder):
	shutil.rmtree(error_report_folder)

os.makedirs(error_report_folder)

# ========================
# collect error statistics
error_dict = {}
statistics_dict = {}

print "==> pre-processing error entries started ..."

for error_entry in error_entries:
	error_entry.find_cause()
	exception_class = error_entry.cause
	if exception_class == None or len(exception_class) == 0:
		continue
	if not exception_class in error_dict.keys():
		error_dict[exception_class] = []

	error_dict[exception_class].append(error_entry)

	# grap the time section of the error entry
	timestamp = error_entry.timestamp
	timestamp_split = str(timestamp).split(' ')
	statistics_key = timestamp_split[0]+' '+timestamp_split[1][0:2]
	# create entry in the statistics dict
	if statistics_key in statistics_dict.keys():
		statistics_dict[statistics_key] += 1
	else:
		statistics_dict[statistics_key]  = 1

statistics_file = open(error_report_folder + '/' + 'statistics.dat','w')
statistics_content = ''

for statistic_key in statistics_dict.keys():
	statistic_value = statistics_dict[statistic_key]
	statistics_content += "\"{0}\" {1}\n".format(statistic_key, statistic_value)

statistics_file.write(statistics_content)
statistics_file.close()

call(["gnuplot", "plotter.pl"])

print "<== pre-processing error entries finished"

# ########################
# HTML
def read_html_page_template(page_template_file):
	ins = open( page_template_file, "r" )
	page_template = ""
	for line in ins:
		page_template += line
	ins.close()
	return page_template

# Read HTML templates from file
page_template = read_html_page_template('page_template.html')
exception_listing_page_template = read_html_page_template('exception_listing_page_template.html')
exception_listing_page_table_template = read_html_page_template('exception_listing_page_table_template.html')

print "==> Generating html output ..."

def generate_html_file(file_name, page_content):

	page_generation_time = str(datetime.now())

	file_content = page_template % (page_content, page_generation_time)

	report_page = open(file_name,'w')
	report_page.write(file_content)
	report_page.close()

def generate_exception_listing_pages(error_entries, page_title, back_page_name, exception_listing_page_name_format):

	found_listing_entry_count 	= len(error_entries)
	pages_required				= math.trunc(math.ceil(found_listing_entry_count*1.0/max_listing_entry_per_page))

	print "Found {0} error entries".format(found_listing_entry_count)
	print "Pages required: {0}".format(pages_required)

	page_range = range(1, pages_required + 1)

	exception_listing_pager_content  = "<div>"
	exception_listing_pager_content += "Pages available:&nbsp;"
	for page_number in page_range:
		exception_listing_pager_content += "<a href='{0}'>{1}</a> ".format(exception_listing_page_name_format.format(page_number), page_number)
	exception_listing_pager_content += "</div>"
	

	for page_number in page_range:

		start_index = (page_number - 1) * 100
		end_index = min( (start_index + 100), found_listing_entry_count )

		exception_listing_page_table_content = ""

		for error_entry in error_entries[start_index:end_index]:

			# Message
			message_text = str(error_entry.message)
			if len(message_text) > 150:
				message_text = message_text[0:150] + " ..."

			# Cause
			if (error_entry.cause is not None and len(error_entry.cause) > 0):

				stacktrace_html_file_name = error_entry.id + '.html';
				stacktrace_html_file_path = error_report_folder + '/' + stacktrace_html_file_name;

				stack_trace_text  = "<h4>%s<h4>" % error_entry.message
				stack_trace_text += "<hr />"
				stack_trace_text += "<br />".join(error_entry.message_lines)
				generate_html_file(stacktrace_html_file_path, stack_trace_text)
		
				exception_text = "<a href='{0}'>{1}</a>".format(stacktrace_html_file_name, str(error_entry.cause))

			else:
				exception_text = ""

			exception_listing_page_table_content += exception_listing_page_table_template % (error_entry.timestamp, error_entry.loglevel, error_entry.classname, error_entry.thread, message_text, exception_text)

		exception_listing_page_content = exception_listing_page_template % (page_title, page_number, back_page_name, exception_listing_pager_content, exception_listing_page_table_content)

		exception_listing_html_file_name = exception_listing_page_name_format.format(page_number);
		exception_listing_html_file_path = error_report_folder + '/' + exception_listing_html_file_name;

		generate_html_file(exception_listing_html_file_path, exception_listing_page_content)

# Defing page names and paths
index_html_file_name = 'index.html';
index_html_file_path = error_report_folder + '/' + index_html_file_name;

exception_listing_html_file_name_pattern = "exception_listing_page_{0}.html"

exception_dashboard_html_file_name = 'exception_dashboard.html';
exception_dashboard_html_file_path = error_report_folder + '/' + exception_dashboard_html_file_name;

# Generate page content

exception_listing_page_title = "List of all exceptions"

generate_exception_listing_pages(error_entries, exception_listing_page_title, index_html_file_name, exception_listing_html_file_name_pattern)

exception_dashboard_page_content  = "<h3>Exception Dashboard</h3>"
exception_dashboard_page_content += "<a href='%s'>&lt;&lt;&lt; back</a>" % (index_html_file_name)
exception_dashboard_page_content += "<table border = '1'>"

exception_dashboard_page_content += "<thead>"
exception_dashboard_page_content += "<td>Exception class</td>"
exception_dashboard_page_content += "<td># occurances</td>"
exception_dashboard_page_content += "</thead>"

exception_dashboard_page_content += "<tbody>"

for exception_class_name in error_dict.keys():
	exception_listing_by_cause_page_title = "List of all {0}".format(exception_class_name)
	error_entries_by_cause = error_dict[exception_class_name]
	error_entries_by_cause_page_title = "List of {0} entries".format(exception_class_name)
	exception_by_cause_html_file_name_pattern = "exception_cause_listing_"+exception_class_name+"_page_{0}.html"
	generate_exception_listing_pages(error_entries_by_cause, exception_listing_by_cause_page_title, exception_dashboard_html_file_name, exception_by_cause_html_file_name_pattern)
	exception_dashboard_page_content += "<tr>"
	exception_dashboard_page_content += "<td><a href='{0}'>{1}</a></td>".format(exception_by_cause_html_file_name_pattern.format(1), exception_class_name)
	exception_dashboard_page_content += "<td>%s</td>" % len(error_dict[exception_class_name])
	exception_dashboard_page_content += "</tr>"

exception_dashboard_page_content += "</tbody>"
exception_dashboard_page_content += "</table>"

exception_dashboard_page_content += "<div stlye='margin-top: 5px; margin-bottom: 5px;'>"
exception_dashboard_page_content += "<img src='exception_dashboard.png' />"
exception_dashboard_page_content += "</div>"

generate_html_file(exception_dashboard_html_file_path, exception_dashboard_page_content)

index_page_content = ""

index_page_content += "<h2>Log folder name %s</h2>" % (logfile_folder)

index_page_content += "<h4><a href='{0}'>Exception listing</a></h4>".format(exception_listing_html_file_name_pattern.format(1))
index_page_content += "<h4><a href='{0}'>Exception dashboard</a></h4>".format(exception_dashboard_html_file_name)

generate_html_file(index_html_file_path, index_page_content)

print "<== Generating html output finished"

print "<<< log analyzer finished in {0} seconds.".format(time.time() - start_time)
