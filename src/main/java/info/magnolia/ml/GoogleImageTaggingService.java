package info.magnolia.ml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

public class GoogleImageTaggingService implements ImageTaggingService {

    private static final Logger log = LoggerFactory.getLogger(GoogleImageTaggingService.class);

    @Override
    public Collection<String> processImage(byte[] imageData) {
        ImageAnnotatorClient vision = instantiateImageClient();

        ByteString imgBytes = ByteString.copyFrom(imageData);

        // Builds the image annotation request
        List<AnnotateImageRequest> requests = Lists.newArrayList();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);

        // Performs label detection on the image file
        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        List<String> tags = Lists.newArrayList();
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                log.error("An error occurred while fetching responses.", res.getError().getMessage());
                continue;
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                tags.add(annotation.getDescription());
            }
        }

        return tags;
    }

    @Override
    public Collection<String> processImage(InputStream imageDataStream) {
        try {
            return processImage(IOUtils.toByteArray(imageDataStream));
        } catch (IOException e) {
            log.error("An error occurred while convert given input stream to byte array", e);
            return Lists.newArrayList();
        }
    }

    private ImageAnnotatorClient instantiateImageClient() {
        ImageAnnotatorClient vision;
        try {
            vision = ImageAnnotatorClient.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vision;
    }
}
