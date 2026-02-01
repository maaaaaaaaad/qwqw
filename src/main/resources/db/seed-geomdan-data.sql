-- JelloMark 검단 지역 시드 데이터
-- 인천 서구 검단/원당 지역 50개 샵

-- =====================================================
-- 1. 검단 지역 샵 (ID: 750-799, 50개)
-- 중심 좌표: 37.60, 126.65
-- =====================================================
INSERT INTO beautishops (id, owner_id, name, shop_reg_num, phone_number, address, latitude, longitude, operating_time, description, image, average_rating, review_count, created_at, updated_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('aaaa' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 4, '0') || '-' || LPAD(((n % 50) + 51)::text, 12, '0'))::uuid,
    (CASE (n % 10)
        WHEN 0 THEN '글로우네일'
        WHEN 1 THEN '러블리래쉬'
        WHEN 2 THEN '스킨케어랩'
        WHEN 3 THEN '핑크네일'
        WHEN 4 THEN '왁싱하우스'
        WHEN 5 THEN '뷰티풀래쉬'
        WHEN 6 THEN '선탠스튜디오'
        WHEN 7 THEN '엘르네일'
        WHEN 8 THEN '글램스킨'
        ELSE '아트네일'
    END) || ' ' || (CASE ((n / 10) % 5)
        WHEN 0 THEN '검단'
        WHEN 1 THEN '원당'
        WHEN 2 THEN '검암'
        WHEN 3 THEN '마전'
        ELSE '불로'
    END) || (n - 749)::text || '점',
    '120-' || LPAD(((n % 90) + 10)::text, 2, '0') || '-' || LPAD(n::text, 5, '0'),
    '032-' || LPAD((6000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    '인천 서구 ' || (CASE ((n / 10) % 5)
        WHEN 0 THEN '검단로'
        WHEN 1 THEN '원당대로'
        WHEN 2 THEN '검암로'
        WHEN 3 THEN '마전로'
        ELSE '불로로'
    END) || ' ' || (100 + (n % 900))::text,
    -- 검단 지역 좌표: 37.58~37.62, 126.63~126.68
    37.58 + (random() * 0.04) + (CASE ((n / 10) % 5)
        WHEN 0 THEN 0.02   -- 검단
        WHEN 1 THEN 0.03   -- 원당
        WHEN 2 THEN 0.01   -- 검암
        WHEN 3 THEN 0.00   -- 마전
        ELSE 0.02          -- 불로
    END),
    126.63 + (random() * 0.05) + (CASE ((n / 10) % 5)
        WHEN 0 THEN 0.02   -- 검단
        WHEN 1 THEN 0.01   -- 원당
        WHEN 2 THEN 0.00   -- 검암
        WHEN 3 THEN 0.03   -- 마전
        ELSE 0.02          -- 불로
    END),
    '{"monday":"10:00-21:00","tuesday":"10:00-21:00","wednesday":"10:00-21:00","thursday":"10:00-21:00","friday":"10:00-22:00","saturday":"10:00-20:00","sunday":"closed"}',
    '검단 지역 최고의 뷰티샵입니다. 전문적인 시술과 친절한 서비스로 고객만족을 최우선으로 합니다.',
    NULL,
    0,
    0,
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(750, 799) AS n
ON CONFLICT DO NOTHING;

-- =====================================================
-- 2. Shop Category Mappings for 검단 shops
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
FROM generate_series(750, 799) AS n
ON CONFLICT DO NOTHING;

-- =====================================================
-- 3. Treatments for 검단 shops (3 per shop)
-- =====================================================
INSERT INTO treatments (id, shop_id, name, description, price, duration_minutes, created_at, updated_at)
SELECT
    ('dddd' || LPAD((n * 3 + t + 2000)::text, 4, '0') || '-' || LPAD((n * 3 + t + 2000)::text, 4, '0') || '-' || LPAD((n * 3 + t + 2000)::text, 4, '0') || '-' || LPAD((n * 3 + t + 2000)::text, 4, '0') || '-' || LPAD((n * 3 + t + 2000)::text, 12, '0'))::uuid,
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
FROM generate_series(750, 799) AS n
CROSS JOIN generate_series(0, 2) AS t
ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. Reviews for 검단 shops (20+ per shop)
-- =====================================================
INSERT INTO shop_reviews (id, shop_id, member_id, rating, content, created_at, updated_at)
SELECT
    gen_random_uuid(),
    ('bbbb' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 4, '0') || '-' || LPAD(shop_n::text, 12, '0'))::uuid,
    ('cccc' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 4, '0') || '-' || LPAD((102 + ((shop_n * 23 + review_n) % 499))::text, 12, '0'))::uuid,
    (3 + (random() * 2))::int,
    (CASE (review_n % 20)
        WHEN 0 THEN '검단에서 이런 샵을 찾다니! 정말 만족스러웠어요.'
        WHEN 1 THEN '원당 근처에서 최고의 시술을 받았습니다.'
        WHEN 2 THEN '분위기도 좋고 시술도 깔끔해요. 추천합니다!'
        WHEN 3 THEN '가격 대비 최고의 서비스를 받았어요.'
        WHEN 4 THEN '직원분들이 정말 친절하세요. 편안한 시간이었습니다.'
        WHEN 5 THEN '예약하기 쉽고 대기시간도 없어서 좋았어요.'
        WHEN 6 THEN '결과물이 정말 예뻐요! 만족스럽습니다.'
        WHEN 7 THEN '청결하고 위생적인 환경이 인상적이었어요.'
        WHEN 8 THEN '세심한 케어 덕분에 피부가 좋아졌어요.'
        WHEN 9 THEN '전문적인 상담이 좋았습니다. 다시 방문할게요.'
        WHEN 10 THEN '검단 신도시에서 이 정도 퀄리티면 최고!'
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
FROM generate_series(750, 799) AS shop_n
CROSS JOIN generate_series(1, 22) AS review_n
ON CONFLICT DO NOTHING;

-- =====================================================
-- 5. Update review counts for 검단 shops
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
    FROM generate_series(750, 799) AS n
);

-- =====================================================
-- Result Summary
-- =====================================================
SELECT
    '검단 지역 데이터 삽입 완료!' as status,
    (SELECT COUNT(*) FROM beautishops WHERE address LIKE '%서구%검단%' OR address LIKE '%서구%원당%' OR address LIKE '%서구%검암%' OR address LIKE '%서구%마전%' OR address LIKE '%서구%불로%') as geomdan_shops;
