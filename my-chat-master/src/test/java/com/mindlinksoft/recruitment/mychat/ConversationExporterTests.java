package com.mindlinksoft.recruitment.mychat;

import com.google.gson.*;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link ConversationExporter}.
 */
public class ConversationExporterTests {
    /**
     * Tests that exporting a conversation will export the conversation correctly.
     * @throws Exception When something bad happens.
     */
    @Test
    public void testExportingConversationExportsConversation() throws Exception {
        ConversationExporter exporter = new ConversationExporter();

        exporter.exportConversation("chat.txt", "chat.json",null,null,null,false);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, new InstantDeserializer());

        Gson g = builder.create();

        Conversation c = g.fromJson(new InputStreamReader(new FileInputStream("chat.json")), Conversation.class);

        assertEquals("My Conversation", c.name);

        assertEquals(7, c.messages.size());

        Message[] ms = new Message[c.messages.size()];
        c.messages.toArray(ms);

        assertEquals(Instant.ofEpochSecond(1448470901), ms[0].timestamp);
        assertEquals("bob", ms[0].senderId);
        assertEquals("Hello there!", ms[0].content);

        assertEquals(Instant.ofEpochSecond(1448470905), ms[1].timestamp);
        assertEquals("mike", ms[1].senderId);
        assertEquals("how are you?", ms[1].content);

        assertEquals(Instant.ofEpochSecond(1448470906), ms[2].timestamp);
        assertEquals("bob", ms[2].senderId);
        assertEquals("I'm good thanks, do you like pie?", ms[2].content);

        assertEquals(Instant.ofEpochSecond(1448470910), ms[3].timestamp);
        assertEquals("mike", ms[3].senderId);
        assertEquals("no, let me ask Angus...", ms[3].content);

        assertEquals(Instant.ofEpochSecond(1448470912), ms[4].timestamp);
        assertEquals("angus", ms[4].senderId);
        assertEquals("Hell yes! Are we buying some pie?", ms[4].content);

        assertEquals(Instant.ofEpochSecond(1448470914), ms[5].timestamp);
        assertEquals("bob", ms[5].senderId);
        assertEquals("No, just want to know if there's anybody else in the pie society...", ms[5].content);

