/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.runtime.adaptor;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;

public class ContextFinder extends ClassLoader {
	 
	private static final class Finder extends SecurityManager {
		public Class[] getClassContext() {
			return super.getClassContext();
		}
	}

	static final Finder contextFinder = (Finder) AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
			return new Finder();	
		}
	});

	public ContextFinder(ClassLoader contextClassLoader) {
		super(contextClassLoader);
	}

	// Return the first classloader of the stack that is neither the ContextFinder classloader nor the boot classloader.
	// We assume that the bootclassloader never uses the context classloader to find classes in itself
	ClassLoader basicFindClassLoader() {
		Class[] stack = contextFinder.getClassContext();
		for (int i = 1; i < stack.length; i++) {
			if (stack[i] != ContextFinder.class && stack[i].getClassLoader() == null)  
				return stack[i].getClassLoader();
		}
		return null;
	}
	
	private ClassLoader findClassLoader() {
		if (System.getSecurityManager() == null) {
			return basicFindClassLoader();
		}
		
		return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return basicFindClassLoader();
			}
		});
	}

	protected synchronized Class loadClass(String arg0, boolean arg1) throws ClassNotFoundException {
		Class result = null;
		try {
			result = super.loadClass(arg0, arg1);
		} catch (ClassNotFoundException e) {
			//Ignore
		}
		if (result == null) {
			ClassLoader toConsult = findClassLoader();
			if (toConsult != null && toConsult != getParent())
				result = findClassLoader().loadClass(arg0);
		}
		if (result == null)
			throw new ClassNotFoundException(arg0);
		return result;
	}

	protected URL findResource(String arg0) {
		URL result = super.findResource(arg0);
		if (result == null) {
			ClassLoader toConsult = findClassLoader();
			if (toConsult != null && toConsult != getParent())
				result = findClassLoader().getResource(arg0);
		}
		return result;
	}

	protected Enumeration findResources(String arg0) {
		try {
			Enumeration result = super.findResources(arg0);
			if (result == null) {
				ClassLoader toConsult = findClassLoader();
				if (toConsult != null && toConsult != getParent())
					result = toConsult.getResources(arg0);
			}
			return result;
		} catch (IOException e) {
			return null;
		}
	}
}