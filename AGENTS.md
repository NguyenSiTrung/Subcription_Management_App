# AGENTS.md — Quy tắc & Hướng dẫn dành cho Codex CLI

> **Mục tiêu**: Giúp Codex CLI (và các agent tương tự) hiểu đúng kiến trúc, quy ước, lệnh build/test/lint và cách triển khai thay đổi **an toàn** trong repo này. File này đóng vai trò như *README dành cho agent*.

Repo: Ứng dụng **Quản Lý Đăng Ký** (subscriptions) xây bằng **Kotlin + Jetpack Compose (Material 3)**, kiến trúc **MVVM + Clean Architecture (UI → Domain → Data)**, DI bằng **Hilt**, lưu trữ **Room** (DB **version = 2**, đã có migration 1→2 để `categoryId` nullable). Dòng dữ liệu dùng **Coroutines + Flow**.

---

## 0) Nguyên tắc vàng khi tác động mã nguồn

1. **Tôn trọng phân tầng Clean Architecture**
   UI (Compose + ViewModel) ↔ **Use Case** ↔ **Repository** ↔ **DAO/Manager**.

   * Logic nghiệp vụ đặt ở **Use Case**.
   * **ViewModel** chỉ điều phối state (expose `StateFlow<UiState>` và `SharedFlow<Event>`).
   * **Repository** ánh xạ Domain ↔ Data (Room/Manager).
2. **DI bằng Hilt**

   * Sử dụng `@HiltViewModel` cho ViewModel, `@Inject` constructor.
   * Cấu hình Modules trong `di/` (DatabaseModule/RepositoryModule/ManagerModule).
3. **Room & Migration**

   * **Không** đổi schema nếu **chưa có Migration** (v2→v3, …) **+ test migration**.
   * DAO trả về `Flow<T>` cho stream, hoặc `suspend` cho thao tác đơn.
4. **Compose-first UI**

   * Composable **không chứa** logic domain; nhận `UiState` + `onEvent`.
   * Chuẩn hoá trạng thái: `Loading | Success | Empty | Error`.
5. **Luồng & Hiệu năng**

   * I/O ở `Dispatchers.IO`; sử dụng `viewModelScope`.
   * Debounce tìm kiếm 250–300ms.
6. **Quyền hạn & An toàn**

   * Android 13+: xin `POST_NOTIFICATIONS`; cân nhắc Exact Alarm.
   * SQLCipher chỉ bật khi có key quản lý qua Android Keystore (SecurityManager).
7. **Thay đổi tối thiểu, có thể hoàn nguyên**; mọi thay đổi đều cần lint + test pass.

---

## 1) Cấu trúc thư mục chuẩn (tham chiếu/điều chỉnh theo repo thực)

```
app/
  src/main/java/com/example/subscriptions/
    ui/                # Compose screens + components
    viewmodel/         # @HiltViewModel cho từng màn
    domain/
      model/           # Domain models (không phụ thuộc Android)
      usecase/         # Nhóm theo context: subscription, category, reminder, payment, backup, calendar, security
      repository/      # Interfaces (Domain-facing)
    data/
      db/              # Room: AppDatabase, Entities, Dao, Converters, Migrations
      repo/            # Repository implementations
      manager/         # NotificationScheduler, ReminderManager, BackupManager, GoogleCalendarManager, SecurityManager
    di/                # Hilt Modules (Database/Repository/Manager)
    util/              # Date/time, formatters, Result wrappers, etc.
  src/androidTest/     # UI/Instrumented tests (Compose, Navigation)
  src/test/            # Unit tests (JUnit, Turbine, Mockito)
```

> Agent: **Giữ nguyên cấu trúc hiện có** nếu repo đã khác đôi chút; mở rộng theo logic trên.

---

## 2) Kiến trúc & thành phần chính

* **UI (Compose + Navigation):** `NavHost` trong `MainActivity`; routes: `home`, `subscription_list`, `subscription_detail/{id:Long}`, `add_edit_subscription/{id:Long=-1}`, `category_list`, `statistics`, `settings` (gồm Security/Backup/Calendar).
* **ViewModel:** Compose-first; expose `StateFlow<UiState>` & `SharedFlow<Event>`; nhận Use Case qua DI.
* **Domain (Use Case):** forward sang Repository/Manager + gom logic (thống kê, backup).
* **Data:** Room v2 (migration 1→2 đã cho `categoryId` nullable); DAO có index: `category_id`, `next_billing_date`, `is_active`.
* **Managers:** `NotificationScheduler` (AlarmManager+Notification), `ReminderManager`, `BackupManager` (JSON), `GoogleCalendarManager`, `SecurityManager` (Android Keystore + Crypto).

---

## 3) Lệnh thiết lập, build, chạy test & lint

