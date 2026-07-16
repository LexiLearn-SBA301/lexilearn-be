package com.sba.lexilearnbe.modules.chat.client;

import java.io.Closeable;
import java.io.IOException;


public class StreamSession {
    // cancelled mặc định là false
    private volatile boolean cancelled;
    private volatile Closeable connection;
    // volatile tránh JVM/CPU được phép cache biến vào stack
    // mỗi thread -> thread relay đọc cancelled = false, thread /stop ghi cancelled = true -> thread relay vẫn block readLine.

    /** AI vừa mở kết nối -> giữ lại để có thể đóng khi bị huỷ. Nếu đã huỷ TRƯỚC đó -> đóng ngay. */
    public void attach(Closeable connection) {
        this.connection = connection;
        if (cancelled) {
            closeQuietly();
        }
    }

    /** /stop gọi: đánh dấu huỷ RỒI đóng kết nối BE↔AI (làm readLine đang block bung IOException). */
    public void cancel() {
        cancelled = true;
        closeQuietly();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    private void closeQuietly() {
        Closeable c = connection;
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignore) {
                // đóng để huỷ -> lỗi lúc đóng không quan trọng
            }
        }
    }
}
