UPDATE works
SET genre = CASE
        WHEN sub_genre IN (
            'truyen_co_tich',
            'su_thi',
            'su_thi_dan_gian',
            'truyen_tho_dan_gian',
            'Truyện cổ tích',
            'Sử thi',
            'Truyện thơ dân gian'
        )
            OR genre IN ('truyen_dan_gian', 'su_thi', 'Truyện dân gian')
            THEN 'Truyện dân gian'
        WHEN genre IN ('khao_cuu', 'van_chinh_luan')
            OR sub_genre IN ('van_chinh_luan', 'Văn chính luận')
            THEN 'Ký'
        WHEN genre IN ('tho_ca', 'truyen_tho', 'Thơ ca') THEN 'Thơ ca'
        WHEN genre IN ('tieu_thuyet', 'Tiểu thuyết') THEN 'Tiểu thuyết'
        WHEN genre IN ('truyen_ngan', 'Truyện ngắn') THEN 'Truyện ngắn'
        WHEN genre IN ('kich', 'Kịch') THEN 'Kịch'
        WHEN genre IN ('ky', 'Ký') THEN 'Ký'
        ELSE genre
    END,
    sub_genre = CASE
        WHEN sub_genre IN ('truyen_ngan_hien_thuc', 'Truyện ngắn hiện thực') THEN 'Truyện ngắn hiện thực'
        WHEN sub_genre IN ('truyen_ngan_lang_man', 'Truyện ngắn lãng mạn') THEN 'Truyện ngắn lãng mạn'
        WHEN sub_genre IN ('truyen_ngan_trao_phung', 'Truyện ngắn trào phúng') THEN 'Truyện ngắn trào phúng'
        WHEN sub_genre IN ('truyen_ngan_tam_ly', 'Truyện ngắn tâm lý') THEN 'Truyện ngắn tâm lý'
        WHEN sub_genre IN ('tieu_thuyet_hien_thuc', 'Tiểu thuyết hiện thực') THEN 'Tiểu thuyết hiện thực'
        WHEN sub_genre IN ('tieu_thuyet_lich_su', 'Tiểu thuyết lịch sử') THEN 'Tiểu thuyết lịch sử'
        WHEN sub_genre IN ('tieu_thuyet_tam_ly', 'Tiểu thuyết tâm lý') THEN 'Tiểu thuyết tâm lý'
        WHEN sub_genre IN ('tieu_thuyet_chien_tranh', 'Tiểu thuyết chiến tranh') THEN 'Tiểu thuyết chiến tranh'
        WHEN sub_genre IN ('tho_tu_do', 'Thơ tự do') THEN 'Thơ tự do'
        WHEN sub_genre IN ('tho_duong_luat', 'that_ngon_tu_tuyet', 'tho_nom_duong_luat', 'Thơ Đường luật') THEN 'Thơ Đường luật'
        WHEN sub_genre IN ('tho_luc_bat', 'luc_bat', 'truyen_tho_nom', 'Thơ lục bát') THEN 'Thơ lục bát'
        WHEN sub_genre IN ('that_ngon_bat_cu', 'Thơ thất ngôn bát cú') THEN 'Thơ thất ngôn bát cú'
        WHEN sub_genre IN ('but_ky', 'Bút ký') THEN 'Bút ký'
        WHEN sub_genre IN ('tuy_but', 'Tùy bút') THEN 'Tùy bút'
        WHEN sub_genre IN ('phong_su', 'Phóng sự') THEN 'Phóng sự'
        WHEN sub_genre IN ('hoi_ky', 'hoi_ki', 'Hồi ký') THEN 'Hồi ký'
        WHEN sub_genre IN ('kich_noi', 'Kịch nói') THEN 'Kịch nói'
        WHEN sub_genre IN ('bi_kich', 'Bi kịch') THEN 'Bi kịch'
        WHEN sub_genre IN ('hai_kich', 'Hài kịch') THEN 'Hài kịch'
        WHEN sub_genre IN ('truyen_co_tich', 'Truyện cổ tích') THEN 'Truyện cổ tích'
        WHEN sub_genre IN ('su_thi', 'su_thi_dan_gian', 'Sử thi') THEN 'Sử thi'
        WHEN sub_genre IN ('truyen_tho_dan_gian', 'Truyện thơ dân gian') THEN 'Truyện thơ dân gian'
        WHEN sub_genre IN ('ca_dao', 'Ca dao') THEN 'Ca dao'
        WHEN sub_genre IN ('song_that_luc_bat', 'Song thất lục bát') THEN 'Song thất lục bát'
        WHEN sub_genre IN ('truyen_ky', 'truyen_truyen_ky', 'Truyền kỳ') THEN 'Truyền kỳ'
        WHEN sub_genre IN ('van_chinh_luan', 'Văn chính luận') THEN 'Văn chính luận'
        WHEN sub_genre IN ('van_te', 'Văn tế') THEN 'Văn tế'
        ELSE sub_genre
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
        'kich',
        'ky',
        'Thơ ca',
        'Truyện ngắn',
        'Tiểu thuyết',
        'Kịch',
        'Ký',
        'Truyện dân gian'
    )
    OR sub_genre IN (
        'truyen_ngan_hien_thuc',
        'truyen_ngan_lang_man',
        'truyen_ngan_trao_phung',
        'truyen_ngan_tam_ly',
        'tieu_thuyet_hien_thuc',
        'tieu_thuyet_lich_su',
        'tieu_thuyet_tam_ly',
        'tieu_thuyet_chien_tranh',
        'tho_tu_do',
        'tho_duong_luat',
        'tho_luc_bat',
        'luc_bat',
        'that_ngon_bat_cu',
        'that_ngon_tu_tuyet',
        'tho_nom_duong_luat',
        'but_ky',
        'tuy_but',
        'phong_su',
        'hoi_ky',
        'hoi_ki',
        'kich_noi',
        'bi_kich',
        'hai_kich',
        'truyen_co_tich',
        'su_thi',
        'su_thi_dan_gian',
        'truyen_tho_dan_gian',
        'truyen_tho_nom',
        'ca_dao',
        'song_that_luc_bat',
        'truyen_ky',
        'truyen_truyen_ky',
        'van_chinh_luan',
        'van_te',
        'Truyện ngắn hiện thực',
        'Truyện ngắn lãng mạn',
        'Truyện ngắn trào phúng',
        'Truyện ngắn tâm lý',
        'Tiểu thuyết hiện thực',
        'Tiểu thuyết lịch sử',
        'Tiểu thuyết tâm lý',
        'Tiểu thuyết chiến tranh',
        'Thơ tự do',
        'Thơ Đường luật',
        'Thơ lục bát',
        'Thơ thất ngôn bát cú',
        'Bút ký',
        'Tùy bút',
        'Phóng sự',
        'Hồi ký',
        'Kịch nói',
        'Bi kịch',
        'Hài kịch',
        'Truyện cổ tích',
        'Sử thi',
        'Truyện thơ dân gian',
        'Ca dao',
        'Song thất lục bát',
        'Truyền kỳ',
        'Văn chính luận',
        'Văn tế'
    );