> Agent: chạy các lệnh này trước khi tạo PR hoặc sau khi chỉnh sửa tự động.

* **Build:**

  * `./gradlew clean build`
  * `./gradlew :app:assembleDebug`
* **Cài app:** `./gradlew :app:installDebug`
* **Unit tests (JVM):** `./gradlew testDebugUnitTest`
* **Instrumented/UI tests:** (cần emulator/thiết bị) `./gradlew :app:connectedDebugAndroidTest`
* **Lint:** `./gradlew lint`
* **Detekt/Ktlint (nếu có):** `./gradlew detekt` / `./gradlew ktlintCheck`

Khi đụng **DB schema**: sau khi bổ sung Migration + test migration, chạy chuỗi:
`./gradlew clean testDebugUnitTest :app:connectedDebugAndroidTest lint`

---

## 4) Chuẩn code & quy ước

**Kotlin**

* Theo Kotlin style; `val` mặc định; `data class` cho model immutable.
* Sử dụng `Result<T>` hoặc sealed class cho luồng lỗi domain.

**Compose**

* Tách state hoisting: `@Composable fun Screen(state: UiState, onEvent: ...)`.
* Không truy cập repo/dao trực tiếp trong composable.
* Tài nguyên text/màu qua resource, **không** hardcode.

**ViewModel**

* Chỉ expose luồng `StateFlow/SharedFlow`; không dùng LiveData.
* Sử dụng `stateIn()/shareIn()` khi cần chia sẻ luồng cho UI.

**Repository/Use Case**

* Tên chuẩn: `GetXxxUseCase`, `UpdateXxxUseCase`, `SearchXxxUseCase`... mỗi use case **một trách nhiệm**.
* Repository mapping Domain ↔ Data; DAO chỉ lo CRUD/Query.

**Room**

* Entities, Dao để trong `data/db`; Converters cho Enum.
* Index quan trọng: `category_id`, `next_billing_date`, `is_active`.

**Logging/Errors**

* Không throw exception lên UI; convert thành Result/UiState.

---

## 5) Mô hình dữ liệu (Room) — tóm tắt hiện tại

| Entity           | Ghi chú                                                                                                                            |
| ---------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| `Subscription`   | `categoryId` **nullable**; metadata: `websiteUrl`, `appPackage`, `notes`; indexes `category_id`, `next_billing_date`, `is_active`. |
| `Category`       | Cờ `isPredefined`, `keywords` (chuỗi) để gợi ý tự động.                                                                            |
| `Reminder`       | `reminderType`, `reminderDate`, `notificationId`, `isNotified`.                                                                    |
| `PaymentHistory` | Lịch sử thanh toán; lọc theo khoảng thời gian; tính tổng.                                                                          |

**Migration 1→2:** tái tạo `subscriptions` để `category_id` cho phép `NULL`; chuyển `0 → NULL`; tái tạo index.

> Agent: Nếu cần đổi schema → **tăng version** (2→3) + viết Migration + test (mục 8).

---

## 6) Use Cases (nhóm theo domain)

* **SubscriptionUseCases**: `Add`, `Get`, `GetAll`, `GetActive`, `Update`, `Delete`, `Search`, `FilterByCategory`, `FilterByBillingRange`.
* **CategoryUseCases**: `Create`, `Update`, `Delete`, `GetAll`, `GetPredefined`, `FindByKeyword`.
* **ReminderUseCases**: `Add`, `Update`, `Delete`, `GetPending`, `Schedule`, `Cancel` (qua `ReminderManager` + `NotificationScheduler`).
* **PaymentUseCases**: `HistoriesBySubscription`, `HistoriesByDateRange`, `TotalSpend`.
* **StatisticsUseCases**: `MonthlyTotal`, `YearlyTotal`, `ByCategory`, `SpendingTrend` (cơ bản).
* **CalendarUseCases**: `Add/Update/Remove` event theo billing cycle; kiểm tra đăng nhập Google.
* **BackupUseCases**: `CreateBackupJson`, `RestoreFromJson`, `ShareBackup`.
* **SecurityUseCases**: `Encrypt`, `Decrypt`, `CheckBiometric`, `Authenticate`.

---

## 7) Bản đồ tác vụ ưu tiên (điểm vào & yêu cầu kiểm thử)

> Agent: Khi thực hiện các hạng mục dưới, **cập nhật test** tương ứng.

### A) Subscription List — Tìm kiếm/Lọc nâng cao

* **Điểm vào:** `SubscriptionListScreen`, `SubscriptionViewModel`, `SubscriptionUseCases.search/filter`.
* **Việc cần làm:** thêm `UiState{ query, selectedCategoryIds, status }`; debounce; query Room đa điều kiện.
* **Test:** use case (nhiều ràng buộc), UI test (gõ search, chọn filter).

