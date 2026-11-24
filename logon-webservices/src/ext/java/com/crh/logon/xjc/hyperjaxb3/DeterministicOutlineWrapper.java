/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.xjc.hyperjaxb3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jvnet.hyperjaxb3.ejb.plugin.EjbPlugin;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.ElementOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;
import com.sun.tools.xjc.util.CodeModelClassFactory;

/**
 * @author AdNovum Informatik AG
 * 
 * This class wraps the xjc implementation of the Outline class (BeanGenerator).
 * It only sorts the various collections returned by the original implementation.
 * 
 */
final class DeterministicOutlineWrapper implements Outline {

	/**
	 * this inner class is to compare ClassOutline, to enable deterministic sorting
	 */
	private final static class ClassOutlineComparator implements Comparator<ClassOutline> {

		public int compare(ClassOutline o1, ClassOutline o2) {
			return o1.target.fullName().compareTo(o2.target.fullName());
		}
	}

	/**
	 * this inner class is to compare EnumOutline, to enable deterministic sorting
	 */
	private final static class EnumOutlineComparator implements Comparator<EnumOutline> {

		public int compare(EnumOutline o1, EnumOutline o2) {
			return o1.target.fullName().compareTo(o2.target.fullName());
		}
	}
	
	// our comparators
	private final static ClassOutlineComparator classOutlineComparator;
	private final static EnumOutlineComparator enumOutlineComparator;
	
	static {
		classOutlineComparator = new ClassOutlineComparator();
		enumOutlineComparator = new EnumOutlineComparator();				
	}

	/**
	 * the original implementation to wrap
	 */
	private final Outline outline;
	
	/** 
	 * @see org.jvnet.hyperjaxb3.ejb.plugin.EjbPlugin#generateFieldDecl
	 */
	private final Method generateFieldDecl;
	
	DeterministicOutlineWrapper(EjbPlugin ejbPLugin, Outline outline) {
		this.outline = outline;
		
		try {
			/*
			 * Hyperjaxb3 have to generate fields out of "thin air" into the existing model
			 * (of the persistent entities); like maximum database length of a field.
			 * Unfortunately this is not supported by xjc.
			 * 
			 * To circumvent this limitation the hyperjaxb3 plugin assumes that the passed 
			 * Outline interface is implemented by the (com.sun.tools.xjc.generator.bean)
			 * BeanGenerator class, and gets it's private method "generateFieldDecl" (which
			 * generates field declarations), and marks it as accessible.
			 * 
			 * However we switch the provided implementation from BeanGenerator to our class
			 * (DeterministicOutlineWrapper), and this will fail on runtime, because hyperjaxb3 
			 * will want to call the a BeanGenerator method on our DeterministicOutlineWrapper.
			 * 
			 * In order to make this work we swap the EjbPlugin's method field with our's. 
			 */
			
			// get the method field, and mark it accessible (because it is private final)
			final Field field = EjbPlugin.class.getDeclaredField("generateFieldDecl");
			field.setAccessible(true);
			
			// get the method pointing to the BeanGenerator, and store it in our instance
			generateFieldDecl = (Method) field.get(ejbPLugin);
			
			// get our method declaration (which will call the stored BeanGenerator method)
			final Method ourGenerateFieldDecl = DeterministicOutlineWrapper.class.getDeclaredMethod(
					"generateFieldDecl", new Class[] { ClassOutlineImpl.class, CPropertyInfo.class });
			// set it accessible, so EjbPlugin can access our private method
			ourGenerateFieldDecl.setAccessible(true);
			
			// swap the EjbPlugin's method with ours
			field.set(ejbPLugin, ourGenerateFieldDecl);			
			
			/*
			 * So the original (unmodified) behavior:
			 * whenever EjbPlugin wants to generate a FieldOutline it calls a BeanGenerator 
			 * private function on the provided Outline interface.
			 * 
			 * New behavior:
			 * whenever EjbPlugin wants to generate a FieldOutline it calls a DeterministicOutlineWrapper
			 * private function on the provided Outline interface, which in turn propagates the call
			 * using the original behavior (calls a BeanGenerator private function on the wrapped 
			 * Outline interface).
			 */
		} catch (Exception ex) {
			// assume we have the privileges from the SecurityManager (same as in the EjbPlugin)
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	/**
	 * This method only acts as a link to the original Outline implementation.
	 * We trick EjbPlugin to call our method, and we call the original implementation
	 * on the wrapped Outline object/
	 * 
	 * @param cc the ClassOutline to generate the filed in
	 * @param prop the field's properties
	 * @return the generated FieldOutline
	 */
	@SuppressWarnings("unused")
	private FieldOutline generateFieldDecl(ClassOutlineImpl cc, CPropertyInfo prop) {
		try {
			return ((FieldOutline)this.generateFieldDecl.invoke(outline, new Object[] { cc, prop }));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public Model getModel() {
		return outline.getModel();
	}

	public JCodeModel getCodeModel() {
		return outline.getCodeModel();
	}

	public FieldOutline getField(CPropertyInfo paramCPropertyInfo) {
		return outline.getField(paramCPropertyInfo);
	}

	public PackageOutline getPackageContext(JPackage paramJPackage) {
		return outline.getPackageContext(paramJPackage);
	}

	/**
	 * returns the collection obtained from the wrapped Outline object (after sorting)
	 */
	public Collection<? extends ClassOutline> getClasses() {
		final List<ClassOutline> list = new ArrayList<ClassOutline>(outline.getClasses());
		
		Collections.sort(list, classOutlineComparator);
		
		return list;	
	}

	public ClassOutline getClazz(CClassInfo paramCClassInfo) {
		return outline.getClazz(paramCClassInfo);
	}

	public ElementOutline getElement(CElementInfo paramCElementInfo) {
		return outline.getElement(paramCElementInfo);
	}

	public EnumOutline getEnum(CEnumLeafInfo paramCEnumLeafInfo) {
		return outline.getEnum(paramCEnumLeafInfo);
	}

	/**
	 * returns the collection obtained from the wrapped Outline object (after sorting)
	 */
	public Collection<EnumOutline> getEnums() {
		final List<EnumOutline> list = new ArrayList<EnumOutline>(outline.getEnums());
		
		Collections.sort(list, enumOutlineComparator);
		
		return list;
	}

	public Iterable<? extends PackageOutline> getAllPackageContexts() {
		return outline.getAllPackageContexts();
	}

	public CodeModelClassFactory getClassFactory() {
		return outline.getClassFactory();
	}

	public ErrorReceiver getErrorReceiver() {
		return outline.getErrorReceiver();
	}

	public JClassContainer getContainer(CClassInfoParent paramCClassInfoParent, Aspect paramAspect) {
		return outline.getContainer(paramCClassInfoParent, paramAspect);
	}

	public JType resolve(CTypeRef paramCTypeRef, Aspect paramAspect) {
		return outline.resolve(paramCTypeRef, paramAspect);
	}

	public JClass addRuntime(@SuppressWarnings("rawtypes") Class paramClass) {
		return outline.addRuntime(paramClass);
	}
}
