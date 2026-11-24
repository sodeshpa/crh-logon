/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.renderer;

/**
 * Used for renderer specific tasks. Should be handled in the print section
 * of the renderer.
 * 
 * @author AdNovum Informatik AG
 */
public interface CustomPrintSection {
	
	public void print(Object outputObject);

}
