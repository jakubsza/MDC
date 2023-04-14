package com.szarawara.jakub.mdc.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.szarawara.jakub.mdc.rest.utils.XmlUtils.getNode;
import static com.szarawara.jakub.mdc.rest.utils.XmlUtils.getXml;

@RestController
public class Matches {

    @GetMapping(path = "/matches/{team1Id}/{team2Id}/{date}")
    public List<String> getMatches(@PathVariable String team1Id, @PathVariable String team2Id, @PathVariable String date) throws ParserConfigurationException, IOException, SAXException {
        List<String> matches = new ArrayList<>();
        matches.addAll(getFixedOrResultMatches("http://www.managerzone.com/xml/team_matchlist.php?sport_id=1&team_id=" + team1Id + "&match_status=1&limit=50", team2Id, date));
        matches.addAll(getFixedOrResultMatches("http://www.managerzone.com/xml/team_matchlist.php?sport_id=1&team_id=" + team1Id + "&match_status=2&limit=50", team2Id, date));
        return matches;
    }

    public List<String> getFixedOrResultMatches(String url, String team2Id, String date) throws ParserConfigurationException, IOException, SAXException {
        Document xml = getXml(url);
        Node matchListNode = getNode(xml.getChildNodes(), "ManagerZone_MatchList");
        if (matchListNode == null) {
            return null;
        }
        List<Node> matchNodes = getMatchNodeList(matchListNode.getChildNodes(), team2Id);
        return matchNodes.stream()
                .filter(node -> node.getAttributes().getNamedItem("type").getNodeValue().equals("friendly"))
                .filter(node -> node.getAttributes().getNamedItem("date").getNodeValue().contains(date))
                .map(node -> node.getAttributes().getNamedItem("id").getNodeValue())
                .toList();
    }

    private List<Node> getMatchNodeList(NodeList nodeList, String team2Id) {
        List<Node> matchNodes = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("Match")) {
                NodeList matchNodeList = nodeList.item(i).getChildNodes();
                for (int k = 0; k < matchNodeList.getLength(); k++) {
                    getMatchNode(matchNodes, nodeList, matchNodeList, team2Id, i, k);
                }
            }
        }
        return matchNodes;
    }

    private void getMatchNode(List<Node> matchNodes, NodeList nodeList, NodeList matchNodeList, String team2Id, int i, int k) {
        if (matchNodeList.item(k).getNodeName().equals("Team")) {
            Node teamNode = matchNodeList.item(k);
            if (team2Id.equals(teamNode.getAttributes().getNamedItem("teamId").getNodeValue())) {
                matchNodes.add(nodeList.item(i));
            }
        }
    }
}
