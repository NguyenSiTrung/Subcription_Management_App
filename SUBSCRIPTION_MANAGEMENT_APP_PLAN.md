# KẾ HOẠCH & KIẾN TRÚC ỨNG DỤNG QUẢN LÝ ĐĂNG KÝ

## 1. TÓM TẮT HIỆN TRẠNG
- Ứng dụng được phát triển bằng **Kotlin + Jetpack Compose (Material 3)**, sử dụng mô hình **MVVM + Clean Architecture (UI → Domain → Data)**.
- **Hilt** chịu trách nhiệm cung cấp phụ thuộc (Room DAOs, Repository, Notification/Calendar/Backup managers, ViewModel).
- **Room** là nguồn lưu trữ chính, cung cấp dữ liệu dạng `Flow`. CSDL hiện tại ở **version 2** (đã có migration 1→2 để hỗ trợ `categoryId` nullable).
- Lớp domain gồm các **use case tách theo ngữ cảnh (subscription, category, reminder, payment, backup, notification, calendar)**.
- Lớp trình bày gồm các **ViewModel Compose-first** cho Subscription, Category, Reminder, PaymentHistory, Statistics, Backup, Security, Notification...
- Hệ thống tính năng đã xây dựng:
  - Quản lý đăng ký (danh sách, chi tiết, tạo/sửa/xoá, lọc theo danh mục)
  - Theo dõi đăng ký đang hoạt động và lịch thanh toán sắp tới
  - Quản lý danh mục (danh sách, keyword, trạng thái predefined)
  - Thống kê và lịch sử thanh toán hằng tháng
  - Nhắc nhở (AlarmManager + BroadcastReceiver) và quản lý Reminder
  - Sao lưu/Khôi phục dữ liệu (JSON backup)
  - Khung tích hợp Google Calendar, bảo mật (Biometric/Security Crypto), SQLCipher đã chuẩn bị nhưng cần hoàn thiện cấu hình
- Các điểm còn dang dở/placeholder:
  - Màn hình Category chưa hỗ trợ thêm/sửa trực tiếp từ UI
  - Màn hình SubscriptionList còn chỗ trống cho tìm kiếm/lọc nâng cao
  - Chưa có kết nối UI rõ ràng cho Calendar/Backup/Security flows
  - Bộ test unit/UI còn lệch với ViewModel hiện hành
  - SQLCipher và DataStore mới dừng lại ở mức dependency, chưa cấu hình runtime

## 2. KIẾN TRÚC TỔNG QUAN
### 2.1 Sơ đồ lớp (cập nhật)
```mermaid
graph TD
    subgraph UI (Jetpack Compose)
        HomeScreen --> SubscriptionViewModel
        SubscriptionListScreen --> SubscriptionViewModel
        SubscriptionDetailScreen --> SubscriptionViewModel
        AddEditSubscriptionScreen --> SubscriptionViewModel
        CategoryListScreen --> CategoryViewModel
        StatisticsScreen --> StatisticsViewModel
        SettingsScreen --> SecurityViewModel
        BackupScreen --> BackupViewModel
    end

    subgraph Domain (Use Cases)
        SubscriptionViewModel -->|Add/Get/Update/Delete| UC_Subscription
        CategoryViewModel --> UC_Category
        StatisticsViewModel --> UC_Payment
        BackupViewModel --> UC_Backup
        SecurityViewModel --> UC_Security
        NotificationViewModel --> UC_Reminder
        GoogleCalendarViewModel --> UC_Calendar
    end

    subgraph Data Layer
        UC_Subscription --> SubscriptionRepository
        UC_Category --> CategoryRepository
        UC_Reminder --> ReminderRepository
        UC_Payment --> PaymentHistoryRepository
        SubscriptionRepository --> SubscriptionDao
        CategoryRepository --> CategoryDao
        ReminderRepository --> ReminderDao
        PaymentHistoryRepository --> PaymentHistoryDao
        SubscriptionRepository --> GoogleCalendarManager
        ReminderRepository --> NotificationScheduler
        BackupManager -.-> SubscriptionRepository
        BackupManager -.-> CategoryRepository
        BackupManager -.-> ReminderRepository
        BackupManager -.-> PaymentHistoryRepository
    end

    subgraph Platform/Services
        NotificationScheduler --> AlarmManager
        ReminderBroadcastReceiver --> NotificationScheduler
        GoogleCalendarManager --> GoogleCalendarAPI
        SecurityManager --> AndroidKeystore
    end
```

