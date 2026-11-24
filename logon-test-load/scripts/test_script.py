# The Grinder 3.4
# HTTP script recorded by TCPProxy at Aug 18, 2011 1:42:23 PM

from net.grinder.script import Test
from net.grinder.script.Grinder import grinder
from net.grinder.plugin.http import HTTPPluginControl, HTTPRequest
from HTTPClient import NVPair
connectionDefaults = HTTPPluginControl.getConnectionDefaults()
httpUtilities = HTTPPluginControl.getHTTPUtilities()

from scripts.base_config import url0

# These definitions at the top level of the file are evaluated once,
# when the worker process is started.


class TestRunner:
  """A TestRunner instance is created for each worker thread."""

  def __call__(self):
    """This method is called for every run performed by the worker thread."""

    # Empty recording!
    print url0