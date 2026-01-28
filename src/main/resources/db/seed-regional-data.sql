-- JelloMark Regional Bulk Data
-- 인천, 수원, 안양, 성남, 부천 각 지역 100개+ 샵, 샵당 20개+ 리뷰

-- =====================================================
-- 1. Additional Owners (51-100)
-- =====================================================
INSERT INTO owners (id, business_number, phone_number, nickname, email, created_at, updated_at)
SELECT
    ('aaaa' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    LPAD((200000000 + n)::text, 9, '0'),
    '010-' || LPAD((2000 + n)::text, 4, '0') || '-' || LPAD((2000 + n)::text, 4, '0'),
    'owner_' || n,
    'owner' || n || '@jellomark.com',
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(51, 100) AS n
ON CONFLICT DO NOTHING;

-- =====================================================
-- 2. Additional Members (102-600)
-- =====================================================
INSERT INTO members (id, nickname, display_name, social_provider, social_id, created_at, updated_at)
SELECT
    ('cccc' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    '젤리' || LPAD(n::text, 6, '0'),
    (CASE (n % 10)
        WHEN 0 THEN '뷰티러버'
        WHEN 1 THEN '네일매니아'
        WHEN 2 THEN '피부미인'
        WHEN 3 THEN '래쉬퀸'
        WHEN 4 THEN '왁싱달인'
        WHEN 5 THEN '뷰티홀릭'
        WHEN 6 THEN '스킨케어'
        WHEN 7 THEN '네일아트'
        WHEN 8 THEN '뷰티스타'
        ELSE '젤리'
    END) || (n % 100)::text,
    'KAKAO',
    'kakao_regional_' || n,
    NOW() - (random() * INTERVAL '180 days'),
    NOW()
FROM generate_series(102, 600) AS n
ON CONFLICT DO NOTHING;

-- =====================================================
-- 3. 인천 지역 샵 (ID: 200-309, 110개)
-- =====================================================
INSERT INTO beautishops (id, owner_id, name, shop_reg_num, phone_number, address, latitude, longitude, operating_time, description, image, average_rating, review_count, created_at, updated_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('aaaa' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 12, '0'))::uuid,
    (CASE (n % 15)
        WHEN 0 THEN '글로우네일'
        WHEN 1 THEN '러블리래쉬'
        WHEN 2 THEN '스킨케어랩'
        WHEN 3 THEN '핑크네일'
        WHEN 4 THEN '왁싱하우스'
        WHEN 5 THEN '뷰티풀래쉬'
        WHEN 6 THEN '선탠스튜디오'
        WHEN 7 THEN '엘르네일'
        WHEN 8 THEN '글램스킨'
        WHEN 9 THEN '아트네일'
        WHEN 10 THEN '래쉬바'
        WHEN 11 THEN '뷰티살롱'
        WHEN 12 THEN '프렌치네일'
        WHEN 13 THEN '스킨앤바디'
        ELSE '뷰티랩'
    END) || ' ' || (CASE ((n / 15) % 8)
        WHEN 0 THEN '부평'
        WHEN 1 THEN '인천구월'
        WHEN 2 THEN '송도'
        WHEN 3 THEN '주안'
        WHEN 4 THEN '간석'
        WHEN 5 THEN '작전'
        WHEN 6 THEN '청라'
        ELSE '계양'
    END) || (n - 199)::text || '점',
    '120-' || LPAD(((n % 90) + 10)::text, 2, '0') || '-' || LPAD(n::text, 5, '0'),
    '032-' || LPAD((5000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    '인천 ' || (CASE ((n / 15) % 8)
        WHEN 0 THEN '부평구 부평대로'
        WHEN 1 THEN '남동구 구월로'
        WHEN 2 THEN '연수구 송도과학로'
        WHEN 3 THEN '미추홀구 주안로'
        WHEN 4 THEN '남동구 간석로'
        WHEN 5 THEN '계양구 작전로'
        WHEN 6 THEN '서구 청라대로'
        ELSE '계양구 계양대로'
    END) || ' ' || (100 + (n % 900))::text,
    37.45 + (random() * 0.04) - 0.02 + (CASE ((n / 15) % 8)
        WHEN 0 THEN 0.02   -- 부평
        WHEN 1 THEN -0.01  -- 구월
        WHEN 2 THEN -0.03  -- 송도
        WHEN 3 THEN 0.00   -- 주안
        WHEN 4 THEN -0.01  -- 간석
        WHEN 5 THEN 0.03   -- 작전
        WHEN 6 THEN 0.02   -- 청라
        ELSE 0.04          -- 계양
    END),
    126.70 + (random() * 0.06) - 0.03 + (CASE ((n / 15) % 8)
        WHEN 0 THEN -0.02  -- 부평
        WHEN 1 THEN 0.05   -- 구월
        WHEN 2 THEN 0.10   -- 송도
        WHEN 3 THEN 0.00   -- 주안
        WHEN 4 THEN 0.03   -- 간석
        WHEN 5 THEN -0.01  -- 작전
        WHEN 6 THEN -0.05  -- 청라
        ELSE 0.00          -- 계양
    END),
    '{"monday":"10:00-21:00","tuesday":"10:00-21:00","wednesday":"10:00-21:00","thursday":"10:00-21:00","friday":"10:00-22:00","saturday":"10:00-20:00","sunday":"closed"}',
    (CASE (n % 10)
        WHEN 0 THEN '인천 최고의 네일아트 전문점입니다. 트렌디한 디자인과 섬세한 시술로 고객님을 맞이합니다.'
        WHEN 1 THEN '자연스러운 속눈썹 연장 전문. 볼륨래쉬부터 클래식까지 다양한 스타일을 제공합니다.'
        WHEN 2 THEN '피부 타입별 맞춤 관리로 건강한 피부를 만들어드립니다. 최신 장비 완비.'
        WHEN 3 THEN '청결하고 위생적인 왁싱 전문샵. 브라질리언, 페이스, 바디 왁싱 가능.'
        WHEN 4 THEN '건강한 선탠을 위한 프리미엄 태닝샵. 스프레이탠, 머신탠 가능합니다.'
        WHEN 5 THEN '감각적인 네일 디자인과 친절한 서비스로 만족을 드립니다.'
        WHEN 6 THEN '수분관리, 탄력관리, 미백관리 등 다양한 피부 케어 프로그램 운영.'
        WHEN 7 THEN '래쉬 리프팅, 연장 전문. 자연스러운 눈매를 연출해드립니다.'
        WHEN 8 THEN '합리적인 가격에 최상의 서비스를 제공하는 뷰티샵입니다.'
        ELSE '프리미엄 뷰티 서비스를 경험해보세요. 단골 할인 이벤트 진행중!'
    END),
    'https://images.unsplash.com/photo-' || (CASE (n % 6)
        WHEN 0 THEN '1604654894610-df63bc536371'
        WHEN 1 THEN '1522337360788-8b13dee7a37e'
        WHEN 2 THEN '1570172619644-dfd03ed5d881'
        WHEN 3 THEN '1519014816548-bf5fe059798b'
        WHEN 4 THEN '1560750588-73207b1ef5b8'
        ELSE '1583001931096-959e9a1a6223'
    END) || '?w=800',
    3.5 + (random() * 1.5),
    0,
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(200, 309) AS n
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 4. 수원 지역 샵 (ID: 310-419, 110개)
-- =====================================================
INSERT INTO beautishops (id, owner_id, name, shop_reg_num, phone_number, address, latitude, longitude, operating_time, description, image, average_rating, review_count, created_at, updated_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('aaaa' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 12, '0'))::uuid,
    (CASE (n % 15)
        WHEN 0 THEN '글로우네일'
        WHEN 1 THEN '러블리래쉬'
        WHEN 2 THEN '스킨케어랩'
        WHEN 3 THEN '핑크네일'
        WHEN 4 THEN '왁싱하우스'
        WHEN 5 THEN '뷰티풀래쉬'
        WHEN 6 THEN '선탠스튜디오'
        WHEN 7 THEN '엘르네일'
        WHEN 8 THEN '글램스킨'
        WHEN 9 THEN '아트네일'
        WHEN 10 THEN '래쉬바'
        WHEN 11 THEN '뷰티살롱'
        WHEN 12 THEN '프렌치네일'
        WHEN 13 THEN '스킨앤바디'
        ELSE '뷰티랩'
    END) || ' ' || (CASE ((n / 15) % 8)
        WHEN 0 THEN '수원역'
        WHEN 1 THEN '영통'
        WHEN 2 THEN '광교'
        WHEN 3 THEN '인계동'
        WHEN 4 THEN '매탄'
        WHEN 5 THEN '권선'
        WHEN 6 THEN '팔달'
        ELSE '장안'
    END) || (n - 309)::text || '점',
    '130-' || LPAD(((n % 90) + 10)::text, 2, '0') || '-' || LPAD(n::text, 5, '0'),
    '031-' || LPAD((2000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    '수원시 ' || (CASE ((n / 15) % 8)
        WHEN 0 THEN '팔달구 덕영대로'
        WHEN 1 THEN '영통구 영통로'
        WHEN 2 THEN '영통구 광교로'
        WHEN 3 THEN '팔달구 인계로'
        WHEN 4 THEN '영통구 매탄로'
        WHEN 5 THEN '권선구 권선로'
        WHEN 6 THEN '팔달구 팔달로'
        ELSE '장안구 장안로'
    END) || ' ' || (100 + (n % 900))::text,
    37.26 + (random() * 0.04) - 0.02 + (CASE ((n / 15) % 8)
        WHEN 0 THEN 0.00   -- 수원역
        WHEN 1 THEN 0.02   -- 영통
        WHEN 2 THEN 0.03   -- 광교
        WHEN 3 THEN 0.01   -- 인계동
        WHEN 4 THEN 0.02   -- 매탄
        WHEN 5 THEN -0.01  -- 권선
        WHEN 6 THEN 0.00   -- 팔달
        ELSE 0.03          -- 장안
    END),
    127.03 + (random() * 0.04) - 0.02 + (CASE ((n / 15) % 8)
        WHEN 0 THEN 0.00   -- 수원역
        WHEN 1 THEN 0.05   -- 영통
        WHEN 2 THEN 0.07   -- 광교
        WHEN 3 THEN 0.02   -- 인계동
        WHEN 4 THEN 0.04   -- 매탄
        WHEN 5 THEN -0.01  -- 권선
        WHEN 6 THEN 0.00   -- 팔달
        ELSE 0.01          -- 장안
    END),
    '{"monday":"10:00-21:00","tuesday":"10:00-21:00","wednesday":"10:00-21:00","thursday":"10:00-21:00","friday":"10:00-22:00","saturday":"10:00-20:00","sunday":"closed"}',
    (CASE (n % 10)
        WHEN 0 THEN '수원 최고의 네일아트 전문점입니다. 트렌디한 디자인과 섬세한 시술.'
        WHEN 1 THEN '자연스러운 속눈썹 연장 전문. 다양한 스타일을 제공합니다.'
        WHEN 2 THEN '피부 타입별 맞춤 관리로 건강한 피부를 만들어드립니다.'
        WHEN 3 THEN '청결하고 위생적인 왁싱 전문샵입니다.'
        WHEN 4 THEN '건강한 선탠을 위한 프리미엄 태닝샵.'
        WHEN 5 THEN '감각적인 네일 디자인과 친절한 서비스.'
        WHEN 6 THEN '수분관리, 탄력관리, 미백관리 등 다양한 케어.'
        WHEN 7 THEN '래쉬 리프팅, 연장 전문샵입니다.'
        WHEN 8 THEN '합리적인 가격에 최상의 서비스를 제공.'
        ELSE '프리미엄 뷰티 서비스를 경험해보세요.'
    END),
    'https://images.unsplash.com/photo-' || (CASE (n % 6)
        WHEN 0 THEN '1604654894610-df63bc536371'
        WHEN 1 THEN '1522337360788-8b13dee7a37e'
        WHEN 2 THEN '1570172619644-dfd03ed5d881'
        WHEN 3 THEN '1519014816548-bf5fe059798b'
        WHEN 4 THEN '1560750588-73207b1ef5b8'
        ELSE '1583001931096-959e9a1a6223'
    END) || '?w=800',
    3.5 + (random() * 1.5),
    0,
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(310, 419) AS n
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 5. 안양 지역 샵 (ID: 420-529, 110개)
-- =====================================================
INSERT INTO beautishops (id, owner_id, name, shop_reg_num, phone_number, address, latitude, longitude, operating_time, description, image, average_rating, review_count, created_at, updated_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('aaaa' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 12, '0'))::uuid,
    (CASE (n % 15)
        WHEN 0 THEN '글로우네일'
        WHEN 1 THEN '러블리래쉬'
        WHEN 2 THEN '스킨케어랩'
        WHEN 3 THEN '핑크네일'
        WHEN 4 THEN '왁싱하우스'
        WHEN 5 THEN '뷰티풀래쉬'
        WHEN 6 THEN '선탠스튜디오'
        WHEN 7 THEN '엘르네일'
        WHEN 8 THEN '글램스킨'
        WHEN 9 THEN '아트네일'
        WHEN 10 THEN '래쉬바'
        WHEN 11 THEN '뷰티살롱'
        WHEN 12 THEN '프렌치네일'
        WHEN 13 THEN '스킨앤바디'
        ELSE '뷰티랩'
    END) || ' ' || (CASE ((n / 15) % 6)
        WHEN 0 THEN '안양역'
        WHEN 1 THEN '범계'
        WHEN 2 THEN '평촌'
        WHEN 3 THEN '인덕원'
        WHEN 4 THEN '비산'
        ELSE '관양'
    END) || (n - 419)::text || '점',
    '140-' || LPAD(((n % 90) + 10)::text, 2, '0') || '-' || LPAD(n::text, 5, '0'),
    '031-' || LPAD((3000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    '안양시 ' || (CASE ((n / 15) % 6)
        WHEN 0 THEN '만안구 안양로'
        WHEN 1 THEN '동안구 시민대로'
        WHEN 2 THEN '동안구 평촌대로'
        WHEN 3 THEN '동안구 인덕원로'
        WHEN 4 THEN '만안구 비산로'
        ELSE '동안구 관양로'
    END) || ' ' || (100 + (n % 900))::text,
    37.39 + (random() * 0.03) - 0.015 + (CASE ((n / 15) % 6)
        WHEN 0 THEN 0.00   -- 안양역
        WHEN 1 THEN 0.01   -- 범계
        WHEN 2 THEN 0.02   -- 평촌
        WHEN 3 THEN 0.02   -- 인덕원
        WHEN 4 THEN -0.01  -- 비산
        ELSE 0.01          -- 관양
    END),
    126.92 + (random() * 0.04) - 0.02 + (CASE ((n / 15) % 6)
        WHEN 0 THEN 0.00   -- 안양역
        WHEN 1 THEN 0.03   -- 범계
        WHEN 2 THEN 0.04   -- 평촌
        WHEN 3 THEN 0.05   -- 인덕원
        WHEN 4 THEN 0.01   -- 비산
        ELSE 0.04          -- 관양
    END),
    '{"monday":"10:00-21:00","tuesday":"10:00-21:00","wednesday":"10:00-21:00","thursday":"10:00-21:00","friday":"10:00-22:00","saturday":"10:00-20:00","sunday":"closed"}',
    (CASE (n % 10)
        WHEN 0 THEN '안양 최고의 네일아트 전문점입니다.'
        WHEN 1 THEN '자연스러운 속눈썹 연장 전문샵.'
        WHEN 2 THEN '피부 타입별 맞춤 관리 전문.'
        WHEN 3 THEN '청결하고 위생적인 왁싱 전문샵.'
        WHEN 4 THEN '건강한 선탠을 위한 프리미엄 태닝샵.'
        WHEN 5 THEN '감각적인 네일 디자인과 친절한 서비스.'
        WHEN 6 THEN '다양한 피부 케어 프로그램 운영.'
        WHEN 7 THEN '래쉬 리프팅, 연장 전문.'
        WHEN 8 THEN '합리적인 가격에 최상의 서비스.'
        ELSE '프리미엄 뷰티 서비스를 경험해보세요.'
    END),
    'https://images.unsplash.com/photo-' || (CASE (n % 6)
        WHEN 0 THEN '1604654894610-df63bc536371'
        WHEN 1 THEN '1522337360788-8b13dee7a37e'
        WHEN 2 THEN '1570172619644-dfd03ed5d881'
        WHEN 3 THEN '1519014816548-bf5fe059798b'
        WHEN 4 THEN '1560750588-73207b1ef5b8'
        ELSE '1583001931096-959e9a1a6223'
    END) || '?w=800',
    3.5 + (random() * 1.5),
    0,
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(420, 529) AS n
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 6. 성남 지역 샵 (ID: 530-639, 110개)
-- =====================================================
INSERT INTO beautishops (id, owner_id, name, shop_reg_num, phone_number, address, latitude, longitude, operating_time, description, image, average_rating, review_count, created_at, updated_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('aaaa' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 12, '0'))::uuid,
    (CASE (n % 15)
        WHEN 0 THEN '글로우네일'
        WHEN 1 THEN '러블리래쉬'
        WHEN 2 THEN '스킨케어랩'
        WHEN 3 THEN '핑크네일'
        WHEN 4 THEN '왁싱하우스'
        WHEN 5 THEN '뷰티풀래쉬'
        WHEN 6 THEN '선탠스튜디오'
        WHEN 7 THEN '엘르네일'
        WHEN 8 THEN '글램스킨'
        WHEN 9 THEN '아트네일'
        WHEN 10 THEN '래쉬바'
        WHEN 11 THEN '뷰티살롱'
        WHEN 12 THEN '프렌치네일'
        WHEN 13 THEN '스킨앤바디'
        ELSE '뷰티랩'
    END) || ' ' || (CASE ((n / 15) % 7)
        WHEN 0 THEN '분당'
        WHEN 1 THEN '판교'
        WHEN 2 THEN '정자'
        WHEN 3 THEN '서현'
        WHEN 4 THEN '야탑'
        WHEN 5 THEN '모란'
        ELSE '위례'
    END) || (n - 529)::text || '점',
    '150-' || LPAD(((n % 90) + 10)::text, 2, '0') || '-' || LPAD(n::text, 5, '0'),
    '031-' || LPAD((7000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    '성남시 ' || (CASE ((n / 15) % 7)
        WHEN 0 THEN '분당구 분당로'
        WHEN 1 THEN '분당구 판교로'
        WHEN 2 THEN '분당구 정자로'
        WHEN 3 THEN '분당구 서현로'
        WHEN 4 THEN '분당구 야탑로'
        WHEN 5 THEN '중원구 모란로'
        ELSE '수정구 위례로'
    END) || ' ' || (100 + (n % 900))::text,
    37.38 + (random() * 0.06) - 0.03 + (CASE ((n / 15) % 7)
        WHEN 0 THEN 0.04   -- 분당
        WHEN 1 THEN 0.02   -- 판교
        WHEN 2 THEN 0.05   -- 정자
        WHEN 3 THEN 0.04   -- 서현
        WHEN 4 THEN 0.03   -- 야탑
        WHEN 5 THEN 0.00   -- 모란
        ELSE -0.01         -- 위례
    END),
    127.10 + (random() * 0.06) - 0.03 + (CASE ((n / 15) % 7)
        WHEN 0 THEN 0.05   -- 분당
        WHEN 1 THEN 0.02   -- 판교
        WHEN 2 THEN 0.06   -- 정자
        WHEN 3 THEN 0.05   -- 서현
        WHEN 4 THEN 0.04   -- 야탑
        WHEN 5 THEN 0.00   -- 모란
        ELSE -0.02         -- 위례
    END),
    '{"monday":"10:00-21:00","tuesday":"10:00-21:00","wednesday":"10:00-21:00","thursday":"10:00-21:00","friday":"10:00-22:00","saturday":"10:00-20:00","sunday":"closed"}',
    (CASE (n % 10)
        WHEN 0 THEN '성남/분당 최고의 네일아트 전문점입니다.'
        WHEN 1 THEN '자연스러운 속눈썹 연장 전문샵.'
        WHEN 2 THEN '피부 타입별 맞춤 관리 전문.'
        WHEN 3 THEN '청결하고 위생적인 왁싱 전문샵.'
        WHEN 4 THEN '건강한 선탠을 위한 프리미엄 태닝샵.'
        WHEN 5 THEN '감각적인 네일 디자인과 친절한 서비스.'
        WHEN 6 THEN '다양한 피부 케어 프로그램 운영.'
        WHEN 7 THEN '래쉬 리프팅, 연장 전문.'
        WHEN 8 THEN '합리적인 가격에 최상의 서비스.'
        ELSE '프리미엄 뷰티 서비스를 경험해보세요.'
    END),
    'https://images.unsplash.com/photo-' || (CASE (n % 6)
        WHEN 0 THEN '1604654894610-df63bc536371'
        WHEN 1 THEN '1522337360788-8b13dee7a37e'
        WHEN 2 THEN '1570172619644-dfd03ed5d881'
        WHEN 3 THEN '1519014816548-bf5fe059798b'
        WHEN 4 THEN '1560750588-73207b1ef5b8'
        ELSE '1583001931096-959e9a1a6223'
    END) || '?w=800',
    3.5 + (random() * 1.5),
    0,
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(530, 639) AS n
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 7. 부천 지역 샵 (ID: 640-749, 110개)
-- =====================================================
INSERT INTO beautishops (id, owner_id, name, shop_reg_num, phone_number, address, latitude, longitude, operating_time, description, image, average_rating, review_count, created_at, updated_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('aaaa' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 12, '0'))::uuid,
    (CASE (n % 15)
        WHEN 0 THEN '글로우네일'
        WHEN 1 THEN '러블리래쉬'
        WHEN 2 THEN '스킨케어랩'
        WHEN 3 THEN '핑크네일'
        WHEN 4 THEN '왁싱하우스'
        WHEN 5 THEN '뷰티풀래쉬'
        WHEN 6 THEN '선탠스튜디오'
        WHEN 7 THEN '엘르네일'
        WHEN 8 THEN '글램스킨'
        WHEN 9 THEN '아트네일'
        WHEN 10 THEN '래쉬바'
        WHEN 11 THEN '뷰티살롱'
        WHEN 12 THEN '프렌치네일'
        WHEN 13 THEN '스킨앤바디'
        ELSE '뷰티랩'
    END) || ' ' || (CASE ((n / 15) % 6)
        WHEN 0 THEN '부천역'
        WHEN 1 THEN '중동'
        WHEN 2 THEN '상동'
        WHEN 3 THEN '송내'
        WHEN 4 THEN '역곡'
        ELSE '소사'
    END) || (n - 639)::text || '점',
    '160-' || LPAD(((n % 90) + 10)::text, 2, '0') || '-' || LPAD(n::text, 5, '0'),
    '032-' || LPAD((6000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    '부천시 ' || (CASE ((n / 15) % 6)
        WHEN 0 THEN '원미구 부천로'
        WHEN 1 THEN '원미구 중동로'
        WHEN 2 THEN '원미구 상동로'
        WHEN 3 THEN '원미구 송내대로'
        WHEN 4 THEN '원미구 역곡로'
        ELSE '소사구 소사로'
    END) || ' ' || (100 + (n % 900))::text,
    37.48 + (random() * 0.04) - 0.02 + (CASE ((n / 15) % 6)
        WHEN 0 THEN 0.00   -- 부천역
        WHEN 1 THEN 0.02   -- 중동
        WHEN 2 THEN 0.03   -- 상동
        WHEN 3 THEN -0.01  -- 송내
        WHEN 4 THEN -0.02  -- 역곡
        ELSE -0.01         -- 소사
    END),
    126.76 + (random() * 0.04) - 0.02 + (CASE ((n / 15) % 6)
        WHEN 0 THEN 0.00   -- 부천역
        WHEN 1 THEN 0.02   -- 중동
        WHEN 2 THEN 0.03   -- 상동
        WHEN 3 THEN -0.02  -- 송내
        WHEN 4 THEN -0.04  -- 역곡
        ELSE -0.01         -- 소사
    END),
    '{"monday":"10:00-21:00","tuesday":"10:00-21:00","wednesday":"10:00-21:00","thursday":"10:00-21:00","friday":"10:00-22:00","saturday":"10:00-20:00","sunday":"closed"}',
    (CASE (n % 10)
        WHEN 0 THEN '부천 최고의 네일아트 전문점입니다.'
        WHEN 1 THEN '자연스러운 속눈썹 연장 전문샵.'
        WHEN 2 THEN '피부 타입별 맞춤 관리 전문.'
        WHEN 3 THEN '청결하고 위생적인 왁싱 전문샵.'
        WHEN 4 THEN '건강한 선탠을 위한 프리미엄 태닝샵.'
        WHEN 5 THEN '감각적인 네일 디자인과 친절한 서비스.'
        WHEN 6 THEN '다양한 피부 케어 프로그램 운영.'
        WHEN 7 THEN '래쉬 리프팅, 연장 전문.'
        WHEN 8 THEN '합리적인 가격에 최상의 서비스.'
        ELSE '프리미엄 뷰티 서비스를 경험해보세요.'
    END),
    'https://images.unsplash.com/photo-' || (CASE (n % 6)
        WHEN 0 THEN '1604654894610-df63bc536371'
        WHEN 1 THEN '1522337360788-8b13dee7a37e'
        WHEN 2 THEN '1570172619644-dfd03ed5d881'
        WHEN 3 THEN '1519014816548-bf5fe059798b'
        WHEN 4 THEN '1560750588-73207b1ef5b8'
        ELSE '1583001931096-959e9a1a6223'
    END) || '?w=800',
    3.5 + (random() * 1.5),
    0,
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(640, 749) AS n
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 8. Shop Category Mappings for new shops
-- =====================================================
INSERT INTO shop_category_mappings (shop_id, category_id, created_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    (CASE (n % 6)
        WHEN 0 THEN '11111111-1111-1111-1111-111111111111'  -- 네일
        WHEN 1 THEN '22222222-2222-2222-2222-222222222222'  -- 속눈썹
        WHEN 2 THEN '44444444-4444-4444-4444-444444444444'  -- 피부관리
        WHEN 3 THEN '33333333-3333-3333-3333-333333333333'  -- 왁싱
        WHEN 4 THEN '55555555-5555-5555-5555-555555555555'  -- 태닝
        ELSE '11111111-1111-1111-1111-111111111111'         -- 네일
    END)::uuid,
    NOW()
FROM generate_series(200, 749) AS n
ON CONFLICT DO NOTHING;

-- =====================================================
-- 9. Treatments for new shops (3 per shop)
-- =====================================================
INSERT INTO treatments (id, shop_id, name, description, price, duration_minutes, created_at, updated_at)
SELECT
    ('dddd' || LPAD((n * 3 + t)::text, 4, '0') || '-' || LPAD((n * 3 + t)::text, 4, '0') || '-' || LPAD((n * 3 + t)::text, 4, '0') || '-' || LPAD((n * 3 + t)::text, 4, '0') || '-' || LPAD((n * 3 + t)::text, 12, '0'))::uuid,
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    (CASE t
        WHEN 0 THEN (CASE (n % 6) WHEN 0 THEN '젤네일 풀세트' WHEN 1 THEN '속눈썹 연장 80모' WHEN 2 THEN '수분관리' WHEN 3 THEN '브라질리언 왁싱' WHEN 4 THEN '전신 태닝' ELSE '아트네일' END)
        WHEN 1 THEN (CASE (n % 6) WHEN 0 THEN '손톱 케어' WHEN 1 THEN '속눈썹 펌' WHEN 2 THEN '탄력관리' WHEN 3 THEN '페이스 왁싱' WHEN 4 THEN '상반신 태닝' ELSE '프렌치 네일' END)
        ELSE (CASE (n % 6) WHEN 0 THEN '페디큐어' WHEN 1 THEN '속눈썹 제거' WHEN 2 THEN '미백관리' WHEN 3 THEN '다리 왁싱' WHEN 4 THEN '페이스 태닝' ELSE '젤 제거' END)
    END),
    '프리미엄 서비스로 최상의 만족을 드립니다.',
    (30000 + (random() * 70000))::int,
    (30 + (t * 15))::int,
    NOW() - (random() * INTERVAL '180 days'),
    NOW()
FROM generate_series(200, 749) AS n
CROSS JOIN generate_series(0, 2) AS t
ON CONFLICT DO NOTHING;

-- =====================================================
-- 10. Reviews for new shops (20+ per shop)
-- =====================================================
INSERT INTO shop_reviews (id, shop_id, member_id, rating, content, created_at, updated_at)
SELECT
    gen_random_uuid(),
    ('bbbb' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 12, '0'))::uuid,
    ('cccc' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 12, '0'))::uuid,
    (3 + (random() * 2))::int,
    (CASE (review_n % 20)
        WHEN 0 THEN '정말 만족스러운 시술이었어요! 다음에도 꼭 방문할게요.'
        WHEN 1 THEN '친절한 상담과 섬세한 시술 감사합니다.'
        WHEN 2 THEN '분위기도 좋고 시술도 깔끔해요. 추천합니다!'
        WHEN 3 THEN '가격 대비 최고의 서비스를 받았어요.'
        WHEN 4 THEN '직원분들이 정말 친절하세요. 편안한 시간이었습니다.'
        WHEN 5 THEN '예약하기 쉽고 대기시간도 없어서 좋았어요.'
        WHEN 6 THEN '결과물이 정말 예뻐요! 만족스럽습니다.'
        WHEN 7 THEN '청결하고 위생적인 환경이 인상적이었어요.'
        WHEN 8 THEN '세심한 케어 덕분에 피부가 좋아졌어요.'
        WHEN 9 THEN '전문적인 상담이 좋았습니다. 다시 방문할게요.'
        WHEN 10 THEN '시술 시간도 적당하고 결과도 좋아요.'
        WHEN 11 THEN '주차가 편해서 좋았어요. 시술도 만족!'
        WHEN 12 THEN '처음 방문했는데 친절하게 안내해주셔서 감사해요.'
        WHEN 13 THEN '여러 번 다녀왔는데 항상 만족스러워요.'
        WHEN 14 THEN '트렌디한 스타일로 시술해주셔서 감사합니다.'
        WHEN 15 THEN '시술 후 관리 방법도 알려주셔서 좋았어요.'
        WHEN 16 THEN '프라이빗한 공간이라 편안했어요.'
        WHEN 17 THEN '합리적인 가격에 퀄리티 높은 서비스!'
        WHEN 18 THEN '다음에 친구들도 데려올게요. 좋은 곳이에요.'
        ELSE '전체적으로 만족스러운 경험이었습니다.'
    END),
    NOW() - ((random() * 180) || ' days')::interval,
    NOW()
FROM generate_series(200, 749) AS shop_n
CROSS JOIN generate_series(1, 22) AS review_n
ON CONFLICT DO NOTHING;

-- =====================================================
-- 11. Update review counts for new shops
-- =====================================================
UPDATE beautishops b
SET review_count = (
    SELECT COUNT(*) FROM shop_reviews r WHERE r.shop_id = b.id
),
average_rating = (
    SELECT COALESCE(AVG(r.rating), 0) FROM shop_reviews r WHERE r.shop_id = b.id
)
WHERE b.id IN (
    SELECT ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid
    FROM generate_series(200, 749) AS n
);

-- =====================================================
-- Result Summary
-- =====================================================
SELECT
    '지역별 대규모 데이터 삽입 완료!' as status,
    (SELECT COUNT(*) FROM beautishops) as total_shops,
    (SELECT COUNT(*) FROM treatments) as total_treatments,
    (SELECT COUNT(*) FROM members) as total_members,
    (SELECT COUNT(*) FROM shop_reviews) as total_reviews;
