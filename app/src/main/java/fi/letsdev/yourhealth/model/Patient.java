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
	@SerializedName("__v")
	@Expose
	private Integer v;
	@SerializedName("createdAt")
	@Expose
	private String createdAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getV() {
		return v;
	}

	public void setV(Integer v) {
		this.v = v;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean isNull() {
		return id == null ||
			name == null ||
			channel == null;
	}
}
