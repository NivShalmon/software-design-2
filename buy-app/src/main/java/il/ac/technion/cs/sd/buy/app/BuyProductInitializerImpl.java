package il.ac.technion.cs.sd.buy.app;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class BuyProductInitializerImpl implements BuyProductInitializer {

	Map<String, String> products = new HashMap<>();
	Map<String, String> reservations = new HashMap<>();

	@Override
	public CompletableFuture<Void> setupXml(String xmlData) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlData)));
			NodeList allNodes = doc.getElementsByTagName("*"); // a list
																// containing
																// all the nodes
			for (int i = 0; i < allNodes.getLength(); i++) {
				Element element = (Element) allNodes.item(i);
				if (element.getNodeName().equals("Product")) {
					System.out.println(element.getAttribute("id"));

				}
				// System.out.println(element.getNodeName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// for (int i = 0; i < reviewers.getLength(); i++) {
		// Node nNode = reviewers.item(i);
		// NodeList reviews = ((Element) nNode).getElementsByTagName("Review");
		// String reviewerId = ((Element) nNode).getAttribute("Id");
		// for (int j = 0; j < reviews.getLength(); j++) {
		// String bookName = ((Element)
		// nNode).getElementsByTagName("Id").item(j).getTextContent();
		// String bookScore = ((Element)
		// nNode).getElementsByTagName("Score").item(j).getTextContent();
		// tmpStore.put(new Pair(reviewerId, bookName), bookScore);
		// }
		// }
		return null;
	}

	@Override
	public CompletableFuture<Void> setupJson(String jsonData) {
		// TODO Auto-generated method stub
		return null;
	}

}
