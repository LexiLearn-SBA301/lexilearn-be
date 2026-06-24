-- ============================================================
-- V10: Seed dữ liệu test luồng đọc, bookmark, note/highlight
-- ============================================================

-- Test accounts
-- USER  : test.reader@lexilearn.local / password123
-- ADMIN : admin@gmail.com / Khanh@2005
INSERT INTO accounts (id, email, password_hash, status, email_verified_at, created_at, updated_at)
VALUES
    (
        '00000000-0000-0000-0000-000000000101',
        'test.reader@lexilearn.local',
        '$2b$10$hMxx5YP.Y4SgbZuUt4YCK.7nT60GNzO192zgIfByNt.ThH4.3fCKW',
        'ACTIVE',
        NOW(),
        NOW(),
        NOW()
    ),
    (
        '00000000-0000-0000-0000-000000000102',
        'admin@gmail.com',
        '$2b$10$3KOqC2jr78jCr4FB/BMeKOWcJezUFlTeEBHXbaGjp/l8gubXfFvPG',
        'ACTIVE',
        NOW(),
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;

INSERT INTO account_roles (account_id, role_id)
SELECT account.id, role.id
FROM accounts account
JOIN roles role ON role.name = 'USER'
WHERE LOWER(account.email) = 'test.reader@lexilearn.local'
ON CONFLICT (account_id, role_id) DO NOTHING;

INSERT INTO account_roles (account_id, role_id)
SELECT account.id, role.id
FROM accounts account
JOIN roles role ON role.name = 'ADMIN'
WHERE LOWER(account.email) = 'admin@gmail.com'
ON CONFLICT (account_id, role_id) DO NOTHING;

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
        '10000000-0000-0000-0000-000000000001',
        'Nguyễn Du',
        'Tố Như',
        'nguyen-du-reading-test',
        1765,
        1820,
        'trung_dai',
        'Đại thi hào dân tộc, tác giả Truyện Kiều. Dữ liệu này phục vụ test đọc thơ Nôm, bookmark và highlight.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000002',
        'Nguyễn Đình Chiểu',
        'Đồ Chiểu',
        'nguyen-dinh-chieu-reading-test',
        1822,
        1888,
        'trung_dai',
        'Nhà thơ yêu nước Nam Bộ, tác giả Lục Vân Tiên và nhiều tác phẩm mang tinh thần đạo nghĩa.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000003',
        'Nguyễn Trãi',
        'Ức Trai',
        'nguyen-trai-reading-test',
        1380,
        1442,
        'trung_dai',
        'Danh nhân văn hóa, nhà chính trị và nhà văn lớn của văn học trung đại Việt Nam.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000004',
        'Trần Quốc Tuấn',
        'Hưng Đạo Đại Vương',
        'tran-quoc-tuan-reading-test',
        1228,
        1300,
        'trung_dai',
        'Danh tướng thời Trần, tác giả Hịch tướng sĩ, văn bản chính luận tiêu biểu của tinh thần yêu nước.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000005',
        'Khuyết danh',
        NULL,
        'khuyet-danh-reading-test',
        NULL,
        NULL,
        'dan_gian',
        'Nguồn tác giả dân gian, dùng cho ca dao, tục ngữ và truyện kể truyền miệng.',
        NULL,
        NOW(),
        NOW()
    ),
    (
        '10000000-0000-0000-0000-000000000006',
        'Đặng Trần Côn',
        NULL,
        'dang-tran-con-reading-test',
        1710,
        1745,
        'trung_dai',
        'Tác giả Chinh phụ ngâm bằng chữ Hán, tác phẩm được lưu truyền rộng rãi qua các bản diễn Nôm.',
        NULL,
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;

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
        '20000000-0000-0000-0000-000000000001',
        '10000000-0000-0000-0000-000000000001',
        'Chị em Thúy Kiều',
        'chi-em-thuy-kieu-reading-test',
        'Truyện Kiều',
        'tho_ca',
        'truyen_tho_nom',
        'trung_dai',
        10,
        1,
        1814,
        'Trích đoạn giới thiệu vẻ đẹp Thúy Vân, Thúy Kiều và cảm hứng nhân sinh trong Truyện Kiều.',
        NULL,
        TRUE,
        0,
        'Xã hội phong kiến cuối Lê đầu Nguyễn với nhiều biến động và bất công đối với con người.',
        'Phản ánh số phận con người tài sắc trong xã hội cũ.',
        'Đề cao vẻ đẹp, tài năng, nhân phẩm và niềm thương cảm sâu sắc với con người.',
        'Bút pháp ước lệ tượng trưng, ngôn ngữ lục bát và nghệ thuật tả người tinh tế.',
        'Mai cốt cách, tuyết tinh thần',
        'Nguyễn Du, Truyện Kiều',
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000002',
        '10000000-0000-0000-0000-000000000002',
        'Lục Vân Tiên cứu Kiều Nguyệt Nga',
        'luc-van-tien-cuu-kieu-nguyet-nga-reading-test',
        'Lục Vân Tiên',
        'tho_ca',
        'truyen_tho_nom',
        'trung_dai',
        11,
        1,
        1850,
        'Trích đoạn thể hiện lý tưởng nghĩa hiệp, cứu người gặp nạn và quan niệm sống vì đạo nghĩa.',
        NULL,
        TRUE,
        0,
        'Văn học Nam Bộ thế kỷ XIX, đề cao đạo lý nhân nghĩa và phẩm chất người anh hùng bình dân.',
        'Phản ánh khát vọng công bằng, trọng nghĩa, ghét cái ác trong đời sống nhân dân.',
        'Ca ngợi tinh thần cứu người, không màng danh lợi.',
        'Ngôn ngữ mộc mạc, giọng kể dân gian, kết cấu truyện thơ gần gũi.',
        'Nhớ câu kiến ngãi bất vi',
        'Nguyễn Đình Chiểu, Lục Vân Tiên',
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000003',
        '10000000-0000-0000-0000-000000000003',
        'Bình Ngô đại cáo',
        'binh-ngo-dai-cao-reading-test',
        'Bình Ngô đại cáo',
        'khao_cuu',
        'van_chinh_luan',
        'trung_dai',
        10,
        2,
        1428,
        'Áng văn chính luận lớn, khẳng định độc lập dân tộc và tổng kết cuộc khởi nghĩa Lam Sơn.',
        NULL,
        TRUE,
        0,
        'Ra đời sau thắng lợi của cuộc khởi nghĩa Lam Sơn chống quân Minh.',
        'Tố cáo tội ác xâm lược và phản ánh sức mạnh nhân dân trong chiến tranh vệ quốc.',
        'Khẳng định quyền sống, quyền độc lập và niềm tự hào dân tộc.',
        'Lập luận chặt chẽ, giọng văn hào hùng, hình ảnh giàu sức khái quát.',
        'Việc nhân nghĩa cốt ở yên dân',
        'Nguyễn Trãi, Bình Ngô đại cáo',
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000004',
        '10000000-0000-0000-0000-000000000004',
        'Hịch tướng sĩ',
        'hich-tuong-si-reading-test',
        'Dụ chư tì tướng hịch văn',
        'khao_cuu',
        'van_chinh_luan',
        'trung_dai',
        10,
        2,
        1285,
        'Văn bản chính luận khơi dậy lòng yêu nước, ý chí chiến đấu và trách nhiệm của tướng sĩ.',
        NULL,
        TRUE,
        0,
        'Viết trong bối cảnh quân Nguyên Mông đe dọa xâm lược Đại Việt.',
        'Phản ánh tình thế nguy cấp của đất nước và yêu cầu đoàn kết chống ngoại xâm.',
        'Thức tỉnh lòng trung nghĩa, ý thức trách nhiệm với non sông.',
        'Lập luận sắc bén, cảm xúc mãnh liệt, kết hợp lý và tình.',
        'Ta thường tới bữa quên ăn',
        'Trần Quốc Tuấn, Hịch tướng sĩ',
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000005',
        '10000000-0000-0000-0000-000000000005',
        'Ca dao than thân và yêu thương tình nghĩa',
        'ca-dao-than-than-yeu-thuong-tinh-nghia-reading-test',
        'Ca dao Việt Nam',
        'tho_ca',
        'ca_dao',
        'dan_gian',
        10,
        1,
        NULL,
        'Tuyển chọn ca dao dân gian dùng để test văn bản thơ ngắn, nhiều section và nhiều dòng.',
        NULL,
        TRUE,
        0,
        'Lưu truyền trong đời sống dân gian qua nhiều thế hệ.',
        'Phản ánh tâm tư, thân phận, tình yêu và đạo lý của người bình dân.',
        'Bày tỏ niềm thương cảm, tình nghĩa gia đình và khát vọng hạnh phúc.',
        'Hình ảnh so sánh, ẩn dụ gần gũi, nhịp điệu dân gian.',
        'Thân em như tấm lụa đào',
        'Ca dao Việt Nam',
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000006',
        '10000000-0000-0000-0000-000000000006',
        'Chinh phụ ngâm',
        'chinh-phu-ngam-reading-test',
        'Chinh phụ ngâm khúc',
        'tho_ca',
        'song_that_luc_bat',
        'trung_dai',
        11,
        2,
        1741,
        'Tác phẩm ngâm khúc thể hiện nỗi cô đơn, nhớ thương và bi kịch chiến tranh của người chinh phụ.',
        NULL,
        TRUE,
        0,
        'Ra đời trong bối cảnh chiến tranh phong kiến kéo dài ở thế kỷ XVIII.',
        'Phản ánh nỗi đau chia lìa do chiến tranh gây ra.',
        'Cảm thương sâu sắc cho thân phận người phụ nữ và khát vọng đoàn tụ.',
        'Giọng ngâm ai oán, hình ảnh ước lệ và nhịp thơ giàu nhạc tính.',
        'Thuở trời đất nổi cơn gió bụi',
        'Đặng Trần Côn, Chinh phụ ngâm',
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;

INSERT INTO tags (id, name, slug, description, created_at, updated_at)
VALUES
    ('40000000-0000-0000-0000-000000000001', 'Thơ Nôm Test', 'tho-nom-reading-test', 'Tác phẩm thơ Nôm và truyện thơ Nôm.', NOW(), NOW()),
    ('40000000-0000-0000-0000-000000000002', 'Chính luận trung đại Test', 'chinh-luan-trung-dai-reading-test', 'Văn bản nghị luận, cáo, hịch trong văn học trung đại.', NOW(), NOW()),
    ('40000000-0000-0000-0000-000000000003', 'Ca dao Test', 'ca-dao-reading-test', 'Tác phẩm dân gian truyền miệng.', NOW(), NOW()),
    ('40000000-0000-0000-0000-000000000004', 'Nhân nghĩa Test', 'nhan-nghia-reading-test', 'Chủ đề nhân nghĩa, đạo lý và trách nhiệm cộng đồng.', NOW(), NOW()),
    ('40000000-0000-0000-0000-000000000005', 'Người phụ nữ Test', 'nguoi-phu-nu-reading-test', 'Hình tượng người phụ nữ trong văn học.', NOW(), NOW()),
    ('40000000-0000-0000-0000-000000000006', 'Yêu nước Test', 'yeu-nuoc-reading-test', 'Chủ đề yêu nước và độc lập dân tộc.', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO work_tags (work_id, tag_id, created_at)
VALUES
    ('20000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', NOW()),
    ('20000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000005', NOW()),
    ('20000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000001', NOW()),
    ('20000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000004', NOW()),
    ('20000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000002', NOW()),
    ('20000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000006', NOW()),
    ('20000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000002', NOW()),
    ('20000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000006', NOW()),
    ('20000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000003', NOW()),
    ('20000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000005', NOW()),
    ('20000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000001', NOW()),
    ('20000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000005', NOW())
ON CONFLICT (work_id, tag_id) DO NOTHING;

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
        '30000000-0000-0000-0000-000000000001',
        '20000000-0000-0000-0000-000000000001',
        1,
        'Mở đầu Truyện Kiều',
        'Trăm năm trong cõi người ta,
Chữ tài chữ mệnh khéo là ghét nhau.
Trải qua một cuộc bể dâu,
Những điều trông thấy mà đau đớn lòng.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000002',
        '20000000-0000-0000-0000-000000000001',
        2,
        'Nguồn gốc và gia cảnh',
        'Đầu lòng hai ả tố nga,
Thúy Kiều là chị, em là Thúy Vân.
Mai cốt cách, tuyết tinh thần,
Mỗi người một vẻ, mười phân vẹn mười.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000003',
        '20000000-0000-0000-0000-000000000001',
        3,
        'Chân dung Thúy Vân',
        'Vân xem trang trọng khác vời,
Khuôn trăng đầy đặn, nét ngài nở nang.
Hoa cười ngọc thốt đoan trang,
Mây thua nước tóc, tuyết nhường màu da.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000004',
        '20000000-0000-0000-0000-000000000001',
        4,
        'Chân dung Thúy Kiều',
        'Kiều càng sắc sảo mặn mà,
So bề tài sắc lại là phần hơn.
Làn thu thủy, nét xuân sơn,
Hoa ghen thua thắm, liễu hờn kém xanh.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000005',
        '20000000-0000-0000-0000-000000000001',
        5,
        'Tài năng của Kiều',
        'Một hai nghiêng nước nghiêng thành,
Sắc đành đòi một, tài đành họa hai.
Thông minh vốn sẵn tính trời,
Pha nghề thi họa, đủ mùi ca ngâm.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000006',
        '20000000-0000-0000-0000-000000000001',
        6,
        'Cung đàn bạc mệnh',
        'Cung thương làu bậc ngũ âm,
Nghề riêng ăn đứt hồ cầm một trương.
Khúc nhà tay lựa nên chương,
Một thiên bạc mệnh lại càng não nhân.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000007',
        '20000000-0000-0000-0000-000000000001',
        7,
        'Phong lưu rất mực hồng quần',
        'Phong lưu rất mực hồng quần,
Xuân xanh xấp xỉ tới tuần cập kê.
Êm đềm trướng rủ màn che,
Tường đông ong bướm đi về mặc ai.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000008',
        '20000000-0000-0000-0000-000000000002',
        1,
        'Vân Tiên gặp nạn cướp',
        'Vân Tiên ghé lại bên đàng,
Bẻ cây làm gậy nhằm làng xông vô.
Kêu rằng: Bớ đảng hung đồ,
Chớ quen làm thói hồ đồ hại dân.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000009',
        '20000000-0000-0000-0000-000000000002',
        2,
        'Đối mặt bọn cướp',
        'Phong Lai mặt đỏ phừng phừng:
Thằng nào dám tới lẫy lừng vào đây.
Trước gây việc dữ tại mầy,
Truyền quân bốn phía phủ vây bịt bùng.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000010',
        '20000000-0000-0000-0000-000000000002',
        3,
        'Hành động nghĩa hiệp',
        'Vân Tiên tả đột hữu xông,
Khác nào Triệu Tử phá vòng Đương Dang.
Lâu la bốn phía vỡ tan,
Đều quăng gươm giáo tìm đàng chạy ngay.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000011',
        '20000000-0000-0000-0000-000000000002',
        4,
        'Cứu người gặp nạn',
        'Dẹp rồi lũ kiến chòm ong,
Hỏi: Ai than khóc ở trong xe này?
Thưa rằng: Tôi thiệt người ngay,
Sa cơ nên mới lụy tay hung đồ.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000012',
        '20000000-0000-0000-0000-000000000002',
        5,
        'Nguyệt Nga tỏ lòng',
        'Nguyệt Nga rằng: Chút tôi liễu yếu đào tơ,
Giữa đường lâm phải bụi dơ đã phần.
Hà Khê qua đó cũng gần,
Xin theo cùng thiếp đền ân cho chàng.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000013',
        '20000000-0000-0000-0000-000000000002',
        6,
        'Quan niệm làm ơn',
        'Vân Tiên nghe nói liền cười:
Làm ơn há dễ trông người trả ơn.
Nay đà rõ đặng nguồn cơn,
Nào ai tính thiệt so hơn làm gì.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000014',
        '20000000-0000-0000-0000-000000000002',
        7,
        'Đạo nghĩa của người quân tử',
        'Nhớ câu kiến ngãi bất vi,
Làm người thế ấy cũng phi anh hùng.
Đường xa xin chớ ngại ngùng,
Việc nhân nghĩa vốn nên cùng giữ luôn.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000015',
        '20000000-0000-0000-0000-000000000003',
        1,
        'Tư tưởng nhân nghĩa',
        'Việc nhân nghĩa cốt ở yên dân,
Quân điếu phạt trước lo trừ bạo.
Như nước Đại Việt ta từ trước,
Vốn xưng nền văn hiến đã lâu.',
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000016',
        '20000000-0000-0000-0000-000000000003',
        2,
        'Chủ quyền Đại Việt',
        'Núi sông bờ cõi đã chia,
Phong tục Bắc Nam cũng khác.
Từ Triệu, Đinh, Lý, Trần bao đời gây nền độc lập,
Cùng Hán, Đường, Tống, Nguyên mỗi bên xưng đế một phương.',
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000017',
        '20000000-0000-0000-0000-000000000003',
        3,
        'Hào kiệt đời nào cũng có',
        'Tuy mạnh yếu từng lúc khác nhau,
Song hào kiệt đời nào cũng có.
Vậy nên Lưu Cung tham công nên thất bại,
Triệu Tiết thích lớn phải tiêu vong.',
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000018',
        '20000000-0000-0000-0000-000000000003',
        4,
        'Tội ác quân xâm lược',
        'Nướng dân đen trên ngọn lửa hung tàn,
Vùi con đỏ xuống dưới hầm tai vạ.
Dối trời lừa dân đủ muôn nghìn kế,
Gây binh kết oán trải hai mươi năm.',
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000019',
        '20000000-0000-0000-0000-000000000003',
        5,
        'Nỗi khổ của nhân dân',
        'Bại nhân nghĩa nát cả đất trời,
Nặng thuế khóa sạch không đầm núi.
Người bị ép xuống biển dòng lưng mò ngọc,
Kẻ bị đem vào núi đãi cát tìm vàng.',
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000020',
        '20000000-0000-0000-0000-000000000003',
        6,
        'Khởi nghĩa Lam Sơn',
        'Ta đây:
Núi Lam Sơn dấy nghĩa,
Chốn hoang dã nương mình.
Ngẫm thù lớn há đội trời chung,
Căm giặc nước thề không cùng sống.',
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000021',
        '20000000-0000-0000-0000-000000000003',
        7,
        'Niềm tin đại nghĩa',
        'Đem đại nghĩa để thắng hung tàn,
Lấy chí nhân để thay cường bạo.
Trận Bồ Đằng sấm vang chớp giật,
Miền Trà Lân trúc chẻ tro bay.',
        NULL,
        'MIXED',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000022',
        '20000000-0000-0000-0000-000000000004',
        1,
        'Nêu gương trung nghĩa',
        'Ta thường nghe: Kỷ Tín đem mình chết thay, cứu thoát cho Cao Đế; Do Vu chìa lưng chịu giáo, che chở cho Chiêu Vương.',
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000023',
        '20000000-0000-0000-0000-000000000004',
        2,
        'Tình thế đất nước',
        'Huống chi ta cùng các ngươi sinh phải thời loạn lạc, lớn gặp buổi gian nan. Ngó thấy sứ giặc đi lại nghênh ngang ngoài đường, uốn lưỡi cú diều mà sỉ mắng triều đình.',
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000024',
        '20000000-0000-0000-0000-000000000004',
        3,
        'Nỗi đau của chủ tướng',
        'Ta thường tới bữa quên ăn, nửa đêm vỗ gối; ruột đau như cắt, nước mắt đầm đìa; chỉ căm tức chưa xả thịt lột da, nuốt gan uống máu quân thù.',
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000025',
        '20000000-0000-0000-0000-000000000004',
        4,
        'Ý chí hy sinh',
        'Dẫu cho trăm thân này phơi ngoài nội cỏ, nghìn xác này gói trong da ngựa, ta cũng vui lòng.',
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000026',
        '20000000-0000-0000-0000-000000000004',
        5,
        'Phê phán sự thờ ơ',
        'Các ngươi ở cùng ta coi giữ binh quyền đã lâu ngày, không có mặc thì ta cho áo, không có ăn thì ta cho cơm; quan nhỏ thì ta thăng chức, lương ít thì ta cấp bổng.',
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000027',
        '20000000-0000-0000-0000-000000000004',
        6,
        'Khuyên rèn luyện',
        'Nay ta bảo thật các ngươi: nên nhớ câu đặt mồi lửa vào dưới đống củi là nguy cơ, nên lấy điều kiềng canh nóng mà thổi rau nguội làm răn sợ.',
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000028',
        '20000000-0000-0000-0000-000000000004',
        7,
        'Lựa chọn sống còn',
        'Nếu biết chuyên tập sách này, theo lời dạy bảo của ta, thì mới phải đạo thần chủ; nhược bằng khinh bỏ sách này, trái lời dạy bảo của ta, tức là kẻ nghịch thù.',
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000029',
        '20000000-0000-0000-0000-000000000005',
        1,
        'Thân em như tấm lụa đào',
        'Thân em như tấm lụa đào,
Phất phơ giữa chợ biết vào tay ai.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000030',
        '20000000-0000-0000-0000-000000000005',
        2,
        'Thân em như hạt mưa sa',
        'Thân em như hạt mưa sa,
Hạt vào đài các, hạt ra ruộng cày.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000031',
        '20000000-0000-0000-0000-000000000005',
        3,
        'Công cha nghĩa mẹ',
        'Công cha như núi Thái Sơn,
Nghĩa mẹ như nước trong nguồn chảy ra.
Một lòng thờ mẹ kính cha,
Cho tròn chữ hiếu mới là đạo con.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000032',
        '20000000-0000-0000-0000-000000000005',
        4,
        'Bầu ơi thương lấy bí cùng',
        'Bầu ơi thương lấy bí cùng,
Tuy rằng khác giống nhưng chung một giàn.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000033',
        '20000000-0000-0000-0000-000000000005',
        5,
        'Nhiễu điều phủ lấy giá gương',
        'Nhiễu điều phủ lấy giá gương,
Người trong một nước phải thương nhau cùng.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000034',
        '20000000-0000-0000-0000-000000000005',
        6,
        'Qua đình ngả nón trông đình',
        'Qua đình ngả nón trông đình,
Đình bao nhiêu ngói thương mình bấy nhiêu.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000035',
        '20000000-0000-0000-0000-000000000005',
        7,
        'Muối ba năm',
        'Muối ba năm muối đang còn mặn,
Gừng chín tháng gừng hãy còn cay.
Đôi ta nghĩa nặng tình dày,
Có xa nhau đi nữa cũng ba vạn sáu ngàn ngày mới xa.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000036',
        '20000000-0000-0000-0000-000000000006',
        1,
        'Gió bụi chiến tranh',
        'Thuở trời đất nổi cơn gió bụi,
Khách má hồng nhiều nỗi truân chuyên.
Xanh kia thăm thẳm tầng trên,
Vì ai gây dựng cho nên nỗi này.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000037',
        '20000000-0000-0000-0000-000000000006',
        2,
        'Chàng tuổi trẻ hào kiệt',
        'Chàng tuổi trẻ vốn dòng hào kiệt,
Xếp bút nghiên theo việc đao cung.
Thành liền mong tiến bệ rồng,
Thước gươm đã quyết chẳng dung giặc trời.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000038',
        '20000000-0000-0000-0000-000000000006',
        3,
        'Buổi tiễn đưa',
        'Áo chàng đỏ tựa ráng pha,
Ngựa chàng sắc trắng như là tuyết in.
Tiếng nhạc ngựa lần chen tiếng trống,
Giáp mặt rồi phút bỗng chia tay.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000039',
        '20000000-0000-0000-0000-000000000006',
        4,
        'Cảnh chia lìa',
        'Cùng trông lại mà cùng chẳng thấy,
Thấy xanh xanh những mấy ngàn dâu.
Ngàn dâu xanh ngắt một màu,
Lòng chàng ý thiếp ai sầu hơn ai.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000040',
        '20000000-0000-0000-0000-000000000006',
        5,
        'Nỗi nhớ dằng dặc',
        'Chàng thì đi cõi xa mưa gió,
Thiếp thì về buồng cũ chiếu chăn.
Đoái trông theo đã cách ngăn,
Tuôn màu mây biếc trải ngần núi xanh.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000041',
        '20000000-0000-0000-0000-000000000006',
        6,
        'Trống và khói nơi biên ải',
        'Trống Trường Thành lung lay bóng nguyệt,
Khói Cam Tuyền mờ mịt thức mây.
Chín tầng gươm báu trao tay,
Nửa đêm truyền hịch định ngày xuất chinh.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000042',
        '20000000-0000-0000-0000-000000000006',
        7,
        'Khát vọng đoàn tụ',
        'Thiếp xin chàng chớ bạc đầu,
Thiếp nguyền giữ vẹn trước sau một lòng.
Ngày về xin chớ phụ công,
Để người khuê phụ ngóng trông tháng ngày.',
        NULL,
        'POETRY',
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;

INSERT INTO characters (
    id,
    work_id,
    name,
    description,
    analysis,
    display_order,
    role_type,
    created_at,
    updated_at
)
VALUES
    ('50000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Thúy Kiều', 'Nhân vật trung tâm trong Truyện Kiều.', 'Thúy Kiều nổi bật bởi vẻ đẹp, tài năng và dự cảm số phận truân chuyên.', 0, 'MAIN', NOW(), NOW()),
    ('50000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'Thúy Vân', 'Em gái Thúy Kiều.', 'Thúy Vân được khắc họa bằng vẻ đẹp phúc hậu, hài hòa.', 1, 'SUPPORTING', NOW(), NOW()),
    ('50000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', 'Lục Vân Tiên', 'Người anh hùng nghĩa hiệp.', 'Lục Vân Tiên hành động vì lẽ phải và không màng trả ơn.', 0, 'MAIN', NOW(), NOW()),
    ('50000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000002', 'Kiều Nguyệt Nga', 'Người được Vân Tiên cứu giúp.', 'Kiều Nguyệt Nga đại diện cho phẩm chất biết ơn và trọng nghĩa.', 1, 'SUPPORTING', NOW(), NOW()),
    ('50000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000004', 'Trần Quốc Tuấn', 'Chủ thể trữ tình và người viết hịch.', 'Hình tượng vị chủ tướng đau đáu vận nước, kết hợp tình cảm và trách nhiệm.', 0, 'MAIN', NOW(), NOW()),
    ('50000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000006', 'Người chinh phụ', 'Người vợ có chồng ra trận.', 'Nhân vật bộc lộ nỗi cô đơn, nhớ thương và khát vọng đoàn tụ.', 0, 'MAIN', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO artistic_features (
    id,
    work_id,
    feature_type,
    title,
    description,
    display_order,
    created_at,
    updated_at
)
VALUES
    ('60000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'IMAGERY', 'Ước lệ tượng trưng', 'Dùng hình ảnh thiên nhiên như mai, tuyết, hoa, liễu để gợi vẻ đẹp con người.', 0, NOW(), NOW()),
    ('60000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'LANGUAGE', 'Ngôn ngữ lục bát', 'Câu thơ mềm mại, giàu nhạc tính, phù hợp test hiển thị thơ nhiều dòng.', 1, NOW(), NOW()),
    ('60000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', 'NARRATIVE', 'Kết cấu truyện thơ', 'Tình huống cứu nạn làm nổi bật phẩm chất nghĩa hiệp của nhân vật.', 0, NOW(), NOW()),
    ('60000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000003', 'STRUCTURE', 'Lập luận cáo trạng', 'Từ tư tưởng nhân nghĩa đến chủ quyền, tội ác giặc và chiến thắng chính nghĩa.', 0, NOW(), NOW()),
    ('60000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000004', 'LANGUAGE', 'Giọng hịch thống thiết', 'Kết hợp lời trách, lời khuyên và cảm xúc yêu nước mạnh mẽ.', 0, NOW(), NOW()),
    ('60000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000005', 'SYMBOLISM', 'Hình ảnh dân gian', 'Các hình ảnh lụa đào, mưa sa, bầu bí, gừng muối tạo sức gợi gần gũi.', 0, NOW(), NOW()),
    ('60000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000006', 'IMAGERY', 'Không gian chia lìa', 'Mây, núi, trăng, khói và tiếng trống gợi nỗi xa cách trong chiến tranh.', 0, NOW(), NOW())
ON CONFLICT DO NOTHING;
