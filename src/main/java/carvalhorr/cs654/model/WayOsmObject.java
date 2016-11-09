package carvalhorr.cs654.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WayOsmObject extends OsmObject{
	
	List<String> nodes = new ArrayList<String>();
	
	public void addNodeFromString(String lineString) {
		Map<String, String> nodeProperties = extractPropertiesFromLine(lineString);
		if (nodeProperties.size() != 1) {
			//TODO raise exception
		}
		String ref = nodeProperties.get("ref");
		if (ref == null) {
			//TODO raise exception
			System.out.println("Error adding node to way.");
		} else {
			nodes.add(ref);
		}
	}
	
	public List<String> getNodeIds() {
		return nodes;
	}
	

	@Override
	protected void validateProperties() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void validateTags() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void computeCoordinates() {
		coordinates = "not computed";
	}

}
