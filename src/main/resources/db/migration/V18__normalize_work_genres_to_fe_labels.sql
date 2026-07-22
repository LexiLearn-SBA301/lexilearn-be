UPDATE works
SET genre = CASE
        WHEN genre IN ('tho_ca', 'truyen_tho', 'Thơ ca') THEN 'Thơ ca'
        WHEN genre IN ('tieu_thuyet', 'Tiểu thuyết') THEN 'Tiểu thuyết'
        WHEN genre IN (
            'truyen_ngan',
            'truyen_dan_gian',
            'su_thi',
            'khao_cuu',
            'van_chinh_luan',
            'Truyện ngắn'
        ) THEN 'Truyện ngắn'
        ELSE genre
    END,
    updated_at = NOW()
WHERE genre IN (
        'tho_ca',
        'truyen_tho',
        'truyen_ngan',
        'truyen_dan_gian',
        'su_thi',
        'khao_cuu',
        'van_chinh_luan',
        'tieu_thuyet',
        'Thơ ca',
        'Truyện ngắn',
        'Tiểu thuyết'
    );
