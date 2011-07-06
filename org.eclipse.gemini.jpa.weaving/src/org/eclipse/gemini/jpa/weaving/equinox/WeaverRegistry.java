/*******************************************************************************
 * Copyright (c) 2010 Oracle.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution. 
 * The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 * and the Apache License v2.0 is available at 
 *     http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses.
 *
 * Contributors:
 *     ssmith, tware - EclipseLink integration
 ******************************************************************************/
 package org.eclipse.gemini.jpa.weaving.equinox;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gemini.jpa.weaving.IWeaver;
import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class WeaverRegistry implements ClassLoadingHook, ServiceTrackerCustomizer {
	private static WeaverRegistry instance = new WeaverRegistry();
	private List<ServiceReference> weaverServices = new ArrayList<ServiceReference>();
	private BundleContext ctx;
	private ServiceTracker serviceTracker;
	
	private WeaverRegistry() {}
	
	public static WeaverRegistry getInstance() {
		return instance;
	}

    public boolean addClassPathEntry(@SuppressWarnings("rawtypes") ArrayList cpEntries, String cp,
			ClasspathManager hostmanager, BaseData sourcedata,
			ProtectionDomain sourcedomain) {
		return false;
	}

	public BaseClassLoader createClassLoader(ClassLoader parent,
			ClassLoaderDelegate delegate, BundleProtectionDomain domain,
			BaseData data, String[] bundleclasspath) {
		return null;
	}

	public String findLibrary(BaseData data, String libName) {
		return null;
	}

	public ClassLoader getBundleClassLoaderParent() {
		return null;
	}

	public void initializedClassLoader(BaseClassLoader baseClassLoader,
			BaseData data) {		
	}

	public byte[] processClass(String name, byte[] classbytes,
			ClasspathEntry classpathEntry, BundleEntry entry,
			ClasspathManager manager) {
		if (this.weaverServices.isEmpty()) {
			return null;
		}
		for (Iterator<ServiceReference> iterator = this.weaverServices.iterator(); iterator.hasNext();) {
			ServiceReference reference = iterator.next();
			IWeaver weaver = (IWeaver)ctx.getService(reference);
			if (weaver != null) {
				BaseData baseData = manager.getBaseData();
				byte[] transformedBytes = weaver.transform(name, baseData.getSymbolicName(), baseData.getVersion(), classbytes);
				if (transformedBytes != null) {
					return transformedBytes;
				}
			}
		}
		return null;
	}

	public void start(BundleContext context) {
		this.ctx = context;
		serviceTracker = new ServiceTracker(context, IWeaver.class.getName(), this);
		serviceTracker.open();
	}

	public void stop(BundleContext context) {
		// Close the service tracker
		serviceTracker.close();
		serviceTracker = null;
		weaverServices = new ArrayList<ServiceReference>();
	}
	
	public Object addingService(ServiceReference reference) {
        if (System.getProperty("GEMINI_DEBUG_XML") != null) {
        	System.out.println("Registering Service " + reference);
        }
		this.weaverServices.add(reference);
		return reference;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// Rogue provider -- we don't support modifying provider services
		removedService(reference, service);
	}

	public void removedService(ServiceReference reference, Object service) {
		this.weaverServices.remove(reference);				
	}

}
