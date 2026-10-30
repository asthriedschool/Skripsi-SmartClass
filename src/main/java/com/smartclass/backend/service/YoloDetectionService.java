package com.smartclass.backend.service;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxJavaType;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;
import com.smartclass.backend.dto.PersonDetection;
import com.smartclass.backend.dto.VisionDetectionResponse;
import com.smartclass.backend.exception.VisionInputException;
import com.smartclass.backend.exception.VisionInferenceException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.imageio.ImageReader;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

@Service
public class YoloDetectionService {

    private static final long[] EXPECTED_INPUT_SHAPE = {1, 3, 640, 640};
    private static final int COCO_FEATURE_COUNT =
            YoloOutputDecoder.FEATURES_PER_DETECTION;
    private static final int MAX_IMAGE_DIMENSION = 8192;
    private static final long MAX_IMAGE_PIXELS = 40_000_000L;

    private final String modelResource;
    private final YoloImagePreprocessor preprocessor;
    private OrtEnvironment environment;
    private OrtSession session;
    private String inputName;
    private String outputName;
    private int predictionCount;

    public YoloDetectionService(
            @Value("${smartclass.vision.model:models/yolov5n.onnx}")
            String modelResource,
            YoloImagePreprocessor preprocessor
    ) {
        this.modelResource = modelResource;
        this.preprocessor = preprocessor;
    }

    @PostConstruct
    void initialize() {
        OrtSession.SessionOptions options = null;
        try (InputStream model = getClass().getClassLoader()
                .getResourceAsStream(modelResource)) {
            if (model == null) {
                throw new IllegalStateException(
                        "Model ONNX tidak ditemukan pada classpath: "
                                + modelResource
                );
            }

            environment = OrtEnvironment.getEnvironment();
            options = new OrtSession.SessionOptions();
            session = environment.createSession(model.readAllBytes(), options);
            validateModelMetadata();
        } catch (IOException | OrtException e) {
            closeSession();
            throw new IllegalStateException(
                    "Gagal memuat model ONNX dari classpath: " + modelResource,
                    e
            );
        } catch (RuntimeException e) {
            closeSession();
            throw e;
        } finally {
            if (options != null) {
                options.close();
            }
        }
    }

    public VisionDetectionResponse detect(byte[] imageBytes) {
        long startTime = System.nanoTime();
        BufferedImage image = decodeImage(imageBytes);
        YoloImagePreprocessor.PreprocessedImage preprocessed =
                preprocessor.preprocess(image);

        try (OnnxTensor input = OnnxTensor.createTensor(
                environment,
                FloatBuffer.wrap(preprocessed.tensor()),
                EXPECTED_INPUT_SHAPE
        );
             OrtSession.Result result = session.run(
                     Map.of(inputName, input)
             )) {
            OnnxValue outputValue = result.get(outputName).orElse(null);
            if (!(outputValue instanceof OnnxTensor output)) {
                throw new IllegalStateException(
                        "Output tensor model tidak ditemukan"
                );
            }
            FloatBuffer outputBuffer = output.getFloatBuffer();
            float[] values = new float[outputBuffer.remaining()];
            outputBuffer.get(values);

            List<PersonDetection> detections = YoloOutputDecoder.decode(
                    values,
                    predictionCount,
                    YoloOutputDecoder.DEFAULT_CONFIDENCE_THRESHOLD,
                    YoloOutputDecoder.DEFAULT_IOU_THRESHOLD,
                    image.getWidth(),
                    image.getHeight(),
                    preprocessed.scale(),
                    preprocessed.padLeft(),
                    preprocessed.padTop()
            );
            long inferenceTimeMs = (System.nanoTime() - startTime) / 1_000_000;
            return new VisionDetectionResponse(
                    !detections.isEmpty(),
                    detections.size(),
                    detections,
                    inferenceTimeMs
            );
        } catch (OrtException | IllegalStateException e) {
            throw new VisionInferenceException(
                    "Inferensi gambar gagal",
                    e
            );
        }
    }

    @PreDestroy
    void close() {
        closeSession();
    }

    private BufferedImage decodeImage(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new VisionInputException("File gambar kosong");
        }

        try (ImageInputStream imageInput = ImageIO.createImageInputStream(
                new ByteArrayInputStream(imageBytes)
        )) {
            if (imageInput == null) {
                throw new VisionInputException("File gambar tidak valid");
            }

            var readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw new VisionInputException(
                        "File bukan gambar PNG atau JPEG yang valid"
                );
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(imageInput, true, true);
                String format = reader.getFormatName();
                if (!format.equalsIgnoreCase("png")
                        && !format.equalsIgnoreCase("jpeg")
                        && !format.equalsIgnoreCase("jpg")) {
                    throw new VisionInputException(
                            "Hanya format PNG dan JPEG yang didukung"
                    );
                }

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0
                        || height <= 0
                        || width > MAX_IMAGE_DIMENSION
                        || height > MAX_IMAGE_DIMENSION
                        || (long) width * height > MAX_IMAGE_PIXELS) {
                    throw new VisionInputException(
                            "Dimensi gambar melebihi batas yang didukung"
                    );
                }

                BufferedImage image = reader.read(0);
                if (image == null) {
                    throw new VisionInputException("File gambar tidak valid");
                }
                return image;
            } finally {
                reader.dispose();
            }
        } catch (IOException e) {
            throw new VisionInputException(
                    "Gagal membaca file gambar",
                    e
            );
        }
    }

    private void validateModelMetadata() throws OrtException {
        Map<String, NodeInfo> inputs = session.getInputInfo();
        Map<String, NodeInfo> outputs = session.getOutputInfo();
        if (inputs.size() != 1 || outputs.size() != 1) {
            throw new IllegalStateException(
                    "Model harus memiliki tepat satu input dan satu output"
            );
        }

        Map.Entry<String, NodeInfo> input = inputs.entrySet().iterator().next();
        Map.Entry<String, NodeInfo> output = outputs.entrySet().iterator().next();
        TensorInfo inputInfo = (TensorInfo) input.getValue().getInfo();
        TensorInfo outputInfo = (TensorInfo) output.getValue().getInfo();

        if (inputInfo.type != OnnxJavaType.FLOAT
                || !Arrays.equals(inputInfo.getShape(), EXPECTED_INPUT_SHAPE)) {
            throw new IllegalStateException(
                    "Bentuk input model tidak didukung: " + inputInfo
            );
        }

        long[] outputShape = outputInfo.getShape();
        if (outputInfo.type != OnnxJavaType.FLOAT
                || outputShape.length != 3
                || outputShape[0] != 1
                || outputShape[2] != COCO_FEATURE_COUNT
                || outputShape[1] <= 0
                || outputShape[1] > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Bentuk output model tidak sesuai deteksi YOLOv5 COCO: "
                            + outputInfo
            );
        }

        Map<String, String> customMetadata =
                session.getMetadata().getCustomMetadata();
        String classNames = customMetadata.get("names");
        if (classNames != null && !classNames.contains("0: 'person'")) {
            throw new IllegalStateException(
                    "Class index 0 pada metadata model bukan person"
            );
        }

        inputName = input.getKey();
        outputName = output.getKey();
        predictionCount = (int) outputShape[1];
    }

    private void closeSession() {
        if (session != null) {
            try {
                session.close();
            } catch (OrtException e) {
                throw new IllegalStateException(
                        "Gagal menutup session ONNX Runtime",
                        e
                );
            } finally {
                session = null;
            }
        }
    }
}
