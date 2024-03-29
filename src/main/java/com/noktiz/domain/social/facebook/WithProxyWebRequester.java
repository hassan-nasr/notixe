package com.noktiz.domain.social.facebook;

import com.noktiz.domain.Utils.UrlContent;
import com.restfb.BinaryAttachment;
import com.restfb.WebRequestor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.logging.Logger;

import static com.restfb.util.StringUtils.ENCODING_CHARSET;
import static com.restfb.util.StringUtils.fromInputStream;
import static com.restfb.util.UrlUtils.urlDecode;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.WARNING;

/**
 * Created by hasan on 2014-12-10.
 */
public class WithProxyWebRequester implements WebRequestor {




    Proxy proxy;

    public WithProxyWebRequester() {
        proxy = UrlContent.getDefaultProxyAddress();
    }

    /**
     * Arbitrary unique boundary marker for multipart {@code POST}s.
     */
    private static final String MULTIPART_BOUNDARY = "**boundarystringwhichwill**neverbeencounteredinthewild**";

    /**
     * Line separator for multipart {@code POST}s.
     */
    private static final String MULTIPART_CARRIAGE_RETURN_AND_NEWLINE = "\r\n";

    /**
     * Hyphens for multipart {@code POST}s.
     */
    private static final String MULTIPART_TWO_HYPHENS = "--";

    /**
     * Default buffer size for multipart {@code POST}s.
     */
    private static final int MULTIPART_DEFAULT_BUFFER_SIZE = 8192;

    /**
     * By default, how long should we wait for a response (in ms)?
     */
    private static final int DEFAULT_READ_TIMEOUT_IN_MS = 180000;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("com.restfb.HTTP");

    /**
     * By default this is true, to prevent breaking existing usage
     */
    private boolean autocloseBinaryAttachmentStream = true;

    /**
     * @see com.restfb.WebRequestor#executeGet(java.lang.String)
     */
    @Override
    public Response executeGet(String url) throws IOException {
        if (logger.isLoggable(FINE))
            logger.fine("Making a GET request to " + url);

        HttpURLConnection httpUrlConnection = null;
        InputStream inputStream = null;

        try {
            httpUrlConnection = getHttpURLConnection(url);
            httpUrlConnection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
            httpUrlConnection.setUseCaches(false);

            // Allow subclasses to customize the connection if they'd like to - set
            // their own headers, timeouts, etc.
            customizeConnection(httpUrlConnection);

            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();

            if (logger.isLoggable(FINER))
                logger.finer("Response headers: " + httpUrlConnection.getHeaderFields());

            try {
                inputStream =
                        httpUrlConnection.getResponseCode() != HTTP_OK ? httpUrlConnection.getErrorStream() : httpUrlConnection
                                .getInputStream();
            } catch (IOException e) {
                if (logger.isLoggable(WARNING))
                    logger.warning(format("An error occurred while making a GET request to %s: %s", url, e));
            }

            Response response = new Response(httpUrlConnection.getResponseCode(), fromInputStream(inputStream));

            if (logger.isLoggable(FINE))
                logger.fine("Facebook responded with " + response);

            return response;
        } finally {
            closeQuietly(httpUrlConnection);
        }
    }

    private HttpURLConnection getHttpURLConnection(String url) throws IOException {
        if(proxy !=null)
            return (HttpURLConnection) new URL(url).openConnection(proxy);
        return (HttpURLConnection) new URL(url).openConnection();
    }

    /**
     * @see com.restfb.WebRequestor#executePost(java.lang.String, java.lang.String)
     */
    @Override
    public Response executePost(String url, String parameters) throws IOException {
        return executePost(url, parameters, (BinaryAttachment[]) null);
    }

    /**
     * @see com.restfb.WebRequestor#executePost(java.lang.String, java.lang.String, com.restfb.BinaryAttachment[])
     */
    @Override
    public Response executePost(String url, String parameters, BinaryAttachment... binaryAttachments) throws IOException {
        if (binaryAttachments == null)
            binaryAttachments = new BinaryAttachment[] {};

        if (logger.isLoggable(FINE))
            logger.fine("Executing a POST to " + url + " with parameters "
                    + (binaryAttachments.length > 0 ? "" : "(sent in request body): ") + urlDecode(parameters)
                    + (binaryAttachments.length > 0 ? " and " + binaryAttachments.length + " binary attachment[s]." : ""));

        HttpURLConnection httpUrlConnection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            httpUrlConnection = getHttpURLConnection(url + (binaryAttachments.length > 0 ? "?" + parameters : ""));
            httpUrlConnection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);

            // Allow subclasses to customize the connection if they'd like to - set
            // their own headers, timeouts, etc.
            customizeConnection(httpUrlConnection);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setUseCaches(false);

