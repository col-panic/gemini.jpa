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
 *     mkeith - Gemini JPA work 
 *     ssmith - EclipseLink integration
 ******************************************************************************/
package org.eclipse.gemini.jpa.provider;

import java.util.Collection;

import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.eclipse.gemini.jpa.PUnitInfo;

/**
 * This is the interface that a provider must implement in order to
 * be able to use the Gemini classes. The extender and servicesUtil 
 * hold a reference to the impl of this interface and call it to
 * obtain any required information about the provider in order to 
 * perform work on its behalf.
 */
public interface OSGiJpaProvider {

    /** 
     * The public string that represents this provider; the one that users
     * are expected to put in the <provider> element in the persistence descriptor.
     */
    String getProviderClassName();
    
    /**
     * The JPA PersistenceProvider implementation instance that is to be invoked
     * when the EntityManagerFactory services are used.
     */
    PersistenceProvider getProviderInstance();
    
    /**
     * The bundle that contains this provider.
     */
    Bundle getBundle();
    
    /**
     * The bundle context for this provider.
     */
    BundleContext getBundleContext();

    /**
     * Called by the Gemini persistence unit extender when a persistence
     * bundle has been detected and is being assigned to this provider.
     * Fragments typically need to be created. 
     * 
     * @see PersistenceUnitExtender#generateAndInstallFragment(Bundle, Collection<PUnitInfo>)
     */
    void assignPersistenceUnitsInBundle(Bundle b, Collection<PUnitInfo> pUnits);

    /**
     * Called by the Gemini persistence unit extender when a persistence
     * bundle has been resolved and is now ready to have its EMF[Builder]
     * services registered. 
     * 
     * @see PeristenceServicesUtil.registerEMFServices(PUnitInfo)
     */
    void registerPersistenceUnits(Collection<PUnitInfo> pUnits);

    /**
     * Called by the Gemini persistence unit extender when a persistence
     * bundle is leaving the active state. It may be called whenever
     * the services for the persistence unit should be removed.
     * 
     * @see PeristenceServicesUtil.unregisterEMFService(PUnitInfo)
     * @see PeristenceServicesUtil.unregisterEMFBuilderService(PUnitInfo)
     */
    void unregisterPersistenceUnits(Collection<PUnitInfo> pUnits);
    
    /**
     * Called by the Gemini persistence unit extender when a persistence
     * bundle is being uninstalled or updated. It may be called whenever
     * the persistence unit is longer being assigned to this provider.
     */
    void unassignPersistenceUnitsInBundle(Bundle b, Collection<PUnitInfo> pUnits);
}
