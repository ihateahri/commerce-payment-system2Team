## 상품 도메인 인덱스 성능 테스트

### 1. SQL 레벨 — EXPLAIN 결과 비교

| 구분 | 조건 | type | key | rows | filtered | 실행시간 |
|---|---|---|---|---|---|---|
| 인덱스 전 | 좁은 조건 (10~15만원) | ALL | null | 59,476 | 0.93 | 348ms |
| 인덱스 전 | 넓은 조건 (0~99만원) | ALL | null | 59,476 | 0.93 | 350ms |
| 인덱스 후 | 좁은 조건 (10~15만원) | range | idx_status_category_price | 199 | 100 | 352ms |
| 인덱스 후 | 넓은 조건 (0~99만원) | range | idx_status_category_price | 15,190 | 100 | 351ms |

### 2. API 레벨 — 포스트맨 응답시간 비교

| 구분 | 조건                  | totalElements | 응답시간 |
|---|-----------------------|---|----------|
| 인덱스 없음 | 좁은 조건 (10~15만원) | 207 | 38ms     |
| 인덱스 없음 | 넓은 조건 (0~99만원)  | 207 | 39ms     |
| 인덱스 있음 | 좁은 조건 (10~15만원) | 207 | 10ms     |
| 인덱스 있음 | 넓은 조건 (0~99만원) | 207 | 25ms     |/

### 3. 사용한 인덱스 (DDL)

CREATE INDEX idx_status_category_price ON product (status, category, price);

CREATE INDEX idx_status_price ON product (status, price);

CREATE INDEX idx_status_created_at ON product (status, created_at);


### 4. 핵심 결론

| 관찰          | 설명                           |
|---------------|--------------------------------|
| rows          | 59,476 → 199 (약 298배 감소)   
| SQL 레벨 시간 | 348ms vs 352ms(거의 차이 없음) 
| API 레벨 시간 | 38ms vs 10ms(약 3.8배 감소)    |

**설계 근거:**

인덱스를 많이 만들수록 조회 성능은 좋아지지만, INSERT/UPDATE 시
유지보수해야 할 인덱스가 늘어나 역효과가 날 수 있다는 점을 고려해,
최소한의 인덱스 구성으로 설계하는 것을 목표로 삼았다.

- `status`는 거의 모든 조회 api에서 공통으로 걸리는 조건이라 앞에 배치를 하여 여러 쿼리에서 재사용 할수 있다 판단하여 두었습니다

- `category`와 `price`의 순서는, 데이터가 어느 컬럼 기준으로 더
  잘게 나뉘는지를 고민해서 정했습니다.
