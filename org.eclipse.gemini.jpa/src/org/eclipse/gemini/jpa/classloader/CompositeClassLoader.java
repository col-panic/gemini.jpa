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
 *    ssmith - A ClassLoader that aggregates multiple ClassLoaders
 *    mkeith - Some changes to debugging code
 ******************************************************************************/  
package org.eclipse.gemini.jpa.classloader;

import static org.eclipse.gemini.jpa.GeminiUtil.debugClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CompositeClassLoader extends ClassLoader {
    private List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    
    /**
     * Create a CompositeClassLoader with two class loaders.
     * 
     * @param loader1
     * @param loader2
     */
    public CompositeClassLoader(ClassLoader loader1, ClassLoader loader2) {
        classLoaders.add(loader1);
        classLoaders.add(loader2);
    }

    /**
     * Create a CompositeClassLoader from a list of class loaders.
     * 
     * @param loaders
     */
    public CompositeClassLoader(List<ClassLoader> loaders) {
        classLoaders.addAll(loaders);
    }

    /**
     * Get the contained class loaders.
     * 
     * @return the list of the contained class loaders
     */
    public List<ClassLoader> getClassLoaders() {
        return classLoaders;
    }

    /**
     * Sets the default assertion status for this class loader to
     * <tt>false</tt> and discards any package defaults or class assertion
     * on all contained class loaders.
     * 
     * @see  ClassLoader#clearAssertionStatus()
     */
    @Override
    public synchronized void clearAssertionStatus() {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.clearAssertionStatus();
        }
    }

    /**
     * Finds the resource with the given name.  Contained class 
     * loaders are queried until one returns the requested
     * resource or <tt>null</tt> if not found. 
     * 
     * @see  ClassLoader#getResource(String)
     */
    @Override
    public URL getResource(String name) {
        for (ClassLoader classLoader : getClassLoaders()) {
            debugClassLoader("Attempting getResource(", name,") on ", classLoader.toString());
            URL resource = classLoader.getResource(name);
            if (resource != null) {
                debugClassLoader("Found resource(", name,") from ", classLoader.toString());
                return resource;
            }
        }
        return null;
    }

    /**
     * Returns an input stream for reading the specified resource.
     * Contained class loaders are queried until one returns the 
     * requested resource stream or <tt>null</tt> if not found.
     * 
     * @see  ClassLoader#getResourceAsStream(String)
     */ 
    @Override
    public InputStream getResourceAsStream(String name) {
        for (ClassLoader classLoader : getClassLoaders()) {
            debugClassLoader("Attempting getResourceAsStream(", name,") on ", classLoader.toString());
            InputStream stream = classLoader.getResourceAsStream(name);
            if (stream != null) {
                debugClassLoader("Found resource(", name,") from ", classLoader.toString());
                return stream;
            }
        }
        return null;
    }

    /**
     * Finds all the resources with the given name. Contained class 
     * loaders are queried and the results aggregated into a single
     * Enumeration.
     * 
     * @throws  IOException
     *          If I/O errors occur
     *          
     * @see  ClassLoader#getResources(String)
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<Enumeration<URL>> enumerations = new ArrayList<Enumeration<URL>>(getClassLoaders().size());
        for (ClassLoader classLoader : getClassLoaders()) {
            debugClassLoader("Attempting getResources(", name,") on ", classLoader.toString());
            Enumeration<URL> resources = classLoader.getResources(name);
            if (resources != null) {
                debugClassLoader("Found resources(", name,") from ", classLoader.toString());
                enumerations.add(resources);
            }
        }
        return new CompositeEnumeration<URL>(enumerations); 
    }

   /**
     * Loads the class with the specified <a href="#name">binary name</a>.
     * Contained class loaders are queried until one returns the 
     * requested class.
     * 
     * @see  ClassLoader#loadClass(String)
     * 
     * @throws  ClassNotFoundException
     *          If the class was not found
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader classLoader : getClassLoaders()) {
            debugClassLoader("Attempting loadClass(", name,") on ", classLoader.toString());
            try {
                Class<?> aClass = classLoader.loadClass(name);
                return aClass;
            } catch (ClassNotFoundException e) {
                debugClassLoader("ClassNotFound '", name,"' by ", classLoader.toString());                
            }            
        }
        throw new ClassNotFoundException(name);
    }

    /** 
     * Sets the desired assertion status for the named top-level class.
     * 
     * @see  ClassLoader#setClassAssertionStatus(String, boolean)
     */
    @Override
    public synchronized void setClassAssertionStatus(String className,
            boolean enabled) {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.setClassAssertionStatus(className, enabled);
        }
    }

    /**
     * Sets the default assertion status for this class loader. 
     * 
     * @see  ClassLoader#setDefaultAssertionStatus(boolean)
     */
    @Override
    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.setDefaultAssertionStatus(enabled);
        }
    }

    /**
     * Sets the package default assertion status for the named package.
     * 
     * @see  ClassLoader#setPackageAssertionStatus(String,boolean)
     */
    @Override
    public synchronized void setPackageAssertionStatus(String packageName,
            boolean enabled) {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.setPackageAssertionStatus(packageName, enabled);
        }
    }
    
    
    
}
