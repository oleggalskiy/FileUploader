package my.edu.uploader.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;


@Setter
@Slf4j
@Component
public class MultiThreadedFileCopyService extends Service<Void> implements Uploader {

    private File sourceFile;
    private File destFile;
    private final ReentrantLock fileLock = new ReentrantLock();

    @Override
    protected Task<Void> createTask() {
        return new UploadTask(sourceFile, destFile, fileLock);
    }

    private static class UploadTask extends Task<Void> {
        private final File sourceFile;
        private final File destFile;
        private final ReentrantLock fileLock;

        public UploadTask(File sourceFile, File destFile, ReentrantLock fileLock) {
            this.sourceFile = sourceFile;
            this.destFile = destFile;
            this.fileLock = fileLock;
        }

        @Override
        protected Void call() throws Exception {
            if (sourceFile == null || destFile == null) {
                throw new IllegalArgumentException("Source and destination files must be set");
            }

            long totalBytes = sourceFile.length();
            int numThreads = Runtime.getRuntime().availableProcessors();
            long chunkSize = totalBytes / numThreads;
            AtomicLong totalBytesCopied = new AtomicLong(0);

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                final long start = i * chunkSize;
                final long end = (i == numThreads - 1) ? totalBytes : start + chunkSize;
                futures.add(executor.submit(() -> {
                    try (RandomAccessFile srcRAF = new RandomAccessFile(this.sourceFile, "r");
                         RandomAccessFile destRAF = new RandomAccessFile(this.destFile, "rw")) {
                        srcRAF.seek(start);
                        destRAF.seek(start);

                        byte[] buffer = new byte[8192]; // standard buffer size 8 kb = 8192
                        long bytesCopied = 0;
                        int bytesRead;

                        while (!isCancelled() && bytesCopied < (end - start) && (bytesRead = srcRAF.read(buffer, 0, (int) Math.min(buffer.length, (end - start - bytesCopied)))) != -1) {
                            destRAF.write(buffer, 0, bytesRead);
                            bytesCopied += bytesRead;

                            long totalCopied = totalBytesCopied.addAndGet(bytesRead);
                            updateProgress(totalCopied, totalBytes);
                        }
                    } catch (IOException e) {
                        log.error("Error while copy chunk of file : {}", e.getMessage());
                        throw e;
                    }
                    return null;
                }));
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    if (isCancelled()) {
                        log.info("Task was cancelled. Deleting partially copied file.");
                        deleteFileWithRetry(destFile);
                        break;
                    } else {
                        throw e;
                    }
                }
            }
            executor.shutdown();
            return null;
        }

        @Override
        protected void cancelled() {
            deleteFileWithRetry(destFile);
            log.info("Task cancelled. Deleting partially copied file.");
        }

        private void deleteFileWithRetry(File file) {
            fileLock.lock();
            try {
                boolean deleted = false;
                for (int i = 0; i < 5; i++) {
                    if (file.delete()) {
                        deleted = true;
                        break;
                    }
                    Thread.sleep(100);
                }
                if (!deleted) {
                    log.error("Failed to delete file: {}", file.getAbsolutePath());
                }
            } catch (InterruptedException e) {
                log.error("Error during file deletion retry: {}", e.getMessage());
            } finally {
                fileLock.unlock();
            }
        }
    }
}
