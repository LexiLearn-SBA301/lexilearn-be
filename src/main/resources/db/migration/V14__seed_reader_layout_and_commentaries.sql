-- Reader layout test data:
--   1 prose work with multiple sections
--   1 long poetry work with exactly one section
--   published, featured and draft commentaries

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
VALUES (
    '10000000-0000-0000-0000-000000000007',
    'Nguyễn Dữ',
    NULL,
    'nguyen-du-truyen-ky-reading-test',
    NULL,
    NULL,
    'trung_dai',
    'Tác giả văn xuôi tự sự trung đại, nổi tiếng với tập Truyền kỳ mạn lục.',
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
        '20000000-0000-0000-0000-000000000007',
        '10000000-0000-0000-0000-000000000007',
        'Chuyện người con gái Nam Xương',
        'chuyen-nguoi-con-gai-nam-xuong-layout-test',
        'Nam Xương nữ tử truyện',
        'truyen_ngan',
        'truyen_truyen_ky',
        'trung_dai',
        10,
        1,
        NULL,
        'Bản kể lại phục vụ kiểm thử giao diện văn xuôi nhiều chương, điều hướng section, bookmark và highlight.',
        NULL,
        TRUE,
        0,
        'Câu chuyện đặt trong xã hội phong kiến với chiến tranh, lễ giáo và quyền lực gia trưởng chi phối số phận con người.',
        'Phản ánh bi kịch gia đình, chiến tranh và sự bất công đối với người phụ nữ.',
        'Trân trọng phẩm hạnh, lòng thủy chung và khát vọng được thấu hiểu của con người.',
        'Kết hợp tự sự, đối thoại, yếu tố kỳ ảo và chi tiết chiếc bóng giàu sức ám ảnh.',
        'Thiếp sở dĩ nương tựa vào chàng vì có cái thú vui nghi gia nghi thất.',
        'Nguyễn Dữ, Chuyện người con gái Nam Xương',
        NOW(),
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000008',
        '10000000-0000-0000-0000-000000000002',
        'Văn tế nghĩa sĩ Cần Giuộc',
        'van-te-nghia-si-can-giuoc-one-page-test',
        'Văn tế nghĩa sĩ Cần Giuộc',
        'tho_ca',
        'van_te',
        'trung_dai',
        11,
        1,
        1861,
        'Một section thơ dài dùng để kiểm thử chế độ đọc toàn bài trên một trang và cuộn liên tục.',
        NULL,
        TRUE,
        0,
        'Tác phẩm ra đời sau trận tập kích đồn Tây Dương ở Cần Giuộc, tưởng niệm những người nông dân nghĩa sĩ đã hi sinh.',
        'Khắc họa hiện thực chiến đấu thiếu thốn nhưng quả cảm của người nông dân Nam Bộ.',
        'Tôn vinh lòng yêu nước, nghĩa khí và sự hi sinh của những con người bình dị.',
        'Giọng văn bi tráng, ngôn ngữ Nam Bộ giàu sức gợi, kết hợp tự sự, trữ tình và hình tượng người anh hùng áo vải.',
        'Sống đánh giặc, thác cũng đánh giặc.',
        'Nguyễn Đình Chiểu, Văn tế nghĩa sĩ Cần Giuộc',
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;

INSERT INTO work_tags (work_id, tag_id, created_at)
VALUES
    (
        '20000000-0000-0000-0000-000000000007',
        '40000000-0000-0000-0000-000000000005',
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000008',
        '40000000-0000-0000-0000-000000000006',
        NOW()
    ),
    (
        '20000000-0000-0000-0000-000000000008',
        '40000000-0000-0000-0000-000000000004',
        NOW()
    )
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
        '30000000-0000-0000-0000-000000000101',
        '20000000-0000-0000-0000-000000000007',
        1,
        'Phẩm hạnh của Vũ Nương',
        $text$Vũ Thị Thiết, người con gái quê ở Nam Xương, tính tình thùy mị, nết na, lại thêm tư dung tốt đẹp. Trong làng có Trương Sinh mến vì dung hạnh, xin với mẹ đem trăm lạng vàng cưới nàng về làm vợ.

Trương Sinh vốn con nhà hào phú nhưng ít học, tính lại hay đa nghi. Đối với vợ, chàng thường phòng ngừa quá sức. Vũ Nương hiểu tính chồng nên luôn giữ gìn khuôn phép, chưa từng để vợ chồng phải đến nỗi bất hòa.

Ngày tháng trôi qua trong một mái nhà tưởng như yên ổn. Nàng chăm lo việc nhà, kính trọng mẹ chồng và giữ trọn đạo làm vợ. Nhưng sự bình yên ấy mong manh, bởi tính ghen tuông của Trương Sinh vẫn âm thầm tồn tại, chỉ chờ một biến cố để bùng lên.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000102',
        '20000000-0000-0000-0000-000000000007',
        2,
        'Buổi tiễn chồng ra trận',
        $text$Khi giặc Chiêm quấy nhiễu biên cương, triều đình bắt nhiều trai tráng đi lính. Trương Sinh tuy con nhà khá giả nhưng không có học nên tên phải ghi trong sổ lính vào loại đầu.

Trong buổi tiễn đưa, mẹ chàng dặn con phải biết giữ mình, thấy khó nên lui, liệu sức mà tiến, đừng ham công danh mà rơi vào nơi nguy hiểm. Vũ Nương rót chén rượu đầy, nói rằng nàng chẳng mong chồng đeo ấn phong hầu, chỉ mong ngày trở về mang theo hai chữ bình yên.

Nàng nghĩ đến cảnh quan san xa cách, áo rét gửi người ải xa, lòng đầy lo lắng. Lời tiễn không phô trương mà chất chứa nỗi thương chồng, thương mẹ già và nỗi sợ hãi trước cảnh chiến tranh chia lìa gia đình.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000103',
        '20000000-0000-0000-0000-000000000007',
        3,
        'Những ngày xa cách',
        $text$Trương Sinh đi chưa đầy tuần thì Vũ Nương sinh một đứa con trai, đặt tên là Đản. Nàng một mình vừa nuôi con nhỏ vừa chăm sóc mẹ chồng tuổi cao sức yếu.

Vì thương nhớ con trai, mẹ Trương Sinh dần sinh bệnh. Vũ Nương hết sức thuốc thang, lễ bái thần Phật, dùng lời ngọt ngào khuyên lơn. Khi bệnh tình không qua khỏi, người mẹ nắm tay nàng mà nói rằng trời xanh sẽ chẳng phụ người có lòng lành như nàng.

Mẹ chồng mất, Vũ Nương lo ma chay chu tất như đối với cha mẹ ruột. Trong căn nhà vắng, nàng vừa làm mẹ vừa thay phần người cha cho bé Đản. Mỗi tối, khi đứa trẻ hỏi cha ở đâu, nàng chỉ bóng mình trên vách và bảo đó là cha Đản.

Chiếc bóng vốn chỉ là cách người mẹ dỗ con, nhưng rồi lại trở thành mầm mống của bi kịch. Đứa trẻ ngây thơ tin rằng người cha ấy đêm nào cũng đến, mẹ đi cũng đi, mẹ ngồi cũng ngồi, nhưng chưa bao giờ bế nó.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000104',
        '20000000-0000-0000-0000-000000000007',
        4,
        'Ngày Trương Sinh trở về',
        $text$Qua năm sau, việc quân kết thúc, Trương Sinh trở về. Hay tin mẹ đã mất, chàng đau buồn, bế con nhỏ ra thăm mộ. Đứa trẻ thấy người lạ thì quấy khóc, nhất định không chịu nhận chàng là cha.

Trương Sinh dỗ dành, bé Đản ngây thơ nói rằng trước đây đêm nào cũng có một người đàn ông đến. Người ấy luôn theo mẹ, nhưng chẳng bao giờ nói chuyện hay bế Đản.

Nghe lời con trẻ, tính đa nghi trong Trương Sinh lập tức trỗi dậy. Chàng cho rằng vợ đã thất tiết trong thời gian mình đi lính. Không hỏi rõ đầu đuôi, không tìm người đối chứng, chàng trở về nhà mắng nhiếc và đánh đuổi Vũ Nương.

Nàng cố giãi bày rằng mình sinh ra trong gia đình khó, được nương tựa nhà giàu, đã giữ gìn tiết hạnh và chăm sóc mẹ con suốt những năm chồng vắng mặt. Nhưng cơn ghen khiến Trương Sinh không còn nghe được lời nào.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000105',
        '20000000-0000-0000-0000-000000000007',
        5,
        'Bi kịch bên bến Hoàng Giang',
        $text$Họ hàng và hàng xóm bênh vực, biện bạch cho Vũ Nương nhưng Trương Sinh vẫn không tin. Điều đau đớn hơn cả là chàng giấu kín lời đứa trẻ, khiến nàng không biết tai họa bắt đầu từ đâu và cũng không có cách nào tự minh oan.

Vũ Nương nói rằng bao năm vun vén hạnh phúc nay đã tan vỡ, bình rơi trâm gãy, mây tạnh mưa tan, không còn có thể trở lại như trước. Bị dồn đến đường cùng, nàng tắm gội sạch sẽ rồi ra bến Hoàng Giang.

Trước dòng nước, nàng ngửa mặt lên trời, xin thần sông chứng giám tấm lòng trong sạch. Nếu nàng giữ trọn phẩm hạnh, xin được làm ngọc dưới nước; nếu có lòng phản bội, xin chịu làm mồi cho cá tôm.

Nói xong, nàng gieo mình xuống sông. Cái chết của Vũ Nương không chỉ là kết quả của cơn ghen mù quáng mà còn là tiếng kêu tuyệt vọng của một con người không được quyền tự bảo vệ danh dự.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000106',
        '20000000-0000-0000-0000-000000000007',
        6,
        'Sự thật về chiếc bóng',
        $text$Sau khi vợ mất, Trương Sinh tuy giận nhưng cũng động lòng thương, tìm vớt thây nàng mà không thấy. Một đêm, chàng ngồi buồn bên ngọn đèn, bé Đản bỗng chỉ chiếc bóng của chàng trên vách và reo lên rằng cha Đản lại đến.

Đến lúc ấy, Trương Sinh mới hiểu người cha mà con nói chính là chiếc bóng Vũ Nương vẫn dùng để dỗ con trong những đêm xa chồng. Chàng nhận ra nỗi oan của vợ, nhưng mọi việc đã quá muộn.

Chi tiết chiếc bóng vừa gần gũi vừa nghiệt ngã. Nó là biểu hiện của tình thương con và nỗi nhớ chồng, nhưng qua lời nói ngây thơ của đứa trẻ và sự hồ đồ của người lớn, nó lại trở thành chứng cứ buộc tội.

Sự thật được sáng tỏ không thể đưa Vũ Nương trở về. Trương Sinh chỉ còn lại sự hối hận và căn nhà trống, còn người đọc phải đối diện với hậu quả không thể cứu vãn của thói gia trưởng và sự thiếu đối thoại.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000107',
        '20000000-0000-0000-0000-000000000007',
        7,
        'Cuộc gặp dưới thủy cung',
        $text$Cùng làng với Vũ Nương có Phan Lang, trước từng cứu một con rùa mai xanh. Sau này gặp nạn đắm thuyền, Phan Lang được Linh Phi cứu và đưa xuống thủy cung. Ở nơi ấy, chàng bất ngờ gặp lại Vũ Nương.

Nàng vẫn nhớ quê nhà, nhớ phần mộ tổ tiên và thương con nhỏ. Ban đầu nàng ngần ngại trở về vì đã mang tiếng xấu, nhưng khi nghe Phan Lang nhắc đến cảnh quê cũ, lòng nàng không thể dứt.

Trước khi Phan Lang trở lại trần gian, Vũ Nương gửi một chiếc hoa vàng và nhờ chuyển lời cho Trương Sinh. Nếu chàng còn nhớ tình xưa nghĩa cũ, hãy lập đàn giải oan bên bến sông, nàng sẽ hiện về.

Yếu tố kỳ ảo mở ra một không gian khác, nơi người bị oan được che chở và phẩm giá được công nhận. Tuy vậy, thủy cung đẹp đẽ cũng không thể thay thế cuộc sống gia đình mà Vũ Nương đã mất.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000108',
        '20000000-0000-0000-0000-000000000007',
        8,
        'Lập đàn giải oan',
        $text$Nhận được chiếc hoa vàng và lời nhắn, Trương Sinh lập một đàn tràng ba ngày đêm ở bến Hoàng Giang. Giữa dòng, Vũ Nương hiện lên trong một chiếc kiệu hoa, theo sau là cờ tán, võng lọng rực rỡ, lúc ẩn lúc hiện.

Nàng nói vọng vào rằng đã cảm tạ tấm lòng của chồng, nhưng vì đã thề sống chết với Linh Phi nên không thể trở lại nhân gian. Dứt lời, bóng nàng mờ dần rồi biến mất.

Vũ Nương được minh oan nhưng không được đoàn tụ. Kết thúc kỳ ảo tạo nên vẻ đẹp cho nhân vật và thể hiện ước mơ công lý, đồng thời vẫn giữ nguyên bản chất bi kịch: hạnh phúc đã mất không thể phục hồi.

Câu chuyện khép lại bằng sự day dứt. Danh dự của người phụ nữ được trả lại, nhưng cái giá phải trả là mạng sống, tuổi xuân và một mái ấm tan vỡ. Đó cũng là lời cảnh báo về định kiến, bạo lực và sự phán xét thiếu căn cứ trong gia đình.$text$,
        NULL,
        'PROSE',
        NOW(),
        NOW()
    ),
    (
        '30000000-0000-0000-0000-000000000201',
        '20000000-0000-0000-0000-000000000008',
        1,
        'Toàn văn trên một trang',
        $poem$Hỡi ôi!

Súng giặc đất rền; lòng dân trời tỏ.

Mười năm công vỡ ruộng, chưa ắt còn danh nổi như phao;
Một trận nghĩa đánh Tây, tuy là mất tiếng vang như mõ.

Nhớ linh xưa:

Cui cút làm ăn; toan lo nghèo khó.

Chưa quen cung ngựa, đâu tới trường nhung;
Chỉ biết ruộng trâu, ở trong làng bộ.

Việc cuốc, việc cày, việc bừa, việc cấy, tay vốn quen làm;
Tập khiên, tập súng, tập mác, tập cờ, mắt chưa từng ngó.

Tiếng phong hạc phập phồng hơn mươi tháng,
Trông tin quan như trời hạn trông mưa;
Mùi tinh chiên vấy vá đã ba năm,
Ghét thói mọi như nhà nông ghét cỏ.

Bữa thấy bòng bong che trắng lốp, muốn tới ăn gan;
Ngày xem ống khói chạy đen sì, muốn ra cắn cổ.

Một mối xa thư đồ sộ, há để ai chém rắn đuổi hươu;
Hai vầng nhật nguyệt chói lòa, đâu dung lũ treo dê bán chó.

Nào đợi ai đòi ai bắt, phen này xin ra sức đoạn kình;
Chẳng thèm trốn ngược trốn xuôi, chuyến này dốc ra tay bộ hổ.

Khá thương thay:

Vốn chẳng phải quân cơ quân vệ, theo dòng ở lính diễn binh;
Chẳng qua là dân ấp dân lân, mến nghĩa làm quân chiêu mộ.

Mười tám ban võ nghệ, nào đợi tập rèn;
Chín chục trận binh thư, không chờ bày bố.

Ngoài cật có một manh áo vải, nào đợi mang bao tấu bầu ngòi;
Trong tay cầm một ngọn tầm vông, chi nài sắm dao tu nón gõ.

Hỏa mai đánh bằng rơm con cúi, cũng đốt xong nhà dạy đạo kia;
Gươm đeo dùng bằng lưỡi dao phay, cũng chém rớt đầu quan hai nọ.

Chi nhọc quan quản gióng trống kỳ, trống giục,
Đạp rào lướt tới, coi giặc cũng như không;
Nào sợ thằng Tây bắn đạn nhỏ, đạn to,
Xô cửa xông vào, liều mình như chẳng có.

Kẻ đâm ngang, người chém ngược,
Làm cho mã tà ma ní hồn kinh;
Bọn hè trước, lũ ó sau,
Trối kệ tàu thiếc tàu đồng súng nổ.

Những lăm lòng nghĩa lâu dùng;
Đâu biết xác phàm vội bỏ.

Một chắc sa trường rằng chữ hạnh,
Nào hay da ngựa bọc thây;
Trăm năm âm phủ ấy chữ quy,
Nào đợi gươm hùm treo mộ.

Đoái sông Cần Giuộc, cỏ cây mấy dặm sầu giăng;
Nhìn chợ Trường Bình, già trẻ hai hàng lụy nhỏ.

Chẳng phải án cướp án gian đày tới,
Mà vì binh tướng nó hãy đóng sông Bến Nghé,
Khiến người nghĩa sĩ phải ra tay;

Chẳng phải đất dữ đất lành chọn ở,
Mà bởi ông cha ta còn ở đất Đồng Nai,
Ai cứu đặng một phường con đỏ.

Thác mà trả nước non rồi nợ,
Danh thơm đồn sáu tỉnh chúng đều khen;
Thác mà ưng đình miếu để thờ,
Tiếng ngay trải muôn đời ai cũng mộ.

Sống đánh giặc, thác cũng đánh giặc,
Linh hồn theo giúp cơ binh,
Muôn kiếp nguyện được trả thù kia;

Sống thờ vua, thác cũng thờ vua,
Lời dụ dạy đã rành rành,
Một chữ ấm đủ đền công đó.

Nước mắt anh hùng lau chẳng ráo,
Thương vì hai chữ thiên dân;
Cây hương nghĩa sĩ thắp thêm thơm,
Cám bởi một câu vương thổ.

Hỡi ôi thương thay!

Có linh xin hưởng.$poem$,
        NULL,
        'POETRY',
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;

INSERT INTO work_commentaries (
    id,
    work_id,
    title,
    content,
    commentator_name,
    commentator_type,
    source_title,
    source_url,
    published_year,
    display_order,
    is_featured,
    is_published,
    created_at,
    updated_at
)
VALUES
    (
        '70000000-0000-0000-0000-000000000001',
        '20000000-0000-0000-0000-000000000001',
        'Nghệ thuật tả người',
        'Đoạn thơ tạo nên hai chân dung vừa hài hòa vừa khác biệt, qua đó cho thấy khả năng dùng ngôn ngữ ước lệ nhưng vẫn cá thể hóa nhân vật của Nguyễn Du.',
        'Ban biên tập LexiLearn',
        'EDITORIAL',
        NULL,
        NULL,
        NULL,
        0,
        TRUE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000002',
        '20000000-0000-0000-0000-000000000002',
        'Tinh thần nghĩa hiệp',
        'Hành động cứu người của Lục Vân Tiên xuất phát từ quan niệm thấy việc nghĩa mà không làm thì không phải người anh hùng.',
        'Nhóm Ngữ văn LexiLearn',
        'TEACHER',
        NULL,
        NULL,
        NULL,
        0,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000003',
        '20000000-0000-0000-0000-000000000003',
        'Bản tuyên ngôn về độc lập',
        'Tác phẩm đặt nền tảng chính nghĩa trên tư tưởng nhân nghĩa và khẳng định Đại Việt là một quốc gia có lãnh thổ, văn hiến, phong tục và lịch sử riêng.',
        'Ban biên tập LexiLearn',
        'EDITORIAL',
        NULL,
        NULL,
        NULL,
        0,
        TRUE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000004',
        '20000000-0000-0000-0000-000000000004',
        'Lời hịch đánh thức trách nhiệm',
        'Sức thuyết phục của bài hịch đến từ việc kết hợp nỗi đau của chủ tướng, danh dự của tướng sĩ và nguy cơ mất nước thành một lời kêu gọi hành động.',
        'Nhóm Ngữ văn LexiLearn',
        'TEACHER',
        NULL,
        NULL,
        NULL,
        0,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000005',
        '20000000-0000-0000-0000-000000000005',
        'Tiếng nói của người bình dân',
        'Những hình ảnh quen thuộc như tấm lụa đào, gừng cay, muối mặn giúp cảm xúc riêng trở thành kinh nghiệm chung của cộng đồng.',
        'Độc giả thử nghiệm',
        'READER',
        NULL,
        NULL,
        NULL,
        0,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000006',
        '20000000-0000-0000-0000-000000000006',
        'Nỗi đau chiến tranh từ đời sống riêng',
        'Tác phẩm không kể chiến công mà nhìn chiến tranh từ sự cô đơn, chờ đợi và khát vọng đoàn tụ của người ở lại.',
        'Ban biên tập LexiLearn',
        'EDITORIAL',
        NULL,
        NULL,
        NULL,
        0,
        TRUE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000007',
        '20000000-0000-0000-0000-000000000007',
        'Chiếc bóng và bi kịch nhận thức',
        'Chiếc bóng vừa thể hiện tình thương con của Vũ Nương, vừa phơi bày hậu quả của sự hồ đồ khi lời nói không được kiểm chứng và con người không được quyền tự minh oan.',
        'Ban biên tập LexiLearn',
        'EDITORIAL',
        NULL,
        NULL,
        NULL,
        0,
        TRUE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000008',
        '20000000-0000-0000-0000-000000000007',
        'Giá trị nhân đạo',
        'Câu chuyện bênh vực phẩm giá người phụ nữ, bày tỏ niềm thương cảm trước một số phận bị lễ giáo và quyền lực gia trưởng đẩy đến cái chết.',
        'Nhóm Ngữ văn LexiLearn',
        'TEACHER',
        NULL,
        NULL,
        NULL,
        1,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000009',
        '20000000-0000-0000-0000-000000000007',
        'Vai trò của yếu tố kỳ ảo',
        'Thủy cung và cuộc trở về trên sông giúp nhân vật được minh oan, nhưng việc Vũ Nương không thể trở lại vẫn giữ nguyên sức nặng bi kịch.',
        'Người đọc thử nghiệm',
        'READER',
        NULL,
        NULL,
        NULL,
        2,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000010',
        '20000000-0000-0000-0000-000000000007',
        'Bản nháp chưa công bố',
        'Dữ liệu này dùng để kiểm tra rằng public API không trả bình phẩm chưa xuất bản, trong khi API ADMIN vẫn nhìn thấy.',
        'Ban biên tập LexiLearn',
        'EDITORIAL',
        'Dữ liệu kiểm thử nội bộ',
        NULL,
        2026,
        3,
        FALSE,
        FALSE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000011',
        '20000000-0000-0000-0000-000000000008',
        'Tượng đài người nông dân nghĩa sĩ',
        'Tác phẩm đưa người nông dân từ đời sống cui cút, nghèo khó bước thẳng vào vị trí trung tâm của lịch sử bằng hành động tự nguyện cứu nước.',
        'Ban biên tập LexiLearn',
        'EDITORIAL',
        'Văn tế nghĩa sĩ Cần Giuộc - Wikisource',
        'https://vi.wikisource.org/wiki/V%C4%83n_t%E1%BA%BF_ngh%C4%A9a_s%C4%A9_C%E1%BA%A7n_Giu%E1%BB%99c',
        1861,
        0,
        TRUE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000012',
        '20000000-0000-0000-0000-000000000008',
        'Vẻ đẹp bi tráng',
        'Giọng văn vừa đau thương vừa hào hùng khiến cái chết không khép lại hình tượng nghĩa sĩ mà làm sáng rõ lựa chọn sống đánh giặc, thác cũng đánh giặc.',
        'Nhóm Ngữ văn LexiLearn',
        'TEACHER',
        NULL,
        NULL,
        NULL,
        1,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000013',
        '20000000-0000-0000-0000-000000000008',
        'Ngôn ngữ mang hơi thở Nam Bộ',
        'Những vật dụng và động tác gần gũi như áo vải, ngọn tầm vông, rơm con cúi, dao phay tạo nên hình tượng chiến đấu chân thực và mạnh mẽ.',
        'Độc giả thử nghiệm',
        'READER',
        NULL,
        NULL,
        NULL,
        2,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000014',
        '20000000-0000-0000-0000-000000000008',
        'Kết cấu của bài văn tế',
        'Mạch cảm xúc đi từ khái quát thời thế, hồi tưởng cuộc đời, tái hiện chiến công đến tiếng khóc và lời khẳng định sự bất tử của nghĩa sĩ.',
        'Nhóm nghiên cứu LexiLearn',
        'SCHOLAR',
        NULL,
        NULL,
        NULL,
        3,
        FALSE,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '70000000-0000-0000-0000-000000000015',
        '20000000-0000-0000-0000-000000000008',
        'Bản nháp về nhịp điệu',
        'Dữ liệu chưa xuất bản dùng để kiểm thử bộ lọc isPublished và màn hình quản trị.',
        'Ban biên tập LexiLearn',
        'EDITORIAL',
        'Dữ liệu kiểm thử nội bộ',
        NULL,
        2026,
        4,
        FALSE,
        FALSE,
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;
