package carvalhorr.cs654.model;

/**
 * Osm user.
 * 
 * @author carvalhorr
 *
 */
public class OsmUser {

	// user id
	private final Integer uid;

	// user name
	private final String userName;

	public OsmUser(Integer uid, String userName) {
		this.uid = uid;
		this.userName = userName;
	}

	public Integer getUid() {
		return uid;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * Compare two user object for equality basedon their properties.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof OsmUser))
			return false;
		OsmUser user = (OsmUser) obj;
		if (uid != user.getUid() || !userName.equals(user.getUserName()))
			return false;
		return true;
	}
}
