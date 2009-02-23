import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.jdom.JDOMException;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestServiceDocument {
	private Namespace SWORD = Namespace.getNamespace("sword","http://purl.org/net/sword/");
	private Namespace ATOM = Namespace.getNamespace("atom","http://www.w3.org/2005/Atom");
	private Namespace APP = Namespace.getNamespace("app","http://www.w3.org/2007/app");

	protected String _serviceDocURL = "";

	public TestServiceDocument() {
			_serviceDocURL = System.getProperty("service_doc");
	}
	
	protected HttpClient getClient() {
		HttpClient tClient = new HttpClient();
		tClient.getParams().setAuthenticationPreemptive(true);

		Credentials tUserPass = new UsernamePasswordCredentials("sword", "sword");
		tClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM), tUserPass);
	
		return tClient;
	}

	protected Document getServiceDoc() throws IOException, JDOMException {
		GetMethod tMethod = new GetMethod(_serviceDocURL);

		int tStatus = this.getClient().executeMethod(tMethod);

		assertEquals("Status non 200", 200, tStatus);

		SAXBuilder tBuilder = new SAXBuilder();
		return tBuilder.build(tMethod.getResponseBodyAsStream());
	}

	@Test
	public void testMediatedDeposit() throws IOException, JDOMException {
		Document tServiceDoc = this.getServiceDoc();

		XPath tPath = XPath.newInstance("/app:service/app:workspace/app:collection/sword:mediation");
		tPath.addNamespace(APP);
		tPath.addNamespace(SWORD);
		Element tMediation = (Element)tPath.selectSingleNode(tServiceDoc);
		assertNotNull("sword:mediation not present", tMediation);
		assertTrue("sword:mediation must contain either true or false", tMediation.getText().equals("true") || tMediation.getText().equals("false"));
	}

	public static void main(final String pArgs[]) throws IOException {
		org.junit.runner.JUnitCore.main(TestServiceDocument.class.getName());
	}
}
