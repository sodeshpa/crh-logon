/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

/**
 * Represents a content control that might get removed depending on the
 * condition.
 */
public class ConditionTemplateTag extends TemplateTag {

	private final Condition condition;

	public ConditionTemplateTag(Path path, boolean isNegation) {
		this.condition = new Condition(path, isNegation);
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("@if");

		if (condition.isNegation()) {
			buf.append("-not ");
		}
		else {
			buf.append(" ");
		}

		buf.append(condition.getPath());
		return buf.toString();
	}
}
