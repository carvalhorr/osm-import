package carvalhorr.cs654.model;

public class OsmUser {
	private Integer uid;
	private String userName;
	
	public OsmUser(Integer uid, String userName) {
		this.uid = uid;
		this.userName = userName;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}