package be.civadis.poc.s3.utils;

import com.google.common.collect.ImmutableListMultimap;
import io.micrometer.common.util.StringUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.activation.MimetypesFileTypeMap;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import static org.apache.tika.metadata.TikaMetadataKeys.RESOURCE_NAME_KEY;


@Component
public class FichierUtils {

    private static final String TMP_FILE_PREFIX = "fichier_utils";

    public static final String MS_WORD = "application/vnd.com.documents4j.any-msword";
    public static final String RTF = "application/rtf";
    public static final String RTF_BIS = "text/rtf";
    public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String DOC = "application/msword";
    public static final String MS_DOC = "application/ms-doc";

    public static final String ODT = "application/vnd.oasis.opendocument.text";
    public static final String OTT = "application/vnd.oasis.opendocument.text";
    public static final String SXW = "application/vnd.sun.xml.writer";
    public static final String TEXT = "text/plain";
    public static final String WPD = "application/wordperfect";
    public static final String WPD_BIS = "application/x-wpwin";
    public static final String HTML = "text/html";

    public static final String ODS = "application/vnd.oasis.opendocument.spreadsheet";
    public static final String OTS = "application/vnd.oasis.opendocument.spreadsheet-template";
    public static final String SXC = "  application/vnd.sun.xml.calc";
    public static final String MS_EXCEL = "application/vnd.com.documents4j.any-msexcel";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String XLS = "application/vnd.ms-excel";
    public static final String CSV = "text/csv";
    public static final String TSV = "text/tab-separated-values";

    public static final String ODP = "application/vnd.oasis.opendocument.presentation";
    public static final String OTP = "application/vnd.oasis.opendocument.presentation-template";
    public static final String SXI = "application/vnd.sun.xml.impress";
    public static final String PPT = "application/vnd.ms-powerpoint";
    public static final String PPT_BIS = "application/powerpoint";
    public static final String PPT_BIS_1 = "application/mspowerpoint";
    public static final String PPT_BIS_2 = "application/x-mspowerpoint";
    public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    public static final String PDF = "application/pdf";
    public static final String IMAGE_BMP = "image/bmp";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_TIFF = "image/tiff";
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_SVG_XML = "image/svg+xml";
    public static final String JSON = "application/json";
    public static final String JASPER = "application/octet-stream";
    public static final String JRXML = "application/octet-stream";
    public static final String XML = "application/xml";
    public static final String MS_OUTLOOK = "application/vnd.ms-outlook";

    public static final String BASE64 = "base64";

    private static MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
    private static final Tika tika = new Tika();

    private FichierUtils() {
    }

    private static ImmutableListMultimap<String, String> biDefautMsOffice = new ImmutableListMultimap.Builder<String, String>()
            //TEXTE
            .put("rtf", RTF)
            .put("RTF", RTF)
            .put("rtf", RTF_BIS)
            .put("RTF", RTF_BIS)
            .put("docx", DOCX)
            .put("DOCX", DOCX)
            .put("docx", MS_WORD)
            .put("DOCX", MS_WORD)
            .put("doc", DOC)
            .put("DOC", DOC)
            .put("doc", MS_DOC)
            .put("DOC", MS_DOC)
            .put("dot", DOC)
            .put("DOT", DOC)
            .put("dotm", DOC)
            .put("DOTM", DOC)
            .put("dotx", DOC)
            .put("DOTX", DOC)

            .build();

