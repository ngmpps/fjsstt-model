package at.ngmpps.fjsstt.model;

/**
 * Proposed structure of a problem set using three Strings as used in
 * https://github.com/ngmpps/scenario/blob/master/Szenarien.md
 */
public class ProblemSet {

	/**
	 * the String contained in the fjs-file
	 */
	private String fjs;

	/**
	 * the String contained in the transport file
	 */
	private String transport;

	/**
	 * the String contained in the properties file - as required by the Solver.
	 */
	private String properties;

	public ProblemSet() {
	}

	public ProblemSet(String fjs, String transport, String properties) {
		this.fjs = fjs;
		this.transport = transport;
		this.properties = properties;
	}

	public String getFjs() {
		return fjs;
	}

	public String getTransport() {
		return transport;
	}

	public String getProperties() {
		return properties;
	}

	public void setFjs(String fjs) {
		this.fjs = fjs;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "ProblemSet{" + "fjs='" + fjs + '\'' + ", transport='" + transport + '\'' + ", properties='" + properties + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ProblemSet that = (ProblemSet) o;

		if (fjs != null ? !fjs.equals(that.fjs) : that.fjs != null)
			return false;
		if (transport != null ? !transport.equals(that.transport) : that.transport != null)
			return false;
		return properties != null ? properties.equals(that.properties) : that.properties == null;

	}

	@Override
	public int hashCode() {
		int result = fjs != null ? fjs.hashCode() : 0;
		result = 31 * result + (transport != null ? transport.hashCode() : 0);
		result = 31 * result + (properties != null ? properties.hashCode() : 0);
		return result;
	}
}
