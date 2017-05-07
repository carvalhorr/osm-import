package carvalhorr.cs654.osh;

import carvalhorr.cs654.exception.ErrorProcessingXml;
import carvalhorr.cs654.exception.UnexpectedTokenException;

public interface GenericParser<T> {
	int size() throws ErrorProcessingXml ;
	T next() throws ErrorProcessingXml, InvalidOsmObjectException, UnexpectedTokenException;
	void close() throws ErrorProcessingXml;
}
