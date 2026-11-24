println "Generating LitDict property files from master LitDict file ${args[0]} ...\n"

languages = [:]
byKey = [:]

buffer = []
msgCode = null
lang = null
lineNo = 0
encoding= "utf-8"

def reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0])), encoding))
def raw = reader.readLine()
while(raw != null) {
	def line = raw ? raw.trim() : ''
	lineNo++
	if (line.startsWith ('#')) {
		// Ignore,
	}
	else if(line.startsWith("[") && msgCode) {
		resetBufferAndLangOnly()
		lang = line[1..(line.indexOf("]") - 1)]
		buffer << line[(line.indexOf("]") + 1)..-1]
	}
	else if (line && line[line.length()-1] == '\\') {
		buffer << line
	}
	else if(line.endsWith(":")) {
		reset()
		msgCode = line[0..(line.indexOf(":") - 1)]?.trim()
	}
	else if (line) {
		// a valid row must be one of the following: comment (starts with '#'), key (ends with ':'), language entry (starts with '['), or empty row  
		throw new IllegalStateException("Invalid row in LitDict.global in row $lineNo: $line");
	}
	raw = reader.readLine()
}

reset()


fileNameBase = args[0]
now = new Date();


if (args.length > 1) {
	fileNameBase = args[1]
}
languages.each { language, codes ->
	def propFileName = fileNameBase + "_" + language + ".properties"
	def jsFileName = fileNameBase + "_" + language + ".js"

	print "Creating ${propFileName} and ${jsFileName} ..."

	def propFile = new File(propFileName)
	propFile.parentFile.mkdirs()
	propFile.createNewFile()
	def propWriter = propFile.newPrintWriter(encoding)
	// write the header of the file
	propWriter << "#\n"
	propWriter << "# !!! WARNING: THIS FILE IS GENERATED - DO NOT EDIT !!!\n"
	propWriter << "#\n"
	propWriter << "# generated at: ${now.dateString}\n";
	propWriter << "# input: ${args[0]}\n"
	propWriter << "#\n"

	def jsFile = new File(jsFileName)
	jsFile.createNewFile()
	def jsWriter = jsFile.newPrintWriter("utf-8")
	// write the header of the file
	jsWriter <<
'''
const LitDict = {};
'''


	codes.each { msgKey, msgValue ->
		if (msgValue.count('"') % 2 == 1) {
			throw new IllegalStateException("Invalid LitDict entry: odd number of double quotes in: $msgKey=$msgValue");
		}
		def message = processMsg(msgValue)
		propWriter << "$msgKey=$message\n"
		jsWriter << "LitDict['$msgKey']='${jsProcessMessage(message)}';\n"
	}

jsWriter <<
'''
LitDict.fmt = function(key, ...params) {
	let result = this[key];
	if (!!result && params.length > 0) {
		result = result.replace(/\\{\\d+\\}/g, (matched) => params[matched.substring(1, matched.length-1)]);
	}
	return result;
}
Object.freeze(LitDict);
'''

	propWriter.close()
	jsWriter.close()

	println " done"
}

// transform and output tab-delimited csv file (txt is used as an extension since Excel can open it more easily)  
// 
println "Creating ${fileNameBase}.txt ..."
new File("${fileNameBase}.txt").withPrintWriter(encoding) { out ->
	out << "#\n"
	out << "# Generated at: ${now}\n";
	out << "#\n";
	out << "# How to edit:\n";
	out << "# - 'Open with ...' in Windows, and select 'Excel' (recognizes it as TAB-delimited)\n";
	out << "# - To highlight empty cells to be translated: Selected whole worksheet, 'Find/Replace' for (empty cells), in whole cell, replace with Format = yellow background (or so)\n";
	out << "# - Save As Text / Tab-delimited again.\n";
	out << "#\n";
	out << "#Key\tGerman\t\tEnglish\t\tFrench\t\tItalian\t\tSpanish\t\tCzech\t\tSlovak\t\tHungarian\t\tRomanian\t\tBulgarian" +
			"\t\tCroatian\t\tSerbian\t\tRussian\t\tPolish\t\tAzerbaijani\t\tTurkish\t\tNetherlands\t\t\n";

	byKey.each { code, langs ->
		out << "${code}" << "\t"
		msg = processMsgWithNotes(langs.de, langs.en);
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.en, langs.de);
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.fr, langs.en);
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.it, langs.en);
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.es, langs.en);
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.cs, langs.en)
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.sk, langs.en)
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.hu, langs.en)
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.ro, langs.en)
		out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.bg, langs.en)
		out << msg.text << "\t" << msg.notes << "\t"
        msg = processMsgWithNotes(langs.hr, langs.en)
        out << msg.text << "\t" << msg.notes << "\t"
        msg = processMsgWithNotes(langs.sr, langs.en)
        out << msg.text << "\t" << msg.notes << "\t"
        msg = processMsgWithNotes(langs.ru, langs.en)
        out << msg.text << "\t" << msg.notes << "\t"
        msg = processMsgWithNotes(langs.pl, langs.en)
        out << msg.text << "\t" << msg.notes << "\t"
        msg = processMsgWithNotes(langs.az, langs.en)
        out << msg.text << "\t" << msg.notes << "\t"
        msg = processMsgWithNotes(langs.tr, langs.en)
        out << msg.text << "\t" << msg.notes << "\t"
		msg = processMsgWithNotes(langs.nl, langs.en)
		out << msg.text << "\t" << msg.notes << "\n"
	}
}

def processMsg(msg) {
	if (msg.startsWith('[')) {
		return msg[msg.indexOf(']')+1..-1];
	}
	return msg;
}

def jsProcessMessage(message) {
	return message.replaceAll('\'', '\\\\\'')
}

def processMsgWithNotes(msg, defaultMsg) {
    res = {}
    res.text = '';
    res.notes = '';
	message = msg;
	if (!msg) {
		/*if no note found for the language add the default
		english/german note once more and a translate annotation*/
		message = defaultMsg;
	}
    if (message.startsWith('[')) {
        res.notes = message[1..message.indexOf(']')-1];
        if (res.notes.size() + 2 < message.size()) {
            res.text = message[message.indexOf(']')+1..-1];
        }
    }
    else {
        res.text = message;
        res.notes = 'Done';
    }
	if (!msg) {
		res.notes = 'TODO Translate';
	}
    return res;
}

def reset () {
	if (lang && buffer && msgCode) {
		if (!languages[lang]) {
			 languages[lang] = [:]
		}
		if (!byKey[msgCode]) {
			byKey[msgCode] = [:]
		}
		languages[lang][msgCode] = buffer.join('\n')
		byKey[msgCode][lang] = buffer.join('\n')
	    msgCode = null
		buffer.clear()
	    lang = null
	}
}

def resetBufferAndLangOnly() {
	if (lang && buffer && msgCode) {
		if (!languages[lang]) {
			 languages[lang] = [:]
		}
		if (!byKey[msgCode]) {
			byKey[msgCode] = [:]
		}
		languages[lang][msgCode] = buffer.join('\n')
		byKey[msgCode][lang] = buffer.join('\n')
		buffer.clear()
	    lang = null
	}
}
