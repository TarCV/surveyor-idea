package android.os

import java.io.FileDescriptor
import java.io.FileInputStream

class ParcelFileDescriptor(val descriptor: FileDescriptor) {
    class AutoCloseInputStream(descriptor: ParcelFileDescriptor): FileInputStream(descriptor.descriptor), AutoCloseable
}