# FoodFlow — Food Delivery Platform with DevOps CI/CD Pipeline

Production-style Food Delivery backend project built to demonstrate Full-Stack + DevOps skills
(Spring Boot, Docker, Kubernetes, Jenkins, Terraform, Ansible, Monitoring).

**Repo:** github.com/engrshahzaman09/foodflow-devops

---

## ✅ Tech Stack

- Java 21, Spring Boot 4.1.0
- Spring Security + JWT (stateless auth, role-based authorization)
- Spring Data JPA / Hibernate, MySQL 8
- Maven
- Docker, Docker Compose
- Jenkins (CI/CD pipeline)
- Kubernetes (Deployment/Service/PVC/Secret manifests)
- Helm (chart wrapping the K8s manifests)
- Terraform (AWS EC2 provisioning example)
- Ansible (server config/deploy automation)
- Prometheus + Grafana (metrics/monitoring)

---

## ✅ Backend — All Modules Complete

### 1. User Module
- `POST /api/users/register` — register (BCrypt password hashing)
- `POST /api/users/login` — login, returns JWT

### 2. Restaurant Module
- `POST /api/restaurants` — create (ADMIN only)
- `GET /api/restaurants` / `GET /api/restaurants/{id}` — public
- `PUT /api/restaurants/{id}` / `DELETE /api/restaurants/{id}` — ADMIN only

### 3. Menu Module
- `POST /api/menu` — add food item (ADMIN only)
- `GET /api/menu/restaurant/{restaurantId}` — public
- `GET /api/menu/search?keyword=...` — public
- `PUT /api/menu/{id}` / `DELETE /api/menu/{id}` — ADMIN only

### 4. Order Module
- `POST /api/orders` — place order (validates menu items, computes total)
- `GET /api/orders/{id}` / `GET /api/orders/user/{userId}`
- `PATCH /api/orders/{id}/cancel`
- `PATCH /api/orders/{id}/status?status=CONFIRMED|DELIVERED|...`

### 5. Payment Module
- `POST /api/payments` — dummy payment gateway (always succeeds, generates mock transaction ID, marks order CONFIRMED)
- `GET /api/payments/order/{orderId}`

### 6. Notification Module
- In-app notifications stored in DB, triggered automatically on order placed/cancelled/confirmed/delivered
- `GET /api/notifications/user/{userId}`
- `PATCH /api/notifications/{id}/read`
- **Note:** does not send real emails yet — see "Known limitations" below.

### 7. Role-Based Authorization
- `@EnableMethodSecurity` + `@PreAuthorize("hasRole('ADMIN')")` on Restaurant/Menu write endpoints
- JWT carries the user's role; `User.getAuthorities()` maps it to `ROLE_<role>`
- Register with `"role": "ADMIN"` or `"role": "USER"` to test both

### Global Exception Handling
Clean JSON errors for: 409 Conflict, 404 Not Found, 401 Unauthorized, 403 Forbidden, 400 Validation/Bad Request, 500 fallback.

---

## ✅ DevOps — All Pieces In Place

| Piece | File(s) | Status |
|---|---|---|
| Docker | `Dockerfile`, `docker-compose.yml` | Multi-stage build; compose runs app + MySQL + Prometheus + Grafana |
| CI/CD | `Jenkinsfile` | Build → Test → Package → Docker Build/Push → Deploy to K8s |
| Kubernetes | `kubernetes/*.yaml` | Namespace, Secret template, MySQL (Deployment+PVC+Service), App (Deployment+Service) |
| Helm | `helm/foodflow/` | Chart wrapping the K8s app manifests, configurable via `values.yaml` |
| Terraform | `terraform/main.tf` | Provisions an AWS EC2 instance + security group |
| Ansible | `ansible/deploy.yml`, `ansible/inventory.ini` | Installs Docker on a server and runs the app container |
| Monitoring | `monitoring/prometheus.yml`, Actuator + Micrometer in `pom.xml` | Prometheus scrapes `/actuator/prometheus`; Grafana included in docker-compose |

**To run locally with Docker:**
```bash
docker-compose up --build
```
This starts: app (`:8087`), MySQL (`:3306`), Prometheus (`:9090`), Grafana (`:3000`, login `admin`/`admin`).

**To run on Kubernetes** (once you have a cluster and have built/pushed the image):
```bash
kubectl apply -f kubernetes/
```

**Terraform → Ansible → K8s flow** (for a real cloud deployment):
1. `cd terraform && terraform init && terraform apply` → provisions the EC2 server, outputs its IP
2. Add that IP to `ansible/inventory.ini`
3. `ansible-playbook -i ansible/inventory.ini ansible/deploy.yml` → installs Docker, runs the app
4. Or, if using a managed K8s cluster (EKS/GKE) instead of a bare EC2 box: `kubectl apply -f kubernetes/` or `helm install foodflow ./helm/foodflow`

---

## Known limitations / things to fix before real production use

- `application.properties` has DB password + JWT secret in plain text — for real deployment,
  move these to environment variables / `AWS Secrets Manager`, and use the Kubernetes Secret
  (`kubernetes/01-secret.yaml`) instead of hardcoded values.
- Notifications are in-app only (stored in DB) — no real email/SMS yet. To add real email,
  wire `JavaMailSender` into `NotificationServiceImpl` with SMTP credentials.
- No refresh token — JWT expires in 24h (`jwt.expiration`), user must re-login after that.
- Terraform AMI ID is hardcoded for `eu-west-1` — verify/update for your target region.
- Jenkinsfile assumes Jenkins credentials `dockerhub-credentials` and tools named `Maven3`/`JDK21`
  are already configured on the Jenkins server — update names to match your actual Jenkins setup.
- Kubernetes/Helm assume the Docker image has already been pushed to Docker Hub as
  `engrshahzaman09/foodflow` — update the image name if using a different registry.
