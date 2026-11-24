package com.crh.logon.pdf.templating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crh.logon.pdf.util.Pair;
import com.crh.logon.utils.StringUtils;

import com.crh.logon.pdf.util.CollectionTool;

/**
 * Represents an expression that is used to reference a value, eg
 * 'object.property.item[4]'. Immutable.
 */
public class Path implements Term {
	// expected: "(type)path.goes.here"
	private static final Pattern extendedPattern = Pattern.compile("\\(([^)]+)\\)(.*)");
	
	private final List<Identifier> path;
	
	public static ExtendedPath parseExtended(String extendedPath) throws ParsingException {
		Matcher matcher = extendedPattern.matcher(extendedPath);
		
		if (matcher.matches()) {
			// path contains type definition, add
			return new ExtendedPath(parse(matcher.group(2)), matcher.group(1)); 
		}
		// fall back to normal path, "string" is the default type.
		return new ExtendedPath(parse(extendedPath), TextValue.Type);
	}
	
	public static Path parse(String path) throws ParsingException {
		ArrayList<Identifier> items = new ArrayList<Identifier>();

		if (path.equals("")) {
			return new Path(items);
		}

		String[] split = path.split("\\.");

		for (String part : split) {
			items.add(Identifier.parse(part));
		}
		return new Path(items);
	}

	public Path(List<Identifier> identifiers) {
		this.path = identifiers;
	}

	public int size() {
		return path.size();
	}

	/**
	 * Returns a read-only copy of the list' items.
	 */
	public List<Identifier> items() {
		return Collections.unmodifiableList(path);
	}

	public Identifier item(int index) {
		return path.get(index);
	}

	public Identifier lastItem() {
		return path.get(path.size() - 1);
	}

	@Override
	public Value getValue(DataProvider dataProvider) {
		Value value = dataProvider.getValue(this);
		return value;
	}

	@Override
	public String toString() {
		return StringUtils.join(this.path, ".");
	}

	@Override
	public Path replacePath(Path findPath, Path replacementPath) {
		Collection<Pair<Identifier, Identifier>> zip = CollectionTool.zipMax(
				path, findPath.path);

		for (Pair<?, ?> pair : zip) {
			if (pair.getRight() == null) {
				// all items of 'findPath' have been matched.
				ArrayList<Identifier> items = new ArrayList<Identifier>();
				items.addAll(replacementPath.path);
				items.addAll(path.subList(findPath.path.size(), path.size()));
				return new Path(items);
			}
			if (pair.getLeft() == null) {
				// not all items of have been matched
				return this;
			}
			if (!pair.getLeft().equals(pair.getRight())) {
				// not all items of findPath and this path match.
				return this;
			}
		}

		// all items matched.
		return replacementPath;
	}

	public boolean isRootOf(Path other, boolean checkLastIndex) {
		Collection<Pair<Identifier, Identifier>> zip = CollectionTool.zipMax(
				path, other.path);

		if (other.size() < size()) {
			// root cannot be larger than compared-to path.
			return false;
		}

		int index = 0;
		for (Pair<Identifier, Identifier> pair : zip) {
			index++;
			Identifier left = pair.getLeft();
			Identifier right = pair.getRight();
			
			if (left == null) {
				// all items of the root have been matched.
				return true;
			}
			
			// we are verifying the index of the last item or not
			boolean verifyIndex = checkLastIndex || size() != index; 			

			if (verifyIndex && !left.equals(right)) {
				// items are not equal 
				return false;
			}
			
			if (!verifyIndex && !left.getKey().equals(right.getKey())){
				// item keys do not match 
				return false;
			}
		}

		// all items of root match.
		return true;
	}

	/**
	 * Returns the first n elements of a path.
	 */
	public Path subPath(int size) {
		return new Path(path.subList(0, size));
	}
}
