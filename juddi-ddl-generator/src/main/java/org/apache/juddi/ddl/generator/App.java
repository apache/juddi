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
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import static org.apache.http.client.methods.RequestBuilder.options;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.TargetType;

/**
 * Source:
 * http://jandrewthompson.blogspot.com/2009/10/how-to-generate-ddl-scripts-from.html
 * https://stackoverflow.com/a/33761464/1203182
 * https://stackoverflow.com/a/41894432/1203182
 *
 * @author john.thompson
 * @author Alex O'Ree
 *
 */
public class App {

        private List<Class> jpaClasses = new ArrayList<Class>();

        public App() {

        }

        //initial from the current class path
        private void initialJpsClassList(String packageName) throws Exception {
                Package pk = Package.getPackage(packageName);
                List<Class> classesForPackage = new ArrayList<Class>();
                if (pk != null) {
                        classesForPackage.addAll(getClassesForPackage(pk));
                } else {
                        classesForPackage.addAll(getClassesForPackage(packageName));
                }
                for (Class<Object> clazz : classesForPackage) {

                        jpaClasses.add(clazz);
                }
        }

        //initialize from a directory /target/classes
        private void initialJpsClassList(File dir, String packageName) throws Exception {

                List<Class> c = new ArrayList<Class>();

                processDirectory(dir, packageName, c);
                for (Class<Object> clazz : c) {

                        jpaClasses.add(clazz);
                }

        }

        /**
         * Method that actually creates the file.
         *
         * @param dbDialect to use
         */
        private void generate(org.hibernate.dialect.Dialect dialect, File outputDir) {

                StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
                ssrb.applySetting("hibernate.dialect", dialect.getClass().getCanonicalName());
                StandardServiceRegistry standardServiceRegistry = ssrb.build();

                MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
                for (Class clzz : jpaClasses) {
                        metadataSources.addAnnotatedClass(clzz);
                }

                Metadata metadata = metadataSources.buildMetadata();

                SchemaExport export = new SchemaExport();

                export.setDelimiter(";");
                export.setOutputFile(outputDir.getAbsolutePath() + File.separator + dialect.getClass().getSimpleName() + ".ddl");
                //export.execute(true, false, false, true);
                export.execute(EnumSet.of(TargetType.SCRIPT), Action.BOTH, metadata);
        }

        /**
         * @param args
         */
        public static void main(String[] args) throws Exception {
                Options opts = new Options();
                opts.addOption("path", true, "Source path");
                opts.addOption("output", true, "Output path");
                opts.addOption("package", true, "Class name prefix, defaults to org.apache.juddi.model");
                opts.addOption("help", false, "this menu");

                CommandLineParser parser = new DefaultParser();
                CommandLine cmd = parser.parse(opts, args);
                if (cmd.hasOption("help")) {
                        HelpFormatter formatter = new HelpFormatter();
                        formatter.printHelp("juddi-ddl-generator", opts);
                        return;
                }
                App gen = new App();
                String pkg = "org.apache.juddi.model";
                if (cmd.hasOption("package")) {
                        pkg = cmd.getOptionValue("package");
                }
                File src = new File("../juddi-core/target/classes/org/apache/juddi/model/");
                if (cmd.hasOption("path")) {
                        src = new File(cmd.getOptionValue("path"));
                }
                File output = new File(".");
                if (cmd.hasOption("output")) {
                        output = new File(cmd.getOptionValue("output"));
                }
                if (src.exists()) {
                        gen.initialJpsClassList(src, pkg);
                } else {
                        gen.initialJpsClassList(pkg);
                }

                Package hibernate = Package.getPackage("org.hibernate.dialect");
                if (hibernate == null) {
                        hibernate = org.hibernate.dialect.DerbyTenSevenDialect.class.getPackage();
                }
                List<Class> dialetcs = null;
                if (hibernate == null) {
                        dialetcs = getClassesForPackage("org.hibernate.dialect");
                } else {
                        dialetcs = getClassesForPackage(hibernate);
                }
                for (Class clz : dialetcs) {
                        try {
                                org.hibernate.dialect.Dialect d = (org.hibernate.dialect.Dialect) clz.newInstance();
                                gen.generate(d, output);
                        } catch (Throwable t) {
                                //System.out.println(clz.getCanonicalName() + " failed with " + t.getMessage());
                        }
                }
                System.out.println(src + " " + output + " " + pkg);
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

        private static void processDirectory(File directory, String pkgname, List<Class> classes) {
                log("Reading Directory '" + directory + "'");
                // Get the list of the files contained in the package
                if (!directory.exists()) {
                        return;
                }
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
                return getClassesForPackage(pkg.getName());
        }

        public static List<Class> getClassesForPackage(String pkgname) {
                List<Class> classes = new ArrayList<Class>();

                log(pkgname);
                String relPath = pkgname.replace('.', '/');

                // Get a File object for the package
                URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
                if (resource == null) {
                        resource = Thread.currentThread().getContextClassLoader().getResource(relPath);
                }
                if (resource == null) {
                        resource = App.class.getClassLoader().getResource(relPath);
                }
                if (resource == null) {
                        File s = new File(pkgname);
                        if (s.exists()) {
                                processDirectory(s, pkgname, classes);
                        }

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

}
