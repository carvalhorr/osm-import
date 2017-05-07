package carvalhorr.cs654.osh;

import javax.xml.stream.XMLStreamReader;

import carvalhorr.cs654.model.WayOsmObject;

public class WayOsmParser extends OsmParser<WayOsmObject> {

	public WayOsmParser(String fileName) {
		super("way", fileName);
	}

	@Override
	protected WayOsmObject createNext() {
		WayOsmObject object = new WayOsmObject();
		return object;
	}

	@Override
	protected boolean processSubTags(WayOsmObject object, XMLStreamReader reader) {
		if ("nd".equals(reader.getLocalName())) {
			((WayOsmObject) object).addNode(reader.getAttributeValue(null, "ref"));
		}
		return true;
	}

}
