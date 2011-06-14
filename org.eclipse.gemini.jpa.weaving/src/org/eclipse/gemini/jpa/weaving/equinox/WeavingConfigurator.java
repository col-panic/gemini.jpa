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

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.debug.FrameworkDebugOptions;

public class WeavingConfigurator implements HookConfigurator {

    private static final String OPTION_DEBUG = "org.eclipse.gemini.jpa.weaving/debug";

    public WeavingConfigurator() {
        super();
    }

    public void addHooks(HookRegistry hookRegistry) {
        FrameworkDebugOptions dbgOptions = FrameworkDebugOptions.getDefault();
        boolean debug = dbgOptions.getBooleanOption(OPTION_DEBUG, false);
        if (debug) {
            Debug.println("Gemini JPA: Adding WeaverRegistry Class Loading Hook"); //$NON-NLS-1$
        }
        hookRegistry.addClassLoadingHook(WeaverRegistry.getInstance());
        hookRegistry.addAdaptorHook(new WeavingAdaptor());
    }
}