    private static ImmutableListMultimap<String, String> biDefautLibreOffice = new ImmutableListMultimap.Builder<String, String>()
            //TEXTE
            .put("odt", ODT)
            .put("ODT", ODT)
            .put("ott", OTT)
            .put("OTT", OTT)
            .put("sxw", SXW)
            .put("SXW", SXW)
            .put("txt", TEXT)
            .put("TXT", TEXT)
            .put("wpd", WPD)
            .put("WPD", WPD)
            .put("wpd", WPD_BIS)
            .put("WPD", WPD_BIS)
            .put("htm", HTML)
            .put("HTM", HTML)
            .put("html", HTML)
            .put("HTML", HTML)
            .put("pdf", PDF)
            .put("PDF", PDF)
            //TABLEUR
            .put("ods", ODS)
            .put("ODS", ODS)
            .put("ots", OTS)
            .put("OTS", OTS)
            .put("sxc", SXC)
            .put("SXC", SXC)
            .put("xls", XLS)
            .put("XLS", XLS)
            .put("xls", MS_EXCEL)
            .put("XLS", MS_EXCEL)
            .put("xlsx", XLSX)
            .put("XLSX", XLSX)
            .put("csv", CSV)
            .put("CSV", CSV)
            .put("tsv", TSV)
            .put("TSV", TSV)
            //PRESENTATION
            .put("odp", ODP)
            .put("ODP", ODP)
            .put("otp", OTP)
            .put("OTP", OTP)
            .put("sxi", SXI)
            .put("SXI", SXI)
            .put("ppt", PPT)
            .put("PPT", PPT)
            .put("ppt", PPT_BIS)
            .put("PPT", PPT_BIS)
            .put("ppt", PPT_BIS_1)
            .put("PPT", PPT_BIS_1)
            .put("ppt", PPT_BIS_2)
            .put("PPT", PPT_BIS_2)
            .put("pptx", PPTX)
            .put("PPTX", PPTX)

            .build();

    private static ImmutableListMultimap<String, String> biDefautJusteStockage = new ImmutableListMultimap.Builder<String, String>()
            //TEXTE
            .put("pdf", PDF)
            .put("PDF", PDF)
            .put("json", JSON)
            .put("JSON", JSON)
            .put("jrxml", JRXML)
            .put("JRXML", JRXML)
            .put("jasper", JASPER)
            .put("JASPER", JASPER)
            .put("xml", XML)
            .put("XML", XML)
            //IMAGE
            .put("bmp", IMAGE_BMP)
            .put("BMP", IMAGE_BMP)
            .put("gif", IMAGE_GIF)
            .put("GIF", IMAGE_GIF)
            .put("jpg", IMAGE_JPEG)
            .put("JPG", IMAGE_JPEG)
            .put("jpeg", IMAGE_JPEG)
            .put("JPEG", IMAGE_JPEG)
            .put("jpe", IMAGE_JPEG)
            .put("JPE", IMAGE_JPEG)
            .put("tiff", IMAGE_TIFF)
            .put("TIFF", IMAGE_TIFF)
            .put("tif", IMAGE_TIFF)
            .put("TIF", IMAGE_TIFF)
            .put("png", IMAGE_PNG)
            .put("PNG", IMAGE_PNG)
            .put("svg", IMAGE_SVG_XML)
            .put("SVG", IMAGE_SVG_XML)
            //message
            .put("msg", MS_OUTLOOK)
            .put("MSG", MS_OUTLOOK)
            .build();


    private static ImmutableListMultimap<String, String> biMapStockageAutorise = new ImmutableListMultimap.Builder<String, String>()
            // RM001 - Formats (ou extensions) de fichiers autorisés
            // avec les maps par défaut on a bien tous les mimes types autorisés
            .putAll(biDefautMsOffice)
            .putAll(biDefautLibreOffice)
            .putAll(biDefautJusteStockage)

            .build();

    private static ImmutableListMultimap<String, String> biMapConversionDetectionAutorise = new ImmutableListMultimap.Builder<String, String>()
            // RM001 - Formats (ou extensions) de fichiers autorisés
            // Correspond à toutes les extensions possible pour une conversion
            // Correspond également à toutes les conversions possibles avec moteur LIBREOFFICE vu que toutes acceptées
            .putAll(biDefautMsOffice)
            .putAll(biDefautLibreOffice)

            .build();

