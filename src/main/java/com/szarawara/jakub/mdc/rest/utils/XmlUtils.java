package com.szarawara.jakub.mdc.rest.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class XmlUtils {

    public static Document getXml(String url) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new URL(url).openStream());
    }

    public static Node getNode(NodeList nodeList, String nodeName) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals(nodeName)) {
                return nodeList.item(i);
            }
        }
        return null;
    }

    public static Node getNode(NodeList nodeList, String nodeName, String attributeName, String attributeValue) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (isNodeNameCorrect(nodeList.item(i), nodeName) && isAttributeNameCorrect(nodeList.item(i), attributeName, attributeValue)) {
                return nodeList.item(i);
            }
        }
        return null;
    }

    private static boolean isNodeNameCorrect(Node node, String nodeName) {
        return node.getNodeName().equals(nodeName);
    }

    private static boolean isAttributeNameCorrect(Node node, String attributeName, String attributeValue) {
        return attributeValue.equals(node.getAttributes().getNamedItem(attributeName).getNodeValue());
    }
}