            if (binaryAttachments.length > 0) {
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + MULTIPART_BOUNDARY);
            }

            httpUrlConnection.connect();
            outputStream = httpUrlConnection.getOutputStream();

            // If we have binary attachments, the body is just the attachments and the
            // other parameters are passed in via the URL.
            // Otherwise the body is the URL parameter string.
            if (binaryAttachments.length > 0) {
                for (BinaryAttachment binaryAttachment : binaryAttachments) {
                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append(MULTIPART_TWO_HYPHENS).append(MULTIPART_BOUNDARY)
                            .append(MULTIPART_CARRIAGE_RETURN_AND_NEWLINE).append("Content-Disposition: form-data; name=\"")
                            .append(createFormFieldName(binaryAttachment)).append("\"; filename=\"")
                            .append(binaryAttachment.getFilename()).append("\"");

                    if (binaryAttachment.getContentType() != null && binaryAttachment.getContentType().length() > 0)
                        stringBuilder.append(MULTIPART_CARRIAGE_RETURN_AND_NEWLINE).append("Content-Type: ")
                                .append(binaryAttachment.getContentType());

                    stringBuilder.append(MULTIPART_CARRIAGE_RETURN_AND_NEWLINE).append(MULTIPART_CARRIAGE_RETURN_AND_NEWLINE);

                    outputStream.write(stringBuilder.toString().getBytes(ENCODING_CHARSET));

                    write(binaryAttachment.getData(), outputStream, MULTIPART_DEFAULT_BUFFER_SIZE);

                    outputStream.write((MULTIPART_CARRIAGE_RETURN_AND_NEWLINE + MULTIPART_TWO_HYPHENS + MULTIPART_BOUNDARY
                            + MULTIPART_TWO_HYPHENS + MULTIPART_CARRIAGE_RETURN_AND_NEWLINE).getBytes(ENCODING_CHARSET));
                }
            } else {
                outputStream.write(parameters.getBytes(ENCODING_CHARSET));
            }

            if (logger.isLoggable(FINER))
                logger.finer("Response headers: " + httpUrlConnection.getHeaderFields());

            try {
                inputStream =
                        httpUrlConnection.getResponseCode() != HTTP_OK ? httpUrlConnection.getErrorStream() : httpUrlConnection
                                .getInputStream();
            } catch (IOException e) {
                if (logger.isLoggable(WARNING))
                    logger.warning("An error occurred while POSTing to " + url + ": " + e);
            }

            return new Response(httpUrlConnection.getResponseCode(), fromInputStream(inputStream));
        } finally {
            if (autocloseBinaryAttachmentStream && binaryAttachments.length > 0)
                for (BinaryAttachment binaryAttachment : binaryAttachments)
                    closeQuietly(binaryAttachment.getData());

            closeQuietly(outputStream);
            closeQuietly(httpUrlConnection);
        }
    }

    /**
     * Given a {@code url}, opens and returns a connection to it.
     * <p>
     * If you'd like to pipe your connection through a proxy, this is the place to do so.
     *
     * @param url
     *          The URL to connect to.
     * @return A connection to the URL.
     * @throws IOException
     *           If an error occurs while establishing the connection.
     * @since 1.6.3
     */
    protected HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection(proxy);
    }

    /**
     * Hook method which allows subclasses to easily customize the {@code connection}s created by
     * {@link #executeGet(String)} and {@link #executePost(String, String)} - for example, setting a custom read timeout
     * or request header.
     * <p>
     * This implementation is a no-op.
     *
     * @param connection
     *          The connection to customize.
     */
    protected void customizeConnection(HttpURLConnection connection) {}

    /**
     * Attempts to cleanly close a resource, swallowing any exceptions that might occur since there's no way to recover
     * anyway.
     * <p>
     * It's OK to pass {@code null} in, this method will no-op in that case.
     *
     * @param closeable
     *          The resource to close.
     */
    protected void closeQuietly(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (Throwable t) {
            if (logger.isLoggable(WARNING))
                logger.warning("Unable to close " + closeable + ": " + t);
        }
    }

    /**
     * Attempts to cleanly close an {@code HttpURLConnection}, swallowing any exceptions that might occur since there's no
     * way to recover anyway.
     * <p>
     * It's OK to pass {@code null} in, this method will no-op in that case.
     *
     * @param httpUrlConnection
     *          The connection to close.
     */
    protected void closeQuietly(HttpURLConnection httpUrlConnection) {
        if (httpUrlConnection == null)
            return;
        try {
            httpUrlConnection.disconnect();
        } catch (Throwable t) {
            if (logger.isLoggable(WARNING))
                logger.warning("Unable to disconnect " + httpUrlConnection + ": " + t);
        }
    }

    /**
     * Writes the contents of the {@code source} stream to the {@code destination} stream using the given
     * {@code bufferSize}.
     *
     * @param source
     *          The source stream to copy from.
     * @param destination
     *          The destination stream to copy to.
     * @param bufferSize
     *          The size of the buffer to use during the copy operation.
     * @throws IOException
     *           If an error occurs when reading from {@code source} or writing to {@code destination}.
     * @throws NullPointerException
     *           If either {@code source} or @{code destination} is {@code null}.
     */
    protected void write(InputStream source, OutputStream destination, int bufferSize) throws IOException {
        if (source == null || destination == null)
            throw new NullPointerException("Must provide non-null source and destination streams.");

        int read = 0;
        byte[] chunk = new byte[bufferSize];
        while ((read = source.read(chunk)) > 0)
            destination.write(chunk, 0, read);
    }

    /**
     * Creates the form field name for the binary attachment filename by stripping off the file extension - for example,
     * the filename "test.png" would return "test".
     *
     * @param binaryAttachment
     *          The binary attachment for which to create the form field name.
     * @return The form field name for the given binary attachment.
     */
    protected String createFormFieldName(BinaryAttachment binaryAttachment) {
        String name = binaryAttachment.getFilename();
        int fileExtensionIndex = name.lastIndexOf(".");
        return fileExtensionIndex > 0 ? name.substring(0, fileExtensionIndex) : name;
    }

    /**
     * returns if the binary attachment stream is closed automatically
     *
     * @since 1.7.0
     * @return
     */
    public boolean isAutocloseBinaryAttachmentStream() {
        return autocloseBinaryAttachmentStream;
    }

    /**
     * define if the binary attachment stream is closed automatically after sending the content to facebook
     *
     * @since 1.7.0
     * @param autocloseBinaryAttachmentStream
     */
    public void setAutocloseBinaryAttachmentStream(boolean autocloseBinaryAttachmentStream) {
        this.autocloseBinaryAttachmentStream = autocloseBinaryAttachmentStream;
    }
}
