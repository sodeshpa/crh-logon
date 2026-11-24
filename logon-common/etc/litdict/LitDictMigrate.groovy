
println "Migrating old LitDict property files into the merged 'global' LitDict format...\n"
encoding= "utf-8"

trs = [:]
languages=['de','en','fr','it','es','cs','sk','hu','ro','bg','hr','sr','ru','pl','az','tr','nl']

inputFilePath = args[0];
outFileName = args[1]

writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFileName)), encoding))

for (lang in languages) {

	// *** Adjust this line if your project uses different filenames *** 
	def fileName = inputFilePath + 'LitDict' + (lang == 'de' ? '' : '_' + lang) + '.properties';
	
	println("Processing ${fileName}");
	
	reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), encoding))
	raw = reader.readLine()
	count = 0
	while(raw != null) {
		def line = raw ? raw.trim() : ''
	    if (line && !line.startsWith('#')) {
	
		def key = line[0..line.indexOf("=")-1]
		
		if (!trs[key]) {
			trs[key] = [:]
		}

		trs[key][lang] = line[line.indexOf("=")+1..-1];
		
	    }
	    raw = reader.readLine()
	}
}

def prevKeyPrefix;

println("\nWriting ${outFileName} ...");

for (keyElem in trs) {

	if (keyElem.key.indexOf('.') > -1) {
		def keyPrefix = keyElem.key[0..keyElem.key.indexOf('.')-1];
		if (keyPrefix != prevKeyPrefix) {
			writer << '#####################################################\n# '<< keyPrefix.toUpperCase() << '\n#####################################################\n\n'
		}
		prevKeyPrefix = keyPrefix;
	}
	
	writer << keyElem.key << ':\n'
	for (lang in languages) {
		keyElem.value['de'] = (keyElem.value['de'] ? keyElem.value['de'] : 'FIXME!!!');
		if (keyElem.value[lang]) {
			writer << '[' << lang << ']' << keyElem.value[lang] << '\n';
		}
	}
	writer << '\n'
}

writer.flush()
writer.close()

println "\nTotal ${trs.size()} keys read from the old LitDict files."
