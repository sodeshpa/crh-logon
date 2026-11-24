/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.DocumentValue;
import com.crh.logon.pdf.templating.ExtendedPath;
import com.crh.logon.pdf.templating.Identifier;
import com.crh.logon.pdf.templating.ParsingException;
import com.crh.logon.pdf.templating.Path;
import com.crh.logon.pdf.templating.Value;

public class KeyValueDataProvider implements DataProvider {

	HashMap<String, Value> data = new HashMap<String, Value>();

	public KeyValueDataProvider() {
	}

	public KeyValueDataProvider(String[][] data) {
		for (String[] pair : data) {
			addPathValue(pair[0], pair[1]);
		}
	}

	public KeyValueDataProvider(Properties props) {
		for (String name : props.stringPropertyNames()) {
			String value = props.getProperty(name);
			addPathValue(name, value);
		}
	}

	private void addPathValue(String key, String value) {
		try {
			ExtendedPath extended = Path.parseExtended(key);
			String path = extended.getPath().toString();
			Value deserialize = Value.Deserialize(extended.getValueType(), value);
			data.put(path, deserialize);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Value getValue(Path path) {
		path = convertNegativeIndices(path);
		String key = path.toString();
		if (!data.containsKey(key)) {
			return null;
			//return new TextValue("");
		}
		return data.get(key);
	}

	public DocumentValue getInclude(Path path) throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(path.toString());
		return DocumentValue.Load(is);
	}

	/**
	 * If the path has negative indices, use positive ones, python style.
	 */
	public Path convertNegativeIndices(Path path) {
		List<Identifier> items = new ArrayList<Identifier>();
		for (Identifier item : path.items()) {
			if (!item.hasIndex() || item.getIndex() >= 0) {
				items.add(item);
			}
			else {
				int index = item.getIndex();
				List<Identifier> subpath = new ArrayList<Identifier>(items);
				subpath.add(new Identifier(item.getKey()));
				int count = this.count(new Path(subpath));
				items.add(new Identifier(item.getKey(), count + index));
			}
		}
		return new Path(items);
	}

	@Override
	public int count(Path path) {
		int max = 0;
		for (Entry<String, Value> entry : data.entrySet()) {
			Path key = getKey(entry);
			// path: collection[0].person
			// key: collection[0].person[1].item
			if (path.isRootOf(key, false) && key.item(path.size() - 1).hasIndex()) {
				max = Math.max(key.item(path.size() - 1).getIndex() + 1, max);
			}
		}
		return max;
	}

	private Path getKey(Entry<String, Value> entry) {
		Path key;
		try {
			key = Path.parse(entry.getKey());
		}
		catch (ParsingException e) {
			// cannot happen
			throw new RuntimeException(e);
		}
		return key;
	}

	public void put(Path path, Value value) {
		data.put(path.toString(), value);
	}

	public Set<Entry<String, Value>> entries() {
		return data.entrySet();
	}

	public Value remove(Path path) {
		return data.remove(path.toString());
	}

}
