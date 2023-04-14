package com.szarawara.jakub.mdc.rest;


import com.szarawara.jakub.mdc.rest.utils.CompetitionTeams;
import com.szarawara.jakub.mdc.rest.utils.Match;
import com.szarawara.jakub.mdc.rest.utils.TeamInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Competition {

    @PostMapping(path = "/competition/{date}")
    public String getCompetition(@RequestBody String body, @PathVariable String date) throws ParserConfigurationException, IOException, SAXException {
        StringBuffer response = new StringBuffer();
        List<CompetitionTeams> competitionTeamsList = new ArrayList<>();
        body.lines().forEach(line -> {
            competitionTeamsList.add(findCompetitionTeams(line));
        });
        Matches matches = new Matches();
        Result result = new Result();
        Team team = new Team();
        for (CompetitionTeams teams : competitionTeamsList) {
            TeamInfo homeTeam = team.getTeamId(teams.homeTeam);
            TeamInfo awayTeam = team.getTeamId(teams.awayTeam);
            List<String> matchesList = matches.getMatches(homeTeam.id, awayTeam.id, date);
            List<Match> matchesResults = result.getMatchResults(matchesList);
            response.append(homeTeam.name);
            response.append(" (");
            response.append(teams.homeTeam);
            response.append(") - ");
            response.append(awayTeam.name);
            response.append(" (");
            response.append(teams.awayTeam);
            response.append(") ");
            matchesResults.forEach(matchesResult -> {
                if (homeTeam.id.equals(matchesResult.teamHome.id)) {
                    response.append(matchesResult.teamHome.goals);
                    response.append(":");
                    response.append(matchesResult.teamAway.goals);
                    response.append(" ");
                } else {
                    response.append(matchesResult.teamAway.goals);
                    response.append(":");
                    response.append(matchesResult.teamHome.goals);
                    response.append(" ");
                }
            });
            response.append("\n");
        }
        response.append("\n" +
                "Wynik sobie sami policzcie. Testuję wyciąganie wyników pod MDC. Proszę o informację jeśli coś jest nie tak :) ");
        return response.toString();
    }

    private CompetitionTeams findCompetitionTeams(String text) {
        String homeTeam = findTeam(text);
        text = text.substring(text.indexOf(")") + 1);
        String awayTeam = findTeam(text);
        CompetitionTeams competitionTeams = new CompetitionTeams();
        competitionTeams.awayTeam = awayTeam;
        competitionTeams.homeTeam = homeTeam;
        return competitionTeams;
    }

    private String findTeam(String text) {
        text = text.substring(text.indexOf("(") + 1);
        return text.substring(0, text.indexOf(")"));
    }

}
