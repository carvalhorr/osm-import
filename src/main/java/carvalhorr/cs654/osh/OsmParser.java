package carvalhorr.cs654.osh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import carvalhorr.cs654.exception.ErrorProcessingXml;
import carvalhorr.cs654.exception.UnexpectedTokenException;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;

public abstract class OsmParser<T extends OsmObject> implements GenericParser<T> {

	private String objectType;
	private String fileName;
	private int objectCount = -1;

	private XMLStreamReader reader = null;

	private boolean osmStarted = false;

	protected OsmParser(String objectType, String fileName) {
		this.objectType = objectType;
		this.fileName = fileName;
	}

	@Override
	public int size() throws ErrorProcessingXml {
		if (objectCount == -1) {
			objectCount = 0;
			try {
				startStream();
				while (reader.hasNext()) {
					int event = reader.next();
					switch (event) {
					case XMLStreamConstants.START_ELEMENT:
						if (objectType.equals(reader.getLocalName())) {
							objectCount++;
						}
						break;
					}
				}
				reader.close();
				reader = null;
			} catch (XMLStreamException e) {
				throw new ErrorProcessingXml(e);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return objectCount;
	}

	protected abstract T createNext();

	@Override
	public T next() throws ErrorProcessingXml, InvalidOsmObjectException, UnexpectedTokenException {
		T object = null;
		try {
			if (reader == null) {
				startStream();
			}
			boolean keepGoing = true;
			boolean objectStarted = false;
			while (reader.hasNext() && keepGoing) {
				int event = reader.next();
				switch (event) {
				case XMLStreamConstants.START_ELEMENT: {
					if ("osm".equals(reader.getLocalName())) {
						osmStarted = true;
					} else {
						if (osmStarted) {
							if (objectType.equals(reader.getLocalName())) {
								if (!objectStarted) {
									objectStarted = true;
									object = createNext();
									processStartTag(object, reader);
								}
							} else {
								if (objectStarted) {
									if ("tag".equals(reader.getLocalName())) {
										if (reader.getAttributeValue(null, "k") != null
												&& reader.getAttributeValue(null, "v") != null)
											object.addTag(reader.getAttributeValue(null, "k").toString(),
													reader.getAttributeValue(null, "v").toString());
									} else if (!processSubTags(object, reader)) {
										throw new UnexpectedTokenException("Object "
												+ reader.getAttributeValue(null, "id") + " version "
												+ reader.getAttributeValue(null, "version") + ": opening <"
												+ reader.getLocalName() + "> tag found inside other object tag");
									}
								}
							}
						} else {
							throw new UnexpectedTokenException(
									"<" + reader.getLocalName() + "> tag found outside <osm> tag");
						}
					}
					break;
				}
				case XMLStreamConstants.CHARACTERS: {
					processXmlTagText(object, reader.getText().trim());
					break;
				}
				case XMLStreamConstants.END_ELEMENT: {
					if (objectType.equals(reader.getLocalName())) {
						keepGoing = false;
						if (objectStarted) {
							if (!object.isValid()) {
								throw new InvalidOsmObjectException(((object instanceof NodeOsmObject) ? "Node" : "Way")
										+ " " + object.getId() + " " + object.getVersion() + " is invalid.");
							} else {
								processEndTag(object, reader);

							}
						} else {
							throw new UnexpectedTokenException("Found closing <" + reader.getLocalName()
									+ "> tag found without corresponding opening <" + reader.getLocalName()
									+ "> tag after object " + object.getId() + " version " + object.getVersion());
						}
						return object;
					}
					break;
				}
				}
			}
		} catch (XMLStreamException e) {
			throw new ErrorProcessingXml(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

	protected boolean processSubTags(T object, XMLStreamReader reader) {
		return true;
	}

	protected void processStartTag(T object, XMLStreamReader reader) {
		// store read properties in OsmObject
		object.setId(Long.parseLong(reader.getAttributeValue(null, "id").toString()));
		object.setVersion(Integer.parseInt(reader.getAttributeValue(null, "version").toString()));
		object.setTimestamp(reader.getAttributeValue(null, "timestamp").toString());
		object.setChangeset(Long.parseLong(reader.getAttributeValue(null, "changeset").toString()));
		OsmUser user = null;
		if (reader.getAttributeValue(null, "uid") == null) {
			user = new OsmUser(-1, "unknown user");
		} else {
			user = new OsmUser(Integer.parseInt(reader.getAttributeValue(null, "uid").toString()),
					reader.getAttributeValue(null, "user").toString());
		}
		object.setUser(user);
		if (reader.getAttributeValue(null, "visible") == null) {
			object.setVisible(false);
		} else {
			object.setVisible(Boolean.parseBoolean(reader.getAttributeValue(null, "visible").toString()));
		}
		if (reader.getAttributeValue(null, "lon") != null && reader.getAttributeValue(null, "lat") != null) {
			object.setCoordinates("[" + reader.getAttributeValue(null, "lon").toString() + ","
					+ reader.getAttributeValue(null, "lat").toString() + "]");
		}
	}

	protected void processXmlTagText(T object, String text) {

	}

	protected void processEndTag(T object, XMLStreamReader reader) {

	}

	private void startStream() throws XMLStreamException, FileNotFoundException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		reader = factory.createXMLStreamReader(new FileReader(new File(fileName)));

	}

	@Override
	public void close() throws ErrorProcessingXml {
		try {
			reader.close();
		} catch (XMLStreamException e) {
			throw new ErrorProcessingXml();
		}
		reader = null;
	}
}
