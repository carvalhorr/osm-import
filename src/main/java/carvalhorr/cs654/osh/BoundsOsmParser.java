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
import carvalhorr.cs654.model.OsmBounds;

public class BoundsOsmParser implements GenericParser<OsmBounds> {

	private String fileName;

	private XMLStreamReader reader = null;

	private boolean osmStarted = false;

	public BoundsOsmParser(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int size() throws ErrorProcessingXml {
		return 1;
	}

	@Override
	public OsmBounds next() throws ErrorProcessingXml, InvalidOsmObjectException, UnexpectedTokenException {
		OsmBounds object = null;
		try {
			if (reader == null) {
				startStream();
			}
			boolean keepGoing = true;
			while (reader.hasNext() && keepGoing) {
				int event = reader.next();
				switch (event) {
				case XMLStreamConstants.START_ELEMENT: {
					if ("osm".equals(reader.getLocalName())) {
						osmStarted = true;
					} else {
						if (osmStarted) {
							if ("bounds".equals(reader.getLocalName())) {
								double minLat, minLon, maxLat, maxLon;
								minLat = Double.parseDouble(reader.getAttributeValue(null, "minlat"));
								minLon = Double.parseDouble(reader.getAttributeValue(null, "minlon"));
								maxLat = Double.parseDouble(reader.getAttributeValue(null, "maxlat"));
								maxLon = Double.parseDouble(reader.getAttributeValue(null, "maxlon"));
								object = new OsmBounds(minLat, minLon, maxLat, maxLon);
								return object;
							}
						} else {
							throw new UnexpectedTokenException(
									"<" + reader.getLocalName() + "> tag found outside <osm> tag");
						}
					}
					break;
				}
				case XMLStreamConstants.CHARACTERS: {
					break;
				}
				case XMLStreamConstants.END_ELEMENT: {
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
