package info.magnolia.ml;

import java.io.InputStream;
import java.util.Collection;

public interface ImageTaggingService {
    Collection<String> processImage(byte[] imageData);

    Collection<String> processImage(InputStream imageDataStream);
}
