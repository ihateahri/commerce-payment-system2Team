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