-- JelloMark Bulk Test Data for Performance Testing
-- Generates 150+ shops with treatments and reviews

-- =====================================================
-- Generate 150 additional shops using generate_series
-- =====================================================

-- Helper: More owners for variety
INSERT INTO owners (id, business_number, phone_number, nickname, email, created_at, updated_at)
SELECT
    ('aaaa' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    LPAD((100000000 + n)::text, 9, '0'),
    '010-' || LPAD((1000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    'owner_' || n,
    'owner' || n || '@test.com',
    NOW() - (random() * INTERVAL '180 days'),
    NOW()
FROM generate_series(10, 50) AS n
ON CONFLICT DO NOTHING;

-- 150 shops across Seoul
INSERT INTO beautishops (id, owner_id, name, shop_reg_num, phone_number, address, latitude, longitude, operating_time, description, image, average_rating, review_count, created_at, updated_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('aaaa' || LPAD(((n % 41) + 10)::text, 4, '0') || '-' || LPAD(((n % 41) + 10)::text, 4, '0') || '-' || LPAD(((n % 41) + 10)::text, 4, '0') || '-' || LPAD(((n % 41) + 10)::text, 4, '0') || '-' || LPAD(((n % 41) + 10)::text, 12, '0'))::uuid,
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
    END) || ' ' || (CASE ((n / 15) % 10)
        WHEN 0 THEN '강남'
        WHEN 1 THEN '홍대'
        WHEN 2 THEN '신촌'
        WHEN 3 THEN '잠실'
        WHEN 4 THEN '성수'
        WHEN 5 THEN '명동'
        WHEN 6 THEN '이태원'
        WHEN 7 THEN '압구정'
        WHEN 8 THEN '여의도'
        ELSE '건대'
    END) || n::text || '점',
    '110-11-' || LPAD(n::text, 5, '0'),
    '02-' || LPAD((5000 + n)::text, 4, '0') || '-' || LPAD((1000 + n)::text, 4, '0'),
    '서울 ' || (CASE ((n / 15) % 10)
        WHEN 0 THEN '강남구 강남대로'
        WHEN 1 THEN '마포구 홍익로'
        WHEN 2 THEN '서대문구 연세로'
        WHEN 3 THEN '송파구 올림픽로'
        WHEN 4 THEN '성동구 성수이로'
        WHEN 5 THEN '중구 명동길'
        WHEN 6 THEN '용산구 이태원로'
        WHEN 7 THEN '강남구 압구정로'
        WHEN 8 THEN '영등포구 여의대로'
        ELSE '광진구 능동로'
    END) || ' ' || (100 + (n % 900))::text,
    -- Seoul area coordinates with variation
    37.50 + (random() * 0.1) - 0.05 + (CASE ((n / 15) % 10)
        WHEN 0 THEN 0.00  -- 강남
        WHEN 1 THEN 0.055  -- 홍대
        WHEN 2 THEN 0.058  -- 신촌
        WHEN 3 THEN 0.01  -- 잠실
        WHEN 4 THEN 0.044  -- 성수
        WHEN 5 THEN 0.063  -- 명동
        WHEN 6 THEN 0.034  -- 이태원
        WHEN 7 THEN 0.025  -- 압구정
        WHEN 8 THEN 0.022  -- 여의도
        ELSE 0.040  -- 건대
    END),
    127.00 + (random() * 0.1) - 0.05 + (CASE ((n / 15) % 10)
        WHEN 0 THEN 0.028  -- 강남
        WHEN 1 THEN -0.076  -- 홍대
        WHEN 2 THEN -0.063  -- 신촌
        WHEN 3 THEN 0.086  -- 잠실
        WHEN 4 THEN 0.057  -- 성수
        WHEN 5 THEN -0.013  -- 명동
        WHEN 6 THEN -0.005  -- 이태원
        WHEN 7 THEN 0.029  -- 압구정
        WHEN 8 THEN -0.075  -- 여의도
        ELSE 0.070  -- 건대
    END),
    '{"monday":"10:00-21:00","tuesday":"10:00-21:00","wednesday":"10:00-21:00","thursday":"10:00-21:00","friday":"10:00-22:00","saturday":"10:00-20:00","sunday":"closed"}',
    (CASE (n % 10)
        WHEN 0 THEN '트렌디한 네일아트 전문점입니다. 젤네일, 아트네일, 케어까지 모든 네일 서비스를 제공합니다.'
        WHEN 1 THEN '자연스러운 속눈썹 연장 전문샵. 볼륨래쉬, 클래식래쉬, 하이브리드까지!'
        WHEN 2 THEN '최신 피부관리 장비를 갖춘 프리미엄 스킨케어샵. 여드름, 모공, 주름 관리 전문'
        WHEN 3 THEN '청결하고 프라이빗한 왁싱 전문샵. 브라질리언, 페이스, 바디 왁싱'
        WHEN 4 THEN '건강한 선탠을 위한 프리미엄 태닝샵. 스프레이탠, 머신탠, 셀프탠'
        WHEN 5 THEN '감각적인 네일 디자인과 친절한 서비스로 고객 만족을 드립니다.'
        WHEN 6 THEN '피부 타입별 맞춤 관리. 수분관리, 탄력관리, 미백관리'
        WHEN 7 THEN '자연스러움을 추구하는 속눈썹 전문샵. 래쉬 리프팅도 가능합니다.'
        WHEN 8 THEN '깔끔한 시술과 합리적인 가격. 단골 고객 할인 이벤트 진행중!'
        ELSE '프리미엄 뷰티 서비스를 합리적인 가격에 제공합니다.'
    END),
    'https://images.unsplash.com/photo-' || (CASE (n % 6)
        WHEN 0 THEN '1604654894610-df63bc536371'
        WHEN 1 THEN '1522337360788-8b13dee7a37e'
        WHEN 2 THEN '1570172619644-dfd03ed5d881'
        WHEN 3 THEN '1519014816548-bf5fe059798b'
        WHEN 4 THEN '1560750588-73207b1ef5b8'
        ELSE '1583001931096-959e9a1a6223'
    END) || '?w=800',
    3.5 + (random() * 1.5),  -- rating between 3.5 and 5.0
    (random() * 200)::int + 10,  -- review count 10-210
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(21, 170) AS n
ON CONFLICT (id) DO NOTHING;

-- Shop category mappings for new shops
INSERT INTO shop_category_mappings (shop_id, category_id, created_at)
SELECT
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    (CASE (n % 6)
        WHEN 0 THEN '11111111-1111-1111-1111-111111111111'  -- 네일
        WHEN 1 THEN '22222222-2222-2222-2222-222222222222'  -- 속눈썹
        WHEN 2 THEN '44444444-4444-4444-4444-444444444444'  -- 피부관리
        WHEN 3 THEN '33333333-3333-3333-3333-333333333333'  -- 왁싱
        WHEN 4 THEN '55555555-5555-5555-5555-555555555555'  -- 태닝
        ELSE '66666666-6666-6666-6666-666666666666'  -- 발관리
    END)::uuid,
    NOW()
FROM generate_series(21, 170) AS n
ON CONFLICT DO NOTHING;

-- Treatments for each new shop (3-5 per shop)
INSERT INTO treatments (id, shop_id, name, price, duration, description, created_at, updated_at)
SELECT
    ('cccc' || LPAD((n * 10 + t)::text, 4, '0') || '-' || LPAD((n * 10 + t)::text, 4, '0') || '-' || LPAD((n * 10 + t)::text, 4, '0') || '-' || LPAD((n * 10 + t)::text, 4, '0') || '-' || LPAD((n * 10 + t)::text, 12, '0'))::uuid,
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    (CASE t
        WHEN 1 THEN '기본 시술'
        WHEN 2 THEN '프리미엄 시술'
        WHEN 3 THEN '스페셜 케어'
        ELSE '정기 관리'
    END),
    (CASE t
        WHEN 1 THEN 30000 + (n % 20) * 1000
        WHEN 2 THEN 50000 + (n % 30) * 1000
        WHEN 3 THEN 80000 + (n % 40) * 1000
        ELSE 100000 + (n % 50) * 1000
    END),
    (CASE t
        WHEN 1 THEN 30
        WHEN 2 THEN 60
        WHEN 3 THEN 90
        ELSE 120
    END),
    '고객님께 최상의 서비스를 제공합니다.',
    NOW(),
    NOW()
FROM generate_series(21, 170) AS n
CROSS JOIN generate_series(1, 4) AS t
ON CONFLICT (id) DO NOTHING;

-- More members for reviews
INSERT INTO members (id, social_provider, social_id, nickname, display_name, created_at, updated_at)
SELECT
    ('dddd' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    'KAKAO',
    'kakao_test_' || LPAD(n::text, 3, '0'),
    'user_' || n,
    (CASE (n % 10)
        WHEN 0 THEN '뷰티러버'
        WHEN 1 THEN '네일매니아'
        WHEN 2 THEN '피부덕후'
        WHEN 3 THEN '래쉬퀸'
        WHEN 4 THEN '뷰티헌터'
        WHEN 5 THEN '강남뷰티'
        WHEN 6 THEN '홍대걸'
        WHEN 7 THEN '직장인뷰티'
        WHEN 8 THEN '대학생뷰티'
        ELSE '뷰티블로거'
    END) || n::text,
    NOW() - (random() * INTERVAL '365 days'),
    NOW()
FROM generate_series(11, 100) AS n
ON CONFLICT DO NOTHING;

-- Generate many reviews (5-10 per shop = 750-1500 reviews)
INSERT INTO shop_reviews (id, shop_id, member_id, rating, content, images, created_at, updated_at)
SELECT
    ('eeee' || LPAD((n * 100 + r)::text, 4, '0') || '-' || LPAD((n * 100 + r)::text, 4, '0') || '-' || LPAD((n * 100 + r)::text, 4, '0') || '-' || LPAD((n * 100 + r)::text, 4, '0') || '-' || LPAD((n * 100 + r)::text, 12, '0'))::uuid,
    ('bbbb' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 4, '0') || '-' || LPAD(n::text, 12, '0'))::uuid,
    ('dddd' || LPAD(((n + r) % 90 + 11)::text, 4, '0') || '-' || LPAD(((n + r) % 90 + 11)::text, 4, '0') || '-' || LPAD(((n + r) % 90 + 11)::text, 4, '0') || '-' || LPAD(((n + r) % 90 + 11)::text, 4, '0') || '-' || LPAD(((n + r) % 90 + 11)::text, 12, '0'))::uuid,
    3 + (random() * 2)::int,  -- rating 3-5
    (CASE (r % 15)
        WHEN 0 THEN '정말 만족스러운 서비스였어요! 다음에 또 방문할게요.'
        WHEN 1 THEN '친절하고 꼼꼼하게 해주셔서 좋았습니다.'
        WHEN 2 THEN '시술 결과가 너무 예뻐요. 강추합니다!'
        WHEN 3 THEN '가격 대비 만족도가 높아요. 재방문 의사 있습니다.'
        WHEN 4 THEN '분위기도 좋고 직원분들도 친절해요.'
        WHEN 5 THEN '예약하고 갔더니 대기 없이 바로 시술 받았어요.'
        WHEN 6 THEN '디자인 추천을 잘 해주셔서 마음에 들었어요.'
        WHEN 7 THEN '청결하고 위생적인 환경이라 안심이 됩니다.'
        WHEN 8 THEN '시술 시간도 적당하고 결과도 만족스러워요.'
        WHEN 9 THEN '주변에 추천하고 싶은 곳이에요!'
        WHEN 10 THEN '전문적인 상담이 인상적이었습니다.'
        WHEN 11 THEN '오래 유지되어서 좋아요. 한 달 넘게 깨끗해요.'
        WHEN 12 THEN '첫 방문이었는데 만족스러웠습니다.'
        WHEN 13 THEN '인테리어도 예쁘고 분위기가 좋아요.'
        ELSE '전체적으로 만족합니다. 또 올게요!'
    END),
    NULL,
    NOW() - (random() * INTERVAL '180 days'),
    NOW()
FROM generate_series(21, 170) AS n
CROSS JOIN generate_series(1, 8) AS r
ON CONFLICT (id) DO NOTHING;

-- Update review counts to match actual reviews
UPDATE beautishops b
SET review_count = (
    SELECT COUNT(*) FROM shop_reviews r WHERE r.shop_id = b.id
),
average_rating = (
    SELECT COALESCE(AVG(r.rating), 0) FROM shop_reviews r WHERE r.shop_id = b.id
);

-- Summary
SELECT
    'Bulk data inserted!' AS status,
    (SELECT COUNT(*) FROM beautishops) AS total_shops,
    (SELECT COUNT(*) FROM treatments) AS total_treatments,
    (SELECT COUNT(*) FROM members) AS total_members,
    (SELECT COUNT(*) FROM shop_reviews) AS total_reviews;