INSERT INTO authors (
    id,
    name,
    pen_name,
    slug,
    birth_year,
    death_year,
    period,
    bio,
    portrait_url,
    created_at,
    updated_at
)
VALUES
    (
        '10000000-0000-0000-0000-000000000101',
        'Thạch Lam',
        NULL,
        'thach_lam',
        1910,
        1942,
        'hien_dai',
        'Thạch Lam là cây bút tiêu biểu của Tự lực văn đoàn, nổi bật với truyện ngắn giàu chất trữ tình, hướng vào đời sống bình dị và những rung động tinh tế của con người.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000102',
        'Kim Lân',
        NULL,
        'kim_lan',
        1920,
        2007,
        'hien_dai',
        'Kim Lân là nhà văn am hiểu sâu sắc đời sống nông thôn Việt Nam, thường viết về người nông dân với giọng văn mộc mạc, cảm động và giàu tình người.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000103',
        'Bảo Ninh',
        NULL,
        'bao_ninh',
        1952,
        NULL,
        'hien_dai',
        'Bảo Ninh là nhà văn hiện đại viết nhiều về chiến tranh và ký ức hậu chiến, nổi bật với cách nhìn nội tâm, day dứt về thân phận con người.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000104',
        'Hoàng Phủ Ngọc Tường',
        NULL,
        'hoang_phu_ngoc_tuong',
        1937,
        2023,
        'hien_dai',
        'Hoàng Phủ Ngọc Tường là cây bút ký đặc sắc, kết hợp vốn tri thức văn hóa, lịch sử, địa lý với lối viết tài hoa và giàu chất trữ tình.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000105',
        'Lưu Quang Vũ',
        NULL,
        'luu_quang_vu',
        1948,
        1988,
        'hien_dai',
        'Lưu Quang Vũ là nhà viết kịch, nhà thơ tiêu biểu của văn học Việt Nam hiện đại, nổi bật với các vở kịch giàu tính triết lý và tinh thần phản biện xã hội.',
        NULL,
        NOW(),
        NOW()
    )
