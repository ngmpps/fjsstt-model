package at.ngmpps.fjsstt;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.model.ProblemSet;

public class ProblemSetTest {

	private String fjs;
	private String transport;
	private String properties;

	@Before
	public void setUp() throws Exception {
		fjs = readFile("/problems/p1/WT1.fjs");
		transport = readFile("/problems/p1/WT1A.TRANSPORT");
		properties = readFile("/problems/p1/WT1A.PROPERTIES");
	}

	private String readFile(String name) throws IOException, URISyntaxException {
		return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(name).toURI())), "UTF-8");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void parseProblemSetFromFileTest() throws IOException {
		final ProblemSet problemSet = ModelFactory.createProblemSet(fjs, transport, properties);
		assertNotNull(problemSet.getFjs());
		assertNotNull(problemSet.getTransport());
		assertNotNull(problemSet.getProperties());
		assertTrue(problemSet.getFjs().length() > 100);
		assertTrue(problemSet.getTransport().length() > 20);
		assertTrue(problemSet.getProperties().length() > 500);
	}
}
