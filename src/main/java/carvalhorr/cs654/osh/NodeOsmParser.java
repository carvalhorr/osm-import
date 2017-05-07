package carvalhorr.cs654.osh;

import carvalhorr.cs654.model.NodeOsmObject;

public class NodeOsmParser extends OsmParser<NodeOsmObject> {

	public NodeOsmParser(String fileName) {
		super("node", fileName);
	}

	@Override
	protected NodeOsmObject createNext() {
		NodeOsmObject object = new NodeOsmObject();
		return object;
	}

}
