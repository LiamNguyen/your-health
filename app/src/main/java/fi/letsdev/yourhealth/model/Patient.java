package fi.letsdev.yourhealth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Patient implements Serializable {

	public Patient() {}

	public Patient(String name, String channel) {
		this.name = name;
		this.channel = channel;
	}

	@SerializedName("_id")
	@Expose
	private String id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("channel")
	@Expose
	private String channel;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getChannel() {
		return channel;
	}

	public Boolean isNull() {
		return name == null ||
			channel == null;
	}
}
