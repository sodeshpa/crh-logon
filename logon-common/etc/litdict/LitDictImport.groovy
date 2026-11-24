println "Reading incoming CSV (tab-delimited txt)  file ..."
encoding= "utf-8"

dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm");
reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0])), encoding))
raw = reader.readLine()
outFileName = args[1];
backupFileName = outFileName + ".backup_at_" + dateFormat.format(new Date());

File currentLitDictFile = new File(outFileName);
if (currentLitDictFile.exists()) {
  currentLitDictFile.renameTo(new File(backupFileName));
}

langList = ['de','en','fr','it','es','cs','sk','hu','ro','bg','hr','sr','ru','pl','az','tr','nl']

// setup pre-requisites per language
prereq = [:]
dependencies = []

for (lang in langList) {
  prereq.put(lang, dependencies.clone())
  dependencies.add(lang)
}

csv = [:]
while (raw != null) {
  def line = raw ? raw.trim() : ''
  if (line) {
    def unescapedLine = line.replaceAll("\u0085", "...")
    if (unescapedLine.count('"') % 2 == 1) {
      throw new IllegalStateException("Invalid row: odd number of double quotes in: $unescapedLine");
    }
    def values = unescapedLine.tokenize("\t")
    def csvRec = [:]

    csvRec['de'] = values[1]
    csvRec['deNotes'] = values[2]
    csvRec['en'] = values[3]
    csvRec['enNotes'] = values[4]
    csvRec['fr'] = values[5]
    csvRec['frNotes'] = values[6]
    csvRec['it'] = values[7]
    csvRec['itNotes'] = values[8]
    csvRec['es'] = values[9]
    csvRec['esNotes'] = values[10]
    csvRec['cs'] = values[11]
    csvRec['csNotes'] = values[12]
    csvRec['sk'] = values[13]
    csvRec['skNotes'] = values[14]
    csvRec['hu'] = values[15]
    csvRec['huNotes'] = values[16]
    csvRec['ro'] = values[17]
    csvRec['roNotes'] = values[18]
    csvRec['bg'] = values[19]
    csvRec['bgNotes'] = values[20]
    csvRec['hr'] = values[21]
    csvRec['hrNotes'] = values[22]
    csvRec['sr'] = values[23]
    csvRec['srNotes'] = values[24]
    csvRec['ru'] = values[25]
    csvRec['ruNotes'] = values[26]
    csvRec['pl'] = values[27]
    csvRec['plNotes'] = values[28]
    csvRec['az'] = values[29]
    csvRec['azNotes'] = values[30]
    csvRec['tr'] = values[31]
    csvRec['trNotes'] = values[32]    
    csvRec['nl'] = values[33]
    csvRec['nlNotes'] = values[34]
    
    csv[values[0]] = csvRec
  }
  raw = reader.readLine()
}

def csvVal(key, lang) {
    if (csv[key] && csv[key][lang] && csv[key][lang].trim().length() > 0) {
      notes = csv[key][lang + 'Notes'];
      notesElem = '';
      if (notes && notes.trim() != '' && notes.trim() != 'Done') {
        notesElem = '[' + notes.trim() + ']';
      }
        return (notesElem + csv[key][lang].trim());
    }
    return null;
}


println "Total ${csv.size()} keys read from ${args[0]}."

println "Recording translations back to '${backupFileName}'"
println "Creating output in '${outFileName}'"

reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(backupFileName)), encoding))
writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFileName)), encoding))

prevKey = null;
keyLangBuf = [];
firstLine = true;

row = reader.readLine()

while(row != null) {
  if (row.startsWith('#') || row.trim().length() == 0) {
    writer << (firstLine ? '' : '\n') << row;
    
    if (prevKey && keyLangBuf.size() > 0) {
        lineBr = '';
        for (lang in langList) {
            if (!keyLangBuf.contains(lang) && csvVal(prevKey, lang)) {
                writer << lineBr << "[$lang]" + csvVal(prevKey, lang) + (lang == 'tr' ? '\n' : '')
            keyLangBuf.add(lang)
                lineBr = '\n';
            }
        }
    }
    keyLangBuf = [];        
  }
  else if (row.endsWith(':') && !row.startsWith('[')) {
    newKey = row[0..-2]
    if (prevKey  && keyLangBuf.size() > 0) {
         lineBr = '\n';
        for (lang in langList) {
            if (!keyLangBuf.contains(lang) && csvVal(prevKey, lang)) {
                writer << lineBr << "[$lang]" + csvVal(prevKey, lang) + (lang == 'bg' ? '\n' : '')
            keyLangBuf.add(lang)
                lineBr = '\n';
            }
        }
    }
    keyLangBuf = [];

    prevKey = newKey;
    writer << '\n' << newKey << ':'
  }
  else if (row.startsWith('[')) {
    msgLang = row[1..2];
    msgText = csvVal(prevKey, msgLang)?:row[4..-1];
    
    for (depLang in prereq[msgLang]) {
      if (!keyLangBuf.contains(depLang) && csvVal(prevKey, depLang)) {
          depText = csvVal(prevKey, depLang);
          writer << '\n' << "[$depLang]$depText"
      }
      keyLangBuf.add(depLang)
    }
    keyLangBuf.add(msgLang)
    writer << '\n' << "[$msgLang]$msgText"
  }
  row = reader.readLine()
  firstLine = false;
}

writer.flush()
writer.close()