### 2.2 Phân tầng chi tiết
- **UI layer**
  - Compose Scaffold (AppTopBar + AppBottomBar) quản lý điều hướng qua `NavHost` và bottom navigation.
  - Mỗi màn hình lấy state từ ViewModel thông qua `collectAsStateWithLifecycle`.
  - Trạng thái tải/lỗi được chuẩn hoá bằng `LoadingIndicator`, `ErrorMessage`, `EmptyState`.

- **Domain layer**
  - Các use case được nhóm theo domain (`/domain/usecase/...`).
  - Use case chủ yếu forward sang Repository/Manager tương ứng; một số chứa logic gộp dữ liệu (ví dụ thống kê, backup).

- **Data layer**
  - Room Database (`AppDatabase` version 2) + `Converters` xử lý Enum.
  - Repository thực thi kết hợp DAO + logic phụ (tạo/sửa Reminder khi lưu Subscription, xoá cascade PaymentHistory...).
  - Các manager bổ trợ: `NotificationScheduler`, `ReminderManager`, `BackupManager`, `GoogleCalendarManager`, `SecurityManager`.

- **Hạ tầng**
  - `DatabaseModule` cung cấp Room singleton.
  - `RepositoryModule` bind interface/domain repository với implementation.
  - `WorkManager` và `AlarmManager` dùng cho nhắc nhở; `Hilt` có hỗ trợ `androidx.hilt.work` (chưa cấu hình worker cụ thể).
  - Các dependency khác (MPAndroidChart, Google Calendar API, SQLCipher, Security Crypto) đã khai báo trong Gradle.

## 3. MÔ HÌNH DỮ LIỆU ROOM
| Entity | Ghi chú chính (phiên bản hiện tại) |
|--------|-----------------------------------|
| `Subscription` | Trường `categoryId` nullable, chứa metadata (websiteUrl, appPackage, notes); có chỉ số trên `category_id`, `next_billing_date`, `is_active`. |
| `Category` | Có cờ `isPredefined`, `keywords` dạng chuỗi; dùng để gợi ý tự động. |
| `Reminder` | Lưu `reminderType`, `reminderDate`, `notificationId`, `isNotified`. |
| `PaymentHistory` | Theo dõi thanh toán, hỗ trợ lọc theo khoảng thời gian và tính tổng. |

**Migration 1→2**: tái tạo bảng `subscriptions` để cho phép `category_id` null, sao chép dữ liệu cũ (chuyển `0` thành `NULL`), tái tạo index.

## 4. REPOSITORY & USE CASE
- **SubscriptionUseCases**: Add / Get / GetAll / GetActive / Update / Delete / Search / Filter theo danh mục / Billing range.
- **CategoryUseCases**: CRUD, lấy danh mục predefined, tra cứu theo keyword.
- **ReminderUseCases**: Add/Update/Delete Reminder, GetPending, Schedule/Cancel (thông qua `ReminderManager` + `NotificationScheduler`).
- **PaymentUseCases**: Lấy lịch sử thanh toán (by subscription / by date range), tính tổng chi tiêu.
- **StatisticsUseCases**: Tổng chi tiêu theo tháng/năm, phân bổ theo danh mục, xu hướng chi tiêu (đang ở mức cơ bản).
- **CalendarUseCases**: Add/Update/Remove subscription trên Google Calendar, kiểm tra trạng thái đăng nhập Google (UI cần hoàn thiện luồng sign-in).
- **BackupUseCases**: Tạo/Khôi phục backup JSON, intent chọn/ chia sẻ file.
- **SecurityUseCases**: Mã hoá/Giải mã, kiểm tra & thực thi xác thực sinh trắc học (phụ thuộc `SecurityManager`, Biometric API, Android Keystore).

