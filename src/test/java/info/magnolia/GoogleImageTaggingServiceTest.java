package info.magnolia;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsNot.not;

import info.magnolia.ml.GoogleImageTaggingService;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

public class GoogleImageTaggingServiceTest {

    private GoogleImageTaggingService googleImageTaggingService;

    @Before
    public void setUp() throws Exception {
        googleImageTaggingService = new GoogleImageTaggingService();
    }

    @Test
    public void name() throws Exception {
        URL resource = Resources.getResource("cute-puppy.jpg");
        byte[] data = Files.readAllBytes(Paths.get(resource.toURI()));

        Collection<String> strings = googleImageTaggingService.processImage(data);

        assertThat(strings, not(empty()));
    }
}