package com.mindlinksoft.recruitment.mychat;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

/**
 * Represents the configuration for the exporter.
 */
@Command(name = "export", mixinStandardHelpOptions = true, version = "exporter 1.0",
         description = "Exports a plain text chat log into a JSON file.")
public final class ConversationExporterConfiguration {
    /**
     * Gets the input file path.
     */
    @Option(names = { "-i", "--inputFilePath" }, description = "The path to the input chat log file.", required = true)
    public String inputFilePath;

    /**
     * Gets the output file path.
     */
    @Option(names = { "-o", "--outputFilePath" }, description = "The path to the output JSON file.", required = true)
    public String outputFilePath;
    
    @Option(names = { "--filterByUser="}, description = "The user whos messages will be filtered into the JSON file")
    public String filterByUser;
   
    @Option(names = {"--filterByKeyword="}, description = "The messages with the keyword will be filtered into the JSON file")
    public String filterByKeyword;
    
    @Option(names = {"--blacklist="}, description = "The words highlighted get redacted")
    public String[] blacklistedWord;
    
    @Option(names = {"--report"}, description = "Gives report")
    public boolean report;
}