## 5. LUỒNG NGHIỆP VỤ CHÍNH
1. **Quản lý đăng ký**
   - HomeScreen nạp `activeSubscriptions` để hiển thị Monthly Summary + Upcoming renewals.
   - SubscriptionListScreen dùng `subscriptions` để hiển thị toàn bộ.
   - AddEditSubscriptionScreen: tạo state cục bộ, đồng bộ `selectedCategory`, emit `subscriptionSaved` từ ViewModel rồi quay lại.
   - SubscriptionDetailScreen (kiểm tra file) hiển thị chi tiết, cho phép hành động (xem PaymentHistory, nhắc nhở...).

2. **Nhắc nhở & Thông báo**
   - `ReminderManager` đồng bộ Reminder với NotificationScheduler.
   - `NotificationScheduler` tạo channel, đặt `AlarmManager`, phát Notification khi đến hạn (`ReminderBroadcastReceiver`).
   - ViewModel Reminder hiện tượng (cần xem file chi tiết) cho phép user quản lý.

3. **Thống kê**
   - `StatisticsViewModel` tải lịch sử thanh toán tháng hiện tại, tính tổng/ trung bình, hiển thị qua Compose.
   - Các biểu đồ MPAndroidChart đã có component (BarChart/PieChart/LineChart) nhưng chưa kết nối dữ liệu thực tế (cần mapping dataset).

4. **Sao lưu / Khôi phục**
   - `BackupManager` trích xuất toàn bộ data thành JSON, lưu vào file nội bộ và trả về `Uri`.
   - Khôi phục: xoá dữ liệu hiện có, chèn lại theo thứ tự Category → Subscription → Reminder → PaymentHistory.
   - ViewModel hiển thị trạng thái thành công/thất bại (UI cần integrate màn hình cụ thể trong Settings).

5. **Bảo mật & Sinh trắc học**
   - `SecurityManager` wrap Android Keystore + Crypto để mã hoá/giải mã chuỗi.
   - Use case kiểm tra thiết bị hỗ trợ, thực thi authenticate (UI Settings/Security screen).

6. **Tích hợp Google Calendar**
   - `GoogleCalendarManager` sử dụng `GoogleAccountCredential`, tạo/ cập nhật/ xoá event recurrence theo billing cycle.
   - Hiện chưa có UI hoàn chỉnh để chọn tài khoản và trigger đồng bộ.

## 6. GIAO DIỆN & ĐIỀU HƯỚNG
- **Điều hướng chính**: `NavHost` trong `MainActivity`, route bắt đầu `home`. Các route quan trọng:
  - `home`
  - `subscription_list`
  - `subscription_detail/{subscriptionId}` (NavType.LongType)
  - `add_edit_subscription/{subscriptionId}` (NavType.LongType, default -1)
  - `category_list`, `statistics`, `settings`
- **AppTopBar**: gradient, hiển thị subtitle tuỳ route, hỗ trợ action search/add (chưa gắn logic search).
- **AppBottomBar**: Home, Subscriptions, Categories, Statistics, Settings.
- **Các màn hình đã có**:
  1. **Home**: Monthly summary, Upcoming renewals, nút "View All" (dẫn tới SubscriptionList), nút thêm nhanh.
  2. **Subscription List**: Danh sách đầy đủ, hiển thị tình trạng (Overdue/Due Soon/Inactive) theo màu sắc.
  3. **Subscription Detail**: (cần review UI chi tiết) hiển thị subscription + action (nhắc nhở, lịch sử).
  4. **Add/Edit Subscription**: Form đầy đủ (price, cycle, category, reminder, notes...).
  5. **Category List**: Liệt kê category (predefined vs custom), hiển thị keywords.
  6. **Statistics**: Tổng chi tiêu, lịch sử thanh toán tháng hiện tại.
  7. **Settings/Security/Backup**: ViewModel đã có, UI cần hoàn thiện để sử dụng use case tương ứng.

