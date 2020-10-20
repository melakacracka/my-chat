package com.mindlinksoft.recruitment.mychat;

import com.google.gson.*;




import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.UnmatchedArgumentException;

import java.io.*;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a conversation exporter that can read a conversation and write it out in JSON.
 */
public class ConversationExporter {

    /**
     * The application entry point.
     * @param args The command line arguments.
     * @throws Exception Thrown when something bad happens.
     */
    public static void main(String[] args) throws Exception {
        // We use picocli to parse the command line - see https://picocli.info/
        ConversationExporterConfiguration configuration = new ConversationExporterConfiguration();
        CommandLine cmd = new CommandLine(configuration);

        try {
            ParseResult parseResult = cmd.parseArgs(args);
        
            if (parseResult.isUsageHelpRequested()) {
                cmd.usage(cmd.getOut());
                System.exit(cmd.getCommandSpec().exitCodeOnUsageHelp());
                return;
            }
            
            if (parseResult.isVersionHelpRequested()) {
                cmd.printVersionHelp(cmd.getOut());
                System.exit(cmd.getCommandSpec().exitCodeOnVersionHelp());
                return;
            }

            ConversationExporter exporter = new ConversationExporter();

            exporter.exportConversation(configuration.inputFilePath, configuration.outputFilePath,configuration.filterByUser,configuration.filterByKeyword,configuration.blacklistedWord,configuration.report);

            System.exit(cmd.getCommandSpec().exitCodeOnSuccess());
        } catch (ParameterException ex) {
            cmd.getErr().println(ex.getMessage());
            if (!UnmatchedArgumentException.printSuggestions(ex, cmd.getErr())) {
                ex.getCommandLine().usage(cmd.getErr());
            }

            System.exit(cmd.getCommandSpec().exitCodeOnInvalidInput());
        } catch (Exception ex) {
            ex.printStackTrace(cmd.getErr());
            System.exit(cmd.getCommandSpec().exitCodeOnExecutionException());
        }
    }

    /**
     * Exports the conversation at {@code inputFilePath} as JSON to {@code outputFilePath}.
     * @param inputFilePath The input file path.
     * @param outputFilePath The output file path.
     * @throws Exception Thrown when something bad happens.
     */
    public void exportConversation(String inputFilePath, String outputFilePath,String filterByUser,String filterByKeyword,String[] blacklistedWords,boolean report) throws Exception {
    	Conversation conversation = this.readConversation(inputFilePath,filterByUser,filterByKeyword,blacklistedWords,report);
    	
        this.writeConversation(conversation, outputFilePath);

        
        System.out.println("Conversation exported from '" + inputFilePath + "' to '" + outputFilePath);
    }

    /**
     * Helper method to write the given {@code conversation} as JSON to the given {@code outputFilePath}.
     * @param conversation The conversation to write.
     * @param outputFilePath The file path where the conversation should be written.
     * @throws Exception Thrown when something bad happens.
     */
    public void writeConversation(Conversation conversation, String outputFilePath) throws Exception {
        // TODO: Do we need both to be resources, or will buffered writer close the stream?
        try (OutputStream os = new FileOutputStream(outputFilePath, true)) {
        	
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            // TODO: Maybe reuse this? Make it more testable...
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Instant.class, new InstantSerializer());

            Gson g = gsonBuilder.create();

            bw.write(g.toJson(conversation));
            bw.close();
        } catch (FileNotFoundException e) {
            // TODO: Maybe include more information?
            throw new FileNotFoundException("Caught FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            // TODO: Should probably throw different exception to be more meaningful :/
            throw new IOException("Caught IOException: " + e.getMessage());
        }
    }

    /**
     * Represents a helper to read a conversation from the given {@code inputFilePath}.
     * @param inputFilePath The path to the input file.
     * @return The {@link Conversation} representing by the input file.
     * @throws Exception Thrown when something bad happens.
     */
    
    
    /* the function below is used for the "filterByKeyword", it works by going from
     * the third element in an array onwards because we have seen the format of the 
     * input chat and the messages start from the third element. If it finds the word
     * in that sentence it returns true, meaning that sentence will be rightfully included
     * in the output
     */
    public boolean keywordFinder(String[] message,List<String> keywords) {
    	for(int i=2; i< message.length;i++) {
    		for(int j=0;j<keywords.size();j++) {
    			if(message[i].equals(keywords.get(j))) {
    				return true;
    			}
    		}
    	}
    	return false;
    	
    }
    
    /*the function below is used for the "blacklistedWord", it works by being used to compare each
     * word in each sentence to the list of blacklisted words, if its found it returns the boolean
     * true, which in the function readConversation results in the word being replaced with the word
     * *redacted* 
     */
    
    public ArrayList<String> punctuationAdder(String word) {
    	String[] punctuation = {"!",".",",","?","..."};
    	ArrayList<String> intermediate = new ArrayList<String>();
    	intermediate.add(word.toLowerCase());
    	intermediate.add(word.toUpperCase());
    	intermediate.add((word.toLowerCase()).substring(0,1).toUpperCase() + (word.toLowerCase()).substring(1));
    	for(int i=0; i<3;i++) {
    		for(int j=0;j<punctuation.length;j++) {
    			String extraword = intermediate.get(i) + punctuation[j];
    			intermediate.add(extraword);
    		}
    	}
    	return intermediate;
    	
    }
    