ON CONFLICT (slug) DO NOTHING;

INSERT INTO works (
    id,
    author_id,
    title,
    slug,
    original_title,
    genre,
    sub_genre,
    period,
    grade,
    semester,
    publish_year,
    summary,
    cover_url,
    is_published,
    view_count,
    historical_context,
    realistic_value,
    humanistic_value,
    artistic_value,
    famous_quote,
    quote_attribution,
    created_at,
    updated_at
)
VALUES
    (
        '20000000-0000-0000-0000-000000000101',
        (SELECT id FROM authors WHERE slug = 'thach_lam'),
        'Hai đứa trẻ',
        'hai-dua-tre-genre-seed',
        'Hai đứa trẻ',
        'Truyện ngắn',
        'Truyện ngắn lãng mạn',
        'hien_dai',
        11,
        1,
        1938,
        'Truyện ngắn giàu chất trữ tình, khắc họa phố huyện nghèo và niềm mong đợi âm thầm của những con người nhỏ bé.',
        NULL,
        TRUE,
        0,
        'Tác phẩm gắn với đời sống đô thị nhỏ trước Cách mạng tháng Tám, nơi nhịp sống nghèo nàn và tù đọng hiện lên qua một buổi chiều tàn.',
        'Phản ánh đời sống mòn mỏi, thiếu ánh sáng và thiếu cơ hội của người dân nơi phố huyện.',
        'Trân trọng những ước mơ nhỏ bé và khát vọng hướng tới ánh sáng của con người bình thường.',
        'Nghệ thuật miêu tả tâm trạng tinh tế, giọng văn trữ tình, hình ảnh ánh sáng và bóng tối giàu tính biểu tượng.',
        NULL,
        NULL,
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000102',
        (SELECT id FROM authors WHERE slug = 'kim_lan'),
        'Vợ nhặt',
        'vo-nhat-genre-seed',
        'Vợ nhặt',
        'Truyện ngắn',
        'Truyện ngắn hiện thực',
        'hien_dai',
        12,
        2,
        1962,
        'Truyện ngắn về tình huống nhặt vợ trong nạn đói, làm nổi bật sức sống và tình người trong hoàn cảnh khốc liệt.',
        NULL,
        TRUE,
        0,
        'Tác phẩm lấy bối cảnh nạn đói năm 1945, một biến cố lịch sử gây mất mát nặng nề cho người dân Việt Nam.',
        'Khắc họa cảnh đói nghèo, ranh giới mong manh giữa sự sống và cái chết trong xã hội cũ.',
        'Đề cao tình thương, khát vọng sống và niềm tin vào tương lai của những con người nghèo khổ.',
        'Xây dựng tình huống truyện độc đáo, ngôn ngữ đời thường và diễn biến tâm lý nhân vật tự nhiên.',
        NULL,
        NULL,
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000103',
        (SELECT id FROM authors WHERE slug = 'bao_ninh'),
        'Nỗi buồn chiến tranh',
        'noi-buon-chien-tranh-genre-seed',
        'Nỗi buồn chiến tranh',
        'Tiểu thuyết',
        'Tiểu thuyết chiến tranh',
        'hien_dai',
        12,
        2,
        1990,
        'Tiểu thuyết nhìn chiến tranh từ ký ức cá nhân, nhấn mạnh mất mát tinh thần và những ám ảnh hậu chiến.',
        NULL,
        TRUE,
        0,
        'Tác phẩm ra đời sau chiến tranh, khi văn học Việt Nam bắt đầu đi sâu hơn vào ký ức cá nhân và những chấn thương tinh thần.',
        'Phản ánh chiến tranh không chỉ bằng chiến công mà còn bằng mất mát, đổ vỡ và nỗi đau kéo dài sau chiến trận.',
        'Thể hiện sự cảm thông với thân phận người lính và nhu cầu được chữa lành của con người sau bạo lực.',
        'Kết cấu phi tuyến tính, dòng hồi ức đan xen, giọng văn trầm buồn và giàu suy tư.',
        NULL,
        NULL,
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000104',
        (SELECT id FROM authors WHERE slug = 'hoang_phu_ngoc_tuong'),
        'Ai đã đặt tên cho dòng sông?',
        'ai-da-dat-ten-cho-dong-song-genre-seed',
        'Ai đã đặt tên cho dòng sông?',
        'Ký',
        'Bút ký',
        'hien_dai',
        12,
        1,
        1981,
        'Bút ký giàu chất trữ tình và tri thức văn hóa, khám phá vẻ đẹp sông Hương trong quan hệ với thiên nhiên, lịch sử và con người Huế.',
        NULL,
        TRUE,
        0,
        'Tác phẩm được viết trong mạch cảm hứng khám phá bản sắc văn hóa Huế sau chiến tranh.',
        'Gợi ra chiều sâu địa lý, lịch sử và văn hóa của một dòng sông gắn với đời sống đô thị Huế.',
        'Thể hiện tình yêu quê hương, sự trân trọng di sản văn hóa và vẻ đẹp tinh thần của xứ Huế.',
        'Lối viết tài hoa, liên tưởng phong phú, kết hợp chất ký, chất thơ và tri thức văn hóa.',
        NULL,
        NULL,
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000105',
        (SELECT id FROM authors WHERE slug = 'luu_quang_vu'),
        'Hồn Trương Ba, da hàng thịt',
        'hon-truong-ba-da-hang-thit-genre-seed',
        'Hồn Trương Ba, da hàng thịt',
        'Kịch',
        'Kịch nói',
        'hien_dai',
        12,
        2,
        1981,
        'Vở kịch đặt ra vấn đề sống đúng với bản thân, sự thống nhất giữa thể xác và tâm hồn, giữa nhu cầu tồn tại và phẩm giá con người.',
        NULL,
        TRUE,
        0,
        'Tác phẩm thuộc giai đoạn sân khấu Việt Nam hiện đại quan tâm mạnh tới các vấn đề đạo đức, nhân cách và đời sống xã hội.',
        'Phản ánh nguy cơ tha hóa khi con người phải sống trong hoàn cảnh lệch lạc, không được là chính mình.',
        'Khẳng định nhu cầu sống trung thực, sống hài hòa giữa phần người bên trong và biểu hiện bên ngoài.',
        'Xung đột kịch giàu tính triết lý, đối thoại sắc bén và tình huống giả tưởng có sức khái quát.',
        NULL,
        NULL,
        NOW(),
        NOW()
    )