## 7. TESTING & CHẤT LƯỢNG
- **Unit test**: Có file test cho ViewModel (SubscriptionViewModelTest, CategoryViewModelTest, ...), sử dụng Turbine + Mockito. Tuy nhiên chữ ký ViewModel đã thay đổi (hiện phụ thuộc `StateFlow`, `SharedFlow` và nhiều use case), cần cập nhật test cho phù hợp.
- **UI test**: Có skeleton trong `androidTest` (SubscriptionListScreenTest, AddEditSubscriptionScreenTest, StatisticsScreenTest) nhưng nội dung còn placeholder.
- **Khuyến nghị**:
  - Cập nhật unit test theo phiên bản ViewModel hiện tại.
  - Viết thêm test cho ReminderManager/BackupManager (logic nhiều nhánh).
  - Bổ sung screenshot test/compose test cho các màn hình chính.

## 8. CÔNG VIỆC ƯU TIÊN TIẾP THEO
1. **Hoàn thiện trải nghiệm quản lý đăng ký**
   - Gắn luồng tìm kiếm/lọc trong SubscriptionList.
   - Bổ sung edit/xoá từ SubscriptionDetail và đồng bộ UI state khi quay lại.
2. **Quản lý danh mục**
   - Thêm màn Add/Edit Category, cho phép ánh xạ keyword.
   - Tích hợp gợi ý category tự động trong AddEditSubscription (dựa vào keyword).
3. **Nhắc nhở & Notification**
   - Kiểm tra quyền Exact Alarm/POST_NOTIFICATIONS (Android 13+).
   - Tạo màn quản lý Reminder rõ ràng (danh sách reminder, trạng thái đã thông báo).
4. **Sao lưu/Khôi phục & Cài đặt**
   - Thiết kế UI trong Settings để trigger backup, restore, share file.
   - Bổ sung thông báo quyền truy cập bộ nhớ/ SAF.
5. **Google Calendar Integration**
   - Hoàn thiện luồng đăng nhập Google, chọn lịch, đồng bộ hai chiều nếu cần.
   - Xử lý trường hợp credential hết hạn.
6. **Bảo mật**
   - Kết nối UI với BiometricAuth (cho phép khoá ứng dụng, yêu cầu xác thực trước khi mở app/restore).
   - Cấu hình SQLCipher (RoomDatabase `SupportFactory` + key management qua Security Crypto) nếu thực sự cần mã hoá.
7. **Thống kê nâng cao**
   - Kết nối dữ liệu thật với component BarChart/PieChart/LineChart.
   - Thêm filter theo thời gian, danh mục, top subscription.
8. **Debt cleanup**
   - Gỡ bỏ dependency chưa dùng hoặc tạo ticket riêng (DataStore, WorkManager integration, etc.).
   - Refactor ViewModelTest để phù hợp code hiện tại.

## 9. PHỤ LỤC – THƯ VIỆN & CÔNG NGHỆ CHÍNH
| Nhóm | Chi tiết |
|------|----------|
| UI | Jetpack Compose (Material3), Navigation Compose, Accompanist (không thấy sử dụng), MPAndroidChart wrapper |
| DI | Hilt (SingletonComponent, @HiltViewModel, @Inject) |
| Database | Room, Room KTX, Migration 1→2, Converters cho Enum |
| Đồng bộ/Service | AlarmManager, NotificationCompat, WorkManager KTX (chưa sử dụng), Google Calendar API |
| Bảo mật | AndroidX Security Crypto, Biometric, SQLCipher (chưa cấu hình) |
| Khác | DataStore Preferences (chưa dùng), Gson (backup), Coroutines + Flow |
| Test | JUnit, Mockito, Turbine, Compose UI Test, Navigation Testing |

---
Tài liệu này phản ánh kiến trúc và tính năng thực tế của codebase tại thời điểm hiện tại, đồng thời liệt kê các hạng mục cần hoàn thiện. Mọi thay đổi mới nên cập nhật lại kế hoạch để đảm bảo đồng bộ giữa tài liệu và triển khai.