    /*The function below uses the function above as an intergral part to essentially add punctuation
     * and upper and lower case cases to account for all the possible ways the words may appear in the 
     * text, it adds all the possibilities to an arraylist and this becomes the new arraylist for
     * blacklisted words
     */
    public ArrayList<String> blacklistedWordAdder(String[] words) {
    	String[] punctuation = {"!",".",",","?","..."};
    	ArrayList<String> intermediate = new ArrayList<String>();
    	for(int i=0;i<words.length;i++) {
    		intermediate.addAll(punctuationAdder(words[i]));
    	}
    	return intermediate;
    }
    
    /*The function below is used in the readConversation method to check if any punctuation comes after
     * a blacklisted word so it can  be added back in so only the word is redacted and the punctuation stays
     * the same.
     */
    public int punctuationIndex(char[] array) {
    	for(int i=0;i<array.length;i++) {
    		if(!Character.isLetter(array[i])) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    /*The function below just checks through the list of blacklisted words to see if
     * a certain word appears in the list and returns true if that is the case, otherwise 
     * it returns false
     */
    public boolean blacklistedWordCheck(List<String> blacklisted,String word) {
    	for(int i=0;i<blacklisted.size();i++) {
    		if(word.equals(blacklisted.get(i))) {
    			return true;
    		}
    	}
    	return false;
    }
    
    
    /* If the --report argument is used this function will be used to check if a sender has
     * sent a message previously and if so adds 1 message to their count. If its the first time
     * the sender is sending a message, a new entry is added to the table under that name with the 
     * count being initialised at 1.
     */
    public void ReportAdder(List<Activity> reportTable,String name) {
    	for(int i=0;i< reportTable.size();i++) {
    		if(reportTable.get(i).sender.equals(name)) {
    			reportTable.get(i).count += 1;
    			return;
    		}
    	}
    	reportTable.add(new Activity(name,1));
    }
    
    
    public Conversation readConversation(String inputFilePath,String filterByUser,String filterByKeyword,String[] blacklistedWords,boolean report) throws Exception {
        try(FileReader reader = new FileReader(inputFilePath);
            BufferedReader r = new BufferedReader(reader)) {

            List<Message> messages = new ArrayList<Message>();
            //The arraylist of type Activity
            List<Activity> reportTable = new ArrayList<Activity>();
            
            
            List<String> keywords = new ArrayList<String>();
            
            List<String> extraBlacklistedWords = new ArrayList<String>();
            
            
            
            if(filterByKeyword != null) {
            	keywords = punctuationAdder(filterByKeyword);
            }
            if(blacklistedWords != null) {
            	extraBlacklistedWords = blacklistedWordAdder(blacklistedWords);
            }
            
            
            

            String conversationName = r.readLine();
            String line;
            while ((line = r.readLine()) != null) {
                String[] split = line.split(" ");
                int i = 2;
                
                //Checks if the 
                if(filterByUser != null && !filterByUser.equals(split[1])) {
                	continue;
                }
                if(filterByKeyword != null && !keywordFinder(split,keywords)) {
                	continue;
                }
                /*The code below solved the initial problem where by only the first word
                 * So the use of content to build the whole sentence as a string from the 
                 * array split, solved that issue and made the whole message show up in the
                 * json 
                 */
                String content = "";
                while(i < split.length - 1) {
                	//This checks if the current word is one of th eblacklisted words and then changes it to *redacted*
                	if(blacklistedWords != null && blacklistedWordCheck(extraBlacklistedWords,split[i])) {
                		char[] punctuationchecker = split[i].toCharArray();
                		int index = punctuationIndex(punctuationchecker);
                		if(!Character.isLetter(punctuationchecker[punctuationchecker.length -1])) {
                			String punctuation = "";
                			for(int j=index;j<punctuationchecker.length;j++) {
                				punctuation = punctuation + punctuationchecker[j];
                			}
                			content = content + "*redacted*" + punctuation + " ";
                		}else {
                			content = content + "*redacted* ";
                		}
                		
                	}else {
                		content = content + split[i] + " ";
                	
                	}
                	i += 1;
                	
                }
                if(blacklistedWords != null && blacklistedWordCheck(extraBlacklistedWords,split[i])) {
                	char[] punctuationchecker = split[i].toCharArray();
                	int index = punctuationIndex(punctuationchecker);
                	if(!Character.isLetter(punctuationchecker[punctuationchecker.length -1])) {
                		String punctuation = "";
            			for(int j=index;j<punctuationchecker.length;j++) {
            				punctuation = punctuation + punctuationchecker[j];
            			}
                		content = content + "*redacted*" + punctuation;
                	}else {
                		content = content + "*redacted*";
                	}
                	
                }else {
                	content = content + split[i];
                }
                
                messages.add(new Message(Instant.ofEpochSecond(Long.parseUnsignedLong(split[0])), split[1], content));
                if(report) {
                	//Checks if report is true implying it was a command line argument and adds to
                	//report table created above
                	ReportAdder(reportTable,split[1]);
                	
                }
            }
            
            if(report) {
            	/*This sorts the report table in descending order and uses the 
            	 * other constructor to create the conversation
            	 */
            	Collections.sort(reportTable);
            	return new Conversation(conversationName,messages,reportTable);
            }

            return new Conversation(conversationName, messages);
        } catch (FileNotFoundException e) {
        	throw new FileNotFoundException("Caught FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
        	throw new IOException("Caught IOException: " + e.getMessage());
        }
    }

    class InstantSerializer implements JsonSerializer<Instant> {
        @Override
        public JsonElement serialize(Instant instant, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(instant.getEpochSecond());
        }
    }
}
