import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class QueueOverSqlTests {
    private final static String jdbcUrl = "jdbc:h2:mem:queueOverSqlTest";


    private

    @Test
    public void test()
    {
        new QueueOverSql(jdbcUrl, 30, TimeUnit.MINUTES, 20, TimeUnit.SECONDS);

        Assertions.assertTrue(true);
    }
}
