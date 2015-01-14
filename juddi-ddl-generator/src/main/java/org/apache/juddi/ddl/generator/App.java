package org.apache.juddi.ddl.generator;
/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Source: http://jandrewthompson.blogspot.com/2009/10/how-to-generate-ddl-scripts-from.html
 * @author john.thompson
 *
 */
public class App {

        private AnnotationConfiguration cfg;

        public App(String packageName) throws Exception {
                cfg = new AnnotationConfiguration();
                cfg.setProperty("hibernate.hbm2ddl.auto", "create");
                List<Class> classesForPackage = getClassesForPackage(org.apache.juddi.model.Address.class.getPackage());
                for (Class<Object> clazz : classesForPackage) {
                        cfg.addAnnotatedClass(clazz);
                }
        }
        
         public App(String dir,String packageName) throws Exception {
                cfg = new AnnotationConfiguration();
                cfg.setProperty("hibernate.hbm2ddl.auto", "create");
                List<Class> c = new ArrayList<Class>();
                processDirectory(new File("../" + dir), packageName,c);
                
                processDirectory(new File(dir), packageName,c);
                for (Class<Object> clazz : c) {
                        cfg.addAnnotatedClass(clazz);
                }
                
                
        }

        /**
         * Method that actually creates the file.
         *
         * @param dbDialect to use
         */
        private void generate(Dialect dialect) {
                cfg.setProperty("hibernate.dialect", dialect.getDialectClass());

                SchemaExport export = new SchemaExport(cfg);
                export.setDelimiter(";");
                export.setOutputFile(dialect.name().toLowerCase() + ".ddl");
                export.execute(true, false, false, true);
        }

        /**
         * @param args
         */
        public static void main(String[] args) throws Exception {
                App gen = null;
                if (args != null && args.length == 1) {
                        gen = new App(args[0],"org.apache.juddi.model");
                } else {
                        gen = new App("org.apache.juddi.model");
                }
                for (int i = 0; i < Dialect.values().length; i++) {
                        gen.generate(Dialect.values()[i]);
                }
        }

        private static void log(String msg) {
                System.out.println("ClassDiscovery: " + msg);
        }

        private static Class<?> loadClass(String className) {
                try {
                        return Class.forName(className);
                } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
                }
        }

        private static void processDirectory(File directory, String pkgname,List<Class> classes) {
                log("Reading Directory '" + directory + "'");
                // Get the list of the files contained in the package
                if (!directory.exists())
                        return;
                String[] files = directory.list();
                for (int i = 0; i < files.length; i++) {
                        String fileName = files[i];
                        String className = null;
                        // we are only interested in .class files
                        if (fileName.endsWith(".class")) {
                                // removes the .class extension
                                className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);
                        }
                        log("FileName '" + fileName + "'  =>  class '" + className + "'");
                        if (className != null) {
                                classes.add(loadClass(className));
                        }
                        File subdir = new File(directory, fileName);
                        if (subdir.isDirectory()) {
                                processDirectory(subdir, pkgname + '.' + fileName, classes);
                        }
                }
        }

        private static void processJarfile(URL resource, String pkgname, List<Class> classes) {
                String relPath = pkgname.replace('.', '/');
                String resPath = resource.getPath();
                String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
                log("Reading JAR file: '" + jarPath + "'");
                JarFile jarFile;
                try {
                        jarFile = new JarFile(jarPath);
                } catch (IOException e) {
                        throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
                }
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        String className = null;
                        if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                        }
                        log("JarEntry '" + entryName + "'  =>  class '" + className + "'");
                        if (className != null) {
                                classes.add(loadClass(className));
                        }
                }
        }

        public static List<Class> getClassesForPackage(Package pkg) {
                List<Class> classes = new ArrayList<Class>();

                String pkgname = pkg.getName();
                log(pkgname);
                log(pkg.getName());
                String relPath = pkgname.replace('.', '/');

                // Get a File object for the package
                URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
                if (resource == null) {
                        processDirectory(new File(pkgname), pkgname, classes);
                        throw new RuntimeException("Unexpected problem: No resource for " + relPath);
                }
                log("Package: '" + pkgname + "' becomes Resource: '" + resource.toString() + "'");

                resource.getPath();
                if (resource.toString().startsWith("jar:")) {
                        processJarfile(resource, pkgname, classes);
                } else {
                        processDirectory(new File(resource.getPath()), pkgname, classes);
                }

                return classes;
        }

        /**
         * Utility method used to fetch Class list based on a package name.
         *
         * @param packageName (should be the package containing your annotated
         * beans.
         */
        private List<Class> getClasses(String packageName) throws Exception {
                List classes = new ArrayList();
                File directory = null;

                ClassLoader cld = Thread.currentThread().getContextClassLoader();
                cld = org.apache.juddi.model.Address.class.getClassLoader();

                if (cld == null) {
                        throw new ClassNotFoundException("Can't get class loader.");
                }

                String path = packageName.replace('.', '/');

                //file:/C:/juddi/trunk/juddi-core/target/classes/org/apache/juddi/model
                System.out.println(path);
                Enumeration resources = cld.getResources(path);

                while (resources.hasMoreElements()) {
                        URL resource = (URL) resources.nextElement();
                        System.out.println(resource.toExternalForm());
                        try {
                                directory = new File(resource.toURI().getPath());
                        } catch (NullPointerException x) {
                                throw new ClassNotFoundException(packageName + " (" + directory
                                     + ") does not appear to be a valid package");
                        }
                        if (directory.exists()) {
                                String[] files = directory.list();
                                for (int i = 0; i < files.length; i++) {
                                        if (files[i].endsWith(".class")) {
// removes the .class extension
                                                classes.add(Class.forName(packageName + '.'
                                                     + files[i].substring(0, files[i].length() - 6)));
                                        }
                                }
                        }
                }
                if (classes.isEmpty()) {
                        System.err.println("No classes could be loaded.");
                        System.exit(1);

                }
                return classes;
        }

        /**
         * Holds the classnames of hibernate dialects for easy reference.
         */
        private static enum Dialect {

                ORACLE("org.hibernate.dialect.Oracle10gDialect"),
                MYSQL("org.hibernate.dialect.MySQLDialect"),
                HSQL("org.hibernate.dialect.HSQLDialect"),
                POSTGRES("org.hibernate.dialect.PostgreSQLDialect"),
                MYSQL5("org.hibernate.dialect.MySQL5Dialect"),
                DB2("org.hibernate.dialect.DB2Dialect"),
                Derby("org.hibernate.dialect.DerbyDialect"),
                MySQLInnoDB("org.hibernate.dialect.MySQLInnoDBDialect"),
                Oracle9i("org.hibernate.dialect.Oracle9iDialect"),
                Sybase("org.hibernate.dialect.SybaseDialect"),
                MSSQL2000("org.hibernate.dialect.SQLServerDialect");
                //   MSSQL2008("org.hibernate.dialect.SQLServer2008Dialect");

                private String dialectClass;

                private Dialect(String dialectClass) {
                        this.dialectClass = dialectClass;
                }

                public String getDialectClass() {
                        return dialectClass;
                }
        }
}