### B) Category — Thêm/Sửa trực tiếp từ UI

* **Điểm vào:** `CategoryListScreen`, `CategoryViewModel`, `CategoryUseCases` + `CategoryRepository`.
* **Việc cần làm:** màn Add/Edit (sheet/screen); validate trùng tên; hỗ trợ `isPredefined`, `keywords`.
* **Test:** unit (CRUD), UI test tạo/sửa/hiển thị keywords.

### C) Reminder & Notification (Android 13+)

* **Điểm vào:** `ReminderManager`, `NotificationScheduler`, `ReminderDao`.
* **Việc cần làm:** xin `POST_NOTIFICATIONS`; xem xét Exact Alarm; đồng bộ schedule/cancel khi CRUD Subscription/Reminder.
* **Test:** unit logic schedule/cancel; (tuỳ chọn) instrumented với AlarmManager mock.

### D) Backup/Restore — UI Settings

* **Điểm vào:** `BackupManager`, `BackupViewModel` + màn Settings/Backup.
* **Việc cần làm:** nút **Tạo backup (JSON)**, **Khôi phục**, **Chia sẻ**; thứ tự restore: Category → Subscription → Reminder → PaymentHistory.
* **Test:** serialize/deserialize; khôi phục giữ quan hệ.

### E) Google Calendar Integration

* **Điểm vào:** `GoogleCalendarManager`, `CalendarUseCases`, Settings/Calendar UI.
* **Việc cần làm:** đăng nhập Google; chọn calendar; sync recurrence theo billing; xử lý credential hết hạn.
* **Test:** mock API/tách interface để test offline.

### F) Bảo mật — Biometric & SQLCipher (tùy chọn)

* **Điểm vào:** `SecurityManager`, `SecurityUseCases`, init `AppDatabase`.
* **Việc cần làm:** tuỳ chọn “Khoá ứng dụng” (biometric trước khi mở); bật SQLCipher qua `SupportFactory` với key từ Keystore.
* **Test:** unit encrypt/decrypt; khởi tạo DB có key và truy vấn mẫu.

### G) Thống kê nâng cao (kết nối MPAndroidChart)

* **Điểm vào:** `StatisticsViewModel`, `PaymentUseCases`.
* **Việc cần làm:** map dữ liệu thật cho Bar/Pie/Line; filter theo thời gian/danh mục.
* **Test:** aggregator; snapshot UI (nếu có).

---

## 8) Quy tắc thay đổi CSDL & Mẫu Migration

1. **Tăng version DB** (2→3…) khi thay cột/bảng/chỉ mục/nullable/kiểu.
2. **Viết Migration** tương ứng; có thể dùng bảng tạm để chuyển đổi; bảo toàn dữ liệu.
3. **Cập nhật Converters/DAO** nếu thêm Enum/kiểu mới.
4. **Test migration**: tạo DB cũ → migrate → assert schema & dữ liệu.

**Mẫu Migration (Kotlin — phác thảo):**

```kotlin
val MIGRATION_2_3 = object : Migration(2, 3) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.beginTransaction()
    try {
      // Ví dụ: thêm cột mới với giá trị mặc định
      db.execSQL("ALTER TABLE subscriptions ADD COLUMN trial_end INTEGER DEFAULT NULL")

      // Ví dụ phức tạp: tạo bảng tạm + copy dữ liệu
      // db.execSQL("CREATE TABLE subscriptions_new (...)")
      // db.execSQL("INSERT INTO subscriptions_new(...) SELECT ... FROM subscriptions")
      // db.execSQL("DROP TABLE subscriptions")
      // db.execSQL("ALTER TABLE subscriptions_new RENAME TO subscriptions")

      // Tái tạo index nếu cần
      // db.execSQL("CREATE INDEX IF NOT EXISTS index_subscriptions_next_billing_date ON subscriptions(next_billing_date)")

      db.setTransactionSuccessful()
    } finally {
      db.endTransaction()
    }
  }
}
```

> Agent: **Không merge** thay đổi schema **nếu thiếu Migration + Test**.

---

## 9) Điều hướng & UI

* **NavHost** trong `MainActivity`; route start = `home`.
* **AppTopBar**: tiêu đề + subtitle theo route; actions (search/add) — *chưa nối logic search*.
* **AppBottomBar**: Home, Subscriptions, Categories, Statistics, Settings.
* **Màn hình đã có**:

  1. **Home**: Monthly summary, Upcoming renewals, “View All”.
  2. **Subscription List**: hiển thị tình trạng (Overdue/Due Soon/Inactive) theo màu.
  3. **Subscription Detail**: hiển thị chi tiết + action (nhắc nhở, lịch sử).
  4. **Add/Edit Subscription**: form đầy đủ (price, cycle, category, reminder, notes…).
  5. **Category List**: liệt kê predefined/custom + `keywords`.
  6. **Statistics**: tổng chi tiêu, lịch sử thanh toán tháng hiện tại.
  7. **Settings/Security/Backup**: ViewModel đã có; cần hoàn thiện UI.

