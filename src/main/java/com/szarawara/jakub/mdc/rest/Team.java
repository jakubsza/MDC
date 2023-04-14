package com.szarawara.jakub.mdc.rest;

import com.szarawara.jakub.mdc.rest.utils.TeamInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static com.szarawara.jakub.mdc.rest.utils.XmlUtils.getNode;
import static com.szarawara.jakub.mdc.rest.utils.XmlUtils.getXml;

@RestController
public class Team {

    @GetMapping(path = "/team/{userName}")
    public TeamInfo getTeamId(@PathVariable String userName) throws ParserConfigurationException, IOException, SAXException {
        Document xml = getXml("http://www.managerzone.com/xml/manager_data.php?sport_id=1&username=" + userName);
        Node mzUserDataNode = getNode(xml.getChildNodes(), "ManagerZone_UserData");
        if (mzUserDataNode == null) {
            return null;
        }
        Node userDataNode = getNode(mzUserDataNode.getChildNodes(), "UserData");
        if (userDataNode == null) {
            return null;
        }
        Node teamNode = getNode(userDataNode.getChildNodes(), "Team", "sport", "soccer");
        if (teamNode == null) {
            return null;
        }
        TeamInfo teamInfo = new TeamInfo();
        teamInfo.id = teamNode.getAttributes().getNamedItem("teamId").getNodeValue();
        teamInfo.name = teamNode.getAttributes().getNamedItem("teamName").getNodeValue();
        return teamInfo;
    }
}
