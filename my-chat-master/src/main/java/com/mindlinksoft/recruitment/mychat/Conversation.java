package com.mindlinksoft.recruitment.mychat;

import java.util.Collection;

/**
 * Represents the model of a conversation.
 */
public final class Conversation {
    /**
     * The name of the conversation.
     */
    public String name;

    /**
     * The messages in the conversation.
     */
    public Collection<Message> messages;
    
    public String filterByUser;
    
    public String filterByKeyword;
    
    public String[] blacklistedWords;
    
    public Collection<Activity> Report;
    
    

    /**
     * Initializes a new instance of the {@link Conversation} class.
     * @param name The name of the conversation.
     * @param messages The messages in the conversation.
     */
    public Conversation(String name, Collection<Message> messages) {
        this.name = name;
        this.messages = messages;
    }
    
    public Conversation(String name, Collection<Message> messages, Collection<Activity> report) {
    	this.name = name;
    	this.messages = messages;
    	this.Report = report;
    	
    }
}
