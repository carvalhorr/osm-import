package carvalhorr.cs654.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelationOsmObject extends OsmObject {

	private List<Member> members = new ArrayList<Member>();

	public void addMember(String lineString) {
		Map<String, String> memberProperties = extractPropertiesFromLine(lineString);
		if (memberProperties.size() != 3) {
			// TODO raise exception
		}
		String type = memberProperties.get("type");
		String ref = memberProperties.get("ref");
		String role = memberProperties.get("role");
		if (type == null || ref == null) {
			// TODO raise exception
			System.out.println("Error adding member to relation.");
		} else {
			members.add(new Member(type, ref, role));
		}
	}

	@Override
	protected void validateProperties() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateTags() {
		// TODO Auto-generated method stub

	}

	public class Member {
		private String type;
		private String ref;
		private String role;

		public Member(String type, String ref, String role) {
			this.type = type;
			this.ref = ref;
			this.role = role;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getRef() {
			return ref;
		}

		public void setRef(String ref) {
			this.ref = ref;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}
	}

}
