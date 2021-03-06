/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.tests.filter;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.osgi.tests.OSGiTestsActivator;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class BundleContextFilterTests extends FilterTests {
	public static Test suite() {
		return new TestSuite(BundleContextFilterTests.class);
	}

	public Filter createFilter(String filterString) throws InvalidSyntaxException {
		return OSGiTestsActivator.getContext().createFilter(filterString);
	}
}
