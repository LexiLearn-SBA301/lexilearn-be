package com.sba.lexilearnbe.modules.reading.entity;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkSection;
import com.sba.lexilearnbe.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    // Đây là field dùng để join với bảng accounts có tác dụng là một bookmark thuộc về một account nào,
    // giúp xác định người dùng đang đọc tác phẩm nào và lưu trữ tiến độ đọc của họ.

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;
    // Đây là field dùng để join với bảng works có tác dụng là một bookmark thuộc về một work nào,
    // giúp xác định tác phẩm nào đang được người dùng đọc và lưu trữ tiến độ đọc của họ.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_section_id")
    private WorkSection currentSection;
    // Đây là field dùng để join với bảng work_sections có tác dụng là một bookmark có thể liên kết với một section nào đó của tác phẩm,
    // giúp xác định người dùng đang đọc đến phần nào của tác phẩm và lưu trữ tiến độ đọc của họ một cách chi tiết hơn.

    @Column(nullable = false)
    private Integer position;
    // Đây là field dùng để lưu trữ vị trí đọc hiện tại của người dùng trong tác phẩm

    @Column(name = "progress_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPercent;
    // Đây là field dùng để lưu trữ phần trăm tiến độ đọc của người dùng trong tác phẩm,
    // được tính toán dựa trên position và tổng độ dài của tác phẩm, giúp người dùng dễ dàng theo dõi mức độ hoàn thành của mình.

    @Column(name = "is_completed", nullable = false)
    private Boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
