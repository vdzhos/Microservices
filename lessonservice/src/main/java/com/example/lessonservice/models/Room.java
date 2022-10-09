package com.example.lessonservice.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(value={ "type" }, allowGetters=true)
public class Room implements Serializable {

    private String room;
    private RoomType type;

    public Room(){
        this.type = RoomType.REMOTELY;
        this.room = "Remotely";
    }

    public Room(String room) {
        setRoom(room);
        this.type = RoomType.ROOM;
    }

    public String getRoom() {
        return room;
    }

    public Room setRoom(String room) {
        this.room = room;
        return this;
    }

    public RoomType getType() {
        return type;
    }

    @Override
    public String toString() {
        return room;
    }

    public String getTypeOrName()
    {
    	if(this.type == RoomType.REMOTELY)
    		return "REMOTELY";
    	return this.room;
    }

    public boolean equalsByString(String room)
    {
    	if(this.type == RoomType.REMOTELY)
    		return room.equals("REMOTELY");
    	return room.equals(this.room);
    }

    public enum RoomType{
        REMOTELY, ROOM;
    }
}
