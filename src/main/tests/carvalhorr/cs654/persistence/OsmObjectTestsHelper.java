package carvalhorr.cs654.persistence;

import java.util.ArrayList;
import java.util.HashMap;

import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;

public class OsmObjectTestsHelper {

	public static OsmObject createNodeObject(long changeset, long id, int version, int uid, String userName, String timestamp,
			String tagKey, String tagValue) {
		OsmObject object = new NodeOsmObject();
		fillOsmObjectCommonInfo(object, GeoJsonObjectType.POINT, changeset, id, version, uid, userName, timestamp, tagKey,
				tagValue);
		return object;
	}

	public static OsmObject createWayObject(GeoJsonObjectType type, long changeset, long id, int version, int uid, String userName,
			String timestamp, String tagKey, String tagValue) {
		OsmObject object = new WayOsmObject();
		fillOsmObjectCommonInfo(object, type, changeset, id, version, uid, userName, timestamp, tagKey, tagValue);
		((WayOsmObject) object).setNodesKeys(new ArrayList<Long>());
		return object;
	}

	private static void fillOsmObjectCommonInfo(OsmObject object, GeoJsonObjectType type, long changeset, long id, int version, int uid,
			String userName, String timestamp, String tagKey, String tagValue) {
		object.setChangeset(changeset);
		object.setId(id);
		object.setVersion(version);
		object.setCoordinates("coordinates");
		object.setGeoJsonType(type);
		object.setTags(new HashMap<String, String>());
		object.getTags().put(tagKey, tagValue);
		object.setTimestamp(timestamp);
		object.setUser(new OsmUser(uid, userName));
		object.setVisible(true);
	}

}
