package com.angolodivino.menu;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import org.springframework.stereotype.Component;

/**
 * Writes a complete file beside its destination and publishes it with a move.
 * Keeping the temporary file on the same filesystem makes an atomic move possible.
 */
@Component
public class AtomicJsonFileWriter {

    public boolean write(Path target, byte[] content, boolean replaceExisting) throws IOException {
        Path parent = target.getParent();
        if (parent == null) {
            throw new IOException("The target must have a parent directory: " + target);
        }
        Files.createDirectories(parent);

        Path temporary = Files.createTempFile(parent, "." + target.getFileName(), ".tmp");
        try {
            try (FileChannel channel = FileChannel.open(
                    temporary,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                channel.write(ByteBuffer.wrap(content));
                channel.force(true);
            }
            return moveIntoPlace(temporary, target, replaceExisting);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    protected boolean moveIntoPlace(Path temporary, Path target, boolean replaceExisting) throws IOException {
        try {
            move(temporary, target, replaceExisting, true);
            return true;
        } catch (AtomicMoveNotSupportedException e) {
            try {
                move(temporary, target, replaceExisting, false);
                return true;
            } catch (FileAlreadyExistsException alreadyExists) {
                return false;
            }
        } catch (FileAlreadyExistsException e) {
            return false;
        }
    }

    private static void move(Path source, Path target, boolean replaceExisting, boolean atomic) throws IOException {
        if (replaceExisting && atomic) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } else if (replaceExisting) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } else if (atomic) {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } else {
            Files.move(source, target);
        }
    }
}
