package webserver;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ExecutorsTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorsTest.class);

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        final ExecutorService es = Executors.newFixedThreadPool(100);

        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 100; i++) {
            es.execute(() -> {
                int idx = counter.addAndGet(1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("Thread {}", idx);
            });
        }
        sw.stop();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);
        logger.info("Total Elaspsed: {}", sw.getTotalTimeSeconds());
    }

    @Test
    void request_resttemplate() throws InterruptedException {
        // given
        final RestTemplate restTemplate = new RestTemplate();
        final ExecutorService es = Executors.newFixedThreadPool(10);
        final int tryCount = 250;

        // when
        IntStream.range(0, tryCount)
                .parallel()
                .forEach(i -> {
                    es.execute(() -> {
                        final int idx = counter.addAndGet(1);
                        restTemplate.getForEntity("http://localhost:8080", String.class);
                        logger.info("request num: {}", idx);
                    });
                });

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        // then
        assertThat(counter).hasValue(tryCount);
    }
}