ON CONFLICT (slug) DO NOTHING;

INSERT INTO work_sections (
    id,
    work_id,
    number,
    title,
    content,
    word_count,
    content_type,
    created_at,
    updated_at
)
VALUES
    (
        '30000000-0000-0000-0000-000000000501',
        (SELECT id FROM works WHERE slug = 'hai-dua-tre-genre-seed'),
        1,
        'Không gian phố huyện lúc chiều tàn',
        $section$Phần mở đầu dựng lên bức tranh phố huyện nhỏ khi ngày sắp tắt. Âm thanh, màu sắc và nhịp sinh hoạt đều chậm rãi, gợi cảm giác buồn lặng. Không gian ấy không chỉ là bối cảnh mà còn là nền tâm trạng cho toàn bộ truyện.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000502',
        (SELECT id FROM works WHERE slug = 'hai-dua-tre-genre-seed'),
        2,
        'Những kiếp người nhỏ bé',
        $section$Các nhân vật nơi phố huyện đều sống bằng những công việc nhỏ nhoi. Mỗi người hiện lên với một mảnh đời nghèo khó, ít hy vọng nhưng vẫn lặng lẽ bám vào sinh hoạt thường ngày. Truyện không tạo xung đột gay gắt mà để nỗi buồn thấm dần qua đời sống.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000503',
        (SELECT id FROM works WHERE slug = 'hai-dua-tre-genre-seed'),
        3,
        'Tâm trạng của Liên',
        $section$Liên là điểm nhìn trung tâm của truyện. Qua cảm nhận của Liên, cảnh vật và con người phố huyện hiện lên vừa gần gũi vừa buồn thương. Nhân vật có tâm hồn nhạy cảm, biết thương người và biết nhận ra sự tù đọng của cuộc sống quanh mình.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000504',
        (SELECT id FROM works WHERE slug = 'hai-dua-tre-genre-seed'),
        4,
        'Ánh sáng và bóng tối',
        $section$Hệ thống hình ảnh ánh sáng và bóng tối tạo nên lớp nghĩa biểu tượng quan trọng. Bóng tối phủ lên phố huyện, còn các nguồn sáng nhỏ bé chỉ le lói, mong manh. Sự đối lập ấy làm nổi bật khát vọng được đổi thay của những con người nghèo.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000505',
        (SELECT id FROM works WHERE slug = 'hai-dua-tre-genre-seed'),
        5,
        'Chuyến tàu đêm',
        $section$Chuyến tàu xuất hiện như một vệt sáng rực rỡ đi qua phố huyện. Nó mang theo âm thanh, ký ức và hình ảnh của một thế giới khác. Với Liên và An, đợi tàu không chỉ là thói quen mà còn là cách níu giữ một niềm hy vọng mơ hồ.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000506',
        (SELECT id FROM works WHERE slug = 'hai-dua-tre-genre-seed'),
        6,
        'Giá trị trữ tình của truyện',
        $section$Sức hấp dẫn của truyện nằm ở giọng văn nhẹ nhàng, tinh tế và giàu chất thơ. Tác phẩm không đặt nặng cốt truyện mà chú ý đến cảm giác, tâm trạng và không khí. Qua đó, nhà văn thể hiện lòng thương với những kiếp sống âm thầm.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000507',
        (SELECT id FROM works WHERE slug = 'vo-nhat-genre-seed'),
        1,
        'Bối cảnh nạn đói',
        $section$Tác phẩm mở ra trong không khí nặng nề của nạn đói. Con người bị đẩy vào tình thế khốn cùng, sự sống trở nên mong manh. Bối cảnh ấy tạo nền cho tình huống truyện đặc biệt và làm nổi bật giá trị nhân đạo của tác phẩm.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000508',
        (SELECT id FROM works WHERE slug = 'vo-nhat-genre-seed'),
        2,
        'Tình huống nhặt vợ',
        $section$Việc Tràng có vợ diễn ra trong hoàn cảnh éo le, vừa bất ngờ vừa xót xa. Tình huống ấy làm bật lên sự rẻ rúng của thân phận con người trong đói nghèo, đồng thời mở ra khả năng thay đổi tâm lý và đời sống của các nhân vật.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000509',
        (SELECT id FROM works WHERE slug = 'vo-nhat-genre-seed'),
        3,
        'Nhân vật Tràng',
        $section$Tràng là người lao động nghèo, thô mộc nhưng giàu tình thương. Khi có gia đình, nhân vật bắt đầu ý thức rõ hơn về trách nhiệm và tương lai. Sự thay đổi trong Tràng cho thấy hoàn cảnh khắc nghiệt không dập tắt được khát vọng sống.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000510',
        (SELECT id FROM works WHERE slug = 'vo-nhat-genre-seed'),
        4,
        'Người vợ nhặt',
        $section$Người vợ nhặt ban đầu hiện lên trong vẻ chao chát do cái đói xô đẩy. Khi bước vào gia đình Tràng, nhân vật dần bộc lộ vẻ nữ tính, sự vun vén và mong muốn có một mái ấm. Đây là cách tác phẩm trả lại phẩm giá cho con người nghèo khổ.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000511',
        (SELECT id FROM works WHERE slug = 'vo-nhat-genre-seed'),
        5,
        'Bà cụ Tứ',
        $section$Bà cụ Tứ là hình ảnh người mẹ nghèo giàu lòng thương con. Tâm trạng nhân vật đan xen giữa ngạc nhiên, tủi cực, lo lắng và hy vọng. Qua bà cụ, tác phẩm thể hiện chiều sâu nhân đạo và vẻ đẹp tình thân trong hoàn cảnh đói nghèo.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000512',
        (SELECT id FROM works WHERE slug = 'vo-nhat-genre-seed'),
        6,
        'Niềm tin vào sự sống',
        $section$Dù bối cảnh đầy chết chóc, truyện vẫn hướng về sự sống. Bữa cơm ngày đói, câu chuyện tương lai và hình ảnh đổi thay cuối tác phẩm đều gợi ra niềm tin. Giá trị của truyện nằm ở khả năng phát hiện ánh sáng nhân phẩm trong bóng tối hiện thực.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000513',
        (SELECT id FROM works WHERE slug = 'noi-buon-chien-tranh-genre-seed'),
        1,
        'Ký ức chiến tranh',
        $section$Tác phẩm nhìn chiến tranh qua ký ức cá nhân, không đi theo trình tự sự kiện tuyến tính. Những mảnh hồi ức trở đi trở lại, tạo cảm giác chiến tranh chưa từng thật sự kết thúc trong tâm trí người lính trở về.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000514',
        (SELECT id FROM works WHERE slug = 'noi-buon-chien-tranh-genre-seed'),
        2,
        'Người lính sau chiến tranh',
        $section$Nhân vật trung tâm mang trong mình vết thương tinh thần nặng nề. Sau chiến tranh, đời sống không trở lại bình thường một cách dễ dàng. Tác phẩm nhấn mạnh phần mất mát bên trong, nơi ký ức, tình yêu và cảm giác tội lỗi cùng tồn tại.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000515',
        (SELECT id FROM works WHERE slug = 'noi-buon-chien-tranh-genre-seed'),
        3,
        'Tình yêu và chia lìa',
        $section$Tình yêu trong tác phẩm không tách khỏi chiến tranh. Nó bị gián đoạn, tổn thương và ám ảnh bởi bạo lực lịch sử. Qua tuyến tình cảm, tiểu thuyết cho thấy chiến tranh làm biến dạng cả những gì riêng tư và đẹp đẽ nhất của con người.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000516',
        (SELECT id FROM works WHERE slug = 'noi-buon-chien-tranh-genre-seed'),
        4,
        'Kết cấu phi tuyến',
        $section$Tác phẩm được tổ chức theo dòng hồi ức đứt nối. Cách kể này phù hợp với trạng thái tâm lý của nhân vật, đồng thời tạo nên cái nhìn đa chiều về chiến tranh. Người đọc tiếp cận câu chuyện qua những mảnh vỡ ký ức hơn là qua một cốt truyện thẳng hàng.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000517',
        (SELECT id FROM works WHERE slug = 'noi-buon-chien-tranh-genre-seed'),
        5,
        'Cái nhìn phản tỉnh',
        $section$Tiểu thuyết không phủ nhận hy sinh, nhưng đặt trọng tâm vào cái giá tinh thần của chiến tranh. Cái nhìn phản tỉnh giúp tác phẩm mở rộng biên độ nhận thức, từ câu chuyện chiến trận sang câu chuyện về ký ức, nhân tính và khả năng hồi phục.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000518',
        (SELECT id FROM works WHERE slug = 'noi-buon-chien-tranh-genre-seed'),
        6,
        'Giá trị nghệ thuật',
        $section$Ngôn ngữ tác phẩm giàu chất suy tư, nhiều đoạn mang sắc thái trữ tình và u buồn. Kết cấu dòng ý thức, hình ảnh ám ảnh và giọng kể nội tâm góp phần tạo nên một cách viết khác về chiến tranh trong văn học hiện đại.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000519',
        (SELECT id FROM works WHERE slug = 'ai-da-dat-ten-cho-dong-song-genre-seed'),
        1,
        'Dòng sông trong thiên nhiên',
        $section$Tác phẩm trước hết khám phá sông Hương như một thực thể của thiên nhiên. Dòng sông hiện lên với nhiều dáng vẻ, khi phóng khoáng ở thượng nguồn, khi mềm mại trong không gian thành phố. Cách miêu tả giàu liên tưởng làm nổi bật vẻ đẹp riêng của Huế.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000520',
        (SELECT id FROM works WHERE slug = 'ai-da-dat-ten-cho-dong-song-genre-seed'),
        2,
        'Dòng sông trong lịch sử',
        $section$Sông Hương không chỉ là cảnh quan mà còn là chứng nhân lịch sử. Tác phẩm gợi lại nhiều lớp ký ức gắn với vùng đất cố đô, từ chiến tranh, văn hóa đến đời sống cộng đồng. Nhờ đó dòng sông mang chiều sâu thời gian.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000521',
        (SELECT id FROM works WHERE slug = 'ai-da-dat-ten-cho-dong-song-genre-seed'),
        3,
        'Dòng sông trong văn hóa Huế',
        $section$Bút ký đặt sông Hương trong mối quan hệ với âm nhạc, thơ ca, kiến trúc và phong cách sống Huế. Dòng sông trở thành nơi kết tinh bản sắc văn hóa, vừa cổ kính vừa dịu dàng, vừa gần gũi vừa có sức gợi tri thức.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000522',
        (SELECT id FROM works WHERE slug = 'ai-da-dat-ten-cho-dong-song-genre-seed'),
        4,
        'Cái tôi tài hoa của tác giả',
        $section$Người viết xuất hiện qua vốn hiểu biết rộng và khả năng liên tưởng phong phú. Cái tôi trong bút ký vừa say mê khám phá, vừa trân trọng vẻ đẹp văn hóa dân tộc. Đây là yếu tố làm nên phong cách riêng của tác phẩm.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000523',
        (SELECT id FROM works WHERE slug = 'ai-da-dat-ten-cho-dong-song-genre-seed'),
        5,
        'Chất ký và chất thơ',
        $section$Tác phẩm kết hợp thông tin địa lý, lịch sử với ngôn ngữ giàu nhạc tính và hình ảnh. Chất ký giúp bài viết có nền tri thức vững, còn chất thơ tạo cảm xúc mềm mại. Sự kết hợp này làm dòng sông trở nên sống động và có linh hồn.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000524',
        (SELECT id FROM works WHERE slug = 'ai-da-dat-ten-cho-dong-song-genre-seed'),
        6,
        'Tình yêu xứ Huế',
        $section$Đằng sau tri thức và liên tưởng là tình yêu sâu nặng dành cho Huế. Tác phẩm không chỉ giới thiệu một dòng sông mà còn khẳng định vẻ đẹp tinh thần của một vùng đất. Đây là giá trị cảm xúc bền vững của bút ký.$section$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000525',
        (SELECT id FROM works WHERE slug = 'hon-truong-ba-da-hang-thit-genre-seed'),
        1,
        'Tình huống kịch',
        $section$Vở kịch đặt nhân vật vào tình huống trái tự nhiên: phần hồn và phần xác không thống nhất. Từ tình huống giả tưởng ấy, tác phẩm triển khai những xung đột đạo đức và triết lý về cách sống của con người.$section$,
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000526',
        (SELECT id FROM works WHERE slug = 'hon-truong-ba-da-hang-thit-genre-seed'),
        2,
        'Xung đột giữa hồn và xác',
        $section$Cuộc đối thoại giữa hồn Trương Ba và xác hàng thịt cho thấy sự giằng co giữa đời sống tinh thần và nhu cầu bản năng. Xác không chỉ là vỏ bọc thụ động mà tác động mạnh đến cách sống, suy nghĩ và quan hệ của nhân vật.$section$,
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000527',
        (SELECT id FROM works WHERE slug = 'hon-truong-ba-da-hang-thit-genre-seed'),
        3,
        'Bi kịch không được là mình',
        $section$Bi kịch của Trương Ba là phải tồn tại trong một hình hài xa lạ, khiến bản thân và người thân đều đau khổ. Tác phẩm đặt ra câu hỏi: sống chỉ để tồn tại sinh học có đủ không, nếu con người đánh mất sự toàn vẹn nhân cách?$section$,
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000528',
        (SELECT id FROM works WHERE slug = 'hon-truong-ba-da-hang-thit-genre-seed'),
        4,
        'Gia đình và sự nhận diện',
        $section$Những người thân của Trương Ba cảm nhận rõ sự thay đổi trong ông. Các mối quan hệ gia đình trở thành tấm gương phản chiếu sự lệch lạc của đời sống hiện tại. Qua đó, xung đột kịch chuyển từ bên trong nhân vật ra đời sống xung quanh.$section$,
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000529',
        (SELECT id FROM works WHERE slug = 'hon-truong-ba-da-hang-thit-genre-seed'),
        5,
        'Lựa chọn cuối cùng',
        $section$Quyết định từ bỏ sự tồn tại vay mượn thể hiện khát vọng sống đúng với mình. Tác phẩm khẳng định con người cần được sống hài hòa giữa thể xác và tâm hồn, giữa nhu cầu cá nhân và phẩm giá tinh thần.$section$,
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000530',
        (SELECT id FROM works WHERE slug = 'hon-truong-ba-da-hang-thit-genre-seed'),
        6,
        'Ý nghĩa triết lý',
        $section$Vở kịch có sức khái quát bởi nó không chỉ kể một câu chuyện kỳ ảo mà bàn về nhân cách. Con người không thể sống bằng mọi giá; sống có ý nghĩa đòi hỏi sự trung thực với bản thân và trách nhiệm với người khác.$section$,
        NULL,
        'MIXED',
        NOW(),
        NOW()
    )
ON CONFLICT (work_id, number) DO NOTHING;
