package carvalhorr.cs654.model;

public class NodeOsmObject extends OsmObject {

	@Override
	protected void validateProperties() {

	}

	@Override
	protected void validateTags() {

	}

	@Override
	public void computeCoordinates() {
		coordinates =  "[" + getPropertyByKey("lat") + "," + getPropertyByKey("lon") + "]";
	}

}
