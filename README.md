# Subscription Management App

Ứng dụng quản lý đăng ký giúp bạn theo dõi tất cả các dịch vụ đăng ký của mình, từ Netflix, Spotify đến các dịch vụ khác.

## Tính năng

- **Quản lý đăng ký**: Thêm, chỉnh sửa và xóa các đăng ký của bạn
- **Nhóm danh mục**: Tổ chức đăng ký của bạn vào các danh mục khác nhau
- **Nhắc nhở**: Thiết lập nhắc nhở trước khi ngày thanh toán tiếp theo
- **Lịch sử thanh toán**: Theo dõi tất cả các khoản thanh toán của bạn
- **Thống kê và biểu đồ**: Xem chi tiêu của bạn theo danh mục và thời gian
- **Tích hợp Google Calendar**: Đồng bộ đăng ký của bạn với Google Calendar
- **Sao lưu và khôi phục**: Sao lưu dữ liệu của bạn và khôi phục khi cần thiết
- **Bảo mật dữ liệu**: Mã hóa dữ liệu của bạn và xác thực sinh trắc học

## Công nghệ sử dụng

- **Kotlin**: Ngôn ngữ lập trình chính
- **Jetpack Compose**: UI toolkit
- **Room**: Cơ sở dữ liệu cục bộ
- **Dagger Hilt**: Dependency injection
- **Coroutines**: Xử lý bất đồng bộ
- **MVVM**: Kiến trúc ứng dụng
- **Material Design 3**: Thiết kế giao diện

## Cài đặt

1. Clone repository:
   ```bash
   git clone https://github.com/yourusername/subscription-management-app.git
   ```

2. Mở dự án trong Android Studio

3. Build và chạy ứng dụng trên thiết bị hoặc emulator

## Cấu trúc dự án

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/subcriptionmanagementapp/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── dao/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── AppDatabase.kt
│   │   │   │   ├── repository/
│   │   │   │   ├── backup/
│   │   │   │   ├── calendar/
│   │   │   │   └── security/
│   │   │   ├── domain/
│   │   │   │   ├── usecase/
│   │   │   │   │   ├── subscription/
│   │   │   │   │   ├── category/
│   │   │   │   │   ├── reminder/
│   │   │   │   │   ├── payment/
│   │   │   │   │   ├── statistics/
│   │   │   │   │   ├── calendar/
│   │   │   │   │   ├── backup/
│   │   │   │   │   └── security/
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   ├── screen/
│   │   │   │   ├── theme/
│   │   │   │   └── viewmodel/
│   │   │   └── util/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── test/
│       └── java/com/example/subcriptionmanagementapp/
│   └── androidTest/
│       └── java/com/example/subcriptionmanagementapp/
├── build.gradle.kts
└── gradle/libs.versions.toml
```

## Kiến trúc

Ứng dụng sử dụng kiến trúc MVVM (Model-View-ViewModel) với các thành phần sau:

- **Model**: Entities (Subscription, Category, Reminder, PaymentHistory)
- **View**: Jetpack Compose UI screens
- **ViewModel**: Classes xử lý logic UI và tương tác với Use Cases
- **Repository**: Classes xử lý truy cập dữ liệu từ nhiều nguồn
- **Use Cases**: Classes xử lý logic nghiệp vụ
- **Data Sources**: Room database, Google Calendar API, etc.

## Đóng góp

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes của bạn (`git commit -m 'Add some amazing feature'`)
4. Push lên branch (`git push origin feature/amazing-feature`)
5. Mở Pull Request

## License

Dự án này được cấp phép theo MIT License - xem file [LICENSE](LICENSE) để biết chi tiết.

## Liên hệ

Your Name - [@yourusername](https://twitter.com/yourusername) - email@example.com

Project Link: [https://github.com/yourusername/subscription-management-app](https://github.com/yourusername/subscription-management-app)