        assertEquals(Instant.ofEpochSecond(1448470915), ms[6].timestamp);
        assertEquals("angus", ms[6].senderId);
        assertEquals("YES! I'm the head pie eater there...", ms[6].content);
    }
    
    @Test
    public void testExportingConversationExportsConversation2() throws Exception {
        ConversationExporter exporter = new ConversationExporter();

        exporter.exportConversation("chat.txt", "chat2.json","bob",null,null,false);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, new InstantDeserializer());

        Gson g = builder.create();

        Conversation c = g.fromJson(new InputStreamReader(new FileInputStream("chat2.json")), Conversation.class);

        assertEquals("My Conversation", c.name);

        assertEquals(3, c.messages.size());

        Message[] ms = new Message[c.messages.size()];
        c.messages.toArray(ms);

        assertEquals(Instant.ofEpochSecond(1448470901), ms[0].timestamp);
        assertEquals("bob", ms[0].senderId);
        assertEquals("Hello there!", ms[0].content);

        assertEquals(Instant.ofEpochSecond(1448470906), ms[1].timestamp);
        assertEquals("bob", ms[1].senderId);
        assertEquals("I'm good thanks, do you like pie?", ms[1].content);

        assertEquals(Instant.ofEpochSecond(1448470914), ms[2].timestamp);
        assertEquals("bob", ms[2].senderId);
        assertEquals("No, just want to know if there's anybody else in the pie society...", ms[2].content);

    }
    
    @Test
    public void testExportingConversationExportsConversation3() throws Exception {
        ConversationExporter exporter = new ConversationExporter();

        exporter.exportConversation("chat.txt", "chat3.json",null,"yes",null,false);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, new InstantDeserializer());

        Gson g = builder.create();

        Conversation c = g.fromJson(new InputStreamReader(new FileInputStream("chat3.json")), Conversation.class);

        assertEquals("My Conversation", c.name);

        assertEquals(2, c.messages.size());

        Message[] ms = new Message[c.messages.size()];
        c.messages.toArray(ms);

       

       
        assertEquals(Instant.ofEpochSecond(1448470912), ms[0].timestamp);
        assertEquals("angus", ms[0].senderId);
        assertEquals("Hell yes! Are we buying some pie?", ms[0].content);


        assertEquals(Instant.ofEpochSecond(1448470915), ms[1].timestamp);
        assertEquals("angus", ms[1].senderId);
        assertEquals("YES! I'm the head pie eater there...", ms[1].content);
    }    

    
    @Test
    public void testExportingConversationExportsConversation4() throws Exception {
        ConversationExporter exporter = new ConversationExporter();
        String [] blacklistedWords = {"society","yes","pie"};
        exporter.exportConversation("chat.txt", "chat4.json",null,null,blacklistedWords,false);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, new InstantDeserializer());

        Gson g = builder.create();

        Conversation c = g.fromJson(new InputStreamReader(new FileInputStream("chat4.json")), Conversation.class);

        assertEquals("My Conversation", c.name);

        assertEquals(7, c.messages.size());

        Message[] ms = new Message[c.messages.size()];
        c.messages.toArray(ms);

        assertEquals(Instant.ofEpochSecond(1448470901), ms[0].timestamp);
        assertEquals("bob", ms[0].senderId);
        assertEquals("Hello there!", ms[0].content);

        assertEquals(Instant.ofEpochSecond(1448470905), ms[1].timestamp);
        assertEquals("mike", ms[1].senderId);
        assertEquals("how are you?", ms[1].content);

        assertEquals(Instant.ofEpochSecond(1448470906), ms[2].timestamp);
        assertEquals("bob", ms[2].senderId);
        assertEquals("I'm good thanks, do you like *redacted*?", ms[2].content);

        assertEquals(Instant.ofEpochSecond(1448470910), ms[3].timestamp);
        assertEquals("mike", ms[3].senderId);
        assertEquals("no, let me ask Angus...", ms[3].content);

        assertEquals(Instant.ofEpochSecond(1448470912), ms[4].timestamp);
        assertEquals("angus", ms[4].senderId);
        assertEquals("Hell *redacted*! Are we buying some *redacted*?", ms[4].content);

        assertEquals(Instant.ofEpochSecond(1448470914), ms[5].timestamp);
        assertEquals("bob", ms[5].senderId);
        assertEquals("No, just want to know if there's anybody else in the *redacted* *redacted*...", ms[5].content);

        assertEquals(Instant.ofEpochSecond(1448470915), ms[6].timestamp);
        assertEquals("angus", ms[6].senderId);
        assertEquals("*redacted*! I'm the head *redacted* eater there...", ms[6].content);
    }

    @Test
    public void testExportingConversationExportsConversation5() throws Exception {
        ConversationExporter exporter = new ConversationExporter();

        exporter.exportConversation("chat.txt", "chat5.json",null,null,null,true);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, new InstantDeserializer());

        Gson g = builder.create();

        Conversation c = g.fromJson(new InputStreamReader(new FileInputStream("chat5.json")), Conversation.class);

        assertEquals("My Conversation", c.name);

        assertEquals(7, c.messages.size());
        assertEquals(3,c.Report.size());
        Message[] ms = new Message[c.messages.size()];
        c.messages.toArray(ms);
        Activity[] ac = new Activity[c.Report.size()];
        c.Report.toArray(ac);

        assertEquals(Instant.ofEpochSecond(1448470901), ms[0].timestamp);
        assertEquals("bob", ms[0].senderId);
        assertEquals("Hello there!", ms[0].content);

        assertEquals(Instant.ofEpochSecond(1448470905), ms[1].timestamp);
        assertEquals("mike", ms[1].senderId);
        assertEquals("how are you?", ms[1].content);

        assertEquals(Instant.ofEpochSecond(1448470906), ms[2].timestamp);
        assertEquals("bob", ms[2].senderId);
        assertEquals("I'm good thanks, do you like pie?", ms[2].content);

        assertEquals(Instant.ofEpochSecond(1448470910), ms[3].timestamp);
        assertEquals("mike", ms[3].senderId);
        assertEquals("no, let me ask Angus...", ms[3].content);

        assertEquals(Instant.ofEpochSecond(1448470912), ms[4].timestamp);
        assertEquals("angus", ms[4].senderId);
        assertEquals("Hell yes! Are we buying some pie?", ms[4].content);

        assertEquals(Instant.ofEpochSecond(1448470914), ms[5].timestamp);
        assertEquals("bob", ms[5].senderId);
        assertEquals("No, just want to know if there's anybody else in the pie society...", ms[5].content);

        assertEquals(Instant.ofEpochSecond(1448470915), ms[6].timestamp);
        assertEquals("angus", ms[6].senderId);
        assertEquals("YES! I'm the head pie eater there...", ms[6].content);
        
        assertEquals("bob",ac[0].sender);
        assertEquals(3,ac[0].count);
        
        assertEquals("mike",ac[1].sender);
        assertEquals(2,ac[1].count);
        
        assertEquals("angus",ac[2].sender);
        assertEquals(2,ac[2].count);
    }
    
    
    @Test
    public void testKeywordFinder() throws Exception {
    	ConversationExporter exporter = new ConversationExporter();
    	List<String> keywords = new ArrayList<String>();
    	String[] message = {"dog","cat","fish"};
    	keywords.add("fish");
    	assertEquals(exporter.keywordFinder(message,keywords),true);
    	keywords.add("horse");
    	keywords.remove(0);
    	assertEquals(exporter.keywordFinder(message,keywords),false);
    }
    
    @Test
    public void testPunctuationAdder() throws Exception {
    	ConversationExporter exporter = new ConversationExporter();
    	ArrayList<String> intermediate = new ArrayList<String>();
    	String[] punctuation = {"!",".",",","?","..."};
    	intermediate.add("no");
    	intermediate.add("NO");
    	intermediate.add("No");
    	for(int i=0;i<3;i++) {
    		for(int j=0;j<punctuation.length;j++) {
    			String extraword = intermediate.get(i) + punctuation[j];
    			intermediate.add(extraword);
    		}
    	}
    	assertEquals(exporter.punctuationAdder("no"),intermediate);
    	assertEquals(exporter.punctuationAdder("NO"),intermediate);
    	assertEquals(exporter.punctuationAdder("No"),intermediate);
    	assertEquals(exporter.punctuationAdder("nO"),intermediate);
    	
    }
    
    @Test
    public void testBlacklistedWordAdder() throws Exception {
    	ConversationExporter exporter = new ConversationExporter();
    	ArrayList<String> intermediate = new ArrayList<String>();
    	String[] punctuation = {"!",".",",","?","..."};
    	String[] blacklistedWords = {"yes","please"};
    	for(int i=0; i<blacklistedWords.length;i++) {
    		intermediate.addAll(exporter.punctuationAdder(blacklistedWords[i]));
    	}
    	assertEquals(exporter.blacklistedWordAdder(blacklistedWords),intermediate);
    	
    	
    }
    
    @Test
    public void testBlacklistedWordCheck() throws Exception {
    	ConversationExporter exporter = new ConversationExporter();
    	ArrayList<String> intermediate = new ArrayList<String>();
    	intermediate.add("dog");
    	intermediate.add("cat");
    	intermediate.add("word");
    	assertEquals(exporter.blacklistedWordCheck(intermediate,"word"),true);
    	assertEquals(exporter.blacklistedWordCheck(intermediate,"nothing"),false);
    }
    
    @Test
    public void testPunctuationIndex() throws Exception {
    	ConversationExporter exporter = new ConversationExporter();
    	char[] array = {'a','b',','};
    	assertEquals(exporter.punctuationIndex(array),2);
    	char[] array1 = {'a','b'};
    	assertEquals(exporter.punctuationIndex(array1),-1);
    }
   

    class InstantDeserializer implements JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonPrimitive()) {
                throw new JsonParseException("Expected instant represented as JSON number, but no primitive found.");
            }

            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

            if (!jsonPrimitive.isNumber()) {
                throw new JsonParseException("Expected instant represented as JSON number, but different primitive found.");
            }

            return Instant.ofEpochSecond(jsonPrimitive.getAsLong());
        }
    }
}
