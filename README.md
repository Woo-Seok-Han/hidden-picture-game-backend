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

관리자에서 업로드한 이미지는 로컬 개발에서는 `uploads/` 폴더에 저장되고 `/uploads/{filename}` 경로로 서빙됩니다.
배포 환경에서는 프론트엔드가 `localhost`를 참조하지 않도록 백엔드 공개 주소를 환경 변수로 지정해주세요.

```bash
APP_PUBLIC_BASE_URL=https://your-backend.example.com ./gradlew bootRun
```

설정하지 않으면 API 응답에는 `/uploads/...` 같은 상대 경로가 내려갑니다.

## Render + Cloudflare R2 배포

Render에서는 Dockerfile 기반 Web Service로 배포합니다.
H2 파일 DB는 Render 인스턴스의 임시 파일 시스템을 사용하고, 관리자 업로드 이미지는 Cloudflare R2에 저장합니다.

Render 환경 변수:

```text
PORT=3000
SPRING_DATASOURCE_URL=jdbc:h2:file:./data/infection-control-detective
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=원하는비밀번호
APP_PUBLIC_BASE_URL=https://your-backend.onrender.com
APP_CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app,http://localhost:5173

APP_STORAGE_TYPE=r2
R2_ENDPOINT=https://ACCOUNT_ID.r2.cloudflarestorage.com
R2_BUCKET=hidden-picture-game
R2_REGION=auto
R2_ACCESS_KEY_ID=...
R2_SECRET_ACCESS_KEY=...
R2_PUBLIC_BASE_URL=https://your-public-r2-domain
```

Vercel 프론트엔드 환경 변수:

```text
VITE_API_URL=https://your-backend.onrender.com/api
VITE_API_ASSET_URL=https://your-public-r2-domain
```

로컬 개발에서는 `APP_STORAGE_TYPE`을 설정하지 않으면 기존처럼 `uploads/` 폴더에 저장됩니다.

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
- `GET /api/game/questions` - 활성 문제 중 랜덤 5문제 반환
- `POST /api/game/submit`
- `POST /api/game/complete`
- `GET /api/admin/questions`
- `POST /api/admin/questions`
- `PUT /api/admin/questions/{id}`
- `PATCH /api/admin/questions/{id}/active?active=true`
- `DELETE /api/admin/questions/{id}`
- `GET /api/admin/results`

게임 시작 시 활성 문제가 5개 미만이면 시작할 수 없습니다. 관리자는 문제를 계속 업로드할 수 있고, 문제 관리 화면에서 출제 여부를 활성/비활성으로 바꿀 수 있습니다.

## 관리자 문제 이미지 업로드 예시

```bash
curl -X POST http://localhost:3000/api/admin/questions \
  -F "image=@/path/to/question.png" \
  -F "imageAlt=감염관리 문제 이미지" \
  -F "explanation=손 위생 물품이 올바르지 않게 보관되어 있습니다." \
  -F "timeLimitSeconds=30" \
  -F 'errorAreas=[{"x":0.2,"y":0.3,"width":0.1,"height":0.1}]'
```