    private static ImmutableListMultimap<String, String> biMapMsOfficeAutorise = new ImmutableListMultimap.Builder<String, String>()
            // RM001 - Formats (ou extensions) de fichiers autorisés
            // Correspond aux conversions possibles avec MSOFFICE
            .putAll(biDefautMsOffice)
            .put("txt", TEXT)
            .put("htm", HTML)
            .put("html", HTML)

            .build();

    public static Optional<String> obtenirExtensionAvecNomFichier(String filename) {
        return Optional.ofNullable(filename).filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf('.') + 1));
    }

    public static String obtenirMimesAvecNomFichier(String fileName) {
        if(fileName==null) return null;
        String mimeType = fileTypeMap.getContentType( fileName.toLowerCase());
        return (typeDeMimeEstAutorisePourStockage(mimeType)) ? mimeType : null;
    }

    public static List<String> obtenirExtensionsAvecMime(String mimeType) {
        if (!StringUtils.isEmpty(mimeType)) {
            return biMapStockageAutorise.inverse().get(mimeType);
        }
        return Collections.emptyList();
    }

    public static List<String> obtenirMimesAvecExtension(String extension) {
        if (!StringUtils.isEmpty(extension)) {
            return biMapStockageAutorise.get(extension);
        }
        return Collections.emptyList();
    }

    public static String genererNouveauNomFichier(String filename, String newExt) {
        StringBuilder str = new StringBuilder();
        Optional<String> ext = obtenirExtensionAvecNomFichier(filename);
        if (ext.isPresent()) {
            str.append(filename.replace(ext.get(), newExt));
        } else {
            str.append(filename);
            str.append(".");
            str.append(newExt);
        }
        return str.toString();
    }

    public static boolean nomFichierEstAutorisePourStockage(String fileName) {
        Optional<String> optionalExt = obtenirExtensionAvecNomFichier(fileName);
        return !optionalExt.isPresent() || !typeExtensionEstAutorisePourStockage(optionalExt.get());
    }

    public static boolean typeDeMimeEstAutorisePourStockage(String mimeType) {
        if (!StringUtils.isEmpty(mimeType)) {
            return biMapStockageAutorise.inverse().containsKey(mimeType);
        }
        return false;
    }

    public static boolean typeExtensionEstAutorisePourStockage(String ext) {
        if (!StringUtils.isEmpty(ext)) {
            return biMapStockageAutorise.containsKey(ext);
        }
        return false;
    }



    public static boolean mimeEstMsOfficeDefault(String mimeType) {
        if (!StringUtils.isEmpty(mimeType)) {
            return biDefautMsOffice.inverse().containsKey(mimeType);
        }
        return false;
    }

    public static Boolean contenuCorrespondMimeType(InputStream is, String filename) throws IOException, TikaException {
        TikaConfig tika = new TikaConfig();
        Metadata metadata = new Metadata();

        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        String mimeType = fileTypeMap.getContentType(filename.toLowerCase());

        metadata.add(RESOURCE_NAME_KEY, filename);
        MediaType typeDetect = tika.getDetector().detect(TikaInputStream.get(is), metadata);
        return mimeType.equalsIgnoreCase(typeDetect.toString());
    }


    public static Boolean verifierTailleSous(double taille, Integer sousEnNbMb) {
        return taille / (1024 * 1024) < sousEnNbMb;
    }

    public static boolean estUnPdf(File fichier) {

        try {
            String mimeType = tika.detect(fichier);
            return PDF.equalsIgnoreCase(mimeType);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean supprimerFichier(String cheminAbsoluFichier) {

        Path filePath = Paths.get(cheminAbsoluFichier);
        try {
            Files.delete(filePath);
            return true;
        } catch (IOException ioException) {
            return false;
        }
    }

    public static MultipartFile decodeBase64Multipart(MultipartFile multipartFile, String encoding) throws IOException {

        if (BASE64.equals(encoding) && isBase64Encoded(multipartFile.getBytes())){
            byte[] decodedBytes = Base64.getMimeDecoder().decode(multipartFile.getBytes());
            return new MockMultipartFile(multipartFile.getName(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), decodedBytes);
        } else {
            return multipartFile;
        }
    }

    public static MultipartFile[] decodeBase64Multipart(MultipartFile[] multipartFiles, String encoding) throws IOException {
        if (BASE64.equals(encoding)) {
            MultipartFile[] multipartFilesDecode = new MultipartFile[multipartFiles.length];
            int i = 0;
            for (MultipartFile multipartFile : multipartFiles) {
                multipartFilesDecode[i] = decodeBase64Multipart(multipartFile, encoding);
                i++;
            }
            return multipartFilesDecode;
        } else {
            return multipartFiles;
        }
    }

    public static String obtenirNomSurResource(Resource resource) throws IOException {
        String retour = (resource.isFile()) ? resource.getFile().getName() : resource.getFilename();
        if (StringUtils.isEmpty(retour)) {
            throw new IOException("Nom sur Resource introuvable. " +
                    "Certainement dû à une utilisation : ByteArrayResource ou InputStreamResource");
        } else {
            return retour;
        }
    }

    /*
     * Etant donné un chemin de destination non null, son nettoyage consiste à ce que :
     *   - il commence par un /
     *   - il ne se termine pas par /
     * exemple:
     *     "   "  =>    "/"
     *     null   => null
     *    "/aaa/bbb ou /aaa/bbb/ ou aaa/bbb/ ou /////aaa////bbb/////"   => "/aaa/bbb"
     *   "/aaa/bbb    /ccc/ /"   => "/aaa/bbb    /ccc"
     * */
    public static String nettoyerCheminDestination(String cheminDestination) {

        String cheminComplet = cheminDestination;
        if (!org.springframework.util.StringUtils.isEmpty(cheminComplet)) {
            List<String> splitted = Arrays.asList(cheminComplet.split("/"));

            cheminComplet = splitted.stream()
                    .filter(a -> a.trim().length() > 0)
                    .map(Object::toString)
                    .collect(Collectors.joining("/"));
            cheminComplet = "/".concat(cheminComplet);
        }

        return cheminComplet;
    }

    public static MultipartFile getMultipart(File file) throws IOException {
        return getMultipart(file, file.getName(), tika.detect(file));
    }

    public static MultipartFile getMultipart(File file, String name, String mimeType) throws IOException {
        // content
        Path path = file.toPath();
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile(name, name, mimeType, content);
    }

    /**
     * Si la resource a été construite avec un fichier, retourne ce fichier
     * Sinon, crée un fichier temporaire et copie le contenu du stream dans ce fichier.
     *
     * @param resource
     * @return
     * @throws IOException
     */
    public static File getFileFromResource(Resource resource) throws IOException {
        File file = null;
        if (resource != null && resource.isFile()) {
            file = resource.getFile();
        } else if (resource != null){
            file = File.createTempFile(TMP_FILE_PREFIX, null);
            FileUtils.copyInputStreamToFile(resource.getInputStream(), file);
        }
        return file;
    }

    public static File getFileFromResource(Resource resource, String suffix) throws IOException {
        File file = null;
        if (resource != null && resource.isFile()) {
            file = resource.getFile();
        } else if (resource != null){
            file = File.createTempFile(TMP_FILE_PREFIX, suffix);
            FileUtils.copyInputStreamToFile(resource.getInputStream(), file);
        }
        return file;
    }

    public static File getFileFromResource(Resource resource, String suffix, String targerDir) throws IOException {
        File file = null;
        if (resource != null && resource.isFile()) {
            file = resource.getFile();
        } else if (resource != null){
            file = File.createTempFile(TMP_FILE_PREFIX + '_', suffix, new File(targerDir));
            FileUtils.copyInputStreamToFile(resource.getInputStream(), file);
        }
        return file;
    }

    public static File getFileFromResource(Resource resource, String baseName, String suffix, String targerDir) throws IOException {
        File file = null;
        if (resource != null && resource.isFile()) {
            file = resource.getFile();
        } else if (resource != null){
            file = File.createTempFile(baseName + '_', suffix, new File(targerDir));
            FileUtils.copyInputStreamToFile(resource.getInputStream(), file);
        }
        return file;
    }

    public static PathInfo splitPath(String destPath) {
        String[] parts = destPath.split("/(?!.*/)");
        if (parts.length > 1) {
            return new PathInfo(parts[0], parts[1]);
        } else {
            return new PathInfo(parts[0]);
        }
    }

    public static class PathInfo {
        private String parentPath;
        private String fileName;

        public PathInfo(String parentPath, String fileName) {
            this.parentPath = parentPath;
            this.fileName = fileName;
        }

        public PathInfo(String fileName) {
            this.fileName = fileName;
        }

        public String getParentPath() {
            return parentPath;
        }

        public void setParentPath(String parentPath) {
            this.parentPath = parentPath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    public static String obtenirMimeType(String nomDocument) {
        Optional<String> ext = FichierUtils.obtenirExtensionAvecNomFichier(nomDocument);
        if (ext.isPresent()) {
            List<String> list = FichierUtils.obtenirMimesAvecExtension(ext.get());
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    /*Étant donne un nom  de fichier, retourne le nom sans l'extension
     * null  -> null
     * monFichier.xml   -> monFichier
     * monFichier.2.xml -> monFichier.2
     * monFichier       -> monFichier
     * monFichier.      -> monFichier
     * */
    public static String obtenirNomFichierSansExtension(String nomDocument) {
        return FilenameUtils.getBaseName(nomDocument);
    }

    /*Dézippe une archive et retourne repertoire d'extraction*/
    public static void extraireArchive(String fichierZip, String repertoireDestination) throws IOException {
        // crée le répertoire de destination s'il n'existe pas
        Files.createDirectories(Paths.get(repertoireDestination));
        byte[] buffer = new byte[1024];
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fichierZip))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {

                String fileName = zipEntry.getName();
                File nouveauFichier = new File(repertoireDestination + File.separator + fileName);

                if (zipEntry.isDirectory()) {
                    if (!nouveauFichier.isDirectory() && !nouveauFichier.mkdirs()) {
                        throw new IOException("Impossible de créer le répertoire " + nouveauFichier);
                    }
                } else {

                    File parent = nouveauFichier.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Impossible de créer le répertoire" + parent);
                    }


                    try (FileOutputStream fileOutputStream = new FileOutputStream(nouveauFichier);) {

                        int taille;
                        while ((taille = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, taille);
                        }
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }


    }

    // utiliser listerRepertoireAsList de FichierBizService

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public static Collection<File> listerRepertoire(String repertoireCible, String[] extensions, boolean recursive) {
        File repertoire = new File(repertoireCible);
        if (repertoire.exists() && repertoire.isDirectory()) {
            return FileUtils.listFiles(repertoire, extensions, recursive);
        }
        return Collections.emptyList();

    }

    public static List<File> toList(File[] files){
        List<File> fileList = new ArrayList<>();
        if (files != null) {
            CollectionUtils.addAll(fileList, files);
        }
        return fileList;
    }

    public static File[] toArray(List<File> files){
        if (files != null){
            return files.stream().toArray(File[]::new);
        }
        return new File[]{};
    }

    public static File[] filtrer(File[] files, Predicate<? super File> predicate){
        return toArray(
                toList(files)
                        .stream()
                        .filter(predicate)
                        .collect(Collectors.toList()
                        )
        );
    }

    public static Resource toResource(File file) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(file);
        return new ByteArrayResource(bytes);
    }

    public static boolean  isBase64Encoded(byte[] bytes) {
        return  org.apache.commons.codec.binary.Base64.isBase64( bytes);
    }

    public static byte[]  readBytes(InputStream inputStream) throws IOException {
        return IOUtils.toByteArray(inputStream);
        //return  FileUtils.readFileToByteArray(inputStream);
    }

}
