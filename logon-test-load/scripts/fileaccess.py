from scripts.base_config import environment

TESTDATA_BASE_DIR = './testdata/' + environment

def readTestData(filename):
	lines = [line.strip() for line in open(TESTDATA_BASE_DIR + '/' + filename)]
	return lines
