package carvalhorr.cs654.files;

public abstract class OsmObjectFileWriterImpl implements OsmObjectFileWriter {
	
	protected String mFullFileName = "";

	@Override
	public String getFullFileName() {
		return mFullFileName;
	}

}
