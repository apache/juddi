/*
 * Copyright 2013 The Apache Software Foundation.
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
package org.apache.juddi.v3.migration.tool;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main entry point for the UDDI migration tool
 *
 * @author Alex O'Ree
 * @since 3.2
 *
 */
public class App {

    public static void main(String[] args) throws ParseException, Exception {
        System.out.println("This tool is used to export and import UDDI data from a UDDI v3 registry");

        Options options = new Options();
        options.addOption("import", false, "Imports data into a UDDIv3 registry");
        options.addOption("export", false, "Exports data into a UDDIv3 registry");
        options.addOption("user", true, "Username, if not defined, those is uddi.xml will be used");
        options.addOption("pass", true, "Password, if not defined, those is uddi.xml will be used");
        //options.addOption("safeMode", false, "Prevents overriding existing entities, saves items one at a time");
        options.addOption("config", true, "Use an alternate config file default is 'uddi.xml'");
        options.addOption("node", true, "The node 'name' in the config, default is 'default'");
        options.addOption("isJuddi", false, "Is this a jUDDI registry? Is so we can in/export more stuff");
        options.addOption("tmodel", true, "Im/Export for tmodels, default is 'tmodel-export.xml'");
        options.addOption("business", true, "Im/Export option, default is 'business-export.xml'");
        options.addOption("mappings", true, "Im/Export option, default is 'entityusermappings.properties'");
        options.addOption("publishers", true, "jUDDI only - In/Export option, default is 'publishers-export.xml'");
        options.addOption("myItemsOnly", false, "Export option, Only export items owned by yourself");
      //  options.addOption("preserveOwnership", false, "Im/Export option, Only export items owned by yourself");
      //  options.addOption("credFile", true, "Used with -preserveOwnership, this is a properties file mapping with user=pass");


        // create the parser
        CommandLineParser parser = new BasicParser();
        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);

            if (line.hasOption("import") && line.hasOption("export")) {
                ShowHelp(options);
                return;
            }
            if (!line.hasOption("import") && !line.hasOption("export")) {
                ShowHelp(options);
                return;
            }
            String config = line.getOptionValue("config", "uddi.xml");
            String node = line.getOptionValue("node", "default");

            String pass = line.getOptionValue("pass", null);
            String user = line.getOptionValue("user", null);

            String tmodel = line.getOptionValue("tmodel", "tmodel-export.xml");
            String business = line.getOptionValue("business", "business-export.xml");
            String publishers = line.getOptionValue("publishers", "publishers-export.xml");

            String mappings = line.getOptionValue("mappings", "entityusermappings.properties");
            boolean preserveOwnership = false;//line.hasOption("preserveOwnership");
            boolean juddi = line.hasOption("isJuddi");
            boolean myItemsOnly = line.hasOption("myItemsOnly");
            boolean safe = true;//line.hasOption("safeMode");
            if (line.hasOption("export")) //CommandLine cmd = parser.parse(options, args);
            {
                System.out.println("Exporting...");
                new Export().Execute(config, node, user, pass, tmodel, business, juddi, safe, publishers, myItemsOnly, mappings);
            }
            if (line.hasOption("import")) //CommandLine cmd = parser.parse(options, args);
            {
                System.out.println("Importing...");
                new Import().Execute(config, node, user, pass, tmodel, business, juddi, safe, publishers, preserveOwnership, mappings);
            }


        } catch (Exception exp) {
            // oops, something went wrong
            ShowHelp(options);
            System.err.println("Failed!");
            exp.printStackTrace();
        }


    }

    private static void ShowHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar juddi-migration-tool-(VERSION)-jar-with-dependencies.jar", options);
    }
}
