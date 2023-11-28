# RecieveSMSBankTransfer

- foreground service
- accessibility hoặc notification service
- từ Android O thì nó bắt phải thêm notification để chạy service dưới backgroud, để tránh trường hợp service lạ chạy ngần bên dưới, hiển thị notification để người dùng biết

# Xử lý Data consistency
- mất mạng, ko put sms lên server
- app ko hoạt động: bị kill, device hẹo(hết pin)
- server hẹo, méo insert đc


# USAGE: Thay thế các biến trong file MyApplication.java


