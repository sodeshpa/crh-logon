/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.objects;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.pdftool.layout.PdfBorderData;
import com.crh.logon.pdftool.layout.PdfBorderLayout;

/**
 * Basic implementation of {@link PdfObject}, which provides all the necessary
 * functions for a basic UI element. The {@link AbstractPdfObject#children} list
 * has overwritten functions to support parent-child consistency. Hence, the
 * setParent functions should not be called.
 * 
 * @author AdNovum Informatik AG
 */
public abstract class AbstractPdfObject implements PdfObject {
	
	private PdfObject parent = null;
	
	private PdfBorderLayout layout = null;
	
	private PdfBorderData layoutData = null;
	
	private int width;
	private int height;
	private double plannedWidth = -1;
	
	private List<PdfObject> children = new ArrayList<PdfObject>() {
		public boolean add(PdfObject element) {
			element.setParent(AbstractPdfObject.this);
			return super.add(element);
		};
		
		public void add(int index, PdfObject element) {
			element.setParent(AbstractPdfObject.this);
			super.add(index, element);
		};
		
		public boolean addAll(java.util.Collection<? extends PdfObject> c) {
			for (PdfObject element : c) {
				element.setParent(AbstractPdfObject.this);
			}
			return super.addAll(c);
		};
		
		public boolean addAll(int index, java.util.Collection<? extends PdfObject> c) {
			for (PdfObject element : c) {
				element.setParent(AbstractPdfObject.this);
			}
			return super.addAll(index, c);
		};
		
		public PdfObject remove(int index) {
			PdfObject removed = super.remove(index);
			removed.setParent(null);
			return removed;
		};
		
		public boolean remove(Object o) {
			boolean removed = super.remove(o);
			if (o instanceof PdfObject && removed) {
				((PdfObject)o).setParent(null);
			}
			return removed;
		};
		
		public boolean removeAll(java.util.Collection<?> c) {
			throw new UnsupportedOperationException("Not supported");
		};
	};
	
	/**
	 * 
	 */
	public AbstractPdfObject() {
		this(-1,-1);
	}
	
	/**
	 * Default constructor. Creates the layoutData for this object
	 * with the default constructor. See {@link PdfBorderData#PdfBorderData()}
	 */
	public AbstractPdfObject(int width, int height) {
		this(new PdfBorderData(), width, height);
	}
	
	/**
	 * New object with the input layoutData
	 * @param layoutData
	 */
	public AbstractPdfObject(PdfBorderData layoutData, int width, int height) {
		this.width = width;
		this.height = height;
		this.layoutData = layoutData;
	}
	
	public PdfObject getParent() {
		return parent;
	}
	
	/**
	 * WARNING: This call doesn't add the object into the children array;
	 * use parent.getChildren().add instead, it sets the parent as well 
	 */
	public void setParent(PdfObject parent) {
		this.parent = parent;
	}
	
	public List<PdfObject> getChildren() {
		return children;
	}
	
	public PdfBorderLayout getLayout() {
		return layout;
	}
	
	public void setLayout(PdfBorderLayout layout) {
		this.layout = layout;
	}
	
	public PdfBorderData getLayoutData() {
		return layoutData;
	}
	
	public void setLayoutData(PdfBorderData data) {
		this.layoutData = data;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the plannedWidth
	 */
	public double getPlannedWidth() {
		return plannedWidth;
	}

	/**
	 * @param plannedWidth the plannedWidth to set
	 */
	public void setPlannedWidth(double plannedWidth) {
		this.plannedWidth = plannedWidth;
	}
	
}
