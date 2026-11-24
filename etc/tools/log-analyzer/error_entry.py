import uuid
import re

class ErrorEntry:
	def __init__(self, timestamp, loglevel, classname, thread, message):
		self.id = str(uuid.uuid4())
		self.timestamp = timestamp
		self.loglevel = loglevel
		self.classname = classname
		self.thread = thread
		self.message = message
		self.cause = None
		self.message_lines = []

	def add_message_line(self, message_line):
		self.message_lines.append(message_line)

	def find_cause(self):
		for message_line in reversed(self.message_lines):
			if "Exception" in message_line:
				self.cause = re.search("(([A-Z]|[a-z])*?Exception)", message_line).group()
				return

	def __str__( self ):
		return ""