---

## 10) Managers & quyền hệ thống

* **NotificationScheduler**: tạo channel, đặt `AlarmManager`, bắn Notification (qua `ReminderBroadcastReceiver`).
* **ReminderManager**: đồng bộ Reminder ↔ NotificationScheduler (schedule/cancel).
* **BackupManager**: xuất/nhập JSON; trả `Uri`; khôi phục theo thứ tự phụ thuộc.
* **GoogleCalendarManager**: sử dụng Google Account Credential; CRUD event/recurrence theo billing.
* **SecurityManager**: bọc Android Keystore + Crypto; cung cấp API `encrypt/decrypt`; hỗ trợ Biometric.

**Quyền liên quan (tham khảo khi đụng code):**

* `POST_NOTIFICATIONS` (Android 13+).
* Exact Alarm (S, T+ — cân nhắc use case).
* SAF / Storage access cho backup/restore.
* Google Sign-In / Calendar scope (khi tích hợp Calendar).

---

## 11) Testing & Chất lượng

* **Unit test**: ViewModel (Turbine + Mockito) — cập nhật theo chữ ký hiện hành (StateFlow/SharedFlow + nhiều use case).
* **UI test**: Compose/Navi skeleton đã có — cần hoàn thiện.
* **Khuyến nghị**:

  * Bổ sung test `ReminderManager`/`BackupManager` (nhiều nhánh).
  * Screenshot test/compose test cho màn chính.
* **Chuỗi kiểm tra trước PR**: `./gradlew testDebugUnitTest :app:connectedDebugAndroidTest lint`.

---

## 12) Quy ước commit/PR

* Mỗi PR giải quyết **một hạng mục** ở mục 7 (A–G).
* Mô tả PR **phải** nêu rõ thay đổi ở UI/UseCase/Repository/DAO/Migration + hướng dẫn QA.
* Xoá code chết, `TODO` đã xong, cập nhật tài liệu liên quan.
* (Khuyến nghị) Dùng Conventional Commits: `feat:`, `fix:`, `refactor:`, `test:`, `chore:`, `docs:`…

---

## 13) Bảo mật & phụ thuộc

* **Không** commit secrets/keys.
* Khi bật **SQLCipher**: dùng `SupportFactory` với key quản lý qua Keystore (SecurityManager).
* Kiểm tra license & CVE khi thêm dependency; chạy lint/scan nếu đã tích hợp.

---

## 14) Hướng dẫn dành riêng cho Codex (cách hành động trong repo này)

1. Đọc **AGENTS.md** (file này) trước khi patch.
2. Xác định hạng mục trong **mục 7 (A–G)** → tạo chi nhánh mới theo tên mô tả (`feature/search-filter-subscriptions`, …).
3. Tuân thủ phân tầng (mục 0, 2, 4).
4. Nếu chạm **DB** → mục 8 (Migration + test).
5. Viết/điều chỉnh **tests** (mục 11).
6. Chạy lệnh build/test/lint (mục 3) và ghi kết quả vào mô tả PR.
7. **Không** tự ý thêm quyền Android mà không cập nhật UI xin quyền/flow.
8. Ưu tiên thay đổi tối thiểu, dễ hoàn nguyên; giữ backward compatibility khi có thể.

---

## 15) Ghi chú triển khai/đặc thù còn dang dở

* **Category screen**: chưa hỗ trợ Add/Edit trực tiếp.
* **SubscriptionList**: còn slot cho search/filter nâng cao.
* **UI cho Calendar/Backup/Security**: cần nối flow/luồng xin quyền.
* **Tests**: chưa đồng bộ với ViewModel mới.
* **SQLCipher & DataStore**: mới dừng ở dependency; cần cấu hình runtime nếu kích hoạt.

> Agent: Khi hoàn tất một hạng mục dang dở, cập nhật lại phần này và/hoặc tạo `CHANGELOG.md`.

---

## 16) Tài liệu nhanh (nhắc lại)

* **Kiến trúc**: Compose UI ↔ ViewModel ↔ UseCase ↔ Repository ↔ Room/Managers.
* **DB**: version = 2; migration 1→2 đã cho `categoryId` nullable.
* **Managers**: Reminder/Notification/Backup/GoogleCalendar/Security.
* **Use Cases**: subscription, category, reminder, payment, statistics, calendar, backup, security.
* **Mục tiêu ngắn hạn**: A–G ở mục 7.
