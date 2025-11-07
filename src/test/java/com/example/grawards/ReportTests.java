package com.example.grawards;

import com.example.grawards.VO.ReportElementVO;
import com.example.grawards.VO.ReportVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void endpointDeveResponderComSucesso() {
        var resposta = restTemplate.getForObject("http://localhost:" + port + "/movies", ReportVO.class);

        assertThat(resposta).isNotNull();

        resposta.getMin().forEach(reportElementVO -> assertThat(reportElementVO.getInterval())
                .isEqualTo(reportElementVO.getFollowingWin() - reportElementVO.getPreviousWin()));

        resposta.getMax().forEach(reportElementVO -> assertThat(reportElementVO.getInterval())
                .isEqualTo(reportElementVO.getFollowingWin() - reportElementVO.getPreviousWin()));

        var minInterval = resposta.getMin().stream().mapToInt(ReportElementVO::getInterval).min().orElse(0);
        var maxInterval = resposta.getMax().stream().mapToInt(ReportElementVO::getInterval).max().orElse(0);

        assertThat(minInterval).isLessThanOrEqualTo(maxInterval);
    }
}
