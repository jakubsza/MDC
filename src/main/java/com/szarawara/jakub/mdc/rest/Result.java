package com.szarawara.jakub.mdc.rest;

import com.szarawara.jakub.mdc.rest.utils.Match;
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

import static com.szarawara.jakub.mdc.rest.utils.XmlUtils.getNode;
import static com.szarawara.jakub.mdc.rest.utils.XmlUtils.getXml;

@RestController
public class Result {

    public List<Match> getMatchResults(List<String> matchIdList) throws ParserConfigurationException, IOException, SAXException {
        List<Match> matches = new ArrayList<>();
        for (String m : matchIdList) {
            matches.add(getMatchResult(m));
        }
        return matches;
    }

    @GetMapping(path = "result/{matchId}")
    public Match getMatchResult(@PathVariable String matchId) throws ParserConfigurationException, IOException, SAXException {
        Document xml = getXml("https://www.managerzone.com/xml/match_info.php?sport_id=1&match_id=" + matchId);
        Node mzNode = getNode(xml.getChildNodes(), "ManagerZone");
        if (mzNode == null) {
            return null;
        }
        Node matchNode = getNode(mzNode.getChildNodes(), "Match");
        if (matchNode == null) {
            return null;
        }
        Match match = new Match();
        Match.Team teamHome = new Match.Team();
        Match.Team teamAway = new Match.Team();
        match.id = getNodeAttributeValue(matchNode, "id");
        match.date = getNodeAttributeValue(matchNode, "date");
        List<Node> teamNodes = getTeamNodes(matchNode.getChildNodes());
        teamHome.name = getNodeAttributeValue(teamNodes.get(0), "name");
        teamHome.id = getNodeAttributeValue(teamNodes.get(0), "id");
        teamHome.goals = getNodeAttributeValue(teamNodes.get(0), "goals");
        teamAway.name = getNodeAttributeValue(teamNodes.get(1), "name");
        teamAway.id = getNodeAttributeValue(teamNodes.get(1), "id");
        teamAway.goals = getNodeAttributeValue(teamNodes.get(1), "goals");
        match.teamHome = teamHome;
        match.teamAway = teamAway;
        return match;
    }

    private List<Node> getTeamNodes(NodeList nodeList) {
        List<Node> teamNodes = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("Team")) {
                teamNodes.add(nodeList.item(i));
            }
        }
        return teamNodes;
    }

    private String getNodeAttributeValue(Node node, String itemName) {
        return node.getAttributes().getNamedItem(itemName).getNodeValue();
    }
}
