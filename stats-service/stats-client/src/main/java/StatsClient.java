import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats_dto.HitDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {

    private static final String HIT_PREFIX = "/hit";
    private static final String START_PREFIX = "/start";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build()
        );
    }

    public ResponseEntity<Object> getStats(
            String start, String end,
            List<String> uris, Boolean unique
    ) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("uris", String.join(",", uris));
        parameters.put("unique", unique);

        return get(START_PREFIX + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> addHit(HitDto hitDto) {
        return post(HIT_PREFIX, hitDto);
    }
}
