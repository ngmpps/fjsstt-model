package at.ngmpps.fjsstt.model;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class MyBean {
	private URI uri;
	private String name;
	private String myField;
	private List<String> myList;
	private Map<Integer, Float> myMap;

	public MyBean() {
	}

	public MyBean(URI uri, String name, String myField, List<String> myList, Map<Integer, Float> myMap) {
		this.uri = uri;
		this.name = name;
		this.myField = myField;
		this.myList = myList;
		this.myMap = myMap;
	}

	public String getMyField() {
		return myField;
	}

	public List<String> getMyList() {
		return myList;
	}

	public Map<Integer, Float> getMyMap() {
		return myMap;
	}

	public String getName() {
		return name;
	}

	public URI getUri() {
		return uri;
	}

	public void setMyField(String myField) {
		this.myField = myField;
	}

	public void setMyList(List<String> myList) {
		this.myList = myList;
	}

	public void setMyMap(Map<Integer, Float> myMap) {
		this.myMap = myMap;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "MyBean{" + "uri=" + uri + ", name='" + name + '\'' + ", myField='" + myField + '\'' + ", myList=" + myList + ", myMap="
				+ myMap + '}';
	}
}
