# 감염관리탐정단 Backend

Spring Boot, Gradle, H2 파일 DB 기반 백엔드입니다.

## 실행

```bash
./gradlew bootRun
```

서버 기본 주소:

```text
http://localhost:3000
```

프론트엔드 기본 API 주소와 맞추기 위해 API prefix는 `/api`를 사용합니다.

## 이미지 URL

관리자에서 업로드한 이미지는 기본적으로 로컬 `uploads/` 디렉터리에 저장되고 `/uploads/{filename}` 경로로 서빙됩니다.
배포 환경에서는 프론트엔드가 `localhost`를 참조하지 않도록 백엔드 공개 주소를 환경 변수로 지정해주세요.

```bash
APP_PUBLIC_BASE_URL=https://your-backend.example.com ./gradlew bootRun
```

설정하지 않으면 API 응답에는 `/uploads/...`, `/sample/...` 같은 상대 경로가 내려갑니다.

## H2 Console

```text
http://localhost:3000/h2-console
```

- JDBC URL: `jdbc:h2:file:./data/infection-control-detective`
- User Name: `sa`
- Password: 비워둠

## 주요 API

- `POST /api/user/validate`
- `GET /api/user/{employeeNumber}/results`
- `POST /api/game/start`
- `GET /api/game/questions`
- `POST /api/game/submit`
- `POST /api/game/complete`
- `GET /api/admin/questions`
- `POST /api/admin/questions`
- `PUT /api/admin/questions/{id}`
- `DELETE /api/admin/questions/{id}`
- `GET /api/admin/results`

## 관리자 문제 이미지 업로드 예시

```bash
curl -X POST http://localhost:3000/api/admin/questions \
  -F "image=@/path/to/question.png" \
  -F "imageAlt=감염관리 문제 이미지" \
  -F "explanation=손 위생 물품이 올바르지 않게 보관되어 있습니다." \
  -F "timeLimitSeconds=30" \
  -F 'errorAreas=[{"x":0.2,"y":0.3,"width":0.1,"height":0.1}]'
